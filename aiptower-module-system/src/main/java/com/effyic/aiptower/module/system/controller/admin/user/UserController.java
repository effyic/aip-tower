package com.effyic.aiptower.module.system.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.effyic.aiptower.framework.apilog.core.annotation.ApiAccessLog;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.CommonResult;
import com.effyic.aiptower.framework.common.pojo.PageParam;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.common.util.collection.MapUtils;
import com.effyic.aiptower.framework.common.util.number.NumberUtils;
import com.effyic.aiptower.framework.excel.core.util.ExcelUtils;
import com.effyic.aiptower.module.system.controller.admin.user.vo.user.*;
import com.effyic.aiptower.module.system.convert.user.UserConvert;
import com.effyic.aiptower.module.system.dal.dataobject.dept.DeptDO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.RoleDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.module.system.enums.common.SexEnum;
import com.effyic.aiptower.module.system.enums.permission.RoleCodeEnum;
import com.effyic.aiptower.module.system.service.dept.DeptService;
import com.effyic.aiptower.module.system.service.permission.PermissionService;
import com.effyic.aiptower.module.system.service.permission.RoleService;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.effyic.aiptower.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.effyic.aiptower.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertList;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertMap;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertSet;
import static com.effyic.aiptower.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;

@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/system/user")
@Validated
public class UserController {

