package com.effyic.aiptower.module.pay.convert.wallet;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.pay.controller.admin.wallet.vo.wallet.PayWalletRespVO;
import com.effyic.aiptower.module.pay.controller.app.wallet.vo.wallet.AppPayWalletRespVO;
import com.effyic.aiptower.module.pay.dal.dataobject.wallet.PayWalletDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletConvert {

    PayWalletConvert INSTANCE = Mappers.getMapper(PayWalletConvert.class);

    AppPayWalletRespVO convert(PayWalletDO bean);

    PayWalletRespVO convert02(PayWalletDO bean);

    PageResult<PayWalletRespVO> convertPage(PageResult<PayWalletDO> page);

}
