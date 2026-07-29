package com.effyic.aiptower.module.system.service.opsuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.common.util.collection.MapUtils;
import com.effyic.aiptower.framework.common.util.number.NumberUtils;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserRespVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.user.vo.user.UserPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.MenuDO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.RoleDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.module.system.dal.mysql.permission.RoleMapper;
import com.effyic.aiptower.module.system.dal.mysql.user.AdminUserMapper;
import com.effyic.aiptower.module.system.enums.permission.MenuTypeEnum;
import com.effyic.aiptower.module.system.enums.permission.OpsShadowRoles;
import com.effyic.aiptower.module.system.enums.permission.RoleCodeEnum;
import com.effyic.aiptower.module.system.enums.permission.RoleTypeEnum;
import com.effyic.aiptower.module.system.service.permission.MenuService;
import com.effyic.aiptower.module.system.service.permission.PermissionService;
import com.effyic.aiptower.module.system.service.permission.RoleService;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.effyic.aiptower.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertList;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertSet;
import static com.effyic.aiptower.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.OPS_USER_LAST_CANNOT_DELETE;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.OPS_USER_MENUS_EMPTY;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.OPS_USER_SHADOW_ROLE_NOT_EXISTS;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static java.util.Collections.singleton;

/**
 * 运营用户 Service 实现：一人一影子角色，菜单树授权
 */
@Service
@Validated
public class OpsUserServiceImpl implements OpsUserService {

    private static final String ADMIN_USERNAME = "admin";

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private MenuService menuService;

