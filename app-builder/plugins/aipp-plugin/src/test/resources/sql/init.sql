create table if not exists aipp_instance_file
(
    file_id    bigserial    not null primary key,
    aipp_id    varchar(255) not null,
    filename   varchar(255) not null,
    create_at  timestamp    not null default current_timestamp,
    create_by  varchar(64)  not null,
    is_deleted int2 DEFAULT 0
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
    path varchar(255),
    app_type   varchar(255) DEFAULT ''
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
    id         varchar(64) not null primary key,
    form_id    varchar(255) not null,
    name       varchar(255) not null,
    data_type  varchar(255) not null,
    default_value  text,
    data_from      varchar(8),
    in_group    varchar(8),
    description varchar(64),
    default_index int2 default 0,
    is_deleted int2 DEFAULT 0,
    app_id    varchar(64)
    );

create table if not exists app_template (
    id varchar(64) not null primary key,
    name varchar(255) not null,
    built_type varchar(8) not null,
    category varchar(16) not null,
    attributes jsonb not null default '{}'::json,
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

create table if not exists t_chat_session (
    chat_id     VARCHAR(32)   NOT NULL DEFAULT NULL,
    app_id      VARCHAR(32)   NULL     DEFAULT NULL,
    app_version VARCHAR(32)   NULL     DEFAULT NULL,
    name        VARCHAR(2000) NULL     DEFAULT NULL,
    attributes  JSON          NULL     DEFAULT NULL,
    create_at   TIMESTAMP(6)  NULL     DEFAULT NULL,
    create_by   VARCHAR(32)   NULL     DEFAULT NULL,
    update_at   TIMESTAMP(6)  NULL     DEFAULT NULL,
    update_by   VARCHAR(32)   NULL     DEFAULT NULL,
    status      INT4          NULL     DEFAULT NULL
    );

CREATE TABLE IF NOT EXISTS t_chat_session_task_instance_wide_relationship (
    msg_id                VARCHAR(32)  NOT NULL DEFAULT NULL,
    chat_id               VARCHAR(32)  NULL     DEFAULT NULL,
    task_instance_wide_id VARCHAR(32)  NULL     DEFAULT NULL,
    create_at             TIMESTAMP(6) NULL     DEFAULT NULL,
    create_by             VARCHAR      NULL     DEFAULT NULL,
    update_at             TIMESTAMP(6) NULL     DEFAULT NULL,
    update_by             VARCHAR      NULL     DEFAULT NULL
    );

ALTER TABLE app_builder_app ADD COLUMN IF NOT EXISTS app_suite_id VARCHAR(32) NULL;
-- UPDATE app_builder_app SET app_suite_id = task.template_id from task where task.attributes->>'app_id' = app_builder_app.id;
ALTER TABLE app_builder_app
    ADD COLUMN IF NOT EXISTS  is_active bool NULL DEFAULT false;
ALTER TABLE app_builder_app
    ADD COLUMN IF NOT EXISTS status varchar(16) NULL;
ALTER TABLE app_builder_app
    ADD COLUMN IF NOT EXISTS unique_name varchar(64) NULL;
ALTER TABLE app_builder_app
    ADD COLUMN IF NOT EXISTS publish_at timestamp(6) NULL;
ALTER TABLE app_builder_app
    ADD COLUMN IF NOT EXISTS app_id varchar(64) NULL;


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