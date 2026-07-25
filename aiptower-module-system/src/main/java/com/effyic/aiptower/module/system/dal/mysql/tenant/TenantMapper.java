package com.effyic.aiptower.module.system.dal.mysql.tenant;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.framework.mybatis.core.util.MyBatisUtils;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO;
import com.effyic.aiptower.module.system.enums.tenant.TenantUsageStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TenantMapper extends BaseMapperX<TenantDO> {

    default PageResult<TenantDO> selectPage(TenantPageReqVO reqVO) {
        LambdaQueryWrapperX<TenantDO> query = new LambdaQueryWrapperX<TenantDO>()
                .likeIfPresent(TenantDO::getName, reqVO.getName())
                .likeIfPresent(TenantDO::getContactName, reqVO.getContactName())
                .likeIfPresent(TenantDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(TenantDO::getPackageId, reqVO.getPackageId())
                .betweenIfPresent(TenantDO::getCreateTime, reqVO.getCreateTime());
        // 使用状态按有效期过滤：使用中=未到期，已过期=已到期
        LocalDateTime now = LocalDateTime.now();
        if (TenantUsageStatusEnum.isInUse(reqVO.getUsageStatus())) {
            query.ge(TenantDO::getExpireTime, now);
        } else if (TenantUsageStatusEnum.isExpired(reqVO.getUsageStatus())) {
            query.lt(TenantDO::getExpireTime, now);
        }
        return selectPage(reqVO, query.orderByDesc(TenantDO::getId));
    }

    default TenantDO selectByName(String name) {
        return selectOne(TenantDO::getName, name);
    }

    default TenantDO selectByCode(String code) {
        return selectOne(TenantDO::getCode, code);
    }

    default TenantDO selectByClientId(String clientId) {
        return selectOne(TenantDO::getClientId, clientId);
    }

    /**
     * 查询最大创建编号（形如 A001），用于生成下一个编号
     */
    default String selectMaxCode() {
        TenantDO tenant = selectOne(new LambdaQueryWrapperX<TenantDO>()
                .isNotNull(TenantDO::getCode)
                .likeRight(TenantDO::getCode, "A")
                .orderByDesc(TenantDO::getCode)
                .last("LIMIT 1"));
        return tenant != null ? tenant.getCode() : null;
    }

    default List<TenantDO> selectListByWebsite(String website) {
        return selectList(new LambdaQueryWrapperX<TenantDO>()
                .apply(MyBatisUtils.findInSet("websites"), website));
    }

    default Long selectCountByPackageId(Long packageId) {
        return selectCount(TenantDO::getPackageId, packageId);
    }

    default List<TenantDO> selectListByPackageId(Long packageId) {
        return selectList(TenantDO::getPackageId, packageId);
    }

    default List<TenantDO> selectListByStatus(Integer status) {
        return selectList(TenantDO::getStatus, status);
    }

}
