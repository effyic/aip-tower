package com.effyic.aiptower.module.mes.dal.mysql.wm.returnissue;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.controller.admin.wm.returnissue.vo.line.MesWmReturnIssueLinePageReqVO;
import com.effyic.aiptower.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 生产退料单行 Mapper
 */
@Mapper
public interface MesWmReturnIssueLineMapper extends BaseMapperX<MesWmReturnIssueLineDO> {

    default PageResult<MesWmReturnIssueLineDO> selectPage(MesWmReturnIssueLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmReturnIssueLineDO>()
                .eqIfPresent(MesWmReturnIssueLineDO::getIssueId, reqVO.getIssueId())
                .orderByDesc(MesWmReturnIssueLineDO::getId));
    }

    default List<MesWmReturnIssueLineDO> selectListByIssueId(Long issueId) {
        return selectList(MesWmReturnIssueLineDO::getIssueId, issueId);
    }

    default void deleteByIssueId(Long issueId) {
        delete(MesWmReturnIssueLineDO::getIssueId, issueId);
    }

}
