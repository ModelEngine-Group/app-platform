drop table if exists t_app_engine_eval_algorithm;
drop table if exists t_app_engine_eval_report;
drop table if exists t_app_engine_eval_record;
drop table if exists t_app_engine_eval_task_case;
drop table if exists t_app_engine_eval_instance;
drop table if exists t_app_engine_eval_task;

create table if not exists t_app_engine_eval_task
(
    "id"          bigserial primary key                  not null,
    "name"        varchar(64)                            not null,
    "description" varchar(512)                           not null,
    "status"      varchar(32)                            not null,
    "created_at"  timestamp(9) default current_timestamp not null,
    "updated_at"  timestamp(9) default current_timestamp not null,
    "created_by"  varchar(64)  default 'system'          not null,
    "updated_by"  varchar(64)  default 'system'          not null,
    "app_id"      varchar(255)                           not null,
    "workflow_id" varchar(32)                            not null
);

create table if not exists t_app_engine_eval_report
(
    "id"            bigserial primary key not null,
    "node_id"       varchar(64)           not null,
    "average_score" real                  not null,
    "histogram"     varchar(512)          not null,
    "instance_id"   bigint                not null
);

create table if not exists t_app_engine_eval_task_case
(
    "id"          bigserial primary key not null,
    "pass"        boolean               not null,
    "instance_id" bigint                not null
);

create table if not exists t_app_engine_eval_record
(
    "id"           bigserial primary key  not null,
    "input"        text                   not null,
    "node_id"      varchar(64)            not null,
    "node_name"    varchar(64)            not null,
    "score"        real                   not null,
    "task_case_id" bigint                 not null
);

create table if not exists t_app_engine_eval_instance
(
    "id"          bigserial primary key                  not null,
    "status"      varchar(32)  default 'RUNNING'         not null,
    "pass_rate"   smallint     default -1                not null,
    "pass_count"  smallint     default 0                 not null,
    "created_by"  varchar(64)  default 'system'          not null,
    "created_at"  timestamp(9) default current_timestamp not null,
    "updated_at"  timestamp(9) default current_timestamp not null,
    "finished_at" timestamp(9) default current_timestamp not null,
    "trace_id"    varchar(32)                            not null,
    "task_id"     bigint                                 not null
);

create table if not exists t_app_engine_eval_algorithm
(
    "id"               bigserial primary key not null,
    "node_id"          varchar(64)           not null,
    "node_name"        varchar(32)           not null,
    "algorithm_schema" text                  not null,
    "pass_score"       real                  not null,
    "task_id"          bigint                not null
);