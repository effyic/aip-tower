package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - B端租户创建 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreateRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "机构编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "A001")
    private String code;

    @Schema(description = "对接 ClientId", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz_a001_xxxx")
    private String clientId;

    @Schema(description = "对接 ClientSecret（明文，仅创建/重置时返回一次）", requiredMode = Schema.RequiredMode.REQUIRED, example = "aB3dE5gH7jK9mN1p")
    private String clientSecret;

    @Schema(description = "默认管理员账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin-xhyyA001")
    private String adminUsername;

    @Schema(description = "默认管理员密码（明文）", requiredMode = Schema.RequiredMode.REQUIRED, example = "aB3dE5gH7j")
    private String adminPassword;

    @Schema(description = "管理员创建时间", example = "2026-04-22T12:12:08")
    private LocalDateTime adminCreateTime;

}