    /**
     * 系统内置超管账号，非超级管理员不可见、不可操作
     */
    private static final String ADMIN_USERNAME = "admin";

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserSaveReqVO reqVO) {
        Long id = userService.createUser(reqVO);
        return success(id);
    }

    @PutMapping("update")
    @Operation(summary = "修改用户")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserSaveReqVO reqVO) {
        validateAdminUserAccessible(reqVO.getId());
        userService.updateUser(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        validateAdminUserAccessible(id);
        userService.deleteUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @Operation(summary = "批量删除用户")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUserList(@RequestParam("ids") List<Long> ids) {
        ids.forEach(this::validateAdminUserAccessible);
        userService.deleteUserList(ids);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        validateAdminUserAccessible(reqVO.getId());
        userService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "修改用户状态")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        validateAdminUserAccessible(reqVO.getId());
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户分页列表")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageReqVO) {
        // 非超管在 SQL 层排除 admin，保证分页总数正确
        pageReqVO.setExcludeUsername(isCurrentUserSuperAdmin() ? null : ADMIN_USERNAME);
        // 获得用户分页列表
        PageResult<AdminUserDO> pageResult = userService.getUserPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(pageResult.getList(), AdminUserDO::getDeptId));
        List<UserRespVO> userList = UserConvert.INSTANCE.convertList(pageResult.getList(), deptMap);
        fillUserExtra(userList);
        return success(new PageResult<>(userList, pageResult.getTotal()));
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户详情列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "[1024]")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<List<UserRespVO>> getUserList(@RequestParam("ids") List<Long> ids) {
        List<AdminUserDO> list = userService.getUserList(ids);
        filterAdminUser(list);
        if (CollUtil.isEmpty(list)) {
            return success(Collections.emptyList());
        }
        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertSet(list, AdminUserDO::getDeptId));
        List<UserRespVO> userList = UserConvert.INSTANCE.convertList(list, deptMap);
        fillUserExtra(userList);
        return success(userList);
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取用户精简信息列表", description = "只包含被开启的用户，主要用于前端的下拉选项")
    public CommonResult<List<UserSimpleRespVO>> getSimpleUserList(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        List<AdminUserDO> list;
        if (deptId != null) {
            List<Long> deptIds = Collections.singletonList(deptId);
            list = userService.getDeptUsers(deptIds);
        } else {
            list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
        }
        filterAdminUser(list);

        // 拼接数据
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        AdminUserDO user = userService.getUser(id);
        if (user == null || isHiddenAdminUser(user)) {
            return success(null);
        }
        // 拼接数据
        DeptDO dept = deptService.getDept(user.getDeptId());
        UserRespVO userVO = UserConvert.INSTANCE.convert(user, dept);
        fillUserExtra(Collections.singletonList(userVO));
        return success(userVO);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserList(@Validated UserPageReqVO exportReqVO,
                               HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        exportReqVO.setExcludeUsername(isCurrentUserSuperAdmin() ? null : ADMIN_USERNAME);
        List<AdminUserDO> list = userService.getUserPage(exportReqVO).getList();
        // 输出 Excel
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(
                convertList(list, AdminUserDO::getDeptId));
        List<UserRespVO> userList = UserConvert.INSTANCE.convertList(list, deptMap);
        fillUserExtra(userList);
        ExcelUtils.write(response, "用户数据.xls", "数据", UserRespVO.class, userList);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入用户模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<UserImportExcelVO> list = Arrays.asList(
                UserImportExcelVO.builder().username("yunai").deptId(1L).email("yunai@effyic.com").mobile("15601691300")
                        .nickname("AIP-Tower").status(CommonStatusEnum.ENABLE.getStatus()).sex(SexEnum.MALE.getSex()).build(),
                UserImportExcelVO.builder().username("yuanma").deptId(2L).email("dev@effyic.com").mobile("15601701300")
                        .nickname("源码").status(CommonStatusEnum.DISABLE.getStatus()).sex(SexEnum.FEMALE.getSex()).build()
        );
        // 输出
        ExcelUtils.write(response, "用户导入模板.xls", "用户列表", UserImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "导入用户")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true")
    })
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public CommonResult<UserImportRespVO> importExcel(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        List<UserImportExcelVO> list = ExcelUtils.read(file, UserImportExcelVO.class);
        return success(userService.importUserList(list, updateSupport));
    }

    /**
     * 补充用户角色、创建人/更新人昵称
     */
    private void fillUserExtra(List<UserRespVO> users) {
        if (CollUtil.isEmpty(users)) {
            return;
        }
        // 1. 角色
        Map<Long, Set<Long>> userRoleIdsMap = permissionService.getUserRoleIdListByUserIds(
                convertSet(users, UserRespVO::getId));
        Set<Long> roleIds = new HashSet<>();
        userRoleIdsMap.values().forEach(roleIds::addAll);
        Map<Long, RoleDO> roleMap = convertMap(roleService.getRoleList(roleIds), RoleDO::getId);
        // 非超管不可见超级管理员角色
        boolean hideSuperAdmin = !isCurrentUserSuperAdmin();
        // 2. 创建人 / 更新人
        Set<Long> userIds = new HashSet<>();
        for (UserRespVO user : users) {
            Long creatorId = NumberUtils.parseLong(user.getCreator());
            if (creatorId != null) {
                userIds.add(creatorId);
            }
            Long updaterId = NumberUtils.parseLong(user.getUpdater());
            if (updaterId != null) {
                userIds.add(updaterId);
            }
        }
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIds);
        // 3. 回填
        for (UserRespVO user : users) {
            Set<Long> userRoleIds = new HashSet<>(userRoleIdsMap.getOrDefault(user.getId(), Collections.emptySet()));
            if (hideSuperAdmin) {
                userRoleIds.removeIf(roleId -> {
                    RoleDO role = roleMap.get(roleId);
                    return role != null && RoleCodeEnum.isSuperAdmin(role.getCode());
                });
            }
            user.setRoleIds(userRoleIds);
            user.setRoleNames(convertSet(userRoleIds, roleId -> {
                RoleDO role = roleMap.get(roleId);
                return role != null ? role.getName() : null;
            }));
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(user.getCreator()),
                    creator -> user.setCreatorName(creator.getNickname()));
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(user.getUpdater()),
                    updater -> user.setUpdaterName(updater.getNickname()));
        }
    }

    /**
     * 非超管时，隐藏 admin 账号
     */
    private void filterAdminUser(List<AdminUserDO> list) {
        if (CollUtil.isEmpty(list) || isCurrentUserSuperAdmin()) {
            return;
        }
        list.removeIf(this::isHiddenAdminUser);
    }

    /**
     * 非超管不可操作 admin 账号
     */
    private void validateAdminUserAccessible(Long userId) {
        if (isCurrentUserSuperAdmin() || userId == null) {
            return;
        }
        AdminUserDO user = userService.getUser(userId);
        if (user != null && isHiddenAdminUser(user)) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    private boolean isHiddenAdminUser(AdminUserDO user) {
        return user != null && ADMIN_USERNAME.equals(user.getUsername()) && !isCurrentUserSuperAdmin();
    }

    private boolean isCurrentUserSuperAdmin() {
        return permissionService.hasAnyRoles(getLoginUserId(), RoleCodeEnum.SUPER_ADMIN.getCode());
    }

    // ==================== 免鉴权接口（用于 IM 点头像弹名片、加好友搜索等场景） ====================

    @GetMapping("/get-simple")
    @Operation(summary = "获得用户精简信息", description = "用于点头像弹名片等场景；免鉴权")
    @Parameter(name = "id", description = "用户编号", required = true, example = "1024")
    public CommonResult<UserSimpleRespVO> getSimpleUser(@RequestParam("id") Long id) {
        AdminUserDO user = userService.getUser(id);
        if (user == null) {
            return success(null);
        }
        // 拼接数据
        DeptDO dept = user.getDeptId() != null ? deptService.getDept(user.getDeptId()) : null;
        Map<Long, DeptDO> deptMap = dept != null ? Collections.singletonMap(dept.getId(), dept) : Collections.emptyMap();
        return success(CollUtil.getFirst(UserConvert.INSTANCE.convertSimpleList(
                Collections.singletonList(user), deptMap)));
    }

    @GetMapping("/list-by-nickname")
    @Operation(summary = "按昵称模糊搜索用户精简信息", description = "用于加好友等场景；免鉴权；当前仅按昵称匹配")
    @Parameter(name = "nickname", description = "昵称关键词", required = true, example = "AIP-Tower")
    public CommonResult<List<UserSimpleRespVO>> getSimpleUserListByNickname(@RequestParam("nickname") String nickname) {
        if (StrUtil.isBlank(nickname)) {
            return success(Collections.emptyList());
        }
        // 拼接数据
        List<AdminUserDO> list = userService.getUserListByNickname(nickname.trim());
        Map<Long, DeptDO> deptMap = deptService.getDeptMap(convertList(list, AdminUserDO::getDeptId));
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }

}
