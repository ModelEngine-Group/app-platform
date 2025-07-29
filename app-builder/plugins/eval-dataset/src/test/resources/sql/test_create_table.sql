drop table if exists t_app_engine_eval_data;
drop table if exists t_app_engine_eval_dataset;

create table t_app_engine_eval_dataset
(
    "id"          bigserial primary key                 not null,
    "name"        varchar(30)                           not null,
    "description" varchar(100)                          not null,
    "schema"      text                                  not null,
    "created_at"  timestamp   default current_timestamp not null,
    "updated_at"  timestamp   default current_timestamp not null,
    "created_by"  varchar(10) default 'system'          not null,
    "updated_by"  varchar(10) default 'system'          not null,
    "app_id"      varchar(255)                          not null
);

create table t_app_engine_eval_data
(
    "id"              bigserial primary key                       not null,
    "content"         text                                        not null,
    "created_version" bigint                                      not null,
    "expired_version" bigint      default 9223372036854775807     not null,
    "created_at"      timestamp   default current_timestamp       not null,
    "updated_at"      timestamp   default current_timestamp       not null,
    "created_by"      varchar(10) default 'system'                not null,
    "updated_by"      varchar(10) default 'system'                not null,
    "dataset_id"      bigint references t_app_engine_eval_dataset not null
);