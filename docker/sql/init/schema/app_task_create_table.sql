create table if not exists t_app_engine_eval_task
(
    "id"          bigserial primary key                  not null,
    "name"        varchar(64)                            not null,
    "description" varchar(512)                           not null,
    "status"      varchar(32)                            not null,
    "created_at"  timestamp default current_timestamp not null,
    "updated_at"  timestamp default current_timestamp not null,
    "created_by"  varchar(64)  default 'system'          not null,
    "updated_by"  varchar(64)  default 'system'          not null,
    "app_id"      varchar(255)                           not null,
    "workflow_id" varchar(32)                            not null
);

comment on table t_app_engine_eval_task is '评估任务表';
comment on column t_app_engine_eval_task.id is '主键';
comment on column t_app_engine_eval_task.name is '评估任务名字';
comment on column t_app_engine_eval_task.description is '评估任务描述';
comment on column t_app_engine_eval_task.status is '评估任务状态';
comment on column t_app_engine_eval_task.created_at is '评估任务创建时间';
comment on column t_app_engine_eval_task.updated_at is '评估任务修改时间';
comment on column t_app_engine_eval_task.created_by is '评估任务创建者';
comment on column t_app_engine_eval_task.updated_by is '评估任务最近更新者';
comment on column t_app_engine_eval_task.app_id is '应用 ID';
comment on column t_app_engine_eval_task.workflow_id is '工作流 ID';

create table if not exists t_app_engine_eval_report
(
    "id"            bigserial primary key not null,
    "node_id"       varchar(64)           not null,
    "average_score" real                  not null,
    "histogram"     varchar(512)          not null,
    "instance_id"   bigint                not null
);

comment on table t_app_engine_eval_report is '评估报告表';
comment on column t_app_engine_eval_report.id is '主键';
comment on column t_app_engine_eval_report.node_id is '评估节点 ID';
comment on column t_app_engine_eval_report.average_score is '评估报告算法平均得分';
comment on column t_app_engine_eval_report.histogram is '评估报告直方图';
comment on column t_app_engine_eval_report.instance_id is '评估任务实例 ID';

create table if not exists t_app_engine_eval_task_case
(
    "id"          bigserial primary key not null,
    "pass"        boolean               not null,
    "instance_id" bigint                not null
);

comment on table t_app_engine_eval_task_case is '评估任务用例表';
comment on column t_app_engine_eval_task_case.id is '主键';
comment on column t_app_engine_eval_task_case.pass is '评估用例结果';
comment on column t_app_engine_eval_task_case.instance_id is '评估任务实例 ID';

create table if not exists t_app_engine_eval_record
(
    "id"           bigserial primary key not null,
    "input"        text                  not null,
    "node_id"      varchar(64)           not null,
    "node_name"    varchar(64)           not null,
    "score"        real                  not null,
    "task_case_id" bigint                not null
);

comment on table t_app_engine_eval_record is '评估任务用例评估结果表';
comment on column t_app_engine_eval_record.id is '主键';
comment on column t_app_engine_eval_record.input is '用例输入';
comment on column t_app_engine_eval_record.node_id is '评估节点 ID';
comment on column t_app_engine_eval_record.node_name is '评估节点名称';
comment on column t_app_engine_eval_record.score is '用例评估得分';
comment on column t_app_engine_eval_record.task_case_id is '评估任务用例 ID';

create table if not exists t_app_engine_eval_instance
(
    "id"          bigserial    primary key               not null,
    "status"      varchar(32)  default 'RUNNING'         not null,
    "pass_rate"   smallint     default -1                not null,
    "pass_count"  smallint     default 0                 not null,
    "created_by"  varchar(64)  default 'system'          not null,
    "created_at"  timestamp default current_timestamp not null,
    "updated_at"  timestamp default current_timestamp not null,
    "finished_at" timestamp default current_timestamp not null,
    "trace_id"    varchar(32)                            not null,
    "task_id"     bigint                                 not null
);

comment on table t_app_engine_eval_instance is '评估任务实例表';
comment on column t_app_engine_eval_instance.id is '主键';
comment on column t_app_engine_eval_instance.status is '评估任务实例状态';
comment on column t_app_engine_eval_instance.pass_rate is '评估任务实例用例通过率';
comment on column t_app_engine_eval_instance.pass_count is '评估任务实例用例通过数量';
comment on column t_app_engine_eval_instance.created_at is '评估任务实例创建时间';
comment on column t_app_engine_eval_instance.updated_at is '评估任务实例修改时间';
comment on column t_app_engine_eval_instance.finished_at is '评估任务实例完成时间';
comment on column t_app_engine_eval_instance.created_by is '评估任务实例创建者';
comment on column t_app_engine_eval_instance.trace_id is '评估任务实例运行 ID';
comment on column t_app_engine_eval_instance.task_id is '评估任务 ID';

create table if not exists t_app_engine_eval_algorithm
(
    "id"               bigserial primary key not null,
    "node_id"          varchar(64)           not null,
    "node_name"        varchar(32)           not null,
    "algorithm_schema" text                  not null,
    "pass_score"       real                  not null,
    "task_id"          bigint                not null
);

comment on table t_app_engine_eval_algorithm is '评估任务算法表';
comment on column t_app_engine_eval_algorithm.id is '主键';
comment on column t_app_engine_eval_algorithm.node_id is '评估节点 ID';
comment on column t_app_engine_eval_algorithm.node_name is '评估节点名称';
comment on column t_app_engine_eval_algorithm.algorithm_schema is '评估算法格式规范';
comment on column t_app_engine_eval_algorithm.pass_score is '评估算法及格分';
comment on column t_app_engine_eval_algorithm.task_id is '评估任务 ID';