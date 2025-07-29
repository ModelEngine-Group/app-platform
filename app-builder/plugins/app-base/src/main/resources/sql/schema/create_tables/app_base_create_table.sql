CREATE TABLE IF NOT EXISTS usr_feedback_info
(
    id bigserial NOT NULL PRIMARY KEY,
    usr_feedback_text text,
    usr_feedback integer,
    instance_id varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS usr_collection_info
(
    id bigserial NOT NULL PRIMARY KEY,
    app_id varchar(255) NOT NULL,
    usr_info varchar(64)  NOT NULL
);

CREATE TABLE IF NOT EXISTS user_info
(
    id bigserial NOT NULL PRIMARY KEY,
    default_app varchar(255) NOT NULL,
    user_name varchar(64) NOT NULL UNIQUE
);

create index if not exists idx_instance_id ON usr_feedback_info(instance_id);
create index if not exists idx_usr_info ON usr_collection_info(usr_info);

create index if not exists idx_user_name ON user_info(user_name);