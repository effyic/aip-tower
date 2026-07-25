package com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - B端菜单创建/修改 Request VO")
@Data
public class BizMenuSaveReqVO {

    @Schema(description = "菜单编号", example = "1024")
    private Long id;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "分诊工作台")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String name;

    @Schema(description = "云端菜单编码", example = "triage_workbench")
    @Size(max = 64, message = "菜单编码长度不能超过64个字符")
    private String menuCode;

    @Schema(description = "权限标识", example = "biz:triage:query")
    @Size(max = 100)
    private String permission;

    @Schema(description = "类型，参见 MenuTypeEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "父菜单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "父菜单 ID 不能为空")
    private Long parentId;

    @Schema(description = "路由地址", example = "triage")
    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;

    @Schema(description = "菜单图标", example = "ep:menu")
    private String icon;

    @Schema(description = "组件路径", example = "biz/triage/index")
    @Size(max = 255, message = "组件路径不能超过255个字符")
    private String component;

    @Schema(description = "组件名", example = "BizTriage")
    private String componentName;

    @Schema(description = "状态,见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "是否可见", example = "true")
    private Boolean visible;

    @Schema(description = "是否缓存", example = "true")
    private Boolean keepAlive;

    @Schema(description = "是否总是显示", example = "true")
    private Boolean alwaysShow;

}
