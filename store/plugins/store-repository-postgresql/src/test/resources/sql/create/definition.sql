drop table if exists store_definition;

create table if not exists store_definition
(
    "id"             bigserial   primary key               not null,
    "created_time"   timestamp default current_timestamp not null,
    "updated_time"   timestamp default current_timestamp not null,
    "creator"        varchar(30) default 'system'          not null,
    "modifier"       varchar(30) default 'system'          not null,
    "name"           varchar(256)                          not null,
    "schema"         text                                  not null,
    "definition_group_name" varchar(256)                   not null,
    unique("definition_group_name", "name")
);
comment on column store_definition.id is '工具定义的自增主键';
comment on column store_definition.created_time is '工具定义的创建时间';
comment on column store_definition.updated_time is '工具定义的更新时间';
comment on column store_definition.creator is '工具定义的创建者';
comment on column store_definition.modifier is '工具定义的修改者';
comment on column store_definition.name is '工具定义的名字';
comment on column store_definition.schema is '工具定义的格式';
comment on column store_definition.definition_group_name is '工具定义组的名字'