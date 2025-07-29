DELETE FROM t_app_engine_eval_record;

INSERT INTO t_app_engine_eval_record(input, node_name, node_id, score, task_case_id)
VALUES ('{"input":"1+1", "output":"2", "expected":"2"}', 'nodeName1', 'nodeId1', '100', 1),
       ('{"input":"3+1", "output":"3", "expected":"4"}', 'nodeName2', 'nodeId2', '0', 1);