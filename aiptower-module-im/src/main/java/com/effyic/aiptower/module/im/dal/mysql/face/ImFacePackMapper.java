package com.effyic.aiptower.module.im.dal.mysql.face;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.im.controller.admin.manager.face.vo.pack.ImFacePackPageReqVO;
import com.effyic.aiptower.module.im.dal.dataobject.face.ImFacePackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 表情包 Mapper
 *
 * @author effyic
 */
@Mapper
public interface ImFacePackMapper extends BaseMapperX<ImFacePackDO> {

    default List<ImFacePackDO> selectListByStatusOrderBySort(Integer status) {
        return selectList(new LambdaQueryWrapperX<ImFacePackDO>()
                .eq(ImFacePackDO::getStatus, status)
                .orderByAsc(ImFacePackDO::getSort)
                .orderByAsc(ImFacePackDO::getId));
    }

    default PageResult<ImFacePackDO> selectPage(ImFacePackPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImFacePackDO>()
                .likeIfPresent(ImFacePackDO::getName, reqVO.getName())
                .eqIfPresent(ImFacePackDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImFacePackDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(ImFacePackDO::getSort)
                .orderByDesc(ImFacePackDO::getId));
    }

}
