DELETE
FROM t_app_engine_eval_instance;
INSERT INTO t_app_engine_eval_instance("status", "pass_rate", "pass_count", "created_by", "task_id", "trace_id")
VALUES ('RUNNING', 76.0, 5, 'sky', 1, '1'),
       ('SUCCESS', 95.0, 10, 'fang', 1, '1'),
       ('FAILED', 23.0, 0, 'sky fang', 2, '2');