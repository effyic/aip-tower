package com.effyic.aiptower.module.mes.dal.mysql.pro.card;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.pro.card.vo.process.MesProCardProcessPageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.pro.card.MesProCardProcessDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 流转卡工序记录 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesProCardProcessMapper extends BaseMapperX<MesProCardProcessDO> {

    default PageResult<MesProCardProcessDO> selectPage(MesProCardProcessPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProCardProcessDO>()
                .eqIfPresent(MesProCardProcessDO::getCardId, reqVO.getCardId())
                .eqIfPresent(MesProCardProcessDO::getProcessId, reqVO.getProcessId())
                .eqIfPresent(MesProCardProcessDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesProCardProcessDO::getUserId, reqVO.getUserId())
                .orderByAsc(MesProCardProcessDO::getSort));
    }

    default List<MesProCardProcessDO> selectListByCardId(Long cardId) {
        return selectList(MesProCardProcessDO::getCardId, cardId);
    }

    default void deleteByCardId(Long cardId) {
        delete(MesProCardProcessDO::getCardId, cardId);
    }

}
