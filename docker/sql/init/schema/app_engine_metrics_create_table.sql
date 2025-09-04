create table if not exists conversation_record
(
    id          bigserial
        primary key,
    app_id      varchar(255),
    question    text,
    answer      text,
    create_user varchar(255),
    create_time timestamp,
    finish_time timestamp,
    instance_id varchar(255)
);

create table if not exists metrics_access
(
    id           bigserial
        primary key,
    app_id       varchar(255),
    total_access bigint,
    create_time  timestamp
);