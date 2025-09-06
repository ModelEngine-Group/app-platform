DELETE FROM t_app_engine_eval_task;

INSERT INTO t_app_engine_eval_task ("id", "name", "description", "status", "created_at", "updated_at", "created_by", "updated_by",
                                    "app_id", "workflow_id")
VALUES (1, 'task1', 'desc1', 'PUBLISHED', '2023-01-01', '2024-01-01', 'user1', 'user1', '123456', 'wf1'),
       (2, 'task2', 'desc2', 'PUBLISHED', '2023-01-01', '2024-01-01', 'user2', 'user2', '123456', 'wf2'),
       (3, 'task3', 'desc3', 'PUBLISHED', '2023-01-01', '2024-01-01', 'user3', 'user3', '123456', 'wf3');