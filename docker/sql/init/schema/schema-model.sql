do
$$
begin
create table if not exists store_model
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(30) default 'system'          not null,
    "modifier"         varchar(30) default 'system'          not null,
    "task_name"        varchar(64)                           not null,
    "name"             varchar(128)                           not null,
    "url"              text                                  not null,
    "context"          json        default '{}'::json        not null,
    unique("task_name", "name")
);
create index if not exists fast_query_model on store_model (task_name);
comment on column store_model.id is '模型的自增主键';
comment on column store_model.created_time is '模型的创建时间';
comment on column store_model.updated_time is '模型的更新时间';
comment on column store_model.creator is '模型的创建者';
comment on column store_model.modifier is '模型的修改者';
comment on column store_model.task_name is '任务的名称';
comment on column store_model.name is '模型的结构';
comment on column store_model.url is '模型的跳转链接';
comment on column store_model.context is '模型的上下文';
end
$$;