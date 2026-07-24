package com.effyic.aiptower.module.system.enums.tenant;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户使用状态（按有效期判断，非表字段）
 */
@Getter
@AllArgsConstructor
public enum TenantUsageStatusEnum {

    IN_USE(0, "使用中"),
    EXPIRED(1, "已过期");

    private final Integer status;
    private final String name;

    public static boolean isInUse(Integer status) {
        return ObjUtil.equal(IN_USE.status, status);
    }

    public static boolean isExpired(Integer status) {
        return ObjUtil.equal(EXPIRED.status, status);
    }

}
