DELETE FROM t_app_engine_eval_record;
DELETE FROM t_app_engine_eval_task_case;
DELETE FROM t_app_engine_eval_instance;
DELETE FROM t_app_engine_eval_task;

INSERT INTO t_app_engine_eval_task ("name", "description", "status", "created_at", "updated_at", "created_by", "updated_by",
                                    "app_id", "workflow_id")
VALUES ('task1', 'desc1', 'PUBLISHED', '2023-01-01', '2024-01-01', 'user1', 'user1', '123456', 'wf1'),
       ('task2', 'desc2', 'PUBLISHED', '2023-01-01', '2024-01-01', 'user2', 'user2', '123456', 'wf2'),
       ('task3', 'desc3', 'PUBLISHED', '2023-01-01', '2024-01-01', 'user3', 'user3', '123456', 'wf3');

INSERT INTO t_app_engine_eval_instance("status", "pass_rate", "pass_count", "created_by", "task_id", "trace_id", "created_at")
VALUES ('RUNNING', 76.0, 5, 'sky', 1, 'trace1', '2023-01-01'),
       ('SUCCESS', 95.0, 10, 'fang', 1, 'trace1', '2023-01-02'),
       ('FAILED', 23.0, 0, 'sky fang', 2, '2', '2023-01-01'),
       ('RUNNING', 0.0, 0, 'go', 2, 'trace2', '2023-01-02');

INSERT INTO t_app_engine_eval_task_case(pass, instance_id)
VALUES (1, 1);

INSERT INTO t_app_engine_eval_record(input, node_name, node_id, score, task_case_id)
VALUES ('{"input":"1+1", "output":"2", "expected":"2"}', 'nodeName1', 'nodeId1', '100', 1),
       ('{"input":"3+1", "output":"3", "expected":"4"}', 'nodeName2', 'nodeId2', '0', 1);

INSERT INTO t_app_engine_eval_algorithm (node_id, node_name, algorithm_schema, pass_score, task_id)
VALUES ('nodeId1','nodeName1', '{}', 10, 1),
       ('nodeId2','nodeName2', '{}', 20, 1);