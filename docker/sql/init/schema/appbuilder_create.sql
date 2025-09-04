create table if not exists aipp_instance_file
(
    file_id    bigserial    not null primary key,
    aipp_id    varchar(255),
    filename   varchar(255) not null,
    create_at  timestamp    not null default current_timestamp,
    create_by  varchar(64)  not null,
    is_deleted int2 not null default 0,
    is_removable int2 not null default 1,
    file_uuid  varchar(255) not null
    );

create table if not exists aipp_instance_log
(
    log_id                 bigserial not null primary key,
    aipp_id                varchar(255) not null,
    version                varchar(255),
    aipp_type              varchar(64),
    instance_id            varchar(255) not null,
    log_data               json not null,
    log_type               varchar(64) not null,
    create_at              timestamp default current_timestamp,
    create_by              varchar(64) not null,
    path                   text,
    is_deleted int2 DEFAULT 0
    );

CREATE INDEX IF NOT EXISTS "idx_aipp_instance_log_path" ON "aipp_instance_log" USING btree ("path" COLLATE "pg_catalog"."default" "pg_catalog"."text_pattern_ops" ASC NULLS LAST);

create table if not exists form_data(
    form_id varchar(64) not null,
    form_version varchar(32) not null,
    form_name varchar(256) not null,
    tenant_id varchar(64),
    update_time timestamp,
    update_user varchar(64),
    create_time timestamp not null,
    create_user varchar(64),
    constraint form_pkey primary key (form_id, form_version)
    );


create or replace function update_time() returns trigger as $$
begin
    NEW.update_time = now();
return NEW;
end;
$$ language plpgsql;

create or replace trigger form_data_updater before UPDATE on form_data for each row execute function update_time();

create table if not exists app_builder_app
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    config_id  varchar(255) not null,
    flow_graph_id varchar(255) not null,
    tenant_id  varchar(255) not null,
    type       varchar(255) not null,
    version    varchar(255),
    attributes JSON not null DEFAULT '{}',
    state varchar(255) not null,
    app_built_type varchar(8) not null default 'basic',
    app_category varchar(16) not null default 'chatbot',
    collection_usr_cnt bigint NOT NULL DEFAULT 0,
    is_deleted int2 DEFAULT 0,
    path       varchar(255),
    app_type   varchar(255) DEFAULT '',
    app_suite_id varchar(32) NULL,
    is_active bool NULL DEFAULT false,
    status varchar(16) NULL,
    unique_name varchar(64) NULL,
    publish_at timestamp(6) NULL,
    app_id varchar(64) NULL
    );

create table if not exists app_builder_component
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    type       varchar(255) not null,
    description varchar(255) not null,
    form_id    varchar(255) not null,
    service_id varchar(255) not null,
    tenant_id  varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_config
(
    id         varchar(64) not null primary key,
    form_id    varchar(255) not null,
    app_id varchar(255) not null,
    tenant_id  varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_config_property
(
    id         varchar(64) not null primary key,
    node_id    varchar(255),
    form_property_id  varchar(255) not null,
    config_id  varchar(64)  not null,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_flow_graph
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    appearance TEXT,
    is_deleted int2 DEFAULT 0
    );

create table if not exists app_builder_form
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    tenant_id  varchar(255) not null,
    appearance TEXT,
    type       varchar(64)  not null,
    create_by  varchar(64)  not null,
    create_at  timestamp    not null default current_timestamp,
    update_by  varchar(64)  not null,
    update_at  timestamp    not null default current_timestamp,
    is_deleted int2 DEFAULT 0,
    form_suite_id varchar(64) not null,
    version varchar(64) not null
    );

create table if not exists app_builder_form_property
(
    id             varchar(64) not null primary key,
    form_id        varchar(255) not null,
    name           varchar(255) not null,
    data_type      varchar(255) not null,
    default_value  text,
    data_from      varchar(8),
    in_group       varchar(8),
    description    varchar(64),
    default_index  int2 default 0,
    is_deleted     int2 DEFAULT 0,
    app_id    varchar(64)
    );

