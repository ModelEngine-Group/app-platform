create table if not exists t_app_engine_eval_dataset
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

comment on table t_app_engine_eval_dataset is '评估数据表';
comment on column t_app_engine_eval_dataset.id is '主键';
comment on column t_app_engine_eval_dataset.name is '数据集名字';
comment on column t_app_engine_eval_dataset.description is '数据集描述';
comment on column t_app_engine_eval_dataset.schema is '数据集 schema';
comment on column t_app_engine_eval_dataset.created_at is '数据集创建时间';
comment on column t_app_engine_eval_dataset.updated_at is '数据集修改时间';
comment on column t_app_engine_eval_dataset.created_by is '数据集创建者';
comment on column t_app_engine_eval_dataset.updated_by is '数据集最近更新者';
comment on column t_app_engine_eval_dataset.app_id is '外键，关联应用 ID';

create table if not exists t_app_engine_eval_data
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

create index if not exists idx_ds_id_created_version_expired_version on t_app_engine_eval_data ("dataset_id", "created_version", "expired_version");

comment on table t_app_engine_eval_data is '评估数据表';
comment on column t_app_engine_eval_data.id is '主键';
comment on column t_app_engine_eval_data.content is '评估内容';
comment on column t_app_engine_eval_data.created_version is '创建版本，只在首次插入时写';
comment on column t_app_engine_eval_data.expired_version is '过期版本';
comment on column t_app_engine_eval_data.created_at is '创建时间，只在首次插入时写';
comment on column t_app_engine_eval_data.updated_at is '更新时间';
comment on column t_app_engine_eval_data.created_by is '创建者';
comment on column t_app_engine_eval_data.updated_by is '更新者';
comment on column t_app_engine_eval_data.dataset_id is '外键，关联评估数据集';
