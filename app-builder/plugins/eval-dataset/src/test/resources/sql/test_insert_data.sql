INSERT INTO t_app_engine_eval_dataset("name", "description", "schema", "app_id")
VALUES ('ds1', 'test dataset', '{}', '1'),
       ('ds2', 'test dataset', '{}', '1'),
       ('ds3', 'test dataset', '{}', '1');

INSERT INTO t_app_engine_eval_data("content", "created_version", "dataset_id", "expired_version")
VALUES ('{}', 1, 1, 9223372036854775807),
       ('{}', 1, 1, 2);
