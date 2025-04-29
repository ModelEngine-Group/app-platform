create table if not exists t_app_engine_user_knowledge
(
    "id" bigserial primary key                           not null,
    "created_at" timestamp(9) default current_timestamp  not null,
    "updated_at" timestamp(9) default current_timestamp  not null,
    "created_by" varchar(64) default 'system'            not null,
    "updated_by" varchar(64) default 'system'            not null,
    "user_id" varchar(64)                               not null,
    "name" varchar(64)                              not null,
    "group_id" varchar(64)                               not null,
    "api_key" varchar(255)                               not null,
    "is_default" smallint default 0                      not null
);

create unique index user_knowledge_index on t_app_engine_user_knowledge ("user_id", "group_id", "api_key");
comment on table  t_app_engine_user_knowledge is '用户知识库信息表';
comment on column t_app_engine_user_knowledge.id is '主键';
comment on column t_app_engine_user_knowledge.created_at is '用户知识库信息创建时间';
comment on column t_app_engine_user_knowledge.updated_at is '用户知识库信息修改时间';
comment on column t_app_engine_user_knowledge.created_by is '用户知识库信息创建者';
comment on column t_app_engine_user_knowledge.updated_by is '用户知识库信息最近更新者';
comment on column t_app_engine_user_knowledge.user_id is '用户id';
comment on column t_app_engine_user_knowledge.name is '知识库平台name';
comment on column t_app_engine_user_knowledge.group_id is '知识库平台groupId';
comment on column t_app_engine_user_knowledge.api_key is '用户访问api key';
comment on column t_app_engine_user_knowledge.is_default is '是否为用户默认使用的api key'