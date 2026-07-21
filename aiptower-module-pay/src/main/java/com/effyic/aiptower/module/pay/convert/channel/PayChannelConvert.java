package com.effyic.aiptower.module.pay.convert.channel;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.pay.controller.admin.channel.vo.PayChannelCreateReqVO;
import com.effyic.aiptower.module.pay.controller.admin.channel.vo.PayChannelRespVO;
import com.effyic.aiptower.module.pay.controller.admin.channel.vo.PayChannelUpdateReqVO;
import com.effyic.aiptower.module.pay.dal.dataobject.channel.PayChannelDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayChannelConvert {

    PayChannelConvert INSTANCE = Mappers.getMapper(PayChannelConvert.class);

    @Mapping(target = "config",ignore = true)
    PayChannelDO convert(PayChannelCreateReqVO bean);

    @Mapping(target = "config",ignore = true)
    PayChannelDO convert(PayChannelUpdateReqVO bean);

    @Mapping(target = "config",expression = "java(com.effyic.aiptower.framework.common.util.json.JsonUtils.toJsonString(bean.getConfig()))")
    PayChannelRespVO convert(PayChannelDO bean);

    PageResult<PayChannelRespVO> convertPage(PageResult<PayChannelDO> page);

}
