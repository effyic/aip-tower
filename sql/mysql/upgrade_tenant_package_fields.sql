-- 租户套餐新增：分诊/问诊 Agent 上限、高级配置、自定义病例
ALTER TABLE `system_tenant_package`
  ADD COLUMN `triage_agent_limit` int NOT NULL DEFAULT 0 COMMENT '分诊Agent上限' AFTER `menu_ids`,
  ADD COLUMN `inquiry_agent_limit` int NOT NULL DEFAULT 0 COMMENT '问诊Agent上限' AFTER `triage_agent_limit`,
  ADD COLUMN `advanced_config_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '高级配置（0关 1开）' AFTER `inquiry_agent_limit`,
  ADD COLUMN `custom_case_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '自定义病例（0关 1开）' AFTER `advanced_config_enabled`;
