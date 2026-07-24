package com.effyic.aiptower.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
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
import com.effyic.aiptower.framework.tenant.core.util.TenantUtils;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantAdminAccountRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantCreateRespVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.effyic.aiptower.module.system.convert.tenant.TenantConvert;
import com.effyic.aiptower.module.system.dal.dataobject.permission.MenuDO;
import com.effyic.aiptower.module.system.dal.dataobject.permission.RoleDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO;
import com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.effyic.aiptower.module.system.dal.dataobject.user.AdminUserDO;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.system.dal.mysql.tenant.TenantMapper;
import com.effyic.aiptower.module.system.dal.mysql.user.AdminUserMapper;
import com.effyic.aiptower.module.system.enums.permission.RoleCodeEnum;
import com.effyic.aiptower.module.system.enums.permission.RoleTypeEnum;
import com.effyic.aiptower.module.system.service.permission.MenuService;
import com.effyic.aiptower.module.system.service.permission.PermissionService;
import com.effyic.aiptower.module.system.service.permission.RoleService;
import com.effyic.aiptower.module.system.service.tenant.handler.TenantInfoHandler;
import com.effyic.aiptower.module.system.service.tenant.handler.TenantMenuHandler;
import com.effyic.aiptower.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.effyic.aiptower.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singleton;

