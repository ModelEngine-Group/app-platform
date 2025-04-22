create table if not exists t_app_engine_model
(
    "id" bigserial primary key                           not null,
    "created_at" timestamp(9) default current_timestamp  not null,
    "updated_at" timestamp(9) default current_timestamp  not null,
    "created_by" varchar(64) default 'system'            not null,
    "updated_by" varchar(64) default 'system'            not null,
    "model_id" varchar(255)                              not null,
    "name" varchar(255)                                  not null,
    "tag" varchar(255)                                   not null,
    "base_url" varchar(255)                              not null,
    "type" varchar(255) default 'chat_completions'       not null
    );
create unique index if not exists model_id_index on t_app_engine_model("model_id");
comment on table t_app_engine_model is '模型信息表';
comment on column t_app_engine_model.id is '主键';
comment on column t_app_engine_model.created_at is '模型信息创建时间';
comment on column t_app_engine_model.updated_at is '模型信息修改时间';
comment on column t_app_engine_model.created_by is '模型信息创建者';
comment on column t_app_engine_model.updated_by is '模型信息最近更新者';
comment on column t_app_engine_model.name is '模型名字';
comment on column t_app_engine_model.tag is '模型标签';
comment on column t_app_engine_model.base_url is '模型访问接口地址';
comment on column t_app_engine_model.type is '模型类型';

create table if not exists t_app_engine_user_model
(
    "id" bigserial primary key                           not null,
    "created_at" timestamp(9) default current_timestamp  not null,
    "updated_at" timestamp(9) default current_timestamp  not null,
    "created_by" varchar(64) default 'system'            not null,
    "updated_by" varchar(64) default 'system'            not null,
    "user_id" varchar(255)                               not null,
    "model_id" varchar(255)                              not null,
    "api_key" varchar(255)                               not null,
    "is_default" smallint default 0                      not null
    );
create unique index if not exists user_model_id_index on t_app_engine_user_model("model_id");
comment on table t_app_engine_user_model is '用户模型关系表';
comment on column t_app_engine_user_model.id is '主键';
comment on column t_app_engine_user_model.created_at is '用户模型关系创建时间';
comment on column t_app_engine_user_model.updated_at is '用户模型关系修改时间';
comment on column t_app_engine_user_model.created_by is '用户模型关系创建者';
comment on column t_app_engine_user_model.updated_by is '用户模型关系最近更新者';
comment on column t_app_engine_user_model.user_id is '用户id';
comment on column t_app_engine_user_model.model_id is '模型id';
comment on column t_app_engine_user_model.api_key is '用户访问token';
comment on column t_app_engine_user_model.is_default is '是否为默认模型';