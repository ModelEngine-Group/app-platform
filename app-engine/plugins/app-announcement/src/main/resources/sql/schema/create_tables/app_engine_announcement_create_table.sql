create table if not exists announcement_info
(
    id          bigserial
    primary key,
    announcement_type varchar(255),
    content       varchar(255),
    details_url   varchar(255),
    start_time    timestamp not null default current_timestamp,
    end_time      timestamp not null default current_timestamp,
    create_time   timestamp not null default current_timestamp
);
