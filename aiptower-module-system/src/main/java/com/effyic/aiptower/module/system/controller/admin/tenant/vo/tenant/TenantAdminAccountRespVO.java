package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 租户管理员账号 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantAdminAccountRespVO {

    @Schema(description = "用户编号", example = "101")
    private Long userId;

    @Schema(description = "初始账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin-xhyyA001")
    private String username;

    @Schema(description = "初始密码（明文）", requiredMode = Schema.RequiredMode.REQUIRED, example = "bjkshgioi1")
    private String password;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
