package com.effyic.aiptower.module.infra.api.logger;

import com.effyic.aiptower.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import com.effyic.aiptower.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import com.effyic.aiptower.module.infra.service.logger.ApiErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * API 访问日志的 API 接口
 *
 * @author effyic
 */
@Service
@Validated
public class ApiErrorLogApiImpl implements ApiErrorLogCommonApi {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }

}
