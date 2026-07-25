-- B 端租户管理员账号（明文账号密码，供 B 端拉取；本平台不用于登录）
DROP SEQUENCE IF EXISTS system_biz_tenant_admin_seq;
CREATE SEQUENCE system_biz_tenant_admin_seq
    START 1
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS system_biz_tenant_admin (
    id int8 NOT NULL,
    tenant_id int8 NOT NULL,
    username varchar(30) NOT NULL,
    password varchar(32) NOT NULL,
    creator varchar(64) NULL DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) NULL DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0
);

ALTER TABLE system_biz_tenant_admin DROP CONSTRAINT IF EXISTS pk_system_biz_tenant_admin;
ALTER TABLE system_biz_tenant_admin ADD CONSTRAINT pk_system_biz_tenant_admin PRIMARY KEY (id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_biz_tenant_admin_username
  ON system_biz_tenant_admin (tenant_id, username) WHERE deleted = 0;

CREATE INDEX IF NOT EXISTS idx_biz_tenant_admin_tenant_id
  ON system_biz_tenant_admin (tenant_id) WHERE deleted = 0;

COMMENT ON TABLE system_biz_tenant_admin IS 'B端租户管理员账号';
COMMENT ON COLUMN system_biz_tenant_admin.tenant_id IS '租户编号';
COMMENT ON COLUMN system_biz_tenant_admin.username IS '登录账号，如 admin-xhyyA001';
COMMENT ON COLUMN system_biz_tenant_admin.password IS '初始密码（明文）';
