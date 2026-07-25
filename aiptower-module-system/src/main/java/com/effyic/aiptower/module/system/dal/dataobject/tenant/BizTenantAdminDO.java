package com.effyic.aiptower.module.system.dal.dataobject.tenant;

import com.effyic.aiptower.framework.mybatis.core.dataobject.BaseDO;
import com.effyic.aiptower.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * B 端租户管理员账号 DO（明文账号密码，供 B 端使用）
 */
@TableName("system_biz_tenant_admin")
@KeySequence("system_biz_tenant_admin_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class BizTenantAdminDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 登录账号，如 admin-xhyyA001
     */
    private String username;
    /**
     * 初始密码（明文）
     */
    private String password;

}
