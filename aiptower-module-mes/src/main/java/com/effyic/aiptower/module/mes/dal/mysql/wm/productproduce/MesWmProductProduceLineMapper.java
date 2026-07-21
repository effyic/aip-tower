package com.effyic.aiptower.module.mes.dal.mysql.wm.productproduce;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.wm.productproduce.vo.MesWmProductProduceLinePageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 生产入库单行 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesWmProductProduceLineMapper extends BaseMapperX<MesWmProductProduceLineDO> {

    default PageResult<MesWmProductProduceLineDO> selectPage(MesWmProductProduceLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmProductProduceLineDO>()
                .eqIfPresent(MesWmProductProduceLineDO::getFeedbackId, reqVO.getFeedbackId())
                .orderByDesc(MesWmProductProduceLineDO::getId));
    }

    default List<MesWmProductProduceLineDO> selectListByProduceId(Long produceId) {
        return selectList(MesWmProductProduceLineDO::getProduceId, produceId);
    }

    default List<MesWmProductProduceLineDO> selectListByFeedbackId(Long feedbackId) {
        return selectList(MesWmProductProduceLineDO::getFeedbackId, feedbackId);
    }

}
