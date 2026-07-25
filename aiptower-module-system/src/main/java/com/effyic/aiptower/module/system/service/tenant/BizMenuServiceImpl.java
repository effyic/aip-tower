package com.effyic.aiptower.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuListReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO;
import com.effyic.aiptower.module.system.dal.mysql.tenant.BizMenuMapper;
import com.effyic.aiptower.module.system.enums.permission.MenuTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static com.effyic.aiptower.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.filterList;
import static com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO.ID_ROOT;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
public class BizMenuServiceImpl implements BizMenuService {

    @Resource
    private BizMenuMapper bizMenuMapper;

    @Override
    public Long createBizMenu(BizMenuSaveReqVO createReqVO) {
        validateParentMenu(createReqVO.getParentId(), null);
        validateMenuName(createReqVO.getParentId(), createReqVO.getName(), null);

        BizMenuDO menu = BeanUtils.toBean(createReqVO, BizMenuDO.class);
        initMenuProperty(menu);
        if (menu.getVisible() == null) {
            menu.setVisible(true);
        }
        if (menu.getKeepAlive() == null) {
            menu.setKeepAlive(true);
        }
        if (menu.getAlwaysShow() == null) {
            menu.setAlwaysShow(true);
        }
        if (menu.getPermission() == null) {
            menu.setPermission("");
        }
        bizMenuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void updateBizMenu(BizMenuSaveReqVO updateReqVO) {
        if (bizMenuMapper.selectById(updateReqVO.getId()) == null) {
            throw exception(BIZ_MENU_NOT_EXISTS);
        }
        validateParentMenu(updateReqVO.getParentId(), updateReqVO.getId());
        validateMenuName(updateReqVO.getParentId(), updateReqVO.getName(), updateReqVO.getId());

        BizMenuDO updateObj = BeanUtils.toBean(updateReqVO, BizMenuDO.class);
        initMenuProperty(updateObj);
        bizMenuMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBizMenu(Long id) {
        if (bizMenuMapper.selectCountByParentId(id) > 0) {
            throw exception(BIZ_MENU_EXISTS_CHILDREN);
        }
        if (bizMenuMapper.selectById(id) == null) {
            throw exception(BIZ_MENU_NOT_EXISTS);
        }
        bizMenuMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBizMenuList(List<Long> ids) {
        ids.forEach(id -> {
            if (bizMenuMapper.selectCountByParentId(id) > 0) {
                throw exception(BIZ_MENU_EXISTS_CHILDREN);
            }
        });
        bizMenuMapper.deleteByIds(ids);
    }

    @Override
    public BizMenuDO getBizMenu(Long id) {
        return bizMenuMapper.selectById(id);
    }

    @Override
    public List<BizMenuDO> getBizMenuList(BizMenuListReqVO reqVO) {
        return bizMenuMapper.selectList(reqVO);
    }

    @Override
    public List<BizMenuDO> getBizMenuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return bizMenuMapper.selectListByIds(ids);
    }

    @Override
    public List<BizMenuRespVO> getBizMenuTree(boolean onlyEnable) {
        BizMenuListReqVO reqVO = new BizMenuListReqVO();
        if (onlyEnable) {
            reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        }
        List<BizMenuDO> list = getBizMenuList(reqVO);
        list.sort(Comparator.comparing(BizMenuDO::getSort));
        return buildMenuTree(list);
    }

    @Override
    public void validateBizMenuIds(Set<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return;
        }
        List<BizMenuDO> menus = getBizMenuList(menuIds);
        if (menus.size() != menuIds.size()) {
            Set<Long> exists = menus.stream().map(BizMenuDO::getId).collect(Collectors.toSet());
            Set<Long> missing = new HashSet<>(menuIds);
            missing.removeAll(exists);
            throw exception(TENANT_PACKAGE_MENU_INVALID, missing);
        }
    }

    /**
     * 构建菜单树（保留按钮节点，供套餐勾选权限使用）
     */
    private List<BizMenuRespVO> buildMenuTree(List<BizMenuDO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        Map<Long, BizMenuRespVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), BeanUtils.toBean(menu, BizMenuRespVO.class)));
        treeNodeMap.values().stream()
                .filter(node -> ObjUtil.notEqual(node.getParentId(), ID_ROOT))
                .forEach(childNode -> {
                    BizMenuRespVO parentNode = treeNodeMap.get(childNode.getParentId());
                    if (parentNode == null) {
                        return;
                    }
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(childNode);
                });
        return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }

    @VisibleForTesting
    void validateParentMenu(Long parentId, Long childId) {
        if (parentId == null || ID_ROOT.equals(parentId)) {
            return;
        }
        if (parentId.equals(childId)) {
            throw exception(BIZ_MENU_PARENT_ERROR);
        }
        BizMenuDO menu = bizMenuMapper.selectById(parentId);
        if (menu == null) {
            throw exception(BIZ_MENU_PARENT_NOT_EXISTS);
        }
        if (!MenuTypeEnum.DIR.getType().equals(menu.getType())
                && !MenuTypeEnum.MENU.getType().equals(menu.getType())) {
            throw exception(BIZ_MENU_PARENT_NOT_DIR_OR_MENU);
        }
    }

    @VisibleForTesting
    void validateMenuName(Long parentId, String name, Long id) {
        BizMenuDO menu = bizMenuMapper.selectByParentIdAndName(parentId, name);
        if (menu == null) {
            return;
        }
        if (id == null) {
            throw exception(BIZ_MENU_NAME_DUPLICATE);
        }
        if (!menu.getId().equals(id)) {
            throw exception(BIZ_MENU_NAME_DUPLICATE);
        }
    }

    private void initMenuProperty(BizMenuDO menu) {
        if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            menu.setComponent("");
            menu.setComponentName("");
            menu.setIcon("");
            menu.setPath("");
        }
        if (StrUtil.isBlank(menu.getPermission())) {
            menu.setPermission("");
        }
    }

}
