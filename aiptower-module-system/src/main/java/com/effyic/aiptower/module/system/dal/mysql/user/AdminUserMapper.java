package com.effyic.aiptower.module.system.dal.mysql.user;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.system.controller.admin.user.vo.user.UserPageReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    /**
     * 按 id 查询用户（含已删除、忽略租户与数据权限），仅用于创建人/更新人展示名回填。
     * <p>
     * 自定义 SQL 不会拼接 {@code deleted=0}；运营用户与超管可能不在同一租户。
     */
    @InterceptorIgnore(tenantLine = "true", dataPermission = "true")
    @Select("""
            <script>
            SELECT * FROM system_users WHERE id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    List<AdminUserDO> selectByIdsForDisplay(@Param("ids") Collection<Long> ids);

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .inIfPresent(AdminUserDO::getId, userIds)
                .neIfPresent(AdminUserDO::getUsername, reqVO.getExcludeUsername())
                .orderByDesc(AdminUserDO::getId));
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(AdminUserDO::getNickname, nickname));
    }

    default List<AdminUserDO> selectListByStatus(Integer status) {
        return selectList(AdminUserDO::getStatus, status);
    }

    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserDO::getDeptId, deptIds);
    }

}
