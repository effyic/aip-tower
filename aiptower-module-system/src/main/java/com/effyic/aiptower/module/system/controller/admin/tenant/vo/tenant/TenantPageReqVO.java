package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import com.effyic.aiptower.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.effyic.aiptower.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 租户分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPageReqVO extends PageParam {

    @Schema(description = "医院名称", example = "协和医院")
    private String name;

    @Schema(description = "联系人", example = "管理员")
    private String contactName;

    @Schema(description = "联系手机", example = "15601691300")
    private String contactMobile;

    @Schema(description = "使用状态：0使用中 1已过期（按有效期 expireTime 判断，非表 status 字段）", example = "0")
    private Integer usageStatus;

    @Schema(description = "套餐版本编号", example = "111")
    private Long packageId;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
