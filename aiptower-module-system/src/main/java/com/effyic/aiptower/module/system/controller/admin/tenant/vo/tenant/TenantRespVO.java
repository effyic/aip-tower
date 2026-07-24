package com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.effyic.aiptower.framework.excel.core.annotations.DictFormat;
import com.effyic.aiptower.framework.excel.core.convert.DictConvert;
import com.effyic.aiptower.module.system.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 租户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TenantRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "创建编号（租户ID展示）", example = "A001")
    @ExcelProperty("租户ID")
    private String code;

    @Schema(description = "医院名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "协和医院")
    @ExcelProperty("医院名称")
    private String name;

    @Schema(description = "医院等级", example = "三甲")
    @ExcelProperty("医院等级")
    private String hospitalLevel;

    @Schema(description = "服务地址", example = "https://hospital.example.com")
    @ExcelProperty("服务地址")
    private String serviceUrl;

    @Schema(description = "联系人", example = "管理员")
    @ExcelProperty("联系人")
    private String contactName;

    @Schema(description = "联系手机", example = "15601691300")
    @ExcelProperty("联系手机")
    private String contactMobile;

    @Schema(description = "租户启用状态（表字段，0正常 1停用）", example = "0")
    @ExcelProperty(value = "启用状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "使用状态（按有效期计算：0使用中 1已过期）", example = "0")
    @ExcelProperty("使用状态")
    private Integer usageStatus;

    @Schema(description = "绑定域名数组")
    private List<String> websites;

    @Schema(description = "租户套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long packageId;

    @Schema(description = "套餐版本名称", example = "标准版")
    @ExcelProperty("套餐版本")
    private String packageName;

    @Schema(description = "有效期至", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("有效期至")
    private LocalDateTime expireTime;

    @Schema(description = "距有效期剩余天数（已过期为负数）", example = "5")
    private Long remainDays;

    @Schema(description = "账号数量", example = "100")
    private Integer accountCount;

    @Schema(description = "创建人", example = "1")
    private String creator;

    @Schema(description = "创建人昵称", example = "超级管理员")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人", example = "1")
    private String updater;

    @Schema(description = "更新人昵称", example = "超级管理员")
    private String updaterName;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
