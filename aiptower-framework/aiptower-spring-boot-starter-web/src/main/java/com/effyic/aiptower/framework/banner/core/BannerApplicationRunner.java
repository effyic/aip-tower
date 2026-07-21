package com.effyic.aiptower.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author effyic
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("\n----------------------------------------------------------\n\t" +
                            "AIP-Tower 启动成功！\n\t" +
                            "接口文档: \t{} \n" +
                            "----------------------------------------------------------",
                    "http://localhost:48090/swagger-ui");

            // 数据报表
            if (isNotPresent("com.effyic.aiptower.module.report.framework.security.config.SecurityConfiguration")) {
                System.out.println("[报表模块 aiptower-module-report - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // 工作流
            if (isNotPresent("com.effyic.aiptower.module.bpm.framework.flowable.config.BpmFlowableConfiguration")) {
                System.out.println("[工作流模块 aiptower-module-bpm - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // 商城系统
            if (isNotPresent("com.effyic.aiptower.module.trade.framework.web.config.TradeWebConfiguration")) {
                System.out.println("[商城系统 aiptower-module-mall - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // ERP 系统
            if (isNotPresent("com.effyic.aiptower.module.erp.framework.web.config.ErpWebConfiguration")) {
                System.out.println("[ERP 系统 aiptower-module-erp - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // WMS 仓库管理系统
            if (isNotPresent("com.effyic.aiptower.module.wms.framework.web.config.WmsWebConfiguration")) {
                System.out.println("[WMS 仓库管理系统 aiptower-module-wms - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // CRM 系统
            if (isNotPresent("com.effyic.aiptower.module.crm.framework.web.config.CrmWebConfiguration")) {
                System.out.println("[CRM 系统 aiptower-module-crm - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // MES 系统
            if (isNotPresent("com.effyic.aiptower.module.mes.framework.web.config.MesWebConfiguration")) {
                System.out.println("[MES 系统 aiptower-module-mes - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // 微信公众号
            if (isNotPresent("com.effyic.aiptower.module.mp.framework.mp.config.MpConfiguration")) {
                System.out.println("[微信公众号 aiptower-module-mp - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // 支付平台
            if (isNotPresent("com.effyic.aiptower.module.pay.framework.pay.config.PayConfiguration")) {
                System.out.println("[支付系统 aiptower-module-pay - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // AI 大模型
            if (isNotPresent("com.effyic.aiptower.module.ai.framework.web.config.AiWebConfiguration")) {
                System.out.println("[AI 大模型 aiptower-module-ai - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // IoT 物联网
            if (isNotPresent("com.effyic.aiptower.module.iot.framework.web.config.IotWebConfiguration")) {
                System.out.println("[IoT 物联网 aiptower-module-iot - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
            // IM 即时通讯
            if (isNotPresent("com.effyic.aiptower.module.im.framework.web.config.ImWebConfiguration")) {
                System.out.println("[IM 即时通讯 aiptower-module-im - 已禁用][请在 pom.xml 中引入模块后开启]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