/**
 * 租户 Service 实现类
 *
 * @author effyic
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    private static final int DEFAULT_ACCOUNT_COUNT = 100;
    private static final int ADMIN_PASSWORD_LENGTH = 10;
    private static final String ADMIN_PASSWORD_CHARS =
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
    private AdminUserMapper userMapper;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;

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
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    @DataPermission(enable = false) // 参见 https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1154 说明
    public TenantCreateRespVO createTenant(TenantSaveReqVO createReqVO) {
        // 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(createReqVO.getWebsites(), null);
        // 校验套餐被禁用
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(createReqVO.getPackageId());

        // 创建租户
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
        tenantMapper.insert(tenant);

        // 创建租户的管理员（账号创建时间与租户创建时间一致；序号按租户隔离从 A001 起）
        String password = generateAdminPassword();
        AtomicReference<Long> userIdRef = new AtomicReference<>();
        AtomicReference<String> usernameRef = new AtomicReference<>();
        AtomicReference<LocalDateTime> userCreateTimeRef = new AtomicReference<>();
        TenantUtils.execute(tenant.getId(), () -> {
            Long roleId = createRole(tenantPackage);
            String username = buildNextAdminUsername(tenant.getName());
            Long userId = createUser(roleId, username, password, tenant.getContactName(), createReqVO.getContactMobile());
            // 第一次管理员创建时间 = 租户创建时间，并保存明文密码
            LocalDateTime tenantCreateTime = tenantMapper.selectById(tenant.getId()).getCreateTime();
            userMapper.update(null, new LambdaUpdateWrapper<AdminUserDO>()
                    .set(AdminUserDO::getCreateTime, tenantCreateTime)
                    .set(AdminUserDO::getPlainPassword, password)
                    .eq(AdminUserDO::getId, userId));
            tenantMapper.updateById(new TenantDO().setId(tenant.getId()).setContactUserId(userId));
            userIdRef.set(userId);
            usernameRef.set(username);
            userCreateTimeRef.set(tenantCreateTime);
        });

        return new TenantCreateRespVO(tenant.getId(), tenant.getCode(),
                usernameRef.get(), password, userIdRef.get(), userCreateTimeRef.get());
    }

    @Override
    @DSTransactional
    @DataPermission(enable = false)
    public TenantAdminAccountRespVO generateTenantAdmin(Long tenantId) {
        TenantDO tenant = validateUpdateTenant(tenantId);
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(tenant.getPackageId());
        String password = generateAdminPassword();
        AtomicReference<TenantAdminAccountRespVO> resultRef = new AtomicReference<>();
        TenantUtils.execute(tenantId, () -> {
            // 获取（或创建）租户管理员角色
            Long roleId = getOrCreateTenantAdminRoleId(tenantPackage);
            // 序号按当前租户已有管理员递增：A001、A002...
            String username = buildNextAdminUsername(tenant.getName());
            Long userId = createUser(roleId, username, password, tenant.getContactName(), tenant.getContactMobile());
            userMapper.update(null, new LambdaUpdateWrapper<AdminUserDO>()
                    .set(AdminUserDO::getPlainPassword, password)
                    .eq(AdminUserDO::getId, userId));
            AdminUserDO user = userMapper.selectById(userId);
            resultRef.set(new TenantAdminAccountRespVO(userId, username, password, user.getCreateTime()));
        });
        return resultRef.get();
    }

    @Override
    @DataPermission(enable = false)
    public List<TenantAdminAccountRespVO> getTenantAdminList(Long tenantId) {
        TenantDO tenant = getTenant(tenantId);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        AtomicReference<List<TenantAdminAccountRespVO>> resultRef = new AtomicReference<>();
        TenantUtils.execute(tenantId, () -> {
            List<AdminUserDO> users = userMapper.selectList(new LambdaQueryWrapperX<AdminUserDO>()
                    .likeRight(AdminUserDO::getUsername, "admin-")
                    .orderByAsc(AdminUserDO::getCreateTime)
                    .orderByAsc(AdminUserDO::getId));
            List<TenantAdminAccountRespVO> list = new ArrayList<>(users.size());
            for (AdminUserDO user : users) {
                list.add(new TenantAdminAccountRespVO(user.getId(), user.getUsername(),
                        user.getPlainPassword(), user.getCreateTime()));
            }
            resultRef.set(list);
        });
        return resultRef.get();
    }

    private Long createUser(Long roleId, String username, String password, String nickname, String mobile) {
        Long userId = userService.createUser(TenantConvert.INSTANCE.convert02(
                username, password, StrUtil.blankToDefault(nickname, "管理员"), mobile));
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private Long createRole(TenantPackageDO tenantPackage) {
        RoleSaveReqVO reqVO = new RoleSaveReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    private Long getOrCreateTenantAdminRoleId(TenantPackageDO tenantPackage) {
        List<RoleDO> roles = roleService.getRoleListByStatus(singleton(CommonStatusEnum.ENABLE.getStatus()));
        RoleDO adminRole = CollUtil.findOne(roles,
                role -> Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode()));
        if (adminRole != null) {
            return adminRole.getId();
        }
        return createRole(tenantPackage);
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

    /**
     * 管理员账号：admin-{医院名称拼音首字母}A{序号}
     * <p>
     * 序号按<strong>当前租户</strong>隔离递增：该租户下第一个为 A001，第二个为 A002...
     * 须在 {@link TenantUtils#execute} 租户上下文中调用。
     */
    private String buildNextAdminUsername(String hospitalName) {
        String initials = getHospitalNameInitials(hospitalName);
        String prefix = "admin-" + initials + "A";
        List<AdminUserDO> users = userMapper.selectList(new LambdaQueryWrapperX<AdminUserDO>()
                .likeRight(AdminUserDO::getUsername, prefix));
        int next = 1;
        for (AdminUserDO user : users) {
            Matcher matcher = ADMIN_USERNAME_SEQ_PATTERN.matcher(user.getUsername());
            if (matcher.matches()) {
                // 再校验是否同一首字母前缀，避免跨医院误匹配
                if (user.getUsername().startsWith(prefix)) {
                    next = Math.max(next, Integer.parseInt(matcher.group(1)) + 1);
                }
            }
        }
        if (next > 999) {
            throw exception(TENANT_CODE_EXCEED);
        }
        String username = prefix + String.format("%03d", next);
        return StrUtil.maxLength(username, 30);
    }

    private static String getHospitalNameInitials(String hospitalName) {
        if (StrUtil.isBlank(hospitalName)) {
            return "hosp";
        }
        String firstLetter = PinyinUtil.getFirstLetter(hospitalName, "");
        if (StrUtil.isBlank(firstLetter)) {
            return "hosp";
        }
        // 仅保留字母数字，小写（与前端展示一致，如 admin-cyyiyA001）
        String initials = firstLetter.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return StrUtil.blankToDefault(StrUtil.maxLength(initials, 16), "hosp");
    }

    private static String generateAdminPassword() {
        return RandomUtil.randomString(ADMIN_PASSWORD_CHARS, ADMIN_PASSWORD_LENGTH);
    }

    @Override
    @DSTransactional // 多数据源，使用 @DSTransactional 保证本地事务，以及数据源的切换
    public void updateTenant(TenantSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "租户编号不能为空");
        // 校验存在
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());
        // 校验租户名称是否重复
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        // 校验租户域名是否重复
        validTenantWebsiteDuplicate(updateReqVO.getWebsites(), updateReqVO.getId());
        // 校验套餐被禁用
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(updateReqVO.getPackageId());

        // 仅更新编辑页字段，避免误清空联系人/账号数/创建编号等
        TenantDO updateObj = new TenantDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setName(updateReqVO.getName());
        updateObj.setHospitalLevel(updateReqVO.getHospitalLevel());
        updateObj.setServiceUrl(updateReqVO.getServiceUrl());
        updateObj.setPackageId(updateReqVO.getPackageId());
        updateObj.setExpireTime(updateReqVO.getExpireTime());
        // 可选字段：有传才更新
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
        // 如果套餐发生变化，则修改其角色的权限
        if (ObjectUtil.notEqual(tenant.getPackageId(), updateReqVO.getPackageId())) {
            updateTenantRoleMenu(tenant.getId(), tenantPackage.getMenuIds());
        }
    }

    private void validTenantNameDuplicate(String name, Long id) {
        TenantDO tenant = tenantMapper.selectByName(name);
        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
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
                tenants.removeIf(tenant -> tenant.getId().equals(excludeId));
            }
            if (CollUtil.isNotEmpty(tenants)) {
                throw exception(TENANT_WEBSITE_DUPLICATE, website);
            }
        });
    }

    @Override
    @DSTransactional
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        TenantUtils.execute(tenantId, () -> {
            // 获得所有角色
            List<RoleDO> roles = roleService.getRoleList();
            roles.forEach(role -> Assert.isTrue(tenantId.equals(role.getTenantId()), "角色({}/{}) 租户不匹配",
                    role.getId(), role.getTenantId(), tenantId)); // 兜底校验
            // 重新分配每个角色的权限
            roles.forEach(role -> {
                // 如果是租户管理员，重新分配其权限为租户套餐的权限
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                    log.info("[updateTenantRoleMenu][租户管理员({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), menuIds);
                    return;
                }
                // 如果是其他角色，则去掉超过套餐的权限
                Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                roleMenuIds = CollUtil.intersectionDistinct(roleMenuIds, menuIds);
                permissionService.assignRoleMenu(role.getId(), roleMenuIds);
                log.info("[updateTenantRoleMenu][角色({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), roleMenuIds);
            });
        });
    }

    @Override
    public void deleteTenant(Long id) {
        // 校验存在
        validateUpdateTenant(id);
        // 删除
        tenantMapper.deleteById(id);
    }

    @Override
    public void deleteTenantList(List<Long> ids) {
        // 1. 校验存在
        ids.forEach(this::validateUpdateTenant);

        // 2. 批量删除
        tenantMapper.deleteByIds(ids);
    }

    private TenantDO validateUpdateTenant(Long id) {
        TenantDO tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // 内置租户，不允许删除
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
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        // 执行处理器
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户，然后获得菜单
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) { // 系统租户，菜单是全量的
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuDO::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        // 执行处理器
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantDO tenant) {
        return Objects.equals(tenant.getPackageId(), TenantDO.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

}