    @Override
    public PageResult<OpsUserRespVO> getOpsUserPage(OpsUserPageReqVO pageReqVO) {
        Set<Long> userIds = listOpsUserIds();
        if (CollUtil.isEmpty(userIds)) {
            return PageResult.empty();
        }
        UserPageReqVO userPageReqVO = new UserPageReqVO();
        userPageReqVO.setPageNo(pageReqVO.getPageNo());
        userPageReqVO.setPageSize(pageReqVO.getPageSize());
        userPageReqVO.setUsername(pageReqVO.getUsername());
        userPageReqVO.setStatus(pageReqVO.getStatus());
        if (!isCurrentUserSuperAdmin()) {
            userPageReqVO.setExcludeUsername(ADMIN_USERNAME);
        }
        PageResult<AdminUserDO> page = adminUserMapper.selectPage(userPageReqVO, null, userIds);
        List<OpsUserRespVO> list = convertList(page.getList(), this::buildResp);
        fillCreatorUpdaterNames(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public OpsUserRespVO getOpsUser(Long id) {
        AdminUserDO user = validateOpsUserAccessible(id);
        OpsUserRespVO respVO = buildResp(user);
        fillCreatorUpdaterNames(Collections.singletonList(respVO));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOpsUser(OpsUserSaveReqVO createReqVO) {
        Set<Long> menuIds = normalizeMenuIds(createReqVO.getMenuIds());
        UserSaveReqVO userSaveReqVO = new UserSaveReqVO();
        userSaveReqVO.setUsername(createReqVO.getUsername());
        userSaveReqVO.setNickname(createReqVO.getUsername());
        userSaveReqVO.setPassword(createReqVO.getPassword());
        Long userId = adminUserService.createUser(userSaveReqVO);
        if (createReqVO.getStatus() != null
                && !Objects.equals(createReqVO.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            adminUserService.updateUserStatus(userId, createReqVO.getStatus());
        }
        Long shadowRoleId = createShadowRole(userId, createReqVO.getUsername());
        permissionService.assignUserRole(userId, singleton(shadowRoleId));
        permissionService.assignRoleMenu(shadowRoleId, menuIds);
        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOpsUser(OpsUserSaveReqVO updateReqVO) {
        validateOpsUserAccessible(updateReqVO.getId());
        Set<Long> menuIds = normalizeMenuIds(updateReqVO.getMenuIds());
        if (updateReqVO.getStatus() != null) {
            adminUserService.updateUserStatus(updateReqVO.getId(), updateReqVO.getStatus());
        }
        if (StrUtil.isNotBlank(updateReqVO.getPassword())) {
            adminUserService.updateUserPassword(updateReqVO.getId(), updateReqVO.getPassword());
        }
        RoleDO shadowRole = requireShadowRole(updateReqVO.getId());
        permissionService.assignRoleMenu(shadowRole.getId(), menuIds);
        // 运营用户以 system_users 为聚合根展示更新人；菜单只改关联表时也 update 主表，
        // 由 MetaObjectHandler 填充 updater/updateTime（对齐 AdminUserService.updateUser）
        adminUserMapper.updateById(new AdminUserDO().setId(updateReqVO.getId()));
    }

    @Override
    public void updateOpsUserStatus(Long id, Integer status) {
        validateOpsUserAccessible(id);
        adminUserService.updateUserStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOpsUser(Long id) {
        validateOpsUserAccessible(id);
        RoleDO shadowRole = requireShadowRole(id);
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(shadowRole.getId());
        if (menuIds.contains(OpsShadowRoles.OPS_USER_MENU_ID)) {
            // 是否还有其他运营用户持有「运营用户管理」菜单
            boolean othersHaveOpsUserMenu = listShadowRoles().stream()
                    .filter(role -> !Objects.equals(role.getId(), shadowRole.getId()))
                    .anyMatch(role -> permissionService.getRoleMenuListByRoleId(role.getId())
                            .contains(OpsShadowRoles.OPS_USER_MENU_ID));
            if (!othersHaveOpsUserMenu) {
                throw exception(OPS_USER_LAST_CANNOT_DELETE);
            }
        }
        adminUserService.deleteUser(id);
        roleService.deleteRole(shadowRole.getId());
    }

    @Override
    public List<MenuSimpleRespVO> getOpsMenuSimpleList() {
        List<MenuDO> list = menuService.getMenuListByTenant(
                new MenuListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        list = menuService.filterDisableMenus(list);
        list.sort(Comparator.comparing(MenuDO::getSort));
        return BeanUtils.toBean(list, MenuSimpleRespVO.class);
    }

    private OpsUserRespVO buildResp(AdminUserDO user) {
        OpsUserRespVO respVO = BeanUtils.toBean(user, OpsUserRespVO.class);
        RoleDO shadowRole = getShadowRole(user.getId());
        if (shadowRole == null) {
            respVO.setMenuIds(Collections.emptySet());
            respVO.setMenuNames("");
            return respVO;
        }
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(shadowRole.getId());
        respVO.setMenuIds(menuIds);
        respVO.setMenuNames(formatMenuNames(menuIds));
        return respVO;
    }

    private String formatMenuNames(Set<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return "";
        }
        List<MenuDO> menus = menuService.getMenuList(menuIds);
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getType(), MenuTypeEnum.DIR.getType())
                        || Objects.equals(menu.getType(), MenuTypeEnum.MENU.getType()))
                .sorted(Comparator.comparing(MenuDO::getSort, Comparator.nullsLast(Integer::compareTo)))
                .map(MenuDO::getName)
                .collect(Collectors.joining("; "));
    }

    private void fillCreatorUpdaterNames(List<OpsUserRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> userIds = new HashSet<>();
        for (OpsUserRespVO item : list) {
            Long creatorId = NumberUtils.parseLong(item.getCreator());
            if (creatorId != null) {
                userIds.add(creatorId);
            }
            Long updaterId = NumberUtils.parseLong(item.getUpdater());
            if (updaterId != null) {
                userIds.add(updaterId);
            }
        }
        Map<Long, AdminUserDO> userMap = adminUserService.getUserMap(userIds);
        for (OpsUserRespVO item : list) {
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(item.getCreator()),
                    creator -> item.setCreatorName(creator.getNickname()));
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(item.getUpdater()),
                    updater -> item.setUpdaterName(updater.getNickname()));
        }
    }

    private Long createShadowRole(Long userId, String username) {
        RoleSaveReqVO roleSaveReqVO = new RoleSaveReqVO();
        roleSaveReqVO.setName(buildShadowRoleName(username));
        roleSaveReqVO.setCode(OpsShadowRoles.buildCode(userId));
        roleSaveReqVO.setSort(999);
        roleSaveReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        roleSaveReqVO.setRemark("运营用户影子角色，勿在角色管理中手动分配");
        return roleService.createRole(roleSaveReqVO, RoleTypeEnum.CUSTOM.getType());
    }

    private static String buildShadowRoleName(String username) {
        return "运营用户-" + username;
    }

    private Set<Long> normalizeMenuIds(Set<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            throw exception(OPS_USER_MENUS_EMPTY);
        }
        return new LinkedHashSet<>(menuIds);
    }

    private Set<Long> listOpsUserIds() {
        List<RoleDO> shadowRoles = listShadowRoles();
        if (CollUtil.isEmpty(shadowRoles)) {
            return Collections.emptySet();
        }
        return permissionService.getUserRoleIdListByRoleId(convertSet(shadowRoles, RoleDO::getId));
    }

    private List<RoleDO> listShadowRoles() {
        return roleMapper.selectListByCodePrefix(OpsShadowRoles.CODE_PREFIX);
    }

    private RoleDO getShadowRole(Long userId) {
        return roleMapper.selectByCode(OpsShadowRoles.buildCode(userId));
    }

    private RoleDO requireShadowRole(Long userId) {
        RoleDO role = getShadowRole(userId);
        if (role == null) {
            throw exception(OPS_USER_SHADOW_ROLE_NOT_EXISTS);
        }
        return role;
    }

    private AdminUserDO validateOpsUserAccessible(Long id) {
        AdminUserDO user = adminUserService.getUser(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isCurrentUserSuperAdmin() && ADMIN_USERNAME.equals(user.getUsername())) {
            throw exception(USER_NOT_EXISTS);
        }
        if (roleService.hasAnySuperAdmin(permissionService.getUserRoleIdListByUserId(id))
                && !isCurrentUserSuperAdmin()) {
            throw exception(USER_NOT_EXISTS);
        }
        if (getShadowRole(id) == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    private boolean isCurrentUserSuperAdmin() {
        Long loginUserId = getLoginUserId();
        if (loginUserId == null) {
            return false;
        }
        return permissionService.hasAnyRoles(loginUserId, RoleCodeEnum.SUPER_ADMIN.getCode());
    }

}
