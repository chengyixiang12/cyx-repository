create table sys_schedule_job
(
    id            bigint auto_increment comment '主键'
        primary key,
    create_by     bigint       null comment '创建人',
    create_time   datetime     null comment '创建时间',
    update_by     bigint       null comment '修改人',
    update_time   datetime     null comment '修改时间',
    del_flag      char         null comment '逻辑删除；1：存在；0：删除',
    job_name      varchar(100) null comment '任务名称',
    job_group     varchar(100) null comment '任务组',
    cron          varchar(100) null comment 'cron表达式',
    job_type      varchar(100) null comment '任务类型',
    status        char         null comment '任务状态；1：启用；0：暂停',
    job_param     text         null comment '任务所需参数',
    start_time    datetime     null comment '开始时间',
    end_time      datetime     null comment '结束时间',
    job_interval  int          null comment '间隔',
    interval_type char         null comment '间隔类型；0：毫秒；1：秒；2：分钟；3：小时',
    schedule_type char         null comment '调度类型；0：简单调度；1：cron表达式调度'
)
    comment '调度任务表';

INSERT INTO radish_master.sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type) VALUES (1, 1, '2025-07-15 19:06:19', 1, '2025-07-15 19:06:19', '1', '测试调度任务1', 'default', null, 'storage_scan', '0', null, null, null, 1, '2', '0');
INSERT INTO radish_master.sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type) VALUES (2, 1, '2025-07-15 19:31:45', 1, '2025-07-15 19:31:45', '0', '测试调度任务2', 'default', null, 'storage_scan', '1', null, null, null, 1, '2', '0');
INSERT INTO radish_master.sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type) VALUES (3, 1, '2025-07-15 19:44:54', 1, '2025-07-15 19:44:54', '0', '测试调度任务3', 'default', null, 'storage_scan', '1', null, null, null, 1, '2', '0');
INSERT INTO radish_master.sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type) VALUES (4, 1, '2025-07-15 19:51:33', 1, '2025-07-15 19:51:33', '0', '测试调度任务4', 'default', null, 'storage_scan', '1', null, null, null, 1, '2', '0');
