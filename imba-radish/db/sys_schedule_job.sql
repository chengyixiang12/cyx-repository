create table sys_schedule_job
(
    id            bigint       not null comment '主键'
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
    status        int          null comment '任务状态；1：启用；0：暂停',
    job_param     text         null comment '任务所需参数',
    start_time    datetime     null comment '开始时间',
    end_time      datetime     null comment '结束时间',
    job_interval  int          null comment '间隔',
    interval_type char         null comment '间隔类型；0：毫秒；1：秒；2：分钟；3：小时',
    schedule_type char         null comment '调度类型；0：简单调度；1：cron表达式调度',
    remark        varchar(500) null comment '备注',
    job_class     varchar(200) null comment '作业执行类'
)
    comment '调度任务表';

INSERT INTO radish_master.sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type, remark, job_class) VALUES (1984492295652417538, 1984481955388682242, '2025-07-15 19:06:19', 1984481955388682242, '2025-10-29 19:20:31', '0', 'nmap扫描', 'default', null, 'storage_scan', 0, null, '2025-10-26 09:38:46', '2025-10-30 16:00:00', 30, '2', '0', null, 'com.soft.base.quartz.job.NmapScanScheduler');
INSERT INTO radish_master.sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type, remark, job_class) VALUES (1984492295711137793, 1984481955388682242, '2025-11-01 11:27:01', 1984481955388682242, '2025-11-01 11:27:01', '0', '修改主键', 'default', '', 'update_primary_key', 0, '', '2025-11-27 00:00:00', '2025-11-29 00:00:00', 1, '3', '0', '', 'com.soft.base.quartz.job.UpdatePrimaryKey');
