-- 运营后台：新增「B端菜单」管理页（挂在「租户管理」1224 下），并赋给超级管理员角色
-- 前端套餐勾选请改调：GET /admin-api/system/biz-menu/list-tree 或 /list-all-simple

-- 菜单：B端菜单管理
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id, path, icon, component, component_name,
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    6100, 'B端菜单', '', 2, 2, 1224, 'biz-menu', 'ep:menu', 'system/bizMenu/index', 'SystemBizMenu',
    0, '1', '1', '1', '1', NOW(), '1', NOW(), 0
) ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id, path, icon, component, component_name,
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES
    (6101, 'B端菜单查询', 'system:biz-menu:query', 3, 1, 6100, '', '', '', NULL, 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
    (6102, 'B端菜单创建', 'system:biz-menu:create', 3, 2, 6100, '', '', '', NULL, 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
    (6103, 'B端菜单更新', 'system:biz-menu:update', 3, 3, 6100, '', '', '', NULL, 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
    (6104, 'B端菜单删除', 'system:biz-menu:delete', 3, 4, 6100, '', '', '', NULL, 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员（role_id=1）授权；若你环境 role_menu 主键策略不同，可按需调整 id
INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT nextval('system_role_menu_seq'), 1, m.id, '1', NOW(), '1', NOW(), 0, 1
FROM system_menu m
WHERE m.id IN (6100, 6101, 6102, 6103, 6104)
  AND m.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM system_role_menu rm
      WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = 0
  );

-- 同步菜单序列，避免后续自增冲突
SELECT setval('system_menu_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM system_menu), 6104));
