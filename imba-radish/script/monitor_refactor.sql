-- 系统监控表重构（开发期手工执行；Flyway 在 dev/prod 均为关闭状态）
-- 执行后会删除旧的 sys_actuator 表，历史监控数据不再保留
DROP TABLE IF EXISTS `sys_actuator`;

-- radish_master.sys_metric_sample 定义（原始样本，30 秒一条，保留 3 天）

CREATE TABLE `sys_metric_sample` (
                                    `id` bigint NOT NULL COMMENT '主键',
                                    `collect_time` datetime(3) NOT NULL COMMENT '采样时间',
                                    `cpu_usage` double(5,2) DEFAULT NULL COMMENT '宿主机cpu使用率(%)',
                                    `process_cpu_usage` double(5,2) DEFAULT NULL COMMENT '应用进程cpu使用率(%)',
                                    `heap_used` bigint DEFAULT NULL COMMENT '堆已用字节',
                                    `heap_max` bigint DEFAULT NULL COMMENT '堆上限字节',
                                    `nonheap_used` bigint DEFAULT NULL COMMENT '非堆已用字节',
                                    `threads_live` int DEFAULT NULL COMMENT '活跃线程数',
                                    `threads_blocked` int DEFAULT NULL COMMENT '阻塞线程数',
                                    `gc_count` int DEFAULT NULL COMMENT '窗口内gc次数',
                                    `gc_pause_ms` int DEFAULT NULL COMMENT '窗口内gc累计停顿(毫秒)',
                                    `qps` double(8,2) DEFAULT NULL COMMENT '窗口内每秒请求数',
                                    `p95_ms` int DEFAULT NULL COMMENT '窗口内p95延迟(毫秒)',
                                    `error_rate` double(5,2) DEFAULT NULL COMMENT '窗口内5xx占比(%)',
                                    `pool_active` int DEFAULT NULL COMMENT '连接池活跃连接',
                                    `pool_pending` int DEFAULT NULL COMMENT '连接池等待连接',
                                    `pool_max` int DEFAULT NULL COMMENT '连接池上限',
                                    `disk_free` bigint DEFAULT NULL COMMENT '磁盘可用字节',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_metric_sample_time` (`collect_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='监控指标原始样本';

-- radish_master.sys_metric_rollup 定义（聚合数据，5m 保留 30 天，1h 保留 1 年）

CREATE TABLE `sys_metric_rollup` (
                                     `granularity` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '粒度；5m：5分钟；1h：1小时',
                                     `bucket_time` datetime NOT NULL COMMENT '聚合桶起始时间',
                                     `cpu_usage_avg` double(5,2) DEFAULT NULL COMMENT 'cpu使用率均值',
                                     `process_cpu_usage_avg` double(5,2) DEFAULT NULL COMMENT '进程cpu使用率均值',
                                     `heap_used_avg` bigint DEFAULT NULL COMMENT '堆已用均值',
                                     `heap_used_max` bigint DEFAULT NULL COMMENT '堆已用峰值',
                                     `heap_max_avg` bigint DEFAULT NULL COMMENT '堆上限均值',
                                     `nonheap_used_avg` bigint DEFAULT NULL COMMENT '非堆已用均值',
                                     `threads_live_avg` int DEFAULT NULL COMMENT '活跃线程均值',
                                     `threads_live_max` int DEFAULT NULL COMMENT '活跃线程峰值',
                                     `gc_count_sum` int DEFAULT NULL COMMENT 'gc次数合计',
                                     `gc_pause_ms_sum` int DEFAULT NULL COMMENT 'gc停顿合计(毫秒)',
                                     `qps_avg` double(8,2) DEFAULT NULL COMMENT 'qps均值',
                                     `qps_max` double(8,2) DEFAULT NULL COMMENT 'qps峰值',
                                     `p95_ms_max` int DEFAULT NULL COMMENT 'p95延迟峰值(毫秒)',
                                     `error_rate_avg` double(5,2) DEFAULT NULL COMMENT '5xx占比均值',
                                     `error_rate_max` double(5,2) DEFAULT NULL COMMENT '5xx占比峰值',
                                     `pool_active_max` int DEFAULT NULL COMMENT '连接池活跃峰值',
                                     `pool_pending_max` int DEFAULT NULL COMMENT '连接池等待峰值',
                                     `pool_max_avg` int DEFAULT NULL COMMENT '连接池上限均值',
                                     `disk_free_min` bigint DEFAULT NULL COMMENT '磁盘可用最小值',
                                     PRIMARY KEY (`granularity`,`bucket_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='监控指标聚合数据';

-- radish_master.sys_component_status 定义（组件当前状态，原地更新）

CREATE TABLE `sys_component_status` (
                                        `component` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组件标识',
                                        `component_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件名称',
                                        `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态；UP/DOWN/DISABLED/UNKNOWN',
                                        `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态说明',
                                        `since_time` datetime DEFAULT NULL COMMENT '进入当前状态的时间',
                                        `last_check_time` datetime DEFAULT NULL COMMENT '最后探测时间',
                                        PRIMARY KEY (`component`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组件当前状态';

-- radish_master.sys_component_health_log 定义（状态变化时追加）

CREATE TABLE `sys_component_health_log` (
                                            `id` bigint NOT NULL COMMENT '主键',
                                            `component` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件标识',
                                            `component_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件名称',
                                            `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '变化后的状态',
                                            `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态说明',
                                            `change_time` datetime DEFAULT NULL COMMENT '变化时间',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_health_log_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组件状态变更记录';
