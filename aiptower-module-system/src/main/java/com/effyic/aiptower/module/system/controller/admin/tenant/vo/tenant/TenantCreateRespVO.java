package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 租户创建 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreateRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "创建编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "A001")
    private String code;

    @Schema(description = "默认管理员账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin-XHYYA001")
    private String username;

    @Schema(description = "默认管理员密码（明文，仅创建时返回一次）", requiredMode = Schema.RequiredMode.REQUIRED, example = "aB3dE5gH7j")
    private String password;

    @Schema(description = "管理员用户编号", example = "100")
    private Long userId;

    @Schema(description = "管理员创建时间", example = "2026-04-22T12:12:08")
    private LocalDateTime userCreateTime;

}
