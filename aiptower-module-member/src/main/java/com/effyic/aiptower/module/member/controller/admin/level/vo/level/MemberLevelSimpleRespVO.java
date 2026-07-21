package com.effyic.aiptower.module.member.controller.admin.level.vo.level;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 会员等级 Response VO")
@Data
@ToString(callSuper = true)
public class MemberLevelSimpleRespVO {

    @Schema(description = "编号", example = "6103")
    private Long id;

    @Schema(description = "等级名称", example = "管理员")
    private String name;

    @Schema(description = "等级图标", example = "https://www.effyic.com/aiptower.jpg")
    private String icon;

}
