package com.effyic.aiptower.module.system.controller.admin.tenant;

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
import com.effyic.aiptower.framework.tenant.core.aop.TenantIgnore;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantAdminAccountRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCreateRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCredentialRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.effyic.aiptower.framework.common.util.date.DateUtils;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.module.system.enums.tenant.TenantUsageStatusEnum;
import com.effyic.aiptower.module.system.service.tenant.TenantPackageService;
import com.effyic.aiptower.module.system.service.tenant.TenantService;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.effyic.aiptower.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertList;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertMap;

@Tag(name = "管理后台 - B端租户")
@RestController
@RequestMapping("/system/tenant")
@Validated
public class TenantController {

    @Resource
    private TenantService tenantService;
    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    private AdminUserService userService;

    @GetMapping("/get-id-by-name")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用租户名，获得租户编号", description = "兼容旧登录界面")
    @Parameter(name = "name", description = "租户名", required = true, example = "1024")
    public CommonResult<Long> getTenantIdByName(@RequestParam("name") String name) {
        TenantDO tenant = tenantService.getTenantByName(name);
        return success(tenant != null ? tenant.getId() : null);
    }

    @GetMapping({ "simple-list" })
    @PermitAll
    @TenantIgnore
    @Operation(summary = "获取租户精简信息列表", description = "只包含被开启的租户")
    public CommonResult<List<TenantRespVO>> getTenantSimpleList() {
        List<TenantDO> list = tenantService.getTenantListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(convertList(list, tenantDO ->
                new TenantRespVO().setId(tenantDO.getId()).setName(tenantDO.getName()).setCode(tenantDO.getCode())));
    }

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用域名，获得租户信息", description = "兼容旧登录界面")
    @Parameter(name = "website", description = "域名", required = true, example = "www.effyic.com")
    public CommonResult<TenantRespVO> getTenantByWebsite(
            @RequestParam("website") @Pattern(regexp = "^[a-zA-Z0-9.-]+(:\\d{1,5})?$", message = "网站域名格式不正确") String website) {
        TenantDO tenant = tenantService.getTenantByWebsite(website);
        if (tenant == null || CommonStatusEnum.isDisable(tenant.getStatus())) {
            return success(null);
        }
        return success(new TenantRespVO().setId(tenant.getId()).setName(tenant.getName()).setCode(tenant.getCode()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建 B 端租户", description = "自动生成对接凭证 + 默认管理员账号")
    // @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<TenantCreateRespVO> createTenant(@Valid @RequestBody TenantSaveReqVO createReqVO) {
        return success(tenantService.createTenant(createReqVO));
    }

    @PostMapping("/reset-credential")
    @Operation(summary = "重置 B 端对接凭证", description = "重新生成 clientSecret，明文仅本次返回；与管理员账号无关")
    @Parameter(name = "id", description = "租户编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<TenantCredentialRespVO> resetTenantCredential(@RequestParam("id") Long id) {
        return success(tenantService.resetTenantCredential(id));
    }

    @GetMapping("/get-credential")
    @Operation(summary = "查询 B 端对接凭证", description = "仅返回 clientId，不含 secret")
    @Parameter(name = "id", description = "租户编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantCredentialRespVO> getTenantCredential(@RequestParam("id") Long id) {
        return success(tenantService.getTenantCredential(id));
    }

    @PostMapping("/generate-admin")
    @Operation(summary = "一键生成 B 端管理员账号",
            description = "账号规则：admin-{医院拼音首字母}A001，序号按租户隔离递增；密码 10 位随机字母数字，明文落库供列表与 B 端拉取")
    @Parameter(name = "id", description = "租户编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<TenantAdminAccountRespVO> generateTenantAdmin(@RequestParam("id") Long id) {
        return success(tenantService.generateTenantAdmin(id));
    }

    @GetMapping("/admin-list")
    @Operation(summary = "B 端管理员账号列表", description = "返回初始账号、明文初始密码、创建时间")
    @Parameter(name = "id", description = "租户编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<List<TenantAdminAccountRespVO>> getTenantAdminList(@RequestParam("id") Long id) {
        return success(tenantService.getTenantAdminList(id));
    }

    @PutMapping("/update")
    @Operation(summary = "编辑租户", description = "编辑医院名称、医院等级、有效期、套餐版本、服务地址；创建编号与默认管理员账号不变")
    // @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantSaveReqVO updateReqVO) {
        tenantService.updateTenant(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantService.deleteTenant(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @Operation(summary = "批量删除租户")
    // @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenantList(@RequestParam("ids") List<Long> ids) {
        tenantService.deleteTenantList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        TenantDO tenant = tenantService.getTenant(id);
        TenantRespVO respVO = BeanUtils.toBean(tenant, TenantRespVO.class);
        fillTenantExtra(CollUtil.newArrayList(respVO));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户分页")
    // @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<PageResult<TenantRespVO>> getTenantPage(@Valid TenantPageReqVO pageVO) {
        PageResult<TenantDO> pageResult = tenantService.getTenantPage(pageVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        PageResult<TenantRespVO> respPage = BeanUtils.toBean(pageResult, TenantRespVO.class);
        fillTenantExtra(respPage.getList());
        return success(respPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租户 Excel")
    // @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTenantExcel(@Valid TenantPageReqVO exportReqVO, HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TenantDO> list = tenantService.getTenantPage(exportReqVO).getList();
        List<TenantRespVO> respList = BeanUtils.toBean(list, TenantRespVO.class);
        fillTenantExtra(respList);
        ExcelUtils.write(response, "租户.xls", "数据", TenantRespVO.class, respList);
    }

    /**
     * 补充套餐名、剩余天数、创建人/更新人昵称
     */
    private void fillTenantExtra(List<TenantRespVO> tenants) {
        if (CollUtil.isEmpty(tenants)) {
            return;
        }
        Set<Long> packageIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (TenantRespVO tenant : tenants) {
            if (tenant.getPackageId() != null) {
                packageIds.add(tenant.getPackageId());
            }
            Long creatorId = NumberUtils.parseLong(tenant.getCreator());
            if (creatorId != null) {
                userIds.add(creatorId);
            }
            Long updaterId = NumberUtils.parseLong(tenant.getUpdater());
            if (updaterId != null) {
                userIds.add(updaterId);
            }
        }
        Map<Long, TenantPackageDO> packageMap = convertMap(
                tenantPackageService.getTenantPackageList(packageIds), TenantPackageDO::getId);
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIds);
        LocalDate today = LocalDate.now();
        for (TenantRespVO tenant : tenants) {
            MapUtils.findAndThen(packageMap, tenant.getPackageId(),
                    pkg -> tenant.setPackageName(pkg.getName()));
            if (tenant.getExpireTime() != null) {
                tenant.setRemainDays(ChronoUnit.DAYS.between(today, tenant.getExpireTime().toLocalDate()));
                tenant.setUsageStatus(DateUtils.isExpired(tenant.getExpireTime())
                        ? TenantUsageStatusEnum.EXPIRED.getStatus()
                        : TenantUsageStatusEnum.IN_USE.getStatus());
            }
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(tenant.getCreator()),
                    user -> tenant.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(tenant.getUpdater()),
                    user -> tenant.setUpdaterName(user.getNickname()));
        }
    }

}
