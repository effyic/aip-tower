package com.effyic.aiptower.module.system.controller.admin.tenant.vo.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Schema(description = "管理后台 - 租户套餐 Response VO")
@Data
public class TenantPackageRespVO {

    @Schema(description = "套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "套餐名", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP")
    private String name;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "备注", example = "好")
    private String remark;

    @Schema(description = "关联的菜单编号（授权资源）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<Long> menuIds;

    @Schema(description = "授权资源名称列表", example = "[\"通用分诊Agent\",\"问诊Agent\"]")
    private List<String> menuNames;

    @Schema(description = "分诊Agent上限", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer triageAgentLimit;

    @Schema(description = "问诊Agent上限", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer inquiryAgentLimit;

    @Schema(description = "高级配置（false 关 / true 开）", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean advancedConfigEnabled;

    @Schema(description = "自定义病例（false 关 / true 开）", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean customCaseEnabled;

    @Schema(description = "创建人", example = "1")
    private String creator;

    @Schema(description = "创建人昵称", example = "超级管理员")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新人", example = "1")
    private String updater;

    @Schema(description = "更新人昵称", example = "超级管理员")
    private String updaterName;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}
