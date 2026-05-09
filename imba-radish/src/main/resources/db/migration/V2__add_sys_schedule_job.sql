CREATE TABLE `sys_schedule_record`
(
    `id`             bigint NOT NULL,
    `create_by`      bigint                                  DEFAULT NULL,
    `create_time`    datetime                                DEFAULT NULL,
    `update_by`      bigint                                  DEFAULT NULL,
    `update_time`    datetime                                DEFAULT NULL,
    `del_flag`       char(1) COLLATE utf8mb4_general_ci      DEFAULT NULL,
    `job_id`         bigint                                  DEFAULT NULL COMMENT 'sys_schedule_job主键',
    `log_info`       text COLLATE utf8mb4_general_ci COMMENT '日志明细',
    `job_name`       varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
    `job_type`       varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务类型',
    `job_group`      varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务分组',
    `execution_time` bigint                                  DEFAULT NULL COMMENT '耗时',
    `start_time`     datetime                                DEFAULT NULL COMMENT '开始时间',
    `end_time`       datetime                                DEFAULT NULL COMMENT '结束时间',
    `job_param`      text COLLATE utf8mb4_general_ci COMMENT '任务参数',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务执行记录';