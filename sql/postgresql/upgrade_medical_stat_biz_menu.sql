-- 医疗数据监控：Tower B 端菜单目录（system_biz_menu）
-- menu_code 与 aip-hub system_menu 对齐
-- 套餐勾选请在 Tower 管理后台操作，由 package-config 组装 menuTree 下发 Hub

INSERT INTO system_biz_menu (
    id, name, menu_code, permission, type, sort, parent_id, path, icon, component, component_name,
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES
(6906, '数据监控', 'medical:stat', '', 1, 5, 0, '/DataMonitor', '', '', '',
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0),
(6907, '用户数据统计', 'medical:stat:user', '', 2, 1, 6906, 'user', '', 'DataMonitor/user/index', 'DataMonitorUser',
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0),
(6908, '运营数据监控', 'medical:stat:ops', '', 2, 2, 6906, 'ops', '', 'DataMonitor/ops/index', 'DataMonitorOps',
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0),
(6909, '监控数据查询', 'medical:stat:user:query', 'medical:stat:query', 3, 1, 6907, '', '', '', NULL,
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0),
(6910, '监控数据查询', 'medical:stat:ops:query', 'medical:stat:query', 3, 1, 6908, '', '', '', NULL,
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0),
(6911, '监控配置查询', 'medical:stat:ops:config:query', 'medical:stat:config:query', 3, 2, 6908, '', '', '', NULL,
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0),
(6912, '监控配置更新', 'medical:stat:ops:config:update', 'medical:stat:config:update', 3, 3, 6908, '', '', '', NULL,
 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    menu_code = EXCLUDED.menu_code,
    permission = EXCLUDED.permission,
    type = EXCLUDED.type,
    sort = EXCLUDED.sort,
    parent_id = EXCLUDED.parent_id,
    path = EXCLUDED.path,
    icon = EXCLUDED.icon,
    component = EXCLUDED.component,
    component_name = EXCLUDED.component_name,
    status = 0,
    visible = true,
    keep_alive = true,
    always_show = true,
    deleted = 0,
    updater = '1',
    update_time = CURRENT_TIMESTAMP;

SELECT setval('system_biz_menu_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM system_biz_menu), 6912));
