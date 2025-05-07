do
$$
begin
create table if not exists store_tag
(
    "id"           bigserial primary key               not null,
    "created_time" timestamp default current_timestamp not null,
    "updated_time" timestamp default current_timestamp not null,
    "creator"      char(30)  default 'system'          not null,
    "modifier"     char(30)  default 'system'          not null,
    "tool_unique_name"      varchar(36)                not null,
    "name"         varchar(64)                         not null,
    unique ("tool_unique_name", "name")
    );
comment on column store_tag.id is '标签的自增主键';
comment on column store_tag.created_time is '标签的创建时间';
comment on column store_tag.updated_time is '标签的更新时间';
comment on column store_tag.creator is '标签的创建者';
comment on column store_tag.modifier is '标签的修改者';
comment on column store_tag.name is '标签的名称';
comment on column store_tag.tool_unique_name is '工具的唯一标识';
end
$$;