package com.effyic.aiptower.module.crm.service.contract;

import com.effyic.aiptower.module.crm.controller.admin.contract.vo.config.CrmContractConfigSaveReqVO;
import com.effyic.aiptower.module.crm.dal.dataobject.contract.CrmContractConfigDO;
import jakarta.validation.Valid;

/**
 * 合同配置 Service 接口
 *
 * @author effyic
 */
public interface CrmContractConfigService {

    /**
     * 获得合同配置
     *
     * @return 合同配置
     */
    CrmContractConfigDO getContractConfig();

    /**
     * 保存合同配置
     *
     * @param saveReqVO 更新信息
     */
    void saveContractConfig(@Valid CrmContractConfigSaveReqVO saveReqVO);

}