comment on column app_builder_form_property.data_from is '数据来源';
comment on column app_builder_form_property.in_group is '应用所属的组';
comment on column app_builder_form_property.description is '应用描述';
comment on column app_builder_form_property.default_index is '属性的默认顺序';

create table if not exists app_builder_runtime_info
(
    id                 BIGSERIAL primary key,
    trace_id           varchar(64) not null,
    flow_definition_id varchar(64) not null,
    instance_id        varchar(64) not null,
    node_id            varchar(64) not null,
    node_type          varchar(32) not null,
    start_time         bigint not null,
    end_time           bigint not null,
    status             varchar(32),
    published          smallint not null,
    error_msg          text,
    next_position_id   varchar(64),
    parameters         json not null DEFAULT '[]',
    create_by          varchar(64),
    create_at          timestamp    not null default current_timestamp,
    update_by          varchar(64),
    update_at          timestamp    not null default current_timestamp
);
create index if not exists idx_trace_id on app_builder_runtime_info(trace_id);

CREATE TABLE IF NOT EXISTS "t_chat_session_task_instance_wide_relationship" (
    "msg_id"                VARCHAR(32)  NOT NULL DEFAULT NULL,
    "chat_id"               VARCHAR(32)  NULL     DEFAULT NULL,
    "task_instance_wide_id" VARCHAR(32)  NULL     DEFAULT NULL,
    "create_at"             TIMESTAMP(6) NULL     DEFAULT NULL,
    "create_by"             VARCHAR      NULL     DEFAULT NULL,
    "update_at"             TIMESTAMP(6) NULL     DEFAULT NULL,
    "update_by"             VARCHAR      NULL     DEFAULT NULL,
    CONSTRAINT "pk_t_chat_session_task_instance_wide_relationship_msg_id" PRIMARY KEY("msg_id")
    );
COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."msg_id" IS '消息ID';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."chat_id" IS '会话ID';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."task_instance_wide_id" IS '实例ID';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."create_at" IS '创建时间';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."create_by" IS '创建人';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."update_at" IS '更新时间';

COMMENT ON COLUMN "t_chat_session_task_instance_wide_relationship"."update_by" IS '更新人';

CREATE TABLE IF NOT EXISTS "t_chat_session" (
    "chat_id"     VARCHAR(32)   NOT NULL DEFAULT NULL,
    "app_id"      VARCHAR(32)   NULL     DEFAULT NULL,
    "app_version" VARCHAR(32)   NULL     DEFAULT NULL,
    "name"        VARCHAR(2000) NULL     DEFAULT NULL,
    "attributes"  JSON          NULL     DEFAULT NULL,
    "create_at"   TIMESTAMP(6)  NULL     DEFAULT NULL,
    "create_by"   VARCHAR(32)   NULL     DEFAULT NULL,
    "update_at"   TIMESTAMP(6)  NULL     DEFAULT NULL,
    "update_by"   VARCHAR(32)   NULL     DEFAULT NULL,
    "status"      INT4          NULL     DEFAULT NULL,
    CONSTRAINT "pk_t_chat_session" PRIMARY KEY("chat_id")
    );
COMMENT ON COLUMN "t_chat_session"."chat_id" IS '会话ID';

COMMENT ON COLUMN "t_chat_session"."app_id" IS '应用ID';

COMMENT ON COLUMN "t_chat_session"."app_version" IS '应用版本';

COMMENT ON COLUMN "t_chat_session"."name" IS '会话纪要';

COMMENT ON COLUMN "t_chat_session"."attributes" IS '属性扩展字段';

COMMENT ON COLUMN "t_chat_session"."create_at" IS '创建时间';

COMMENT ON COLUMN "t_chat_session"."create_by" IS '创建人';

COMMENT ON COLUMN "t_chat_session"."update_at" IS '更新时间';

COMMENT ON COLUMN "t_chat_session"."update_by" IS '更新人';

COMMENT ON COLUMN "t_chat_session"."status" IS '状态 0-正常 1-已删除';

