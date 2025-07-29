DELETE FROM t_app_engine_eval_algorithm;
DELETE FROM t_app_engine_eval_report;

INSERT INTO t_app_engine_eval_report (node_id, average_score, histogram, instance_id)
VALUES ('node1', 10, '[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]', '1'),
       ('node2', 20, '[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]', '1'),
       ('node3', 30, '[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]', '1');

INSERT INTO t_app_engine_eval_algorithm (node_id, node_name, algorithm_schema, pass_score, task_id)
VALUES ('node1','accuracy', '{}', 10, 1),
       ('node2','recall', '{}', 20, 1),
       ('node3','precision', '{}', 30, 1);