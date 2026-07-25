package com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - B端菜单列表 Request VO")
@Data
public class BizMenuListReqVO {

    @Schema(description = "菜单名称，模糊匹配", example = "分诊")
    private String name;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "0")
    private Integer status;

}
