package com.effyic.aiptower.module.system.controller.admin.opsuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "管理后台 - 运营用户 Response VO")
@Data
public class OpsUserRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "158534833736")
    private String username;

    @Schema(description = "授权菜单编号集合")
    private Set<Long> menuIds;

    @Schema(description = "用户权限展示（目录/菜单名拼接）", example = "租户管理; 套餐管理")
    private String menuNames;

    @Schema(description = "启用状态，参见 CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "创建人编号")
    private String creator;

    @Schema(description = "创建人", example = "超级管理员")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人编号")
    private String updater;

    @Schema(description = "更新人", example = "超级管理员")
    private String updaterName;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
