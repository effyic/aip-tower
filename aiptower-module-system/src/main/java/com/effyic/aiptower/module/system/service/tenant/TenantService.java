package com.effyic.aiptower.module.system.service.tenant;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantAdminAccountRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCreateRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCredentialRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO;
import com.effyic.aiptower.module.system.service.tenant.handler.TenantInfoHandler;
import com.effyic.aiptower.module.system.service.tenant.handler.TenantMenuHandler;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Set;

/**
 * 租户 Service 接口（B 端客户档案、对接凭证、管理员账号）
 *
 * @author effyic
 */
public interface TenantService {

    /**
     * 创建 B 端租户（生成机构编号、对接凭证、默认管理员账号）
     */
    TenantCreateRespVO createTenant(@Valid TenantSaveReqVO createReqVO);

    /**
     * 重置租户对接凭证（clientId/clientSecret）
     */
    TenantCredentialRespVO resetTenantCredential(Long tenantId);

    /**
     * 获得租户对接凭证（不含明文 secret）
     */
    TenantCredentialRespVO getTenantCredential(Long tenantId);

    /**
     * 校验 B 端对接凭证
     */
    TenantDO authenticateClient(String clientId, String clientSecret);

    /**
     * 为指定租户生成新的 B 端管理员账号
     */
    TenantAdminAccountRespVO generateTenantAdmin(Long tenantId);

    /**
     * 获得指定租户下的 B 端管理员账号列表（含明文密码）
     */
    List<TenantAdminAccountRespVO> getTenantAdminList(Long tenantId);

    /**
     * 更新租户
     */
    void updateTenant(@Valid TenantSaveReqVO updateReqVO);

    /**
     * 更新租户的角色菜单（本平台多租户关闭后为空操作）
     */
    void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds);

    void deleteTenant(Long id);

    void deleteTenantList(List<Long> ids);

    TenantDO getTenant(Long id);

    PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO);

    TenantDO getTenantByName(String name);

    TenantDO getTenantByWebsite(String website);

    Long getTenantCountByPackageId(Long packageId);

    List<TenantDO> getTenantListByPackageId(Long packageId);

    List<TenantDO> getTenantListByStatus(Integer status);

    void handleTenantInfo(TenantInfoHandler handler);

    void handleTenantMenu(TenantMenuHandler handler);

    List<Long> getTenantIdList();

    void validTenant(Long id);

}