create table if not exists i18n
(
    id         varchar(64) not null primary key,
    key        varchar(255) not null,
    language   varchar(64)  not null,
    value      text
    );

create table if not exists user_custom_inspiration
(
    id         bigserial not null primary key,
    parent_id        varchar(255) not null,
    category_id   varchar(255)  not null,
    value      json not null,
    create_user varchar(255) not null,
    aipp_id varchar(255) not null,
    inspiration_id varchar(255) not null
    );

create table if not exists app_chat_num (
      id varchar(32) primary key,
      app_id varchar(32) not null,
      chat_mode varchar(8) not null, --true\false
      chat_num int check (chat_num <= 16)
);
create unique index if not exists idx_app_chat_mode on app_chat_num("app_id", "chat_mode");

CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_inspiration ON user_custom_inspiration (aipp_id, inspiration_id);

CREATE TABLE IF NOT EXISTS aipp_system_config (
                                    id                    serial PRIMARY KEY,
                                    config_key            VARCHAR(64)         NOT NULL,
                                    config_value          JSONB               NOT NULL,
                                    config_group          VARCHAR(64)         NOT NULL,
                                    config_parent         VARCHAR(64)         DEFAULT NULL
);

CREATE INDEX IF NOT EXISTS config_key_index ON aipp_system_config (config_key);
CREATE UNIQUE INDEX IF NOT EXISTS group_key_unique_index ON aipp_system_config (config_group, config_key);

-- 应用业务分类表
create table if not exists app_builder_app_type
(
    id         varchar(64) not null primary key,
    name       varchar(255) not null,
    tenant_id  varchar(255) not null,
    create_at  timestamp    not null default current_timestamp,
    update_at  timestamp    not null default current_timestamp,
    CONSTRAINT "app_builder_app_type_name_tenant_id_key" UNIQUE ("name", "tenant_id")
);

-- 应用模板表
create table if not exists app_template (
     id varchar(64) not null primary key,
     name varchar(255) not null,
     built_type varchar(8) not null,
     category varchar(16) not null,
     attributes json not null default '{}'::json,
     app_type varchar(255) not null,
     "like" int8 not null default 0,
     collection int8 not null default 0,
     usage int8 not null default 0,
     version varchar(255),
     config_id varchar(255) not null,
     flow_graph_id varchar(255) not null,
     create_by varchar(64) not null,
     create_at timestamp(6) not null default CURRENT_TIMESTAMP,
     update_by varchar(64) not null,
     update_at timestamp(6) not null default CURRENT_TIMESTAMP,
     is_deleted int2 not null default 0
);

CREATE TABLE IF NOT EXISTS task_new (
    "id" char(32)  NOT NULL PRIMARY KEY,
    "name" varchar(255)  NOT NULL,
    "version" varchar(64) NOT NULL,
    "template_id" char(32) NOT NULL,
    "tenant_id" char(32) NOT NULL,
    "attributes" json NOT NULL DEFAULT '{}'::json,
    "created_by" varchar(127) NOT NULL,
    "created_at" timestamp(6) NOT NULL,
    "updated_by" varchar(127) NOT NULL,
    "updated_at" timestamp(6) NOT NULL,
    "is_deleted" int2 NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS task_instance_new (
    "id" char(32) NOT NULL PRIMARY KEY,
    "task_id" char(32) NOT NULL,
    "task_name" varchar(255),
    "creator" varchar(127),
    "create_time" timestamp(6),
    "modify_by" varchar(127),
    "modify_time" timestamp(6),
    "finish_time" timestamp(6),
    "flow_instance_id" char(32),
    "curr_form_id" char(32),
    "curr_form_version" varchar(127),
    "curr_form_data" text,
    "smart_form_time" timestamp(6),
    "instance_status" varchar(32),
    "instance_progress" varchar(127),
    "instance_agent_result" text,
    "instance_child_instance_id" char(32),
    "instance_curr_node_id" varchar(127),
    "is_deleted" int2 NOT NULL DEFAULT 0,
    "resume_duration" varchar(127)
);