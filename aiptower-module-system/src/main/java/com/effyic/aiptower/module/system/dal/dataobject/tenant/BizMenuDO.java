package com.effyic.aiptower.module.system.dal.dataobject.tenant;

import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.mybatis.core.dataobject.BaseDO;
import com.effyic.aiptower.framework.tenant.core.aop.TenantIgnore;
import com.effyic.aiptower.module.system.enums.permission.MenuTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * B 端菜单 DO
 */
@TableName("system_biz_menu")
@KeySequence("system_biz_menu_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class BizMenuDO extends BaseDO {

    public static final Long ID_ROOT = 0L;

    @TableId
    private Long id;
    private String name;
    /**
     * 云端菜单编码（稳定业务码）
     */
    private String menuCode;
    private String permission;
    /**
     * 菜单类型，枚举 {@link MenuTypeEnum}
     */
    private Integer type;
    private Integer sort;
    private Long parentId;
    private String path;
    private String icon;
    private String component;
    private String componentName;
    /**
     * 状态，枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    private Boolean visible;
    private Boolean keepAlive;
    private Boolean alwaysShow;

}
