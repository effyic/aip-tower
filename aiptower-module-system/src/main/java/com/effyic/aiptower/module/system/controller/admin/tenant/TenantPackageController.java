package com.effyic.aiptower.module.system.controller.admin.tenant;

import cn.hutool.core.collection.CollUtil;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.CommonResult;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.common.util.collection.MapUtils;
import com.effyic.aiptower.framework.common.util.number.NumberUtils;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.packages.TenantPackageRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.packages.TenantPackageSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.packages.TenantPackageSimpleRespVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.module.system.service.tenant.BizMenuService;
import com.effyic.aiptower.module.system.service.tenant.TenantPackageService;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertMap;

@Tag(name = "管理后台 - B端租户套餐")
@RestController
@RequestMapping("/system/tenant-package")
@Validated
public class TenantPackageController {

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    private AdminUserService userService;
    @Resource
    private BizMenuService bizMenuService;

    @PostMapping("/create")
    @Operation(summary = "创建租户套餐")
    // @PreAuthorize("@ss.hasPermission('system:tenant-package:create')")
    public CommonResult<Long> createTenantPackage(@Valid @RequestBody TenantPackageSaveReqVO createReqVO) {
        return success(tenantPackageService.createTenantPackage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租户套餐")
    // @PreAuthorize("@ss.hasPermission('system:tenant-package:update')")
    public CommonResult<Boolean> updateTenantPackage(@Valid @RequestBody TenantPackageSaveReqVO updateReqVO) {
        tenantPackageService.updateTenantPackage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租户套餐")
    @Parameter(name = "id", description = "编号", required = true)
    // @PreAuthorize("@ss.hasPermission('system:tenant-package:delete')")
    public CommonResult<Boolean> deleteTenantPackage(@RequestParam("id") Long id) {
        tenantPackageService.deleteTenantPackage(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @Operation(summary = "批量删除租户套餐")
    // @PreAuthorize("@ss.hasPermission('system:tenant-package:delete')")
    public CommonResult<Boolean> deleteTenantPackageList(@RequestParam("ids") List<Long> ids) {
        tenantPackageService.deleteTenantPackageList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户套餐")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant-package:query')")
    public CommonResult<TenantPackageRespVO> getTenantPackage(@RequestParam("id") Long id) {
        TenantPackageDO tenantPackage = tenantPackageService.getTenantPackage(id);
        TenantPackageRespVO respVO = BeanUtils.toBean(tenantPackage, TenantPackageRespVO.class);
        fillTenantPackageExtra(Collections.singletonList(respVO));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户套餐分页")
    // @PreAuthorize("@ss.hasPermission('system:tenant-package:query')")
    public CommonResult<PageResult<TenantPackageRespVO>> getTenantPackagePage(@Valid TenantPackagePageReqVO pageVO) {
        PageResult<TenantPackageDO> pageResult = tenantPackageService.getTenantPackagePage(pageVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        PageResult<TenantPackageRespVO> respPage = BeanUtils.toBean(pageResult, TenantPackageRespVO.class);
        fillTenantPackageExtra(respPage.getList());
        return success(respPage);
    }

    @GetMapping({"/get-simple-list", "simple-list"})
    @Operation(summary = "获取租户套餐精简信息列表", description = "只包含被开启的租户套餐，主要用于前端的下拉选项")
    public CommonResult<List<TenantPackageSimpleRespVO>> getTenantPackageList() {
        List<TenantPackageDO> list = tenantPackageService.getTenantPackageListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(BeanUtils.toBean(list, TenantPackageSimpleRespVO.class));
    }

    /**
     * 补充授权资源名称、创建人/更新人昵称
     */
    private void fillTenantPackageExtra(List<TenantPackageRespVO> packages) {
        if (CollUtil.isEmpty(packages)) {
            return;
        }
        // 1. 收集菜单、用户 ID
        Set<Long> menuIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (TenantPackageRespVO pkg : packages) {
            if (CollUtil.isNotEmpty(pkg.getMenuIds())) {
                menuIds.addAll(pkg.getMenuIds());
            }
            Long creatorId = NumberUtils.parseLong(pkg.getCreator());
            if (creatorId != null) {
                userIds.add(creatorId);
            }
            Long updaterId = NumberUtils.parseLong(pkg.getUpdater());
            if (updaterId != null) {
                userIds.add(updaterId);
            }
        }
        Map<Long, BizMenuDO> menuMap = convertMap(bizMenuService.getBizMenuList(menuIds), BizMenuDO::getId);
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIds);
        // 2. 回填
        for (TenantPackageRespVO pkg : packages) {
            if (CollUtil.isNotEmpty(pkg.getMenuIds())) {
                List<String> names = new ArrayList<>(pkg.getMenuIds().size());
                for (Long menuId : pkg.getMenuIds()) {
                    BizMenuDO menu = menuMap.get(menuId);
                    if (menu != null) {
                        names.add(menu.getName());
                    }
                }
                pkg.setMenuNames(names);
            } else {
                pkg.setMenuNames(Collections.emptyList());
            }
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(pkg.getCreator()),
                    user -> pkg.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(pkg.getUpdater()),
                    user -> pkg.setUpdaterName(user.getNickname()));
        }
    }

}
