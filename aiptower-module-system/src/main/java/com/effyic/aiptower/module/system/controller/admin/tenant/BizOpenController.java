package com.effyic.aiptower.module.system.controller.admin.tenant;

import com.effyic.aiptower.framework.common.pojo.CommonResult;
import com.effyic.aiptower.framework.tenant.core.aop.TenantIgnore;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenPackageConfigRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenTokenReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenTokenRespVO;
import com.effyic.aiptower.module.system.service.tenant.BizOpenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.effyic.aiptower.framework.common.pojo.CommonResult.success;

@Tag(name = "B端开放接口 - 授权配置")
@RestController
@RequestMapping("/system/biz-open")
@Validated
public class BizOpenController {

    @Resource
    private BizOpenService bizOpenService;

    @PostMapping("/token")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "获取访问令牌", description = "使用 clientId + clientSecret 换取短时效 accessToken")
    public CommonResult<BizOpenTokenRespVO> createToken(@Valid @RequestBody BizOpenTokenReqVO reqVO) {
        return success(bizOpenService.createAccessToken(reqVO));
    }

    @GetMapping("/package-config")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "拉取套餐配置", description = "Header: Authorization: Bearer {accessToken}")
    public CommonResult<BizOpenPackageConfigRespVO> getPackageConfig(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return success(bizOpenService.getPackageConfig(authorization));
    }

}
