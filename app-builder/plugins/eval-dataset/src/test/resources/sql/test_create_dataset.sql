DELETE FROM t_app_engine_eval_dataset;

INSERT INTO t_app_engine_eval_dataset (id, name, description, schema, created_at, updated_at, created_by, updated_by, app_id)
VALUES (1, 'name1', 'desc1', 'Fake schema 1', '2023-01-01', '2023-01-01', 'Sky1', 'Fang1', '123456'),
       (2, 'name2', 'desc2', 'Fake schema 2', '2023-01-01', '2023-01-01', 'Sky2', 'Fang2', '123456'),
       (3, 'name3', 'desc3', 'Fake schema 3', '2023-01-01', '2023-01-01', 'Sky3', 'Fang3', '123456');