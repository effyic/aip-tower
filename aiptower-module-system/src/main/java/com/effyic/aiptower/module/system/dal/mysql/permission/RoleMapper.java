package com.effyic.aiptower.module.system.dal.mysql.permission;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.dataobject.BaseDO;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.RoleDO;
import com.effyic.aiptower.module.system.enums.permission.OpsShadowRoles;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapperX<RoleDO> {

    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, reqVO.getName())
                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime())
                .neIfPresent(RoleDO::getCode, reqVO.getExcludeCode())
                // 隐藏运营用户影子角色，避免出现在角色管理
                .notLikeRight(RoleDO::getCode, OpsShadowRoles.CODE_PREFIX)
                .orderByAsc(RoleDO::getSort));
    }

    default RoleDO selectByName(String name) {
        return selectOne(RoleDO::getName, name);
    }

    default RoleDO selectByCode(String code) {
        return selectOne(RoleDO::getCode, code);
    }

    default List<RoleDO> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(new LambdaQueryWrapperX<RoleDO>()
                .inIfPresent(RoleDO::getStatus, statuses)
                .notLikeRight(RoleDO::getCode, OpsShadowRoles.CODE_PREFIX));
    }

    default List<RoleDO> selectListByCodePrefix(String codePrefix) {
        return selectList(new LambdaQueryWrapperX<RoleDO>()
                .likeRight(RoleDO::getCode, codePrefix));
    }

}
