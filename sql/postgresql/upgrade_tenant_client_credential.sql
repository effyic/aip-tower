-- 租户对接凭证（B 端拉取配置用）
ALTER TABLE system_tenant
  ADD COLUMN IF NOT EXISTS client_id varchar(64) NULL,
  ADD COLUMN IF NOT EXISTS client_secret varchar(255) NULL;

COMMENT ON COLUMN system_tenant.client_id IS 'B端对接 ClientId';
COMMENT ON COLUMN system_tenant.client_secret IS 'B端对接 ClientSecret（BCrypt 哈希）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_tenant_client_id
  ON system_tenant (client_id) WHERE client_id IS NOT NULL AND deleted = 0;
