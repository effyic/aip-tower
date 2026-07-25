-- 运营用户管理：页面菜单（PostgreSQL）
-- 权限模型：菜单树勾选 + 每用户影子角色（运行时创建，不在本脚本预置）

-- ========== 1. 页面菜单（生效）==========
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6800, '运营用户管理', '', 2, 50, 1, 'ops-user', 'ep:avatar', 'system/opsUser/index', 'SystemOpsUser', 0, '1', '1', '1', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, '0');

-- ========== 2. 按钮级权限（当前产品不启用；需要时取消注释，并同步解开 OpsUserController 上 @PreAuthorize）==========
-- INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
-- VALUES (6801, '运营用户查询', 'system:ops-user:query', 3, 1, 6800, '', '', '', NULL, 0, '1', '1', '1', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, '0');
-- INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
-- VALUES (6802, '运营用户创建', 'system:ops-user:create', 3, 2, 6800, '', '', '', NULL, 0, '1', '1', '1', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, '0');
-- INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
-- VALUES (6803, '运营用户更新', 'system:ops-user:update', 3, 3, 6800, '', '', '', NULL, 0, '1', '1', '1', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, '0');
-- INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
-- VALUES (6804, '运营用户删除', 'system:ops-user:delete', 3, 4, 6800, '', '', '', NULL, 0, '1', '1', '1', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, '0');
