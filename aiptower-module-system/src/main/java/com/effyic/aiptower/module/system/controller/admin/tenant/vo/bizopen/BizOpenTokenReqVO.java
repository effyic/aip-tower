package com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "B端开放接口 - 获取 Token Request VO")
@Data
public class BizOpenTokenReqVO {

    @Schema(description = "ClientId", requiredMode = Schema.RequiredMode.REQUIRED, example = "biz_a001_xxxx")
    @NotBlank(message = "clientId 不能为空")
    private String clientId;

    @Schema(description = "ClientSecret", requiredMode = Schema.RequiredMode.REQUIRED, example = "aB3dE5gH7jK9mN1p")
    @NotBlank(message = "clientSecret 不能为空")
    private String clientSecret;

}
