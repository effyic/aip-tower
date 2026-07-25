-- B 端菜单表：字段定义与 system_menu 完全一致（仅表名/序列名不同）
-- 来源对齐：sql/postgresql/ruoyi-vue-pro.sql 中 system_menu

DROP SEQUENCE IF EXISTS system_biz_menu_seq;
CREATE SEQUENCE system_biz_menu_seq
    START 1
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

DROP TABLE IF EXISTS system_biz_menu;
CREATE TABLE system_biz_menu (
    id int8 NOT NULL,
    name varchar(50) NOT NULL,
    permission varchar(100) NOT NULL DEFAULT '',
    type int2 NOT NULL,
    sort int4 NOT NULL DEFAULT 0,
    parent_id int8 NOT NULL DEFAULT 0,
    path varchar(200) NULL DEFAULT '',
    icon varchar(100) NULL DEFAULT '#',
    component varchar(255) NULL DEFAULT NULL,
    component_name varchar(255) NULL DEFAULT NULL,
    status int2 NOT NULL DEFAULT 0,
    visible bool NOT NULL DEFAULT '1',
    keep_alive bool NOT NULL DEFAULT '1',
    always_show bool NOT NULL DEFAULT '1',
    creator varchar(64) NULL DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) NULL DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    menu_code varchar(64) NULL DEFAULT ''
);

ALTER TABLE system_biz_menu ADD CONSTRAINT pk_system_biz_menu PRIMARY KEY (id);

COMMENT ON COLUMN system_biz_menu.id IS '菜单ID';
COMMENT ON COLUMN system_biz_menu.name IS '菜单名称';
COMMENT ON COLUMN system_biz_menu.permission IS '权限标识';
COMMENT ON COLUMN system_biz_menu.type IS '菜单类型';
COMMENT ON COLUMN system_biz_menu.sort IS '显示顺序';
COMMENT ON COLUMN system_biz_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN system_biz_menu.path IS '路由地址';
COMMENT ON COLUMN system_biz_menu.icon IS '菜单图标';
COMMENT ON COLUMN system_biz_menu.component IS '组件路径';
COMMENT ON COLUMN system_biz_menu.component_name IS '组件名';
COMMENT ON COLUMN system_biz_menu.status IS '菜单状态';
COMMENT ON COLUMN system_biz_menu.visible IS '是否可见';
COMMENT ON COLUMN system_biz_menu.keep_alive IS '是否缓存';
COMMENT ON COLUMN system_biz_menu.always_show IS '是否总是显示';
COMMENT ON COLUMN system_biz_menu.creator IS '创建者';
COMMENT ON COLUMN system_biz_menu.create_time IS '创建时间';
COMMENT ON COLUMN system_biz_menu.updater IS '更新者';
COMMENT ON COLUMN system_biz_menu.update_time IS '更新时间';
COMMENT ON COLUMN system_biz_menu.deleted IS '是否删除';
COMMENT ON COLUMN system_biz_menu.menu_code IS '云端菜单编码（稳定业务码，非必填；不参与授权同步可空）';
COMMENT ON TABLE system_biz_menu IS 'B端菜单权限表';
