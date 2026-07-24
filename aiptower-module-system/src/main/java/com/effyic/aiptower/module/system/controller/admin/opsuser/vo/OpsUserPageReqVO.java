package com.effyic.aiptower.module.system.controller.admin.opsuser.vo;

import com.effyic.aiptower.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 运营用户分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OpsUserPageReqVO extends PageParam {

    @Schema(description = "用户账号，模糊匹配", example = "158534833736")
    private String username;

    @Schema(description = "启用状态，参见 CommonStatusEnum", example = "0")
    private Integer status;

}
