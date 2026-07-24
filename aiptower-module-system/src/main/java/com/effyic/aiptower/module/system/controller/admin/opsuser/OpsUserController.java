package com.effyic.aiptower.module.system.controller.admin.opsuser;

import com.effyic.aiptower.framework.common.pojo.CommonResult;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserRespVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserUpdateStatusReqVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.effyic.aiptower.module.system.service.opsuser.OpsUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 运营用户")
@RestController
@RequestMapping("/system/ops-user")
@Validated
public class OpsUserController {

    @Resource
    private OpsUserService opsUserService;

    @GetMapping("/page")
    @Operation(summary = "获得运营用户分页")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:query')")
    public CommonResult<PageResult<OpsUserRespVO>> getOpsUserPage(@Valid OpsUserPageReqVO pageReqVO) {
        return success(opsUserService.getOpsUserPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得运营用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:query')")
    public CommonResult<OpsUserRespVO> getOpsUser(@RequestParam("id") Long id) {
        return success(opsUserService.getOpsUser(id));
    }

    @GetMapping("/menu-list")
    @Operation(summary = "获得可授权菜单精简列表", description = "用于运营用户菜单树勾选")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:query')")
    public CommonResult<List<MenuSimpleRespVO>> getOpsMenuSimpleList() {
        return success(opsUserService.getOpsMenuSimpleList());
    }

    @PostMapping("/create")
    @Operation(summary = "创建运营用户")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:create')")
    public CommonResult<Long> createOpsUser(@Valid @RequestBody OpsUserSaveReqVO createReqVO) {
        return success(opsUserService.createOpsUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改运营用户")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:update')")
    public CommonResult<Boolean> updateOpsUser(@Valid @RequestBody OpsUserSaveReqVO updateReqVO) {
        opsUserService.updateOpsUser(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "修改运营用户状态")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:update')")
    public CommonResult<Boolean> updateOpsUserStatus(@Valid @RequestBody OpsUserUpdateStatusReqVO reqVO) {
        opsUserService.updateOpsUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除运营用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:ops-user:delete')")
    public CommonResult<Boolean> deleteOpsUser(@RequestParam("id") Long id) {
        opsUserService.deleteOpsUser(id);
        return success(true);
    }

}
