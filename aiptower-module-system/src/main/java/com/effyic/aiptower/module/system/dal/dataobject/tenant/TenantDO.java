package com.effyic.aiptower.module.system.dal.dataobject.tenant;

import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.mybatis.core.dataobject.BaseDO;
import com.effyic.aiptower.framework.mybatis.core.type.StringListTypeHandler;
import com.effyic.aiptower.framework.tenant.core.aop.TenantIgnore;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户 DO
 *
 * @author effyic
 */
@TableName(value = "system_tenant", autoResultMap = true)
@KeySequence("system_tenant_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TenantIgnore
public class TenantDO extends BaseDO {

    /**
     * 套餐编号 - 系统
     */
    public static final Long PACKAGE_ID_SYSTEM = 0L;

    /**
     * 租户编号，自增
     */
    private Long id;
    /**
     * 创建编号，如 A001、A002
     */
    private String code;
    /**
     * 租户名（医院名称），唯一
     */
    private String name;
    /**
     * 医院等级，如三甲、三乙、社区
     */
    private String hospitalLevel;
    /**
     * 服务地址
     */
    private String serviceUrl;
    /**
     * 联系人的用户编号
     *
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long contactUserId;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系手机
     */
    private String contactMobile;
    /**
     * 租户状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 绑定域名列表
     *
     * 1. 考虑到对微信小程序的兼容，也允许传递 appid
     * 2. 为什么是数组，考虑到管理后台、会员前台都有独立的域名，又或者多个管理后台
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> websites;
    /**
     * 租户套餐编号
     *
     * 关联 {@link TenantPackageDO#getId()}
     * 特殊逻辑：系统内置租户，不使用套餐，暂时使用 {@link #PACKAGE_ID_SYSTEM} 标识
     */
    private Long packageId;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 账号数量
     */
    private Integer accountCount;
    /**
     * B 端对接 ClientId
     */
    private String clientId;
    /**
     * B 端对接 ClientSecret（BCrypt 哈希）
     */
    private String clientSecret;

}
