package com.effyic.aiptower.module.mes.dal.mysql.pro.feedback;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.pro.feedback.vo.MesProFeedbackPageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.pro.feedback.MesProFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 生产报工 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesProFeedbackMapper extends BaseMapperX<MesProFeedbackDO> {

    default PageResult<MesProFeedbackDO> selectPage(MesProFeedbackPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProFeedbackDO>()
                .likeIfPresent(MesProFeedbackDO::getCode, reqVO.getCode())
                .eqIfPresent(MesProFeedbackDO::getType, reqVO.getType())
                .eqIfPresent(MesProFeedbackDO::getWorkOrderId, reqVO.getWorkOrderId())
                .eqIfPresent(MesProFeedbackDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesProFeedbackDO::getFeedbackTime, reqVO.getFeedbackTime())
                .eqIfPresent(MesProFeedbackDO::getItemId, reqVO.getItemId())
                .eqIfPresent(MesProFeedbackDO::getFeedbackUserId, reqVO.getFeedbackUserId())
                .eqIfPresent(MesProFeedbackDO::getCreator, reqVO.getCreator())
                .orderByDesc(MesProFeedbackDO::getId));
    }

}
