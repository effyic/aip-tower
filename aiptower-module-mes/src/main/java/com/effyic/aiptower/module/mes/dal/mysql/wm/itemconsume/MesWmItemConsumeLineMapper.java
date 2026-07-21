package com.effyic.aiptower.module.mes.dal.mysql.wm.itemconsume;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.wm.itemconsume.vo.MesWmItemConsumeLinePageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 物料消耗记录行 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesWmItemConsumeLineMapper extends BaseMapperX<MesWmItemConsumeLineDO> {

    default PageResult<MesWmItemConsumeLineDO> selectPage(MesWmItemConsumeLinePageReqVO reqVO,
                                                          Long consumeId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmItemConsumeLineDO>()
                .eq(MesWmItemConsumeLineDO::getConsumeId, consumeId)
                .orderByDesc(MesWmItemConsumeLineDO::getId));
    }

    default List<MesWmItemConsumeLineDO> selectListByConsumeId(Long consumeId) {
        return selectList(MesWmItemConsumeLineDO::getConsumeId, consumeId);
    }

}
