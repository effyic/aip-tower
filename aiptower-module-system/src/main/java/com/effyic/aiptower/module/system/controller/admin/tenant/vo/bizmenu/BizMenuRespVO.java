package com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - B端菜单 Response VO")
@Data
public class BizMenuRespVO {

    @Schema(description = "菜单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "分诊工作台")
    private String name;

    @Schema(description = "云端菜单编码", example = "triage_workbench")
    private String menuCode;

    @Schema(description = "权限标识", example = "biz:triage:query")
    private String permission;

    @Schema(description = "类型，参见 MenuTypeEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "父菜单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Long parentId;

    @Schema(description = "路由地址", example = "triage")
    private String path;

    @Schema(description = "菜单图标", example = "ep:menu")
    private String icon;

    @Schema(description = "组件路径", example = "biz/triage/index")
    private String component;

    @Schema(description = "组件名", example = "BizTriage")
    private String componentName;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "是否可见", example = "true")
    private Boolean visible;

    @Schema(description = "是否缓存", example = "true")
    private Boolean keepAlive;

    @Schema(description = "是否总是显示", example = "true")
    private Boolean alwaysShow;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子菜单")
    private List<BizMenuRespVO> children;

}
