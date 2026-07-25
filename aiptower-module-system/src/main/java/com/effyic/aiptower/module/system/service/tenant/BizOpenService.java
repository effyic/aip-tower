package com.effyic.aiptower.module.system.service.tenant;

import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenPackageConfigRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenTokenReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenTokenRespVO;

/**
 * B 端开放接口 Service
 */
public interface BizOpenService {

    BizOpenTokenRespVO createAccessToken(BizOpenTokenReqVO reqVO);

    BizOpenPackageConfigRespVO getPackageConfig(String accessToken);

}
