package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - B端租户对接凭证 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCredentialRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long tenantId;

    @Schema(description = "对接 ClientId", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz_a001_xxxx")
    private String clientId;

    @Schema(description = "对接 ClientSecret（明文，仅重置时返回；查询接口不返回）", example = "aB3dE5gH7jK9mN1p")
    private String clientSecret;

}
