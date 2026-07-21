package com.effyic.aiptower.module.system.controller.admin.permission;

import cn.hutool.core.collection.CollUtil;
import com.effyic.aiptower.framework.apilog.core.annotation.ApiAccessLog;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.CommonResult;
import com.effyic.aiptower.framework.common.pojo.PageParam;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.common.util.collection.MapUtils;
import com.effyic.aiptower.framework.common.util.number.NumberUtils;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.framework.excel.core.util.ExcelUtils;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.RoleDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.module.system.enums.permission.RoleCodeEnum;
import com.effyic.aiptower.module.system.service.permission.PermissionService;
import com.effyic.aiptower.module.system.service.permission.RoleService;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.effyic.aiptower.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;
import static com.effyic.aiptower.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static java.util.Collections.singleton;

@Tag(name = "管理后台 - 角色")
@RestController
@RequestMapping("/system/role")
@Validated
public class RoleController {

    @Resource
    private RoleService roleService;
    @Resource
    private AdminUserService userService;
    @Resource
    private PermissionService permissionService;

    @PostMapping("/create")
    @Operation(summary = "创建角色")
    @PreAuthorize("@ss.hasPermission('system:role:create')")
    public CommonResult<Long> createRole(@Valid @RequestBody RoleSaveReqVO createReqVO) {
        return success(roleService.createRole(createReqVO, null));
    }

    @PutMapping("/update")
    @Operation(summary = "修改角色")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public CommonResult<Boolean> updateRole(@Valid @RequestBody RoleSaveReqVO updateReqVO) {
        roleService.updateRole(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRole(@RequestParam("id") Long id) {
        roleService.deleteRole(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除角色")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRoleList(@RequestParam("ids") List<Long> ids) {
        roleService.deleteRoleList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<RoleRespVO> getRole(@RequestParam("id") Long id) {
        RoleDO role = roleService.getRole(id);
        if (role == null) {
            return success(null);
        }
        RoleRespVO respVO = BeanUtils.toBean(role, RoleRespVO.class);
        fillRoleUserNames(Collections.singletonList(respVO));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得角色分页")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<PageResult<RoleRespVO>> getRolePage(RolePageReqVO pageReqVO) {
        // 非超管在 SQL 层排除 super_admin，保证分页总数正确
        pageReqVO.setExcludeCode(permissionService.hasAnyRoles(getLoginUserId(), RoleCodeEnum.SUPER_ADMIN.getCode())
                ? null : RoleCodeEnum.SUPER_ADMIN.getCode());
        PageResult<RoleDO> pageResult = roleService.getRolePage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        PageResult<RoleRespVO> respPage = BeanUtils.toBean(pageResult, RoleRespVO.class);
        fillRoleUserNames(respPage.getList());
        return success(respPage);
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取角色精简信息列表", description = "只包含被开启的角色，主要用于前端的下拉选项")
    public CommonResult<List<RoleRespVO>> getSimpleRoleList() {
        List<RoleDO> list = roleService.getRoleListByStatus(singleton(CommonStatusEnum.ENABLE.getStatus()));
        filterSuperAdminRole(list);
        list.sort(Comparator.comparing(RoleDO::getSort));
        return success(BeanUtils.toBean(list, RoleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出角色 Excel")
    @ApiAccessLog(operateType = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:role:export')")
    public void export(HttpServletResponse response, @Validated RolePageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        exportReqVO.setExcludeCode(permissionService.hasAnyRoles(getLoginUserId(), RoleCodeEnum.SUPER_ADMIN.getCode())
                ? null : RoleCodeEnum.SUPER_ADMIN.getCode());
        List<RoleDO> list = roleService.getRolePage(exportReqVO).getList();
        List<RoleRespVO> roleList = BeanUtils.toBean(list, RoleRespVO.class);
        fillRoleUserNames(roleList);
        // 输出
        ExcelUtils.write(response, "角色数据.xls", "数据", RoleRespVO.class, roleList);
    }

    /**
     * 非超级管理员时，隐藏超级管理员角色
     */
    private void filterSuperAdminRole(List<RoleDO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        if (permissionService.hasAnyRoles(getLoginUserId(), RoleCodeEnum.SUPER_ADMIN.getCode())) {
            return;
        }
        list.removeIf(role -> RoleCodeEnum.isSuperAdmin(role.getCode()));
    }

    /**
     * 补充角色创建人 / 更新人昵称
     */
    private void fillRoleUserNames(List<RoleRespVO> roles) {
        if (CollUtil.isEmpty(roles)) {
            return;
        }
        Set<Long> userIds = new HashSet<>();
        for (RoleRespVO role : roles) {
            Long creatorId = NumberUtils.parseLong(role.getCreator());
            if (creatorId != null) {
                userIds.add(creatorId);
            }
            Long updaterId = NumberUtils.parseLong(role.getUpdater());
            if (updaterId != null) {
                userIds.add(updaterId);
            }
        }
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIds);
        for (RoleRespVO role : roles) {
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(role.getCreator()),
                    user -> role.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(role.getUpdater()),
                    user -> role.setUpdaterName(user.getNickname()));
        }
    }

}
