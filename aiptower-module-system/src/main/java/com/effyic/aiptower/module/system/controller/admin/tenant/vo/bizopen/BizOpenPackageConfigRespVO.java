package com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen;

import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantAdminAccountRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Schema(description = "B端开放接口 - 套餐配置 Response VO")
@Data
public class BizOpenPackageConfigRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long tenantId;

    @Schema(description = "机构编号", example = "A001")
    private String tenantCode;

    @Schema(description = "租户名称", example = "协和医院")
    private String tenantName;

    @Schema(description = "服务地址", example = "https://hospital.example.com")
    private String serviceUrl;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "套餐编号", example = "1")
    private Long packageId;

    @Schema(description = "套餐名称", example = "标准版")
    private String packageName;

    @Schema(description = "分诊Agent上限", example = "10")
    private Integer triageAgentLimit;

    @Schema(description = "问诊Agent上限", example = "10")
    private Integer inquiryAgentLimit;

    @Schema(description = "高级配置", example = "false")
    private Boolean advancedConfigEnabled;

    @Schema(description = "自定义病例", example = "false")
    private Boolean customCaseEnabled;

    @Schema(description = "权限标识列表")
    private Set<String> permissions;

    @Schema(description = "菜单树（不含按钮）")
    private List<BizMenuRespVO> menuTree;

    @Schema(description = "B端管理员账号列表（明文密码）")
    private List<TenantAdminAccountRespVO> admins;

    @Schema(description = "配置版本（时间戳毫秒）", example = "1721808000000")
    private Long configVersion;

}
