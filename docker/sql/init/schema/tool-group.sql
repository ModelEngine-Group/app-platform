do
$$
begin
create table if not exists store_tool_group
(
    "id"             bigserial   primary key               not null,
    "created_time"   timestamp   default current_timestamp not null,
    "updated_time"   timestamp   default current_timestamp not null,
    "creator"        varchar(30) default 'system'          not null,
    "modifier"       varchar(30) default 'system'          not null,
    "name"           varchar(256)                          not null,
    "definition_group_name" varchar(256)                   not null,
    "summary"        text        default 'no summary'      not null,
    "description"    text        default 'no desc'         not null,
    "extensions"     json        default '{}'::json        not null,
    unique("name")
    );
comment on column store_tool_group.id is '实现组的自增主键';
comment on column store_tool_group.created_time is '实现组的创建时间';
comment on column store_tool_group.updated_time is '实现组的更新时间';
comment on column store_tool_group.creator is '实现组的创建者';
comment on column store_tool_group.modifier is '实现组的修改者';
comment on column store_tool_group.name is '实现组的名字';
comment on column store_tool_group.definition_group_name is '定义组的唯一标识';
comment on column store_tool_group.summary is '实现组的摘要';
comment on column store_tool_group.description is '实现组的描述';
comment on column store_tool_group.extensions is '实现组的扩展';
end
$$;