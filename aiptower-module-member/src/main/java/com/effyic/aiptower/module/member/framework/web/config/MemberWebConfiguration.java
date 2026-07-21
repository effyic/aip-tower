package com.effyic.aiptower.module.member.framework.web.config;

import com.effyic.aiptower.framework.swagger.config.AiptowerSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * member 模块的 web 组件的 Configuration
 *
 * @author effyic
 */
@Configuration(proxyBeanMethods = false)
public class MemberWebConfiguration {

    /**
     * member 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi memberGroupedOpenApi() {
        return AiptowerSwaggerAutoConfiguration.buildGroupedOpenApi("member");
    }

}
