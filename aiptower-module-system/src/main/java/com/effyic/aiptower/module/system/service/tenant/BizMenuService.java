package com.effyic.aiptower.module.system.service.tenant;

import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuListReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * B 端菜单 Service
 */
public interface BizMenuService {

    Long createBizMenu(BizMenuSaveReqVO createReqVO);

    void updateBizMenu(BizMenuSaveReqVO updateReqVO);

    void deleteBizMenu(Long id);

    void deleteBizMenuList(List<Long> ids);

    BizMenuDO getBizMenu(Long id);

    List<BizMenuDO> getBizMenuList(BizMenuListReqVO reqVO);

    List<BizMenuDO> getBizMenuList(Collection<Long> ids);

    /**
     * 获得 B 端菜单树（用于套餐勾选权限）
     *
     * @param onlyEnable 是否仅返回开启状态
     * @return 菜单树
     */
    List<BizMenuRespVO> getBizMenuTree(boolean onlyEnable);

    /**
     * 校验 menuIds 均存在于 B 端菜单表
     */
    void validateBizMenuIds(Set<Long> menuIds);

}
