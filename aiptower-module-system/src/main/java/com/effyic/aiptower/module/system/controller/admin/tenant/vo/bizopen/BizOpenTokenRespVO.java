package com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "B端开放接口 - Token Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BizOpenTokenRespVO {

    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessToken;

    @Schema(description = "过期时间（秒）", requiredMode = Schema.RequiredMode.REQUIRED, example = "7200")
    private Long expiresIn;

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long tenantId;

    @Schema(description = "机构编号", example = "A001")
    private String tenantCode;

}
