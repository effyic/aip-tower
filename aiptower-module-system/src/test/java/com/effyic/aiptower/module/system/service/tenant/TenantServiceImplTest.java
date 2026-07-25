package com.effyic.aiptower.module.system.service.tenant;

import com.effyic.aiptower.framework.common.enums.CommonStatusEnum;
import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.tenant.config.TenantProperties;
import com.effyic.aiptower.framework.tenant.core.context.TenantContextHolder;
import com.effyic.aiptower.framework.test.core.ut.BaseDbUnitTest;
import com.effyic.aiptower.module.system.controller.admin.tenant.vo.tenant.TenantAdminAccountRespVO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.effyic.aiptower.framework.common.util.collection.SetUtils.asSet;
import static com.effyic.aiptower.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.effyic.aiptower.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static com.effyic.aiptower.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.effyic.aiptower.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.effyic.aiptower.framework.test.core.util.AssertUtils.assertServiceException;
import static com.effyic.aiptower.framework.test.core.util.RandomUtils.*;
import static com.effyic.aiptower.module.system.dal.dataobject.tenant.TenantDO.PACKAGE_ID_SYSTEM;
import static com.effyic.aiptower.module.system.enums.ErrorCodeConstants.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link TenantServiceImpl} 的单元测试类
 *
 * @author effyic
 */
@Import(TenantServiceImpl.class)
public class TenantServiceImplTest extends BaseDbUnitTest {

    @Resource
    private TenantServiceImpl tenantService;

    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private BizTenantAdminMapper bizTenantAdminMapper;

