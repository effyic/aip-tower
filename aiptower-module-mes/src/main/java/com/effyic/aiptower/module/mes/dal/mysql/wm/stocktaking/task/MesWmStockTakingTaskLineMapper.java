package com.effyic.aiptower.module.mes.dal.mysql.wm.stocktaking.task;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.wm.stocktaking.task.vo.line.MesWmStockTakingTaskLinePageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.wm.stocktaking.task.MesWmStockTakingTaskLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 盘点任务行 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesWmStockTakingTaskLineMapper extends BaseMapperX<MesWmStockTakingTaskLineDO> {

    default List<MesWmStockTakingTaskLineDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<MesWmStockTakingTaskLineDO>()
                .eq(MesWmStockTakingTaskLineDO::getTaskId, taskId)
                .orderByAsc(MesWmStockTakingTaskLineDO::getId));
    }

    default void deleteByTaskId(Long taskId) {
        delete(MesWmStockTakingTaskLineDO::getTaskId, taskId);
    }

    default PageResult<MesWmStockTakingTaskLineDO> selectPage(MesWmStockTakingTaskLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmStockTakingTaskLineDO>()
                .eqIfPresent(MesWmStockTakingTaskLineDO::getTaskId, reqVO.getTaskId())
                .orderByAsc(MesWmStockTakingTaskLineDO::getId));
    }

    default MesWmStockTakingTaskLineDO selectByTaskIdAndItemIdAndAreaId(Long taskId, Long itemId, Long areaId) {
        return selectOne(new LambdaQueryWrapperX<MesWmStockTakingTaskLineDO>()
                .eq(MesWmStockTakingTaskLineDO::getTaskId, taskId)
                .eq(MesWmStockTakingTaskLineDO::getItemId, itemId)
                .eqIfPresent(MesWmStockTakingTaskLineDO::getAreaId, areaId));
    }

}
