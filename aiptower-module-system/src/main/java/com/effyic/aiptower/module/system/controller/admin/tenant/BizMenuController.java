package com.effyic.aiptower.module.system.controller.admin.tenant;

import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.CommonResult;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuListReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuSimpleRespVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO;
import com.effyic.aiptower.module.system.service.tenant.BizMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - B端菜单")
@RestController
@RequestMapping("/system/biz-menu")
@Validated
public class BizMenuController {

    @Resource
    private BizMenuService bizMenuService;

    @PostMapping("/create")
    @Operation(summary = "创建 B 端菜单")
    // @PreAuthorize("@ss.hasPermission('system:biz-menu:create')")
    public CommonResult<Long> createBizMenu(@Valid @RequestBody BizMenuSaveReqVO createReqVO) {
        return success(bizMenuService.createBizMenu(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改 B 端菜单")
    // @PreAuthorize("@ss.hasPermission('system:biz-menu:update')")
    public CommonResult<Boolean> updateBizMenu(@Valid @RequestBody BizMenuSaveReqVO updateReqVO) {
        bizMenuService.updateBizMenu(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 B 端菜单")
    @Parameter(name = "id", description = "菜单编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:biz-menu:delete')")
    public CommonResult<Boolean> deleteBizMenu(@RequestParam("id") Long id) {
        bizMenuService.deleteBizMenu(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除 B 端菜单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    // @PreAuthorize("@ss.hasPermission('system:biz-menu:delete')")
    public CommonResult<Boolean> deleteBizMenuList(@RequestParam("ids") List<Long> ids) {
        bizMenuService.deleteBizMenuList(ids);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取 B 端菜单列表", description = "用于【B端菜单管理】界面")
    // @PreAuthorize("@ss.hasPermission('system:biz-menu:query')")
    public CommonResult<List<BizMenuRespVO>> getBizMenuList(BizMenuListReqVO reqVO) {
        List<BizMenuDO> list = bizMenuService.getBizMenuList(reqVO);
        list.sort(Comparator.comparing(BizMenuDO::getSort));
        return success(BeanUtils.toBean(list, BizMenuRespVO.class));
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "获取 B 端菜单精简列表（平铺）", description = "只包含开启的菜单，前端可自行组树；用于套餐勾选")
    public CommonResult<List<BizMenuSimpleRespVO>> getSimpleBizMenuList() {
        List<BizMenuDO> list = bizMenuService.getBizMenuList(
                new BizMenuListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        list.sort(Comparator.comparing(BizMenuDO::getSort));
        return success(BeanUtils.toBean(list, BizMenuSimpleRespVO.class));
    }

    @GetMapping("/list-tree")
    @Operation(summary = "获取 B 端菜单树", description = "用于【租户套餐】配置菜单权限勾选，含目录/菜单/按钮")
    public CommonResult<List<BizMenuRespVO>> getBizMenuTree(
            @RequestParam(value = "onlyEnable", defaultValue = "true") Boolean onlyEnable) {
        return success(bizMenuService.getBizMenuTree(Boolean.TRUE.equals(onlyEnable)));
    }

    @GetMapping("/get")
    @Operation(summary = "获取 B 端菜单信息")
    // @PreAuthorize("@ss.hasPermission('system:biz-menu:query')")
    public CommonResult<BizMenuRespVO> getBizMenu(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(bizMenuService.getBizMenu(id), BizMenuRespVO.class));
    }

}
