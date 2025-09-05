do
$$
begin
create table if not exists store_plugin
(
    "id"            bigserial   primary key               not null,
    "created_time"  timestamp   default current_timestamp not null,
    "updated_time"  timestamp   default current_timestamp not null,
    "creator"       varchar(30) default 'system'          not null,
    "modifier"      varchar(30) default 'system'          not null,
    "plugin_id"     varchar(64)                           not null,
    "plugin_name"   varchar(256)                          not null,
    "extension"     json        default '{}'::json        not null,
    "deploy_status" varchar(20) default 'UNDEPLOYED'      not null,
    "is_builtin"    boolean     default false             not null,
    "source"        varchar(16) default ''                not null,
    "icon"          text,
    unique ("plugin_id")
);
create index if not exists query_plugin ON store_plugin ("is_builtin");
comment on column store_plugin.id is '插件的自增主键';
comment on column store_plugin.created_time is '插件的创建时间';
comment on column store_plugin.updated_time is '插件的更新时间';
comment on column store_plugin.creator is '插件的创建者';
comment on column store_plugin.modifier is '插件的修改者';
comment on column store_plugin.plugin_id is '插件的唯一标识';
comment on column store_plugin.plugin_name is '插件的名字';
comment on column store_plugin.extension is '插件的扩展字段';
comment on column store_plugin.deploy_status is '插件的部署状态';
comment on column store_plugin.is_builtin is '插件是否内置';
comment on column store_plugin.source is '插件的来源';
comment on column store_plugin.icon is '插件的图标';
end
$$;