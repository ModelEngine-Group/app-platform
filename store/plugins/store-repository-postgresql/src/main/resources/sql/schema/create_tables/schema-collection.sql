do
$$
begin
create table if not exists store_collection
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(30) default 'system'          not null,
    "modifier"         varchar(30) default 'system'          not null,
    "collector"        varchar(32) default 'system'          not null,
    "tool_unique_name" varchar(36)                           not null
    );
create index if not exists fast_query_plugin_collection on store_collection ("collector");
comment on column store_collection.id is '收藏的自增主键';
comment on column store_collection.created_time is '收藏的创建时间';
comment on column store_collection.updated_time is '收藏的更新时间';
comment on column store_collection.creator is '收藏的创建者';
comment on column store_collection.modifier is '收藏的修改者';
comment on column store_collection.collector is '插件的收藏者';
comment on column store_collection.tool_unique_name is '插件的工具唯一标识';
end
$$;