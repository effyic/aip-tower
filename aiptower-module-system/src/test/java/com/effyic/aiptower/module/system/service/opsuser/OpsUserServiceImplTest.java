package com.effyic.aiptower.module.system.service.opsuser;

import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.test.core.ut.BaseMockitoUnitTest;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.RoleDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.module.system.dal.mysql.permission.RoleMapper;
import com.effyic.aiptower.module.system.dal.mysql.user.AdminUserMapper;
import com.effyic.aiptower.module.system.enums.permission.OpsShadowRoles;
import com.effyic.aiptower.module.system.service.permission.MenuService;
import com.effyic.aiptower.module.system.service.permission.PermissionService;
import com.effyic.aiptower.module.system.service.permission.RoleService;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.effyic.aiptower.framework.test.core.util.AssertUtils.assertServiceException;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.OPS_USER_LAST_CANNOT_DELETE;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.OPS_USER_MENUS_EMPTY;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link OpsUserServiceImpl} 单元测试
 */
public class OpsUserServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private OpsUserServiceImpl opsUserService;

    @Mock
    private AdminUserService adminUserService;
    @Mock
    private AdminUserMapper adminUserMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private PermissionService permissionService;
    @Mock
    private MenuService menuService;

    @Test
    void testCreateOpsUser_success() {
        when(adminUserService.createUser(any())).thenReturn(100L);
        when(roleService.createRole(any(), anyInt())).thenReturn(200L);

        OpsUserSaveReqVO reqVO = new OpsUserSaveReqVO();
        reqVO.setUsername("ops001");
        reqVO.setPassword("123456");
        reqVO.setMenuIds(Set.of(1224L, 1138L));

        Long id = opsUserService.createOpsUser(reqVO);
        assertEquals(100L, id);
        verify(adminUserMapper).updateById(any(AdminUserDO.class));
        verify(permissionService).assignUserRole(eq(100L), eq(Set.of(200L)));
        verify(permissionService).assignRoleMenu(eq(200L), eq(Set.of(1224L, 1138L)));
    }

    @Test
    void testCreateOpsUser_menusEmpty() {
        OpsUserSaveReqVO reqVO = new OpsUserSaveReqVO();
        reqVO.setUsername("ops001");
        reqVO.setPassword("123456");
        reqVO.setMenuIds(Collections.emptySet());
        assertServiceException(() -> opsUserService.createOpsUser(reqVO), OPS_USER_MENUS_EMPTY);
    }

    @Test
    void testDeleteOpsUser_lastWithOpsUserMenu() {
        AdminUserDO user = new AdminUserDO();
        user.setId(100L);
        user.setUsername("ops001");
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        when(adminUserService.getUser(100L)).thenReturn(user);

        RoleDO shadowRole = new RoleDO();
        shadowRole.setId(200L);
        shadowRole.setCode(OpsShadowRoles.buildCode(100L));
        when(roleMapper.selectByCode(OpsShadowRoles.buildCode(100L))).thenReturn(shadowRole);
        when(permissionService.getUserRoleIdListByUserId(100L)).thenReturn(Set.of(200L));
        when(roleService.hasAnySuperAdmin(any())).thenReturn(false);
        when(permissionService.getRoleMenuListByRoleId(200L))
                .thenReturn(new HashSet<>(Set.of(OpsShadowRoles.OPS_USER_MENU_ID)));
        when(roleMapper.selectListByCodePrefix(OpsShadowRoles.CODE_PREFIX))
                .thenReturn(Collections.singletonList(shadowRole));

        assertServiceException(() -> opsUserService.deleteOpsUser(100L), OPS_USER_LAST_CANNOT_DELETE);
        verify(adminUserService, never()).deleteUser(anyLong());
    }

    @Test
    void testGetOpsUser_notOpsUser() {
        AdminUserDO user = new AdminUserDO();
        user.setId(100L);
        user.setUsername("normal");
        when(adminUserService.getUser(100L)).thenReturn(user);
        when(permissionService.getUserRoleIdListByUserId(100L)).thenReturn(Collections.emptySet());
        when(roleService.hasAnySuperAdmin(any())).thenReturn(false);
        when(roleMapper.selectByCode(OpsShadowRoles.buildCode(100L))).thenReturn(null);

        assertServiceException(() -> opsUserService.getOpsUser(100L), USER_NOT_EXISTS);
    }

}