    @MockitoBean
    private TenantProperties tenantProperties;
    @MockitoBean
    private TenantPackageService tenantPackageService;
    @MockitoBean
    private MenuService menuService;
    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        // 清理租户上下文
        TenantContextHolder.clear();
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded:" + invocation.getArgument(0));
        when(passwordEncoder.matches(anyString(), anyString())).thenAnswer(invocation ->
                ("encoded:" + invocation.getArgument(0)).equals(invocation.getArgument(1)));
    }

    @Test
    public void testGetTenantIdList() {
        // mock 数据
        TenantDO tenant = randomPojo(TenantDO.class, o -> o.setId(1L));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        List<Long> result = tenantService.getTenantIdList();
        assertEquals(Collections.singletonList(1L), result);
    }

    @Test
    public void testValidTenant_notExists() {
        assertServiceException(() -> tenantService.validTenant(randomLongId()), TENANT_NOT_EXISTS);
    }

    @Test
    public void testValidTenant_disable() {
        // mock 数据
        TenantDO tenant = randomPojo(TenantDO.class, o -> o.setId(1L).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        assertServiceException(() -> tenantService.validTenant(1L), TENANT_DISABLE, tenant.getName());
    }

    @Test
    public void testValidTenant_expired() {
        // mock 数据
        TenantDO tenant = randomPojo(TenantDO.class, o -> o.setId(1L).setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(buildTime(2020, 2, 2)));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        assertServiceException(() -> tenantService.validTenant(1L), TENANT_EXPIRE, tenant.getName());
    }

    @Test
    public void testValidTenant_success() {
        // mock 数据
        TenantDO tenant = randomPojo(TenantDO.class, o -> o.setId(1L).setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(LocalDateTime.now().plusDays(1)));
        tenantMapper.insert(tenant);

        // 调用，并断言业务异常
        tenantService.validTenant(1L);
    }

    @Test
    public void testCreateTenant() {
        // mock 套餐 100L
        TenantPackageDO tenantPackage = randomPojo(TenantPackageDO.class, o -> o.setId(100L));
        when(tenantPackageService.validTenantPackage(eq(100L))).thenReturn(tenantPackage);

        // 准备参数
        TenantSaveReqVO reqVO = randomPojo(TenantSaveReqVO.class, o -> {
            o.setName("协和医院");
            o.setHospitalLevel("三甲");
            o.setServiceUrl("https://hospital.example.com");
            o.setContactName("管理员");
            o.setContactMobile("15601691300");
            o.setPackageId(100L);
            o.setStatus(randomCommonStatus());
            o.setWebsites(singletonList("https://www.effyic.com"));
            o.setAccountCount(100);
        }).setId(null); // 设置为 null，方便后面校验

        // 调用
        var resp = tenantService.createTenant(reqVO);
        // 断言
        assertNotNull(resp.getId());
        assertEquals("A001", resp.getCode());
        assertTrue(resp.getClientId().startsWith("biz_a001_"));
        assertEquals(32, resp.getClientSecret().length());
        // 校验记录的属性是否正确
        TenantDO tenant = tenantMapper.selectById(resp.getId());
        assertEquals("协和医院", tenant.getName());
        assertEquals("三甲", tenant.getHospitalLevel());
        assertEquals("A001", tenant.getCode());
        assertEquals(resp.getClientId(), tenant.getClientId());
        assertEquals("encoded:" + resp.getClientSecret(), tenant.getClientSecret());
        // 默认管理员账号
        assertNotNull(resp.getAdminUsername());
        assertTrue(resp.getAdminUsername().startsWith("admin-"));
        assertTrue(resp.getAdminUsername().endsWith("A001"));
        assertEquals(10, resp.getAdminPassword().length());
        assertEquals(tenant.getCreateTime(), resp.getAdminCreateTime());
        List<BizTenantAdminDO> admins = bizTenantAdminMapper.selectListByTenantId(resp.getId());
        assertEquals(1, admins.size());
        assertEquals(resp.getAdminUsername(), admins.get(0).getUsername());
        assertEquals(resp.getAdminPassword(), admins.get(0).getPassword());
    }

    @Test
    public void testGenerateTenantAdmin_andList() {
        TenantPackageDO tenantPackage = randomPojo(TenantPackageDO.class, o -> o.setId(100L));
        when(tenantPackageService.validTenantPackage(eq(100L))).thenReturn(tenantPackage);
        TenantSaveReqVO reqVO = randomPojo(TenantSaveReqVO.class, o -> {
            o.setName("协和医院");
            o.setPackageId(100L);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setWebsites(singletonList("https://www.effyic.com"));
        }).setId(null);
        var created = tenantService.createTenant(reqVO);

        TenantAdminAccountRespVO second = tenantService.generateTenantAdmin(created.getId());
        assertTrue(second.getUsername().endsWith("A002"));
        assertEquals(10, second.getPassword().length());

        List<TenantAdminAccountRespVO> list = tenantService.getTenantAdminList(created.getId());
        assertEquals(2, list.size());
        assertEquals(created.getAdminUsername(), list.get(0).getUsername());
        assertEquals(second.getUsername(), list.get(1).getUsername());
    }

    @Test
    public void testUpdateTenant_success() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setStatus(randomCommonStatus()));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        TenantSaveReqVO reqVO = randomPojo(TenantSaveReqVO.class, o -> {
            o.setId(dbTenant.getId()); // 设置更新的 ID
            o.setStatus(randomCommonStatus());
            o.setWebsites(singletonList(randomString()));
        });

        // mock 套餐
        TenantPackageDO tenantPackage = randomPojo(TenantPackageDO.class,
                o -> o.setMenuIds(asSet(200L, 201L)));
        when(tenantPackageService.validTenantPackage(eq(reqVO.getPackageId()))).thenReturn(tenantPackage);

        // 调用
        tenantService.updateTenant(reqVO);
        // 校验是否更新正确
        TenantDO tenant = tenantMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, tenant);
    }

    @Test
    public void testAuthenticateClient_success() {
        TenantDO tenant = randomPojo(TenantDO.class, o -> o.setId(1L)
                .setStatus(CommonStatusEnum.ENABLE.getStatus())
                .setExpireTime(LocalDateTime.now().plusDays(1))
                .setClientId("biz_a001_test")
                .setClientSecret("encoded:secret123"));
        tenantMapper.insert(tenant);

        TenantDO result = tenantService.authenticateClient("biz_a001_test", "secret123");
        assertEquals(1L, result.getId());
    }

    @Test
    public void testAuthenticateClient_badCredentials() {
        assertServiceException(() -> tenantService.authenticateClient("missing", "x"),
                TENANT_CLIENT_BAD_CREDENTIALS);
    }

    @Test
    public void testUpdateTenant_notExists() {
        // 准备参数
        TenantSaveReqVO reqVO = randomPojo(TenantSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> tenantService.updateTenant(reqVO), TENANT_NOT_EXISTS);
    }

    @Test
    public void testUpdateTenant_system() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        TenantSaveReqVO reqVO = randomPojo(TenantSaveReqVO.class, o -> {
            o.setId(dbTenant.getId()); // 设置更新的 ID
        });

        // 调用，校验业务异常
        assertServiceException(() -> tenantService.updateTenant(reqVO), TENANT_CAN_NOT_UPDATE_SYSTEM);
    }

    @Test
    public void testDeleteTenant_success() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class,
                o -> o.setStatus(randomCommonStatus()));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenant.getId();

        // 调用
        tenantService.deleteTenant(id);
        // 校验数据不存在了
        assertNull(tenantMapper.selectById(id));
    }

    @Test
    public void testDeleteTenant_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> tenantService.deleteTenant(id), TENANT_NOT_EXISTS);
    }

    @Test
    public void testDeleteTenant_system() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenant.getId();

        // 调用, 并断言异常
        assertServiceException(() -> tenantService.deleteTenant(id), TENANT_CAN_NOT_UPDATE_SYSTEM);
    }

    @Test
    public void testGetTenant() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class);
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbTenant.getId();

        // 调用
        TenantDO result = tenantService.getTenant(id);
        // 校验存在
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantPage() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> { // 等会查询到
            o.setName("AIP-Tower");
            o.setContactName("管理员");
            o.setContactMobile("15601691300");
            o.setExpireTime(LocalDateTime.now().plusDays(10)); // 使用中
            o.setCreateTime(buildTime(2020, 12, 12));
        });
        tenantMapper.insert(dbTenant);
        // 测试 name 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setName(randomString())));
        // 测试 contactName 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setContactName(randomString())));
        // 测试 contactMobile 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setContactMobile(randomString())));
        // 测试 usageStatus 不匹配（已过期）
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setExpireTime(LocalDateTime.now().minusDays(1))));
        // 测试 createTime 不匹配
        tenantMapper.insert(cloneIgnoreId(dbTenant, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // 准备参数
        TenantPageReqVO reqVO = new TenantPageReqVO();
        reqVO.setName("AIP-Tower");
        reqVO.setContactName("管理");
        reqVO.setContactMobile("1560");
        reqVO.setUsageStatus(0); // 使用中
        reqVO.setCreateTime(buildBetweenTime(2020, 12, 1, 2020, 12, 24));

        // 调用
        PageResult<TenantDO> pageResult = tenantService.getTenantPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbTenant, pageResult.getList().get(0));
    }

    @Test
    public void testGetTenantByName() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setName("AIP-Tower"));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据

        // 调用
        TenantDO result = tenantService.getTenantByName("AIP-Tower");
        // 校验存在
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantByWebsite() {
        // mock 数据
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setWebsites(singletonList("https://www.effyic.com")));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据

        // 调用
        TenantDO result = tenantService.getTenantByWebsite("https://www.effyic.com");
        // 校验存在
        assertPojoEquals(result, dbTenant);
    }

    @Test
    public void testGetTenantListByPackageId() {
        // mock 数据
        TenantDO dbTenant1 = randomPojo(TenantDO.class, o -> o.setPackageId(1L));
        tenantMapper.insert(dbTenant1);// @Sql: 先插入出一条存在的数据
        TenantDO dbTenant2 = randomPojo(TenantDO.class, o -> o.setPackageId(2L));
        tenantMapper.insert(dbTenant2);// @Sql: 先插入出一条存在的数据

        // 调用
        List<TenantDO> result = tenantService.getTenantListByPackageId(1L);
        assertEquals(1, result.size());
        assertPojoEquals(dbTenant1, result.get(0));
    }

    @Test
    public void testGetTenantCountByPackageId() {
        // mock 数据
        TenantDO dbTenant1 = randomPojo(TenantDO.class, o -> o.setPackageId(1L));
        tenantMapper.insert(dbTenant1);// @Sql: 先插入出一条存在的数据
        TenantDO dbTenant2 = randomPojo(TenantDO.class, o -> o.setPackageId(2L));
        tenantMapper.insert(dbTenant2);// @Sql: 先插入出一条存在的数据

        // 调用
        Long count = tenantService.getTenantCountByPackageId(1L);
        assertEquals(1, count);
    }

    @Test
    public void testHandleTenantInfo_disable() {
        // 准备参数
        TenantInfoHandler handler = mock(TenantInfoHandler.class);
        // mock 禁用
        when(tenantProperties.getEnable()).thenReturn(false);

        // 调用
        tenantService.handleTenantInfo(handler);
        // 断言
        verify(handler, never()).handle(any());
    }

    @Test
    public void testHandleTenantInfo_success() {
        // 准备参数
        TenantInfoHandler handler = mock(TenantInfoHandler.class);
        // mock 未禁用
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock 租户
        TenantDO dbTenant = randomPojo(TenantDO.class);
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        TenantContextHolder.setTenantId(dbTenant.getId());

        // 调用
        tenantService.handleTenantInfo(handler);
        // 断言
        verify(handler).handle(argThat(argument -> {
            assertPojoEquals(dbTenant, argument);
            return true;
        }));
    }

    @Test
    public void testHandleTenantMenu_disable() {
        // 准备参数
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock 禁用
        when(tenantProperties.getEnable()).thenReturn(false);

        // 调用
        tenantService.handleTenantMenu(handler);
        // 断言
        verify(handler, never()).handle(any());
    }

    @Test // 系统租户的情况
    public void testHandleTenantMenu_system() {
        // 准备参数
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock 未禁用
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock 租户
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setPackageId(PACKAGE_ID_SYSTEM));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        TenantContextHolder.setTenantId(dbTenant.getId());
        // mock 菜单
        when(menuService.getMenuList()).thenReturn(Arrays.asList(randomPojo(MenuDO.class, o -> o.setId(100L)),
                randomPojo(MenuDO.class, o -> o.setId(101L))));

        // 调用
        tenantService.handleTenantMenu(handler);
        // 断言
        verify(handler).handle(asSet(100L, 101L));
    }

    @Test // 普通租户的情况
    public void testHandleTenantMenu_normal() {
        // 准备参数
        TenantMenuHandler handler = mock(TenantMenuHandler.class);
        // mock 未禁用
        when(tenantProperties.getEnable()).thenReturn(true);
        // mock 租户
        TenantDO dbTenant = randomPojo(TenantDO.class, o -> o.setPackageId(200L));
        tenantMapper.insert(dbTenant);// @Sql: 先插入出一条存在的数据
        TenantContextHolder.setTenantId(dbTenant.getId());
        // mock 菜单
        when(tenantPackageService.getTenantPackage(eq(200L))).thenReturn(randomPojo(TenantPackageDO.class,
                o -> o.setMenuIds(asSet(100L, 101L))));

        // 调用
        tenantService.handleTenantMenu(handler);
        // 断言
        verify(handler).handle(asSet(100L, 101L));
    }
}
