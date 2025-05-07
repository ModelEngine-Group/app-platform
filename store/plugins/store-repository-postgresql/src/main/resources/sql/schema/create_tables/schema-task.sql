do
$$
begin
create table if not exists store_task
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(30) default 'system'          not null,
    "modifier"         varchar(30) default 'system'          not null,
    "task_name"        varchar(64)                           not null,
    "context"          json        default '{}'::json        not null,
    "tool_unique_name" varchar(36)                              not null,
    unique("task_name")
);
create index if not exists fast_query_task on store_task (tool_unique_name);
comment on column store_task.id is '任务的自增主键';
comment on column store_task.created_time is '任务的创建时间';
comment on column store_task.updated_time is '任务的更新时间';
comment on column store_task.creator is '任务的创建者';
comment on column store_task.modifier is '任务的修改者';
comment on column store_task.task_name is '任务的名称';
comment on column store_task.context is '任务的上下文';
comment on column store_task.tool_unique_name is '工具的唯一标识';
end
$$;