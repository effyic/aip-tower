package com.effyic.aiptower.module.system.dal.mysql.tenant;

import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizTenantAdminDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizTenantAdminMapper extends BaseMapperX<BizTenantAdminDO> {

    default List<BizTenantAdminDO> selectListByTenantId(Long tenantId) {
        return selectList(new LambdaQueryWrapperX<BizTenantAdminDO>()
                .eq(BizTenantAdminDO::getTenantId, tenantId)
                .orderByAsc(BizTenantAdminDO::getCreateTime)
                .orderByAsc(BizTenantAdminDO::getId));
    }

    default List<BizTenantAdminDO> selectListByTenantIdAndUsernamePrefix(Long tenantId, String usernamePrefix) {
        return selectList(new LambdaQueryWrapperX<BizTenantAdminDO>()
                .eq(BizTenantAdminDO::getTenantId, tenantId)
                .likeRight(BizTenantAdminDO::getUsername, usernamePrefix));
    }

}
