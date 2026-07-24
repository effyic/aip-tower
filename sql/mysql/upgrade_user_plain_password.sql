-- 用户表新增：初始明文密码（MySQL）
ALTER TABLE `system_users`
  ADD COLUMN `plain_password` varchar(50) NULL COMMENT '初始明文密码（租户管理员列表展示用）' AFTER `password`;
