package com.effyic.aiptower.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档地址
 *
 * @author effyic
 */
@Getter
@AllArgsConstructor
public enum DocumentEnum {

    REDIS_INSTALL("https://redis.io/docs/latest/operate/oss_and_stack/install/", "Redis 安装文档"),
    TENANT("https://effyic.com", "SaaS 多租户文档");

    private final String url;
    private final String memo;

}
