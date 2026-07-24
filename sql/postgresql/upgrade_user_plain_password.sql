-- 用户表新增：初始明文密码（PostgreSQL）
ALTER TABLE system_users
  ADD COLUMN IF NOT EXISTS plain_password varchar(50) NULL;

COMMENT ON COLUMN system_users.plain_password IS '初始明文密码（租户管理员列表展示用）';
