package com.effyic.aiptower.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizmenu.BizMenuRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenPackageConfigRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenTokenReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.bizopen.BizOpenTokenRespVO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.effyic.aiptower.module.system.dal.redis.biz.BizOpenAccessTokenRedisDAO;
import com.effyic.aiptower.module.system.enums.permission.MenuTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.effyic.aiptower.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.filterList;
import static com.effyic.aiptower.module.system.dal.dataobject.tenant.BizMenuDO.ID_ROOT;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.BIZ_OPEN_TOKEN_INVALID;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.TENANT_PACKAGE_NOT_EXISTS;

@Service
@Validated
public class BizOpenServiceImpl implements BizOpenService {

    /** 访问令牌有效期：2 小时 */
    private static final long ACCESS_TOKEN_EXPIRES_SECONDS = 7200L;

    @Resource
    private TenantService tenantService;
    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    private BizMenuService bizMenuService;
    @Resource
    private BizOpenAccessTokenRedisDAO bizOpenAccessTokenRedisDAO;

    @Override
    public BizOpenTokenRespVO createAccessToken(BizOpenTokenReqVO reqVO) {
        TenantDO tenant = tenantService.authenticateClient(reqVO.getClientId(), reqVO.getClientSecret());
        String accessToken = IdUtil.fastSimpleUUID();
        bizOpenAccessTokenRedisDAO.set(accessToken, tenant.getId(), ACCESS_TOKEN_EXPIRES_SECONDS);
        return new BizOpenTokenRespVO(accessToken, ACCESS_TOKEN_EXPIRES_SECONDS, tenant.getId(), tenant.getCode());
    }

    @Override
    public BizOpenPackageConfigRespVO getPackageConfig(String accessToken) {
        Long tenantId = resolveTenantId(accessToken);
        TenantDO tenant = tenantService.getTenant(tenantId);
        if (tenant == null) {
            throw exception(BIZ_OPEN_TOKEN_INVALID);
        }
        tenantService.validTenant(tenantId);

        TenantPackageDO pkg = tenantPackageService.getTenantPackage(tenant.getPackageId());
        if (pkg == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }

        List<BizMenuDO> menus = bizMenuService.getBizMenuList(pkg.getMenuIds());
        // 仅返回启用的菜单
        menus = menus.stream()
                .filter(m -> CommonStatusEnum.ENABLE.getStatus().equals(m.getStatus()))
                .collect(Collectors.toList());

        Set<String> permissions = menus.stream()
                .map(BizMenuDO::getPermission)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        BizOpenPackageConfigRespVO respVO = new BizOpenPackageConfigRespVO();
        respVO.setTenantId(tenant.getId());
        respVO.setTenantCode(tenant.getCode());
        respVO.setTenantName(tenant.getName());
        respVO.setServiceUrl(tenant.getServiceUrl());
        respVO.setExpireTime(tenant.getExpireTime());
        respVO.setPackageId(pkg.getId());
        respVO.setPackageName(pkg.getName());
        respVO.setTriageAgentLimit(pkg.getTriageAgentLimit());
        respVO.setInquiryAgentLimit(pkg.getInquiryAgentLimit());
        respVO.setAdvancedConfigEnabled(pkg.getAdvancedConfigEnabled());
        respVO.setCustomCaseEnabled(pkg.getCustomCaseEnabled());
        respVO.setPermissions(permissions);
        respVO.setMenuTree(buildMenuTree(menus));
        respVO.setAdmins(tenantService.getTenantAdminList(tenantId));
        respVO.setConfigVersion(resolveConfigVersion(tenant, pkg));
        return respVO;
    }

    private Long resolveTenantId(String accessToken) {
        if (StrUtil.isBlank(accessToken)) {
            throw exception(BIZ_OPEN_TOKEN_INVALID);
        }
        // 兼容 Bearer xxx
        if (StrUtil.startWithIgnoreCase(accessToken, "Bearer ")) {
            accessToken = accessToken.substring(7).trim();
        }
        Long tenantId = bizOpenAccessTokenRedisDAO.get(accessToken);
        if (tenantId == null) {
            throw exception(BIZ_OPEN_TOKEN_INVALID);
        }
        return tenantId;
    }

    private static Long resolveConfigVersion(TenantDO tenant, TenantPackageDO pkg) {
        long tenantTs = tenant.getUpdateTime() != null
                ? tenant.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
        long pkgTs = pkg.getUpdateTime() != null
                ? pkg.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
        return Math.max(tenantTs, pkgTs);
    }

    private List<BizMenuRespVO> buildMenuTree(List<BizMenuDO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        List<BizMenuDO> copy = new ArrayList<>(menuList);
        copy.removeIf(menu -> MenuTypeEnum.BUTTON.getType().equals(menu.getType()));
        copy.sort(Comparator.comparing(BizMenuDO::getSort));

        Map<Long, BizMenuRespVO> treeNodeMap = new LinkedHashMap<>();
        copy.forEach(menu -> treeNodeMap.put(menu.getId(), BeanUtils.toBean(menu, BizMenuRespVO.class)));
        treeNodeMap.values().stream()
                .filter(node -> ObjUtil.notEqual(node.getParentId(), ID_ROOT))
                .forEach(childNode -> {
                    BizMenuRespVO parentNode = treeNodeMap.get(childNode.getParentId());
                    if (parentNode == null) {
                        return;
                    }
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(childNode);
                });
        return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }

}
