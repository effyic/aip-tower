package com.effyic.aiptower.module.mes.dal.mysql.wm.returnsales;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.wm.returnsales.vo.detail.MesWmReturnSalesDetailPageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.wm.returnsales.MesWmReturnSalesDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 销售退货明细 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesWmReturnSalesDetailMapper extends BaseMapperX<MesWmReturnSalesDetailDO> {

    default PageResult<MesWmReturnSalesDetailDO> selectPage(MesWmReturnSalesDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmReturnSalesDetailDO>()
                .eqIfPresent(MesWmReturnSalesDetailDO::getReturnId, reqVO.getReturnId())
                .eqIfPresent(MesWmReturnSalesDetailDO::getLineId, reqVO.getLineId())
                .eqIfPresent(MesWmReturnSalesDetailDO::getItemId, reqVO.getItemId())
                .orderByDesc(MesWmReturnSalesDetailDO::getId));
    }

    default List<MesWmReturnSalesDetailDO> selectListByReturnId(Long returnId) {
        return selectList(new LambdaQueryWrapperX<MesWmReturnSalesDetailDO>()
                .eq(MesWmReturnSalesDetailDO::getReturnId, returnId));
    }

    default List<MesWmReturnSalesDetailDO> selectListByLineId(Long lineId) {
        return selectList(new LambdaQueryWrapperX<MesWmReturnSalesDetailDO>()
                .eq(MesWmReturnSalesDetailDO::getLineId, lineId));
    }

    default void deleteByReturnId(Long returnId) {
        delete(new LambdaQueryWrapperX<MesWmReturnSalesDetailDO>()
                .eq(MesWmReturnSalesDetailDO::getReturnId, returnId));
    }

    default void deleteByLineId(Long lineId) {
        delete(new LambdaQueryWrapperX<MesWmReturnSalesDetailDO>()
                .eq(MesWmReturnSalesDetailDO::getLineId, lineId));
    }

}
