package com.effyic.aiptower.module.system.dal.mysql.tenant;

import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuListReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BizMenuMapper extends BaseMapperX<BizMenuDO> {

    default BizMenuDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(BizMenuDO::getParentId, parentId, BizMenuDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(BizMenuDO::getParentId, parentId);
    }

    default List<BizMenuDO> selectList(BizMenuListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<BizMenuDO>()
                .likeIfPresent(BizMenuDO::getName, reqVO.getName())
                .eqIfPresent(BizMenuDO::getStatus, reqVO.getStatus())
                .orderByAsc(BizMenuDO::getSort));
    }

    default List<BizMenuDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

}
