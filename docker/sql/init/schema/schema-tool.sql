do
$$
begin
create table if not exists store_tool
(
    "id"             bigserial   primary key               not null,
    "created_time"   timestamp   default current_timestamp not null,
    "updated_time"   timestamp   default current_timestamp not null,
    "creator"        varchar(30) default 'system'          not null,
    "modifier"       varchar(30) default 'system'          not null,
    "name"           varchar(256)                          not null,
    "schema"         json        default '{}'::json        not null,
    "runnables"      json        default '{}'::json        not null,
    "extensions"     json        default '{}'::json        not null,
    "unique_name"    varchar(36)                           not null,
    "version"        varchar(10) default '1.0.0'           not null,
    "is_latest"      boolean     default true              not null,
    "group_name"     varchar(256)                          not null,
    "definition_name" varchar(256)                         not null,
    "definition_group_name" varchar(256)                   not null,
    unique("unique_name", "version")
);
comment on column store_tool.id is '工具的自增主键';
comment on column store_tool.created_time is '工具的创建时间';
comment on column store_tool.updated_time is '工具的更新时间';
comment on column store_tool.creator is '工具的创建者';
comment on column store_tool.modifier is '工具的修改者';
comment on column store_tool.name is '工具的名字';
comment on column store_tool.group_name is '工具组的名字';
comment on column store_tool.definition_name is '工具定义的名字';
comment on column store_tool.definition_group_name is '工具定义组的名字';
comment on column store_tool.schema is '工具的格式';
comment on column store_tool.runnables is '工具的运行描述';
comment on column store_tool.extensions is '工具的扩展描述';
comment on column store_tool.unique_name is '工具的唯一标识';
comment on column store_tool.version is '工具的版本';
comment on column store_tool.is_latest is '表示当前版本工具是否最新';
end
$$;