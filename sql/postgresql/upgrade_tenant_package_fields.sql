-- 租户套餐新增：分诊/问诊 Agent 上限、高级配置、自定义病例（PostgreSQL）
ALTER TABLE system_tenant_package
  ADD COLUMN IF NOT EXISTS triage_agent_limit int4 NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS inquiry_agent_limit int4 NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS advanced_config_enabled bool NOT NULL DEFAULT false,
  ADD COLUMN IF NOT EXISTS custom_case_enabled bool NOT NULL DEFAULT false;

COMMENT ON COLUMN system_tenant_package.triage_agent_limit IS '分诊Agent上限';
COMMENT ON COLUMN system_tenant_package.inquiry_agent_limit IS '问诊Agent上限';
COMMENT ON COLUMN system_tenant_package.advanced_config_enabled IS '高级配置（false关 true开）';
COMMENT ON COLUMN system_tenant_package.custom_case_enabled IS '自定义病例（false关 true开）';
