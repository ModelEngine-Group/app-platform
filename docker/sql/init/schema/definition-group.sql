do
$$
begin
create table if not exists store_definition_group
(
    "id"             bigserial   primary key               not null,
    "created_time"   timestamp   default current_timestamp not null,
    "updated_time"   timestamp   default current_timestamp not null,
    "creator"        varchar(30) default 'system'          not null,
    "modifier"       varchar(30) default 'system'          not null,
    "name"           varchar(256)                          not null,
    "summary"        text        default 'no summary'      not null,
    "description"    text        default 'no desc'         not null,
    "extensions"     json        default '{}'::json        not null,
    unique("name")
    );
comment on column store_definition_group.id is '定义组的自增主键';
comment on column store_definition_group.created_time is '定义组的创建时间';
comment on column store_definition_group.updated_time is '定义组的更新时间';
comment on column store_definition_group.creator is '定义组的创建者';
comment on column store_definition_group.modifier is '定义组的修改者';
comment on column store_definition_group.name is '定义组的名字';
comment on column store_definition_group.summary is '定义组的摘要';
comment on column store_definition_group.description is '定义组的描述';
comment on column store_definition_group.extensions is '定义组的扩展';
end
$$;