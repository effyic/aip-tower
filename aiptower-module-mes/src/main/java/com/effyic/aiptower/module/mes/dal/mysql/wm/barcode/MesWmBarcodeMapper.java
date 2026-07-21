package com.effyic.aiptower.module.mes.dal.mysql.wm.barcode;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodePageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.wm.barcode.MesWmBarcodeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 条码清单 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesWmBarcodeMapper extends BaseMapperX<MesWmBarcodeDO> {

    default PageResult<MesWmBarcodeDO> selectPage(MesWmBarcodePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmBarcodeDO>()
                .eqIfPresent(MesWmBarcodeDO::getConfigId, reqVO.getConfigId())
                .eqIfPresent(MesWmBarcodeDO::getFormat, reqVO.getFormat())
                .eqIfPresent(MesWmBarcodeDO::getBizType, reqVO.getBizType())
                .likeIfPresent(MesWmBarcodeDO::getContent, reqVO.getContent())
                .eqIfPresent(MesWmBarcodeDO::getBizId, reqVO.getBizId())
                .likeIfPresent(MesWmBarcodeDO::getBizCode, reqVO.getBizCode())
                .likeIfPresent(MesWmBarcodeDO::getBizName, reqVO.getBizName())
                .eqIfPresent(MesWmBarcodeDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesWmBarcodeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesWmBarcodeDO::getId));
    }

    default MesWmBarcodeDO selectByBizTypeAndBizId(Integer bizType, Long bizId) {
        return selectOne(new LambdaQueryWrapperX<MesWmBarcodeDO>()
                .eq(MesWmBarcodeDO::getBizType, bizType)
                .eq(MesWmBarcodeDO::getBizId, bizId));
    }

    default MesWmBarcodeDO selectByContent(String content) {
        return selectOne(MesWmBarcodeDO::getContent, content);
    }

    default Long selectCountByConfigId(Long configId) {
        return selectCount(MesWmBarcodeDO::getConfigId, configId);
    }

}
