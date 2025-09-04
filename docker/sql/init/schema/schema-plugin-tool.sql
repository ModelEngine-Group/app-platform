do
$$
begin
create table if not exists store_plugin_tool
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(30) default 'system'          not null,
    "modifier"         varchar(30) default 'system'          not null,
    "like_count"       bigint      default 0                 not null,
    "download_count"   bigint      default 0                 not null,
    "tool_name"        varchar(256)                          not null,
    "plugin_id"        varchar(64),
    "tool_unique_name" varchar(36)                           not null,
    "source"           varchar(16) default ''                not null,
    "icon"             text,
    unique("plugin_id", "tool_unique_name")
);
comment on column store_plugin_tool.id is '插件工具的自增主键';
comment on column store_plugin_tool.created_time is '插件工具的创建时间';
comment on column store_plugin_tool.updated_time is '插件工具的更新时间';
comment on column store_plugin_tool.creator is '插件工具的创建者';
comment on column store_plugin_tool.modifier is '插件工具的修改者';
comment on column store_plugin_tool.like_count is '插件工具的点赞数量';
comment on column store_plugin_tool.download_count is '插件工具的下载数量';
comment on column store_plugin_tool.tool_name is '插件工具的名字';
comment on column store_plugin_tool.plugin_id is '插件的唯一标识，工具流的插件唯一标识为空';
comment on column store_plugin_tool.tool_unique_name is '工具的唯一标识';
comment on column store_plugin_tool.source is '插件工具的来源';
comment on column store_plugin_tool.icon is '插件工具的图标';
end
$$