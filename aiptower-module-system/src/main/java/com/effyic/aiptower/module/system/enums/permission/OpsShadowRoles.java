package com.effyic.aiptower.module.system.enums.permission;

/**
 * 运营用户影子角色约定
 */
public final class OpsShadowRoles {

    /**
     * 影子角色 code 前缀，完整形如 ops_shadow_{userId}
     */
    public static final String CODE_PREFIX = "ops_shadow_";

    /**
     * 「运营用户管理」页面菜单编号（与 sql/postgresql/ops-user.sql 中 id=6800 一致）
     */
    public static final Long OPS_USER_MENU_ID = 6800L;

    private OpsShadowRoles() {
    }

    public static String buildCode(Long userId) {
        return CODE_PREFIX + userId;
    }

    public static boolean isShadowRoleCode(String code) {
        return code != null && code.startsWith(CODE_PREFIX);
    }

}
