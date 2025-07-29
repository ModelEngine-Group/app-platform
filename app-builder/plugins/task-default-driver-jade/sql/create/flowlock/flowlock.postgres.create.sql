CREATE TABLE IF NOT EXISTS flow_lock
(
    lock_key VARCHAR(50) NOT NULL PRIMARY KEY,
    expired_at timestamp without time zone,
    locked_client VARCHAR(50)
);

comment on table flow_lock is '流程锁';
comment on column flow_lock.lock_key is '锁名称';
comment on column flow_lock.expired_at is '锁过期时间';
comment on column flow_lock.locked_client is '上锁的客户端IP';