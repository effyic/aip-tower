package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 租户创建/修改 Request VO")
@Data
public class TenantSaveReqVO {

    @Schema(description = "租户编号（编辑时必填）", example = "1024")
    private Long id;

    @Schema(description = "医院名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "协和医院")
    @NotBlank(message = "医院名称不能为空")
    private String name;

    @Schema(description = "医院等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "三甲")
    @NotBlank(message = "医院等级不能为空")
    private String hospitalLevel;

    @Schema(description = "服务地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://hospital.example.com")
    @NotBlank(message = "服务地址不能为空")
    private String serviceUrl;

    @Schema(description = "联系人", example = "管理员")
    private String contactName;

    @Schema(description = "联系手机", example = "15601691300")
    private String contactMobile;

    @Schema(description = "租户状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "绑定域名数组", example = "[\"https://www.effyic.com\"]")
    private List<String> websites;

    @Schema(description = "租户套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "套餐版本不能为空")
    private Long packageId;

    @Schema(description = "有效期至", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "有效期至不能为空")
    private LocalDateTime expireTime;

    @Schema(description = "账号数量", example = "100")
    private Integer accountCount;

}
