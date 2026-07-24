-- 租户表新增：创建编号、医院等级、服务地址（MySQL）
ALTER TABLE `system_tenant`
  ADD COLUMN `code` varchar(16) NULL COMMENT '创建编号，如 A001' AFTER `id`,
  ADD COLUMN `hospital_level` varchar(32) NULL COMMENT '医院等级' AFTER `name`,
  ADD COLUMN `service_url` varchar(512) NULL COMMENT '服务地址' AFTER `hospital_level`;

CREATE UNIQUE INDEX `uk_system_tenant_code` ON `system_tenant` (`code`);
