package com.effyic.aiptower.module.promotion.convert.seckill;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.promotion.controller.admin.seckill.vo.config.SeckillConfigCreateReqVO;
import com.effyic.aiptower.module.promotion.controller.admin.seckill.vo.config.SeckillConfigRespVO;
import com.effyic.aiptower.module.promotion.controller.admin.seckill.vo.config.SeckillConfigSimpleRespVO;
import com.effyic.aiptower.module.promotion.controller.admin.seckill.vo.config.SeckillConfigUpdateReqVO;
import com.effyic.aiptower.module.promotion.controller.app.seckill.vo.config.AppSeckillConfigRespVO;
import com.effyic.aiptower.module.promotion.dal.dataobject.seckill.SeckillConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 秒杀时段 Convert
 *
 * @author effyic
 */
@Mapper
public interface SeckillConfigConvert {

    SeckillConfigConvert INSTANCE = Mappers.getMapper(SeckillConfigConvert.class);

    SeckillConfigDO convert(SeckillConfigCreateReqVO bean);

    SeckillConfigDO convert(SeckillConfigUpdateReqVO bean);

    SeckillConfigRespVO convert(SeckillConfigDO bean);

    List<SeckillConfigRespVO> convertList(List<SeckillConfigDO> list);

    List<SeckillConfigSimpleRespVO> convertList1(List<SeckillConfigDO> list);

    PageResult<SeckillConfigRespVO> convertPage(PageResult<SeckillConfigDO> page);

    List<AppSeckillConfigRespVO> convertList2(List<SeckillConfigDO> list);

    AppSeckillConfigRespVO convert1(SeckillConfigDO filteredConfig);
}
