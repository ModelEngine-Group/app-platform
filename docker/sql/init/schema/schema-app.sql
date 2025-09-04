do
$$
begin
create table if not exists store_app
(
    "id"               bigserial   primary key               not null,
    "created_time"     timestamp   default current_timestamp not null,
    "updated_time"     timestamp   default current_timestamp not null,
    "creator"          varchar(30) default 'system'          not null,
    "modifier"         varchar(30) default 'system'          not null,
    "like_count"       bigint      default 0                 not null,
    "download_count"   bigint      default 0                 not null,
    "source"           varchar(16) default ''                not null,
    "icon"             text,
    "app_category" varchar(16) default 'chatbot',
    "tool_name"        varchar(256)                          not null,
    "tool_unique_name" varchar(36)                           not null,
    unique("tool_unique_name")
    );
create index if not exists fast_query_app on store_app ("tool_unique_name");
comment on column store_app.id is '应用的自增主键';
comment on column store_app.created_time is '应用的创建时间';
comment on column store_app.updated_time is '应用的更新时间';
comment on column store_app.creator is '应用的创建者';
comment on column store_app.modifier is '应用的修改者';
comment on column store_app.like_count is '应用的点赞数量';
comment on column store_app.download_count is '应用的下载数量';
comment on column store_app.source is '应用的来源';
comment on column store_app.icon is '应用的图标';
comment on column store_app.app_category is '应用的种类';
comment on column store_app.tool_name is '工具及应用的统一名称';
comment on column store_app.tool_unique_name is '应用的工具唯一标识';
end
$$;