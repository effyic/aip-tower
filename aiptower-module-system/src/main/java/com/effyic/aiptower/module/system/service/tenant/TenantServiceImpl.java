package com.effyic.aiptower.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.common.util.collection.CollectionUtils;
import com.effyic.aiptower.framework.common.util.date.DateUtils;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.framework.datapermission.core.annotation.DataPermission;
import com.effyic.aiptower.framework.tenant.config.TenantProperties;
import com.effyic.aiptower.framework.tenant.core.context.TenantContextHolder;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantAdminAccountRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCreateRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCredentialRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.MenuDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.BizTenantAdminDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.effyic.aiptower.module.system.dal.mysql.tenant.BizTenantAdminMapper;
import com.effyic.aiptower.module.system.dal.mysql.tenant.TenantMapper;
import com.effyic.aiptower.module.system.service.permission.MenuService;
import com.effyic.aiptower.module.system.service.tenant.handler.TenantInfoHandler;
import com.effyic.aiptower.module.system.service.tenant.handler.TenantMenuHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.effyic.aiptower.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.*;

/**
 * 租户 Service 实现类（B 端客户档案与对接凭证）
 *
 * @author effyic
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    private static final int DEFAULT_ACCOUNT_COUNT = 100;
    private static final int CLIENT_SECRET_LENGTH = 32;
    private static final int ADMIN_PASSWORD_LENGTH = 10;
    private static final String RANDOM_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Pattern TENANT_CODE_PATTERN = Pattern.compile("^A(\\d{3})$");
    /** 租户内管理员账号序号：admin-{首字母}A001 */
    private static final Pattern ADMIN_USERNAME_SEQ_PATTERN = Pattern.compile("^admin-[a-zA-Z0-9]+A(\\d+)$");

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // 由于 aiptower.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private BizTenantAdminMapper bizTenantAdminMapper;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    private MenuService menuService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Override
    public List<Long> getTenantIdList() {
        List<TenantDO> tenants = tenantMapper.selectList();
        return CollectionUtils.convertList(tenants, TenantDO::getId);
    }

    @Override
    public void validTenant(Long id) {
        TenantDO tenant = getTenant(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (tenant.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_DISABLE, tenant.getName());
        }
        if (DateUtils.isExpired(tenant.getExpireTime())) {
            throw exception(TENANT_EXPIRE, tenant.getName());
        }
    }

    @Override
    @DSTransactional
    @DataPermission(enable = false)
    public TenantCreateRespVO createTenant(TenantSaveReqVO createReqVO) {
        validTenantNameDuplicate(createReqVO.getName(), null);
        validTenantWebsiteDuplicate(createReqVO.getWebsites(), null);
        tenantPackageService.validTenantPackage(createReqVO.getPackageId());

        TenantDO tenant = BeanUtils.toBean(createReqVO, TenantDO.class);
        if (StrUtil.isBlank(tenant.getContactName())) {
            tenant.setContactName("管理员");
        }
        if (tenant.getStatus() == null) {
            tenant.setStatus(CommonStatusEnum.ENABLE.getStatus());
        }
        if (tenant.getAccountCount() == null) {
            tenant.setAccountCount(DEFAULT_ACCOUNT_COUNT);
        }
        tenant.setCode(generateNextTenantCode());

        String clientId = generateClientId(tenant.getCode());
        String rawSecret = generateClientSecret();
        tenant.setClientId(clientId);
        tenant.setClientSecret(passwordEncoder.encode(rawSecret));
        tenantMapper.insert(tenant);

        // 默认创建第一个 B 端管理员：创建时间与租户创建时间一致
        TenantDO saved = tenantMapper.selectById(tenant.getId());
        BizTenantAdminDO admin = createBizTenantAdmin(saved, saved.getCreateTime());

        return new TenantCreateRespVO(tenant.getId(), tenant.getCode(), clientId, rawSecret,
                admin.getUsername(), admin.getPassword(), admin.getCreateTime());
    }

    @Override
    @DSTransactional
    @DataPermission(enable = false)
    public TenantCredentialRespVO resetTenantCredential(Long tenantId) {
        TenantDO tenant = validateUpdateTenant(tenantId);
        String clientId = StrUtil.blankToDefault(tenant.getClientId(), generateClientId(tenant.getCode()));
        String rawSecret = generateClientSecret();
        tenantMapper.updateById(new TenantDO().setId(tenantId)
                .setClientId(clientId)
                .setClientSecret(passwordEncoder.encode(rawSecret)));
        return new TenantCredentialRespVO(tenantId, clientId, rawSecret);
    }

    @Override
    @DataPermission(enable = false)
    public TenantCredentialRespVO getTenantCredential(Long tenantId) {
        TenantDO tenant = getTenant(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (StrUtil.isBlank(tenant.getClientId())) {
            throw exception(TENANT_CLIENT_NOT_CONFIGURED);
        }
        return new TenantCredentialRespVO(tenantId, tenant.getClientId(), null);
    }

    @Override
    public TenantDO authenticateClient(String clientId, String clientSecret) {
        if (StrUtil.isBlank(clientId) || StrUtil.isBlank(clientSecret)) {
            throw exception(TENANT_CLIENT_BAD_CREDENTIALS);
        }
        TenantDO tenant = tenantMapper.selectByClientId(clientId);
        if (tenant == null || StrUtil.isBlank(tenant.getClientSecret())
                || !passwordEncoder.matches(clientSecret, tenant.getClientSecret())) {
            throw exception(TENANT_CLIENT_BAD_CREDENTIALS);
        }
        validTenant(tenant.getId());
        return tenant;
    }

    @Override
    @DSTransactional
    @DataPermission(enable = false)
    public TenantAdminAccountRespVO generateTenantAdmin(Long tenantId) {
        TenantDO tenant = validateUpdateTenant(tenantId);
        BizTenantAdminDO admin = createBizTenantAdmin(tenant, LocalDateTime.now());
        return new TenantAdminAccountRespVO(admin.getId(), admin.getUsername(),
                admin.getPassword(), admin.getCreateTime());
    }

    @Override
    @DataPermission(enable = false)
    public List<TenantAdminAccountRespVO> getTenantAdminList(Long tenantId) {
        TenantDO tenant = getTenant(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        List<BizTenantAdminDO> admins = bizTenantAdminMapper.selectListByTenantId(tenantId);
        List<TenantAdminAccountRespVO> list = new ArrayList<>(admins.size());
        for (BizTenantAdminDO admin : admins) {
            list.add(new TenantAdminAccountRespVO(admin.getId(), admin.getUsername(),
                    admin.getPassword(), admin.getCreateTime()));
        }
        return list;
    }

    /**
     * 创建 B 端管理员账号（写入 system_biz_tenant_admin）
     */
    private BizTenantAdminDO createBizTenantAdmin(TenantDO tenant, LocalDateTime createTime) {
        String username = buildNextAdminUsername(tenant.getId(), tenant.getName());
        String password = generateAdminPassword();
        BizTenantAdminDO admin = BizTenantAdminDO.builder()
                .tenantId(tenant.getId())
                .username(username)
                .password(password)
                .build();
        bizTenantAdminMapper.insert(admin);
        if (createTime != null) {
            bizTenantAdminMapper.update(null, new LambdaUpdateWrapper<BizTenantAdminDO>()
                    .set(BizTenantAdminDO::getCreateTime, createTime)
                    .eq(BizTenantAdminDO::getId, admin.getId()));
            admin.setCreateTime(createTime);
        }
        return admin;
    }

    /**
     * 管理员账号：admin-{医院名称拼音首字母}A{序号}
     * 序号按当前租户隔离递增：A001、A002...
     */
    private String buildNextAdminUsername(Long tenantId, String hospitalName) {
        String initials = getHospitalNameInitials(hospitalName);
        String prefix = "admin-" + initials + "A";
        List<BizTenantAdminDO> users = bizTenantAdminMapper.selectListByTenantIdAndUsernamePrefix(tenantId, prefix);
        int next = 1;
        for (BizTenantAdminDO user : users) {
            Matcher matcher = ADMIN_USERNAME_SEQ_PATTERN.matcher(user.getUsername());
            if (matcher.matches() && user.getUsername().startsWith(prefix)) {
                next = Math.max(next, Integer.parseInt(matcher.group(1)) + 1);
            }
        }
        if (next > 999) {
            throw exception(TENANT_CODE_EXCEED);
        }
        return StrUtil.maxLength(prefix + String.format("%03d", next), 30);
    }

    private static String getHospitalNameInitials(String hospitalName) {
        if (StrUtil.isBlank(hospitalName)) {
            return "hosp";
        }
        String firstLetter = PinyinUtil.getFirstLetter(hospitalName, "");
        if (StrUtil.isBlank(firstLetter)) {
            return "hosp";
        }
        String initials = firstLetter.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return StrUtil.blankToDefault(StrUtil.maxLength(initials, 16), "hosp");
    }

    private String generateClientId(String code) {
        return "biz_" + StrUtil.blankToDefault(code, "t").toLowerCase() + "_" + IdUtil.fastSimpleUUID().substring(0, 8);
    }

    private static String generateClientSecret() {
        return RandomUtil.randomString(RANDOM_CHARS, CLIENT_SECRET_LENGTH);
    }

    private static String generateAdminPassword() {
        return RandomUtil.randomString(RANDOM_CHARS, ADMIN_PASSWORD_LENGTH);
    }
    /**
     * 生成下一个创建编号：A001、A002...
     */
    private String generateNextTenantCode() {
        String maxCode = tenantMapper.selectMaxCode();
        int next = 1;
        if (StrUtil.isNotBlank(maxCode)) {
            Matcher matcher = TENANT_CODE_PATTERN.matcher(maxCode);
            if (matcher.matches()) {
                next = Integer.parseInt(matcher.group(1)) + 1;
            }
        }
        if (next > 999) {
            throw exception(TENANT_CODE_EXCEED);
        }
        return String.format("A%03d", next);
    }

    @Override
    @DSTransactional
    public void updateTenant(TenantSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "租户编号不能为空");
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        validTenantWebsiteDuplicate(updateReqVO.getWebsites(), updateReqVO.getId());
        tenantPackageService.validTenantPackage(updateReqVO.getPackageId());

        TenantDO updateObj = new TenantDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setName(updateReqVO.getName());
        updateObj.setHospitalLevel(updateReqVO.getHospitalLevel());
        updateObj.setServiceUrl(updateReqVO.getServiceUrl());
        updateObj.setPackageId(updateReqVO.getPackageId());
        updateObj.setExpireTime(updateReqVO.getExpireTime());
        if (updateReqVO.getContactName() != null) {
            updateObj.setContactName(updateReqVO.getContactName());
        }
        if (updateReqVO.getContactMobile() != null) {
            updateObj.setContactMobile(updateReqVO.getContactMobile());
        }
        if (updateReqVO.getStatus() != null) {
            updateObj.setStatus(updateReqVO.getStatus());
        }
        if (updateReqVO.getAccountCount() != null) {
            updateObj.setAccountCount(updateReqVO.getAccountCount());
        }
        if (updateReqVO.getWebsites() != null) {
            updateObj.setWebsites(updateReqVO.getWebsites());
        }
        tenantMapper.updateById(updateObj);
        // 套餐变更不再同步本库 role_menu，B 端拉取开放接口即可
        if (ObjectUtil.notEqual(tenant.getPackageId(), updateReqVO.getPackageId())) {
            log.info("[updateTenant][租户({}) 套餐从 {} 变更为 {}，由 B 端拉取最新配置]",
                    tenant.getId(), tenant.getPackageId(), updateReqVO.getPackageId());
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {
        TenantDO tenant = tenantMapper.selectByName(name);
        if (tenant == null) {
            return;
        }
        if (id == null) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
    }

    private void validTenantWebsiteDuplicate(List<String> websites, Long excludeId) {
        if (CollUtil.isEmpty(websites)) {
            return;
        }
        websites.forEach(website -> {
            List<TenantDO> tenants = tenantMapper.selectListByWebsite(website);
            if (excludeId != null) {
                tenants.removeIf(t -> t.getId().equals(excludeId));
            }
            if (CollUtil.isNotEmpty(tenants)) {
                throw exception(TENANT_WEBSITE_DUPLICATE, website);
            }
        });
    }

    @Override
    @DSTransactional
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        // 本平台已关闭多租户运行时；保留方法以兼容旧调用，不再修改本库角色菜单
        log.info("[updateTenantRoleMenu][跳过本库同步 tenantId={} menuIds={}]", tenantId, menuIds);
    }

    @Override
    public void deleteTenant(Long id) {
        validateUpdateTenant(id);
        tenantMapper.deleteById(id);
    }

    @Override
    public void deleteTenantList(List<Long> ids) {
        ids.forEach(this::validateUpdateTenant);
        tenantMapper.deleteByIds(ids);
    }

    private TenantDO validateUpdateTenant(Long id) {
        TenantDO tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (isSystemTenant(tenant)) {
            throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
        }
        return tenant;
    }

    @Override
    public TenantDO getTenant(Long id) {
        return tenantMapper.selectById(id);
    }

    @Override
    public PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO) {
        return tenantMapper.selectPage(pageReqVO);
    }

    @Override
    public TenantDO getTenantByName(String name) {
        return tenantMapper.selectByName(name);
    }

    @Override
    public TenantDO getTenantByWebsite(String website) {
        List<TenantDO> tenants = tenantMapper.selectListByWebsite(website);
        return CollUtil.getFirst(tenants);
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return tenantMapper.selectCountByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return tenantMapper.selectListByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByStatus(Integer status) {
        return tenantMapper.selectListByStatus(status);
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {
        if (isTenantDisable()) {
            return;
        }
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        if (isTenantDisable()) {
            return;
        }
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) {
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuDO::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantDO tenant) {
        return Objects.equals(tenant.getPackageId(), TenantDO.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

}
