-- 租户表新增：创建编号、医院等级、服务地址（PostgreSQL）
ALTER TABLE system_tenant
  ADD COLUMN IF NOT EXISTS code varchar(16) NULL,
  ADD COLUMN IF NOT EXISTS hospital_level varchar(32) NULL,
  ADD COLUMN IF NOT EXISTS service_url varchar(512) NULL;

COMMENT ON COLUMN system_tenant.code IS '创建编号，如 A001';
COMMENT ON COLUMN system_tenant.hospital_level IS '医院等级';
COMMENT ON COLUMN system_tenant.service_url IS '服务地址';

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_tenant_code ON system_tenant (code) WHERE code IS NOT NULL AND deleted = 0;
