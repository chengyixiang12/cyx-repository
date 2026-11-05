-- radish_master.sys_actuator 定义

CREATE TABLE `sys_actuator` (
                                `id` bigint NOT NULL,
                                `create_by` bigint DEFAULT NULL,
                                `create_time` datetime DEFAULT NULL,
                                `update_by` bigint DEFAULT NULL,
                                `update_time` datetime DEFAULT NULL,
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                `cpu_usage` double(5,2) DEFAULT NULL COMMENT 'cpu使用率',
                                `cpu_count` tinyint unsigned DEFAULT NULL COMMENT 'cpu核心数',
                                `memory_used` bigint DEFAULT NULL COMMENT '内存使用量',
                                `memory_max` bigint DEFAULT NULL COMMENT '最大内存容量',
                                `uptime` bigint DEFAULT NULL COMMENT '系统运行时长',
                                `disk_free` bigint DEFAULT NULL COMMENT '磁盘空闲空间',
                                `disk_total` bigint DEFAULT NULL COMMENT '磁盘总量',
                                `health_db` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数据库状态；up：正常；down：异常',
                                `health_disk_space` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '磁盘状态；up：正常；down：异常',
                                `health_mail` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱状态；up：正常；down：异常',
                                `health_ping` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统响应状态；up：正常；down：异常',
                                `health_rabbit` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'rabbitmq状态；up：正常；down：异常',
                                `health_redis` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'redis状态；up：正常；down：异常',
                                `health_ssl` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ssl证书状态；up：正常；down：异常',
                                `health` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '健康状态；up：正常；down：异常',
                                `memory_heap_max` bigint DEFAULT NULL COMMENT '堆内存最大容量',
                                `memory_heap_used` bigint DEFAULT NULL COMMENT '堆内存使用量',
                                `memory_noheap_max` bigint DEFAULT NULL COMMENT '非堆内存最大容量',
                                `memory_noheap_used` bigint DEFAULT NULL COMMENT '非堆内存使用量',
                                `memory_g1_eden_used` bigint DEFAULT NULL COMMENT 'G1的eden区内存使用量',
                                `memory_g1_survivor_used` bigint DEFAULT NULL COMMENT 'G1的survivor区内存使用量',
                                `memory_g1_old_used` bigint DEFAULT NULL COMMENT 'G1的老年代内存使用量',
                                `memory_code_cache_max` bigint DEFAULT NULL COMMENT '代码缓存最大内存容量',
                                `memory_code_cache_used` bigint DEFAULT NULL COMMENT '代码缓存内存使用量',
                                `memory_metaspace_used` bigint DEFAULT NULL COMMENT '元空间内存使用量',
                                `memory_compress_class_space_used` bigint DEFAULT NULL COMMENT '压缩类空间内存使用容量',
                                `memory_compress_class_space_max` bigint DEFAULT NULL COMMENT '压缩类空间最大内存容量',
                                `memory_metaspace_max` bigint DEFAULT NULL COMMENT '元空间最大内存容量',
                                PRIMARY KEY (`id`),
                                KEY `sys_actuator_create_time_IDX` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统监控表';


-- radish_master.sys_announcement 定义

CREATE TABLE `sys_announcement` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `sort_order` bigint DEFAULT NULL COMMENT '排序',
                                    `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                    `del_flag` tinyint DEFAULT NULL COMMENT '逻辑删除',
                                    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公告标题',
                                    `publish_time` datetime DEFAULT NULL COMMENT '公布时间',
                                    `context` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公告内容',
                                    `status` tinyint DEFAULT NULL COMMENT '状态；1：发布；0：草稿',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';


-- radish_master.sys_dept 定义

CREATE TABLE `sys_dept` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `sort_order` int DEFAULT NULL COMMENT '排序字段',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint DEFAULT NULL COMMENT '逻辑删除',
                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门编码',
                            `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
                            `parent_id` bigint DEFAULT NULL COMMENT '父级id',
                            `level` tinyint DEFAULT NULL COMMENT '层级',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `idx_sys_dept_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';


-- radish_master.sys_dialogue 定义

CREATE TABLE `sys_dialogue` (
                                `id` bigint NOT NULL COMMENT '主键',
                                `sort_order` int DEFAULT NULL COMMENT '排序',
                                `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                `del_flag` tinyint DEFAULT NULL COMMENT '逻辑删除',
                                `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
                                `top_flag` tinyint DEFAULT NULL COMMENT '置顶标识；1：置顶；0：不置顶',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能对话表';


-- radish_master.sys_dialogue_details 定义

CREATE TABLE `sys_dialogue_details` (
                                        `id` bigint NOT NULL COMMENT '主键',
                                        `parent_id` bigint DEFAULT NULL COMMENT '表sys_dialogue_history的id',
                                        `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                        `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                        `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                        `tag` tinyint DEFAULT NULL COMMENT '标识；1：用户；0：AI',
                                        `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '对话内容',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能对话详情表';


-- radish_master.sys_dict_data 定义

CREATE TABLE `sys_dict_data` (
                                 `id` bigint NOT NULL COMMENT '主键',
                                 `dict_type_id` bigint NOT NULL COMMENT 'sys_dict_type的主键',
                                 `sort_order` int DEFAULT NULL COMMENT '排序',
                                 `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                 `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签',
                                 `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '键值',
                                 `css_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
                                 `list_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
                                 `is_default` tinyint DEFAULT NULL COMMENT '是否默认；1：是 0：否',
                                 `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：停用',
                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `sys_dict_data_dict_type_id_IDX` (`dict_type_id`,`value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';


-- radish_master.sys_dict_type 定义

CREATE TABLE `sys_dict_type` (
                                 `id` bigint NOT NULL COMMENT '主键',
                                 `sort_order` int DEFAULT NULL COMMENT '排序',
                                 `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                 `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典名称',
                                 `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型',
                                 `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：停用',
                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `sys_dict_type_dict_type_IDX` (`dict_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';


-- radish_master.sys_file 定义

CREATE TABLE `sys_file` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                            `original_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '源文件名',
                            `file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储文件名',
                            `location` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储地址；1：minio；2：磁盘',
                            `bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '桶名',
                            `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
                            `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件后缀；示例：.txt、.jpg',
                            `file_size` bigint DEFAULT NULL COMMENT '文件大小；单位：B',
                            `file_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件hash',
                            PRIMARY KEY (`id`),
                            KEY `idx_sys_file_hash` (`file_hash`) COMMENT '文件hash普通索引',
                            KEY `idx_sys_file_key` (`file_key`) COMMENT '存储文件名普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件表';


-- radish_master.sys_log 定义

CREATE TABLE `sys_log` (
                           `id` bigint NOT NULL COMMENT '主键',
                           `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_by` bigint DEFAULT NULL COMMENT '修改人',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                           `log_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志级别',
                           `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求ip',
                           `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问路径',
                           `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求方法',
                           `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数',
                           `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '响应结果',
                           `operation_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '操作描述',
                           `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志来源',
                           `execution_time` bigint DEFAULT NULL COMMENT '耗时',
                           `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模块名称',
                           `status_code` int DEFAULT NULL COMMENT '状态码',
                           `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常信息',
                           `os_browser_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作系统/浏览器信息',
                           `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志类型',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日志表';


-- radish_master.sys_menu 定义

CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL COMMENT '菜单唯一标识符，主键',
                            `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID，顶级菜单的父ID通常为0',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                            `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
                            `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '前端路由路径',
                            `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '前端组件路径',
                            `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单图标（如字体图标类名）',
                            `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
                            `sort_order` int DEFAULT NULL COMMENT '排序号，数字越小，排序越靠前',
                            `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '菜单状态：0-禁用，1-启用',
                            `visible` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '是否显示：0-隐藏，1-显示',
                            `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注信息',
                            PRIMARY KEY (`id`),
                            KEY `idx_sys_menu_parent_id` (`parent_id`) COMMENT '父级菜单普通索引',
                            KEY `idx_sys_menu_type` (`type`) COMMENT '菜单类型普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单信息表';


-- radish_master.sys_permission 定义

CREATE TABLE `sys_permission` (
                                  `id` bigint NOT NULL COMMENT '主键',
                                  `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限名称',
                                  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
                                  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型；1：菜单；2：按钮',
                                  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `idx_sys_permission_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';


-- radish_master.sys_role 定义

CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `sort_order` int DEFAULT NULL COMMENT '排序字段',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                            `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
                            `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色名称',
                            `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                            `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
                            `is_default` tinyint DEFAULT NULL COMMENT '是否标记为系统默认角色；1：是；0：不是',
                            `fix_role` tinyint DEFAULT NULL COMMENT '是否为固定角色（固定角色无法被删除）；1：是；0：不是',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `idx_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';


-- radish_master.sys_role_menu 定义

CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL COMMENT '角色id',
                                 `menu_id` bigint NOT NULL COMMENT '菜单id',
                                 UNIQUE KEY `uk_sys_role_menu` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单表';


-- radish_master.sys_role_permission 定义

CREATE TABLE `sys_role_permission` (
                                       `role_id` bigint NOT NULL COMMENT '角色主键',
                                       `permission_id` bigint NOT NULL COMMENT '权限主键',
                                       UNIQUE KEY `uk_sys_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限表';


-- radish_master.sys_schedule_job 定义

CREATE TABLE `sys_schedule_job` (
                                    `id` bigint NOT NULL COMMENT '主键',
                                    `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                    `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                    `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
                                    `job_group` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务组',
                                    `cron` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cron表达式',
                                    `job_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务类型',
                                    `status` int DEFAULT NULL COMMENT '任务状态；1：启用；0：暂停',
                                    `job_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '任务所需参数',
                                    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                    `job_interval` int DEFAULT NULL COMMENT '间隔',
                                    `interval_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '间隔类型；0：毫秒；1：秒；2：分钟；3：小时',
                                    `schedule_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '调度类型；0：简单调度；1：cron表达式调度',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                    `job_class` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '作业执行类',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调度任务表';


-- radish_master.sys_schedule_record 定义

CREATE TABLE `sys_schedule_record` (
                                       `id` bigint NOT NULL,
                                       `create_by` bigint DEFAULT NULL,
                                       `create_time` datetime DEFAULT NULL,
                                       `update_by` bigint DEFAULT NULL,
                                       `update_time` datetime DEFAULT NULL,
                                       `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                       `job_id` bigint DEFAULT NULL COMMENT 'sys_schedule_job主键',
                                       `log_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '日志明细',
                                       `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
                                       `job_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务类型',
                                       `job_group` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务分组',
                                       `execution_time` bigint DEFAULT NULL COMMENT '耗时',
                                       `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                       `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                       `job_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '任务参数',
                                       `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行状态；0：未执行；1：执行中；2：执行完成；3：执行失败',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务执行记录';


-- radish_master.sys_secret_key 定义

CREATE TABLE `sys_secret_key` (
                                  `id` bigint NOT NULL COMMENT '主键',
                                  `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                  `public_key` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公钥',
                                  `private_key` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '私钥',
                                  `type` tinyint DEFAULT NULL COMMENT '用途类型；登录、数据传输等',
                                  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                                  `secret_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密钥类型；AES；RSA',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='密钥';


-- radish_master.sys_user 定义

CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL,
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：不存在',
                            `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
                            `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                            `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
                            `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
                            `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电话',
                            `enabled` tinyint(1) DEFAULT '1' COMMENT '用户是否被启用；1：启用；0：禁用',
                            `account_non_locked` tinyint(1) DEFAULT '1' COMMENT '账户是否被锁定；1：正常；0：锁定',
                            `credentials_non_expired` tinyint(1) DEFAULT '1' COMMENT '凭证是否过期；1：正常；0：过期',
                            `account_non_expired` tinyint(1) DEFAULT '1' COMMENT '账户是否过期；1：正常；0：过期',
                            `dept_id` bigint DEFAULT NULL COMMENT '部门id',
                            `avatar` bigint DEFAULT NULL COMMENT '用户头像',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `sys_user_email_IDX` (`email`) USING BTREE,
                            UNIQUE KEY `sys_user_username_IDX` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';


-- radish_master.sys_user_role 定义

CREATE TABLE `sys_user_role` (
                                 `role_id` bigint NOT NULL COMMENT '角色主键',
                                 `user_id` bigint NOT NULL COMMENT '用户主键',
                                 UNIQUE KEY `uk_role_user` (`role_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色表';

INSERT INTO sys_dept
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`)
VALUES(1984483212773847041, 1, 1984481955388682242, '2024-10-26 10:58:36', 1984481955388682242, '2025-05-16 23:49:42', 0, 'IT', '互联网公司', NULL, 1);
INSERT INTO sys_dept
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`)
VALUES(1984567407013588993, 2, 1984481955388682242, '2025-11-01 18:25:16', 1984481955388682242, '2025-11-01 19:14:24', 0, 'TD', '技术研发部', 1984483212773847041, 2);
INSERT INTO sys_dept
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`)
VALUES(1984583320156778498, 3, 1984481955388682242, '2025-11-01 19:28:30', 1984481955388682242, '2025-11-01 19:28:30', 0, 'BD', '后端研发部', 1984567407013588993, 3);
INSERT INTO sys_dept
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`)
VALUES(1984583475144699905, 4, 1984481955388682242, '2025-11-01 19:29:07', 1984481955388682242, '2025-11-01 19:29:07', 0, 'FD', '前端研发部', 1984567407013588993, 3);
INSERT INTO sys_dept
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`)
VALUES(2055217137777696769, NULL, 1984481955388682242, '2026-05-15 17:22:04', 1984481955388682242, '2026-05-15 17:22:04', 0, 'java', 'java研发部', 1984583320156778498, 4);

INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867211083778, 1984484774384603137, 1, 1984481955388682242, '2025-04-24 15:03:44', 1984481955388682242, '2025-04-24 17:25:01', '0', '男', '1', '', '', 1, 1, '1');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867253026818, 1984484774384603137, 2, 1984481955388682242, '2025-04-24 17:12:48', 1984481955388682242, '2025-04-24 17:12:48', '0', '女', '0', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867253026819, 1984484774405574658, 1, 1984481955388682242, '2025-04-25 22:48:59', 1984481955388682242, '2025-04-25 22:48:59', '0', '启用', '1', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867253026820, 1984484774405574658, 2, 1984481955388682242, '2025-04-25 22:49:14', 1984481955388682242, '2025-04-25 22:49:14', '0', '禁用', '0', '', '', 1, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867253026821, 1984484774405574659, 1, 1984481955388682242, '2025-07-14 20:03:57', 1984481955388682242, '2025-07-14 20:03:57', '0', '存储文件扫描', 'storage_scan', '', '', 1, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867282386946, 1984484774405574660, 1, 1984481955388682242, '2025-09-06 22:16:42', 1984481955388682242, '2025-11-01 19:41:54', '0', 'minio存储', '1', '', '', 1, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867282386947, 1984484774405574660, 2, 1984481955388682242, '2025-09-06 22:16:58', 1984481955388682242, '2025-09-06 22:16:58', '0', '本地磁盘', '2', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867282386948, 1984484774405574661, NULL, 1984481955388682242, '2025-10-26 18:24:58', 1984481955388682242, '2025-10-26 18:24:58', '0', '简单调度', '0', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867282386949, 1984484774405574661, NULL, 1984481955388682242, '2025-10-26 18:25:11', 1984481955388682242, '2025-10-26 18:25:11', '0', 'cron调度', '1', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867311747074, 1984484774405574662, NULL, 1984481955388682242, '2025-10-26 18:28:43', 1984481955388682242, '2025-10-26 18:28:43', '0', '存储文件扫描', 'storage_scan', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984483867311747075, 1984484774405574662, NULL, 1984481955388682242, '2025-11-01 11:24:35', 1984481955388682242, '2025-11-01 11:24:35', '0', '修改主键', 'update_primary_key', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1984610087038955521, 1984484774405574662, NULL, 1984481955388682242, '2025-11-01 21:14:52', 1984481955388682242, '2025-11-01 21:14:52', '0', '清理文件', 'clear_deleted_file', '', '', 0, 1, '清理已删除的文件');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1985710533040242689, 1985710409769648130, NULL, 1984481955388682242, '2025-11-04 22:07:38', 1984481955388682242, '2025-11-04 22:07:38', '0', '菜单', '1', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(1985710563537027073, 1985710409769648130, NULL, 1984481955388682242, '2025-11-04 22:07:46', 1984481955388682242, '2025-11-04 22:07:46', '0', '按钮', '2', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269551443804161, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:33:38', 1984481955388682242, '2026-05-01 22:19:07', '0', 'image/jpeg', 'jpg', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269596801007617, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:33:48', 1984481955388682242, '2025-12-17 20:33:48', '0', 'image/jpeg', 'jpeg', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269648034430977, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:01', 1984481955388682242, '2026-05-01 22:19:13', '0', 'image/png', 'png', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269696017268737, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:12', 1984481955388682242, '2025-12-17 20:34:12', '0', 'image/gif', 'gif', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269744687972353, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:24', 1984481955388682242, '2025-12-17 20:34:24', '0', 'image/webp', 'webp', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269795946561538, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:36', 1984481955388682242, '2025-12-17 20:34:36', '0', 'image/svg+xml', 'svg', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269863076397058, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:52', 1984481955388682242, '2025-12-17 20:34:52', '0', 'application/pdf', 'pdf', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269906969788417, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:35:02', 1984481955388682242, '2025-12-17 20:35:02', '0', 'text/plain', 'txt', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269956429021186, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:35:14', 1984481955388682242, '2025-12-17 20:35:14', '0', 'text/html', 'html', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001269998841823234, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:35:24', 1984481955388682242, '2025-12-17 20:35:24', '0', 'application/json', 'json', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001270170804092930, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:05', 1984481955388682242, '2025-12-17 20:36:05', '0', 'text/csv', 'csv', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001270219969724417, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:17', 1984481955388682242, '2025-12-17 20:36:17', '0', 'audio/mpeg', 'mp3', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001270257131257857, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:26', 1984481955388682242, '2025-12-17 20:36:26', '0', 'video/mp4', 'mp4', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2001270296146673666, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:35', 1984481955388682242, '2025-12-17 20:36:35', '0', 'video/webm', 'webm', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2079817441663348738, 2079817242924642305, NULL, 1984481955388682242, '2026-07-22 14:34:54', 1984481955388682242, '2026-07-22 14:34:54', '0', 'RSA', 'rsa', '', '', 0, 1, '');
INSERT INTO sys_dict_data
(id, dict_type_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark)
VALUES(2079817468435591169, 2079817242924642305, NULL, 1984481955388682242, '2026-07-22 14:35:00', 1984481955388682242, '2026-07-22 14:35:00', '0', 'AES', 'aes', '', '', 0, 1, '');

INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(1984484774384603137, 2, 1984481955388682242, '2025-04-23 22:52:19', 1984481955388682242, '2025-04-23 23:04:57', '0', '性别', 'sex', 1, '表示男女的性别');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(1984484774405574658, 2, 1984481955388682242, '2025-04-25 22:48:38', 1984481955388682242, '2025-04-25 22:50:17', '0', '状态', 'status', 1, '');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(1984484774405574660, 4, 1984481955388682242, '2025-09-06 22:16:11', 1984481955388682242, '2026-07-22 14:43:32', '0', '文件存储位置', 'file_storage_location', 1, '');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(1984484774405574661, 3, 1984481955388682242, '2025-10-26 18:24:18', 1984481955388682242, '2025-11-01 19:47:30', '0', '调度类型', 'schedule_type', 1, '');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(1984484774405574662, 2, 1984481955388682242, '2025-10-26 18:26:42', 1984481955388682242, '2025-11-01 19:47:35', '0', '作业类型', 'job_type', 1, '');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(1985710409769648130, 1, 1984481955388682242, '2025-11-04 22:07:09', 1984481955388682242, '2025-12-20 19:56:06', '0', '权限类型', 'premission_type', 1, '');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(2001266854774767618, 2, 1984481955388682242, '2025-12-17 20:22:55', 1984481955388682242, '2025-12-20 19:56:02', '0', '在线预览文件类型', 'inline_file_type', 1, '');
INSERT INTO sys_dict_type
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark)
VALUES(2079817242924642305, NULL, 1984481955388682242, '2026-07-22 14:34:07', 1984481955388682242, '2026-07-22 14:34:07', '0', '密钥类型', 'secret_type', 1, '');

INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400598355969, NULL, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2026-04-29 08:32:47', '0', '系统管理', '/system', NULL, 'setting', '0', 2, '1', '1', '系统管理顶级菜单');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400631910402, NULL, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2026-04-29 08:25:43', '0', '内容管理', '/content', NULL, 'folder', '0', 1, '0', '0', '内容管理顶级菜单');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400631910403, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '用户管理', '/user', 'system/User', 'user', '1', 1, '1', '1', '管理用户信息');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400631910404, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '角色管理', '/role', 'system/Role', 'avatar', '1', 2, '1', '1', '管理角色信息');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400657076226, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '菜单管理', '/menu', 'system/Menu', 'menu', '1', 3, '1', '1', '管理菜单信息');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400657076227, 1984485400598355969, 1984481955388682242, '2025-04-22 16:48:24', 1984481955388682242, '2025-11-01 05:00:56', '0', '字典管理', '/dict', 'system/DictType', 'Collection', '1', 2, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400686436353, 1984485400598355969, 1984481955388682242, '2025-04-23 23:06:59', 1984481955388682242, '2025-11-01 05:00:56', '0', '字典数据', '/dictData', 'system/DictData', 'Document', '1', 2, '1', '0', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400686436354, 1984485400598355969, 1984481955388682242, '2025-04-25 22:41:19', 1984481955388682242, '2025-11-01 05:00:56', '0', '部门管理', '/dept', 'system/Dept', 'OfficeBuilding', '1', 4, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400686436355, 1984485400598355969, 1984481955388682242, '2025-04-25 22:42:14', 1984481955388682242, '2025-11-01 05:00:56', '0', '日志管理', '/log', 'system/Log', 'Memo', '1', 5, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400686436356, 1984485400598355969, 1984481955388682242, '2025-04-27 16:47:48', 1984481955388682242, '2025-11-01 15:29:03', '0', '日志详情', '/logDetail', 'system/LogDetail', 'Document', '1', 7, '1', '0', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400715796482, NULL, 1984481955388682242, '2025-05-28 16:10:09', 1984481955388682242, '2026-04-29 08:32:47', '0', '智能问答', '/ai', '', 'Tools', '0', 0, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400715796483, 1984485400715796482, 1984481955388682242, '2025-05-28 16:18:11', 1984481955388682242, '2026-04-30 04:45:50', '0', '新对话', '/dialog', 'chat/Chat', 'ChatDotSquare', '1', 0, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400715796484, 1984485400715796482, 1984481955388682242, '2025-05-29 15:35:28', 1984481955388682242, '2025-11-01 05:00:56', '0', '历史对话', '/answerHistory', 'chat/AnswerHistory', 'Comment', '1', 2, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400715796485, 1984485400598355969, 1984481955388682242, '2025-08-17 19:36:03', 1984481955388682242, '2026-05-02 22:06:48', '0', '定时任务', '/quartz', 'system/Quartz', 'Clock', '1', 8, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400715796486, 1984485400598355969, 1984481955388682242, '2025-09-06 18:34:18', 1984481955388682242, '2025-11-01 05:00:56', '0', '文件管理', '/file', 'system/File', 'Files', '1', 3, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984485400715796487, 1984485400598355969, 1984481955388682242, '2025-10-22 13:58:44', 1984481955388682242, '2025-11-01 05:00:56', '0', 'cron表达式生成器', '/cron', 'system/component/CronGenerate', 'AlarmClock', '1', 20, '1', '0', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984595671614558209, NULL, 1984481955388682242, '2025-11-01 20:17:35', 1984481955388682242, '2026-05-23 11:19:17', '1', '网盘管理', '/onlineDisk', '', 'Platform', '0', 0, '0', '0', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984596219776536578, 1984595671614558209, 1984481955388682242, '2025-11-01 20:19:45', 1984481955388682242, '2026-05-23 11:17:12', '1', '我的文件', '/myFile', 'onlinedisk/File', 'Files', '1', 0, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1984999219198464002, 1984485400598355969, 1984481955388682242, '2025-11-02 23:01:08', 1984481955388682242, '2025-11-02 23:01:08', '0', '权限管理', '/permisson', 'system/Permission', 'Key', '1', 4, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(1988216780108926978, 1984485400598355969, 1984481955388682242, '2025-11-11 20:06:34', 1984481955388682242, '2025-11-16 13:42:40', '0', '系统监控', '/actuator', 'system/Actuator', 'Odometer', '1', 6, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2047558521822138370, 1984485400598355969, 1984481955388682242, '2026-04-24 14:09:28', 1984481955388682242, '2026-04-24 14:14:52', '0', '定时任务运行记录', '/quartzRecord', 'system/QuartzRecord', 'Memo', '1', 15, '1', '0', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2074337820700315650, NULL, 1984481955388682242, '2026-07-07 11:40:50', 1984481955388682242, '2026-11-05 05:06:16', '0', '审批管理', '/approve', '', 'UserFilled', '0', NULL, '0', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2074338019053146113, 2074337820700315650, 1984481955388682242, '2026-07-07 11:41:38', 1984481955388682242, '2026-07-07 11:41:38', '0', '请假管理', '/leave', 'system/LeaveApproval', '', '1', NULL, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2079814109544673282, 1984485400598355969, 1984481955388682242, '2026-07-22 14:21:39', 1984481955388682242, '2026-07-22 05:20:09', '0', '密钥管理', '/secretKey', 'system/SecretKey', 'Key', '1', 7, '1', '1', '');

INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415355244546, 1984481955388682242, '2024-11-20 20:06:35', 1984481955388682242, '2024-11-20 20:06:41', '0', '添加菜单', 'sys_menu_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415409770497, 1984481955388682242, '2024-11-20 20:51:39', 1984481955388682242, '2024-11-20 20:51:39', '0', '添加角色', 'sys_role_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415409770498, 1984481955388682242, '2024-11-20 20:53:17', 1984481955388682242, '2024-11-20 20:53:17', '0', '添加权限', 'sys_pms_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415409770499, 1984481955388682242, '2024-11-20 20:53:44', 1984481955388682242, '2024-11-20 20:53:44', '0', '编辑角色', 'sys_role_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415409770500, 1984481955388682242, '2024-11-20 20:55:08', 1984481955388682242, '2024-11-20 20:55:08', '0', '编辑菜单', 'sys_menu_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415409770501, 1984481955388682242, '2024-11-20 20:55:18', 1984481955388682242, '2024-11-20 20:55:18', '0', '删除菜单', 'sys_menu_del', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415409770502, 1984481955388682242, '2024-11-20 21:39:14', 1984481955388682242, '2024-11-20 21:39:14', '0', '添加部门', 'sys_dept_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130625, 1984481955388682242, '2024-11-20 21:39:26', 1984481955388682242, '2024-11-20 21:39:26', '0', '编辑部门', 'sys_dept_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130626, 1984481955388682242, '2024-11-20 21:39:52', 1984481955388682242, '2024-11-20 21:39:52', '0', '删除部门', 'sys_dept_del', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130627, 1984481955388682242, '2024-11-20 21:42:27', 1984481955388682242, '2024-11-20 21:42:27', '0', '角色赋予菜单', 'sys_role_set_menu', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130628, 1984481955388682242, '2024-11-20 21:42:38', 1984481955388682242, '2024-11-20 21:42:38', '0', '角色赋予权限', 'sys_role_set_per', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130629, 1984481955388682242, '2024-11-20 21:47:29', 1984481955388682242, '2024-11-20 21:47:29', '0', '设置默认角色', 'sys_role_set_def', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130630, 1984481955388682242, '2024-11-20 21:48:11', 1984481955388682242, '2024-11-20 21:48:11', '0', '禁用角色', 'sys_role_fbn', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415439130631, 1984481955388682242, '2024-11-20 21:48:37', 1984481955388682242, '2024-11-20 21:48:37', '0', '启用角色', 'sys_role_enable', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490754, 1984481955388682242, '2024-11-20 21:51:21', 1984481955388682242, '2024-11-20 21:51:21', '0', '添加字典数据', 'sys_dict_data_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490755, 1984481955388682242, '2024-11-20 21:51:46', 1984481955388682242, '2024-11-20 21:51:46', '0', '编辑字典数据', 'sys_dict_data_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490756, 1984481955388682242, '2024-11-20 21:52:10', 1984481955388682242, '2024-11-20 21:52:10', '0', '删除字典数据', 'sys_dict_data_del', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490757, 1984481955388682242, '2024-11-20 21:52:51', 1984481955388682242, '2024-11-20 21:52:51', '0', '添加字典类型', 'sys_dict_type_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490758, 1984481955388682242, '2024-11-20 21:53:21', 1984481955388682242, '2024-11-20 21:53:21', '0', '编辑字典类型', 'sys_dict_type_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490759, 1984481955388682242, '2024-11-20 21:53:44', 1984481955388682242, '2024-11-20 21:53:44', '0', '删除字典类型', 'sys_dict_type_del', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490760, 1984481955388682242, '2024-11-20 21:54:18', 1984481955388682242, '2024-11-20 21:54:18', '0', '删除文件', 'sys_file_del', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490761, 1984481955388682242, '2024-11-20 21:57:41', 1984481955388682242, '2024-11-20 21:57:41', '0', '删除角色', 'sys_role_del', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415468490762, 1984481955388682242, '2024-11-20 21:58:58', 1984481955388682242, '2024-11-20 21:58:58', '0', '重置密码', 'sys_user_reset', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850881, 1984481955388682242, '2024-11-20 21:59:34', 1984481955388682242, '2024-11-20 21:59:34', '0', '添加用户', 'sys_user_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850882, 1984481955388682242, '2024-11-20 22:01:40', 1984481955388682242, '2024-11-20 22:01:40', '0', '编辑用户', 'sys_user_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850883, 1984481955388682242, '2024-11-26 19:23:51', 1984481955388682242, '2024-11-26 19:23:51', '0', '生成密钥', 'sys_secret_key_generate', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850884, 1984481955388682242, '2025-03-28 11:00:40', 1984481955388682242, '2025-03-28 11:00:40', '0', '删除用户', 'sys_user_delete', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850885, 1984481955388682242, '2025-04-17 18:30:21', 1984481955388682242, '2025-04-17 18:30:21', '0', '锁定用户', 'sys_user_lock', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850886, 1984481955388682242, '2025-04-17 18:31:31', 1984481955388682242, '2025-04-17 18:31:31', '0', '解锁用户', 'sys_user_unlock', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850887, 1984481955388682242, '2025-07-13 20:52:28', 1984481955388682242, '2025-07-13 20:52:28', '0', '启用用户', 'sys_user_enable', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(1984481415497850888, 1984481955388682242, '2025-07-13 20:54:00', 1984481955388682242, '2025-07-13 20:54:00', '0', '禁用用户', 'sys_user_forbidden', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2053647730383265793, 1984481955388682242, '2026-05-11 09:25:49', 1984481955388682242, '2026-05-11 09:25:49', '0', '获取用户列表', 'sys_user_page', '1', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2053648597316644866, 1984481955388682242, '2026-05-11 09:29:15', 1984481955388682242, '2026-05-11 09:29:15', '0', '重置用户名', 'sys_user_reset_username', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2053651384364544002, 1984481955388682242, '2026-05-11 09:40:20', 1984481955388682242, '2026-05-11 09:40:20', '0', '设置固定角色', 'sys_role_set_fix', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2053651719212609538, 1984481955388682242, '2026-05-11 09:41:40', 1984481955388682242, '2026-05-11 09:41:40', '0', '取消固定角色', 'sys_role_set_fix_cancel', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2053654108875370497, 1984481955388682242, '2026-05-11 09:51:09', 1984481955388682242, '2026-05-11 09:51:09', '0', '获取用户详情', 'sys_user_get_user_info', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2067454601849749506, 1984481955388682242, '2026-06-18 11:49:23', 1984481955388682242, '2026-06-18 11:49:23', '0', '编辑权限', 'sys_pms_edit', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2067454733303431169, 1984481955388682242, '2026-06-18 11:49:55', 1984481955388682242, '2026-06-18 11:49:55', '0', '删除权限', 'sys_pms_delete', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2067454867705708545, 1984481955388682242, '2026-06-18 11:50:27', 1984481955388682242, '2026-06-18 11:50:27', '0', '启用权限', 'enable_permission', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2067454943610028034, 1984481955388682242, '2026-06-18 11:50:45', 1984481955388682242, '2026-06-18 11:50:45', '0', '禁用权限', 'forbidden_permission', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2067483329837416449, 1984481955388682242, '2026-06-18 13:43:32', 1984481955388682242, '2026-06-18 13:43:32', '0', '获取角色详情', 'sys_role_get_role', '1', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2067483406324744193, 1984481955388682242, '2026-06-18 13:43:51', 1984481955388682242, '2026-06-18 13:43:51', '0', '获取角色列表', 'sys_role_get_roles', '1', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2118183186434101249, 1984481955388682242, '2026-11-05 11:26:40', 1984481955388682242, '2026-11-05 11:26:40', '0', '强制下线', 'sys_user_force_offline', '2', '1', '');

INSERT INTO sys_role
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role)
VALUES(1984489247068725249, 1, 1984481955388682242, '2024-10-25 15:07:55', 1984481955388682242, '2026-05-09 16:57:09', '0', 'ROLE_ADMIN', '管理员', '拥有所有权限', 1, 0, 1);
INSERT INTO sys_role
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role)
VALUES(1984489247123251201, 2, 1984481955388682242, '2024-10-25 15:08:15', 1984481955388682242, '2024-10-25 15:08:07', '0', 'ROLE_NORMAL', '普通用户', '没有任何权限', 1, 1, 1);
INSERT INTO sys_role
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role)
VALUES(2074338240218796034, NULL, 1984481955388682242, '2026-07-07 11:42:30', 1984481955388682242, '2026-07-07 11:42:30', '0', 'ROLE_DEPT_MANAGER', '部门审批', '', 1, 0, 0);
INSERT INTO sys_role
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role)
VALUES(2074338379620683778, NULL, 1984481955388682242, '2026-07-07 11:43:04', 1984481955388682242, '2026-07-07 11:43:04', '0', 'ROLE_GENERAL_MANAGER', '总经理审批', '', 1, 0, 0);
INSERT INTO sys_role
(id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role)
VALUES(2074338426814992386, NULL, 1984481955388682242, '2026-07-07 11:43:15', 1984481955388682242, '2026-07-07 11:43:15', '0', 'ROLE_HR', '人事', '', 1, 0, 0);

INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400598355969);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400631910403);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400631910404);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400657076226);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400657076227);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400686436353);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400686436354);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400686436355);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400686436356);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400715796482);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400715796483);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400715796484);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400715796485);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400715796486);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984485400715796487);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1984999219198464002);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 1988216780108926978);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 2047558521822138370);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 2074337820700315650);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 2074338019053146113);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 2079814109544673282);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247123251201, 1984485400715796482);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247123251201, 1984485400715796483);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247123251201, 1984485400715796484);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247123251201, 2074337820700315650);
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247123251201, 2074338019053146113);

INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415355244546);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415409770497);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415409770498);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415409770499);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415409770500);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415409770501);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415409770502);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130625);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130626);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130627);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130628);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130629);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130630);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415439130631);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490754);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490755);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490756);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490757);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490758);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490759);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490760);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490761);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415468490762);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850881);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850882);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850883);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850884);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850885);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850886);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850887);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 1984481415497850888);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2053647730383265793);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2053648597316644866);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2053651384364544002);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2053651719212609538);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2053654108875370497);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2067454601849749506);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2067454733303431169);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2067454867705708545);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2067454943610028034);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2067483329837416449);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2067483406324744193);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2118183186434101249);

INSERT INTO sys_secret_key
(id, create_by, create_time, update_by, update_time, del_flag, public_key, private_key, `type`, description, secret_type)
VALUES(1984491955066576897, 1984481955388682242, '2024-11-26 19:52:27', 1984481955388682242, '2024-11-26 11:53:23', '0', 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5zuUH2yI4yOlaEUJ6PBk9mme52FmtSvchu+8MNqd3ALJXPauXTAdmzGMqCur8OscE5XQR+UEBtibDM6NtvBlxt89DHpgHZPQpZrC9HtSDIsFk/ck2Bs5oM/clPwYXKumRTkk/SgDx+7oq55fezV6XfAc7rMoKKXyF/g591cKmFlDdmMdHEh6Fbz9kvFQ0kfdpikXuaoPpC3O32I+xXx77uIAO2W+vhyYRsuy6TozFs3Ba/nveg27gVZcvlWAWPXZY/cG6ggeuuTBSd3kR9Tf0TfHu1xIr8aOOFu82gY5+B4h9eWhdjhmboBZvIBHwlMgiBvEkkr+qWunltxeicNe1wIDAQAB', 'MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDnO5QfbIjjI6VoRQno8GT2aZ7nYWa1K9yG77ww2p3cAslc9q5dMB2bMYyoK6vw6xwTldBH5QQG2JsMzo228GXG3z0MemAdk9ClmsL0e1IMiwWT9yTYGzmgz9yU/Bhcq6ZFOST9KAPH7uirnl97NXpd8BzusygopfIX+Dn3VwqYWUN2Yx0cSHoVvP2S8VDSR92mKRe5qg+kLc7fYj7FfHvu4gA7Zb6+HJhGy7LpOjMWzcFr+e96DbuBVly+VYBY9dlj9wbqCB665MFJ3eRH1N/RN8e7XEivxo44W7zaBjn4HiH15aF2OGZugFm8gEfCUyCIG8SSSv6pa6eW3F6Jw17XAgMBAAECggEAMFD0rHRDTiLepyD15ySEFDERsQtbKLQXimKBkju8DILQjIpG+NXa+diqqWEmtlqKLVV6hetGoh+UlmJ6niUxPxLacMcJWmTOjiv+XJOAG3rZGYfkvPtDWWTVlJPwizyaq5A7OGKqF5bGK0YWcWpFPWe0w/PPil7SbUvC4PnhDuACTiQmmW4jZBZ9X6IjXPx95nZ2TRCRHdQ9vFMBz5oY55+gV5efrgBibkg8MKzoOnmjcvfzutuy62RzUtYefKv6WQRVRni8ZOlcp3DjJ59t5zkP95unlLF8tQfri27cEq5d4tmrxgQZUWZnThWqsa79ejIdX/TKxznIsaOqYqHJQQKBgQD6hBPSPyLPnVu+z7E67MIjFVZmFZtdreUTQFxbB0yg5lBM3Y/ObQSKpvvHdTtp0z1jrAWJmta5d3M5tziYIcwrsRl0k/UAYLRlvwFey7OgGaNCQft98JCYLDT/26bHIgZ+IAEfLhwuA8bcj1loGdgVyWCT6+0tcV9vWOwY6fnX5wKBgQDsS2+LhEKzlr3d7H/G8rkosd4obvMGMPV5/lmjPHM4b315Wp70Iy30cwIWIkfTodK7gllyRRJ7wXCaH7mdkq52wZV7GlPSdb+2t+LKpPKFnyW3c0Dgk0vrYcwFzSluZUhYxhXjPHwY6FsINzD0O+jxX5Wyf9DPnGNpWBEGIfijkQKBgAPsAWtvNZpOels4YSvs/PUTpnCesfn7ePSeM1Pxf0+di3BIn7G5nzKUfqiWu0Fi3zkqPkPzOp1Ys2MZ7TbkgI/GjAF5N4K0AN7+6ISVZ9B/1kB5S/iixYC8YHAI/klrzPI4igv06tgFkx1s2Rd6IBnnNy3ZqbLmbXoOyFNzhkfNAoGAcM1aRKoxBXay0Ryzqw/4YHr46Sh+D7iTl1dbB1g2UPy4U5R1SWr55zZ4CoT28QrRhP4nISvkNPwVex4mCBkb/ElRyOC6nz/i86E5PTAdLrjY0ojMsejfV1DqiuJ0IuVq8iYuELqxK1rRCkz+q7ll7MSKvBnUXyfzNTj7d4gEIGECgYEAz8hNR5zW0915qJk/TMQFpjwAfvLi1g686eRnjwbp721ADFA6Wvw4w6ie7wBVrQTkMIcGYMLqNUZ3f9Y4iyle/6Z6CMJOwcErdhHxkAXOxG+jgCxbRpmzqicBpB0YPopOG6QhsUS0dcJHUkMPlEijb3WfikQcRTTYChg3G5o7nm0=', 0, NULL, NULL);

INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(1984481955388682242, 1984481955388682242, '2024-10-24 07:25:34', 1984481955388682242, '2026-11-04 10:55:07', '0', 'admin', '$2a$10$fiIbLgvf/qyUTTQPXHjFPO6ytl.Id0hGp9/0UwgFaTFYgVfhZIw6y', '管理员', '1907452460@qq.com', '', 1, 1, 1, 1, 1984483212773847041, 2049774005242138625);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(1984583825226477570, 1984481955388682242, '2025-11-01 19:30:30', 1984583825226477570, '2026-04-16 20:37:27', '0', 'radish', '$2a$10$wJ3aGId3/NhXiHtOJ6OBPuImLA1LByIO6t7p4vASyd9KBa5ejKeTO', '萝卜', '2@qq.com', '', 1, 1, 1, 1, 1984583320156778498, 2044757035367112706);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2002379807972450306, 1984481955388682242, '2025-12-20 22:05:23', 1984481955388682242, '2026-05-08 17:58:35', '0', 'yys1999', '$2a$10$2Ok21kIYYbrkm4KJtGteuuKAoB1d0X2DW8tlrMYtK29jcgNWUEvx.', '杨永胜', '2210263330@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2004934672951070721, 1984481955388682242, '2025-12-27 23:17:31', 1984481955388682242, '2025-12-27 23:48:38', '1', '1', '$2a$10$zZdAiavPptGm5XD5270uZeXLsiIyjJHAYLJg/P4iZr8wvp6eFD3s2', '1', '132354232@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038556764151513090, 1984481955388682242, '2026-03-30 17:59:42', 1984481955388682242, '2026-03-30 17:59:42', '0', 'demo99', '$2a$10$yKe654Ba7e0MeBpYnCSH7.aCJmdrLMHWMDguZ6mxEto.7wimkZ2xK', 'demo99', '2763853225@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038563923576782850, 1984481955388682242, '2026-03-30 18:28:09', 1984481955388682242, '2026-03-30 18:28:09', '0', 'wujiawei', '$2a$10$7QF1kI.GWFKME92J5tCw0OMLFHP1.0CIo/JetufeahAq2zgZyR1gW', 'wujiawei', 'wjw123145@gmail.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038573781797601281, 1984481955388682242, '2026-03-30 19:07:19', 1984481955388682242, '2026-03-30 19:07:19', '0', 'ZBL', '$2a$10$3mGceyoz0xfAAEsRp791KuBzxg6oWTznf2E5R/E.xSvHFm.wAmVle', 'ZBL', 'zhaobolesunny@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038610137609437186, 1984481955388682242, '2026-03-30 21:31:47', 1984481955388682242, '2026-03-30 21:31:47', '0', 'yangyu91', '$2a$10$X/FjSo63qQctE1WAT0rz0.zmV1qHAN6ac254z6Aa7jortENEVnbLi', 'yy', 'yangyu788018@gmail.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038611875334750210, 1984481955388682242, '2026-03-30 21:38:41', 1984481955388682242, '2026-03-30 21:38:41', '0', 'test123', '$2a$10$2YSzt8agydJR03AQWnCoqeQKg1XhtM.Ks1hFFlWJrCI32SEuhaLYy', '', 'hotipo8759@flownue.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038616250476167169, 1984481955388682242, '2026-03-30 21:56:04', 2038616250476167169, '2026-05-10 17:48:46', '0', '123456', '$2a$10$O2hH6Ve4Ph181Zh5h77r8OWEodhUnJLa.YZvzOkqo.Io/NaHT49Y2', '123456', '146151173', NULL, 1, 0, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038635037313236994, 1984481955388682242, '2026-03-30 23:10:44', 1984481955388682242, '2026-03-30 23:10:44', '0', 'caleb', '$2a$10$ed8e/hh10xegzvug43jioe7cqVuJphqg0I.Zvapl/wFO1IYDbLSqy', 'caleb', 'chenjibzh@163.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038645184941031425, 1984481955388682242, '2026-03-30 23:51:03', 1984481955388682242, '2026-03-30 23:51:03', '0', '1919722231', '$2a$10$MGmTBhbLUusHwRhnX8Fh1.K2NA7VO6uHKyCXK/oPX5SCOn9YYBE/u', 'nan', '1919722231@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038670616264474626, 1984481955388682242, '2026-03-31 01:32:06', 1984481955388682242, '2026-03-31 01:32:06', '0', '666', '$2a$10$cmSQP6vJaR2JGqsxm5MDvuJ7xjq6oNAp3P/OucHVa0lP7wT45iLyO', '666', '3031350270@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038790741596602369, 1984481955388682242, '2026-03-31 09:29:26', 1984481955388682242, '2026-03-31 09:29:26', '0', 'qqq', '$2a$10$zt0DDjLPfctsJ0FZZ9YJse07YQ54anb4mO4oq9zvVbBkOkGwEF3fm', 'qqq', '943948104@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038806523890139137, 1984481955388682242, '2026-03-31 10:32:09', 1984481955388682242, '2026-03-31 10:32:09', '0', 'zhaoyang', '$2a$10$1WvOeQQZ8Ro.3AllaOzKPuij6jOuRqLHFqknsKA8s0kbBBuYn6hkW', 'zhaoyang', '965052722@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2038935426344452097, 1984481955388682242, '2026-03-31 19:04:22', 1984481955388682242, '2026-03-31 19:04:22', '0', 'jascb', '$2a$10$AaFmR7qpe43Q0KC/dGiM1ekK9z1IYdMcFD7Ks0Ipdnl1Ck2wuVuFG', 'jascb', '2772625280@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2039142773356535809, 1984481955388682242, '2026-04-01 08:48:17', 1984481955388682242, '2026-04-01 08:48:17', '0', 'modify', '$2a$10$zLqeKtxvikUN2cfmJex0EOXbbnnkJSpCTI1m8xTEzuIU3Irmx2Ncy', 'modify', 'gayati7976@nexafilm.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2044337732946800642, 1984481955388682242, '2026-04-15 16:51:12', 1984481955388682242, '2026-04-15 16:51:12', '0', 'moque', '$2a$10$af/VN/ykk5ztAjOJShfUc.U9.OhYOuuYn7S/uX5VCPVn6NKtimbkm', '', '1844722032@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2044433541147176962, 1984481955388682242, '2026-04-15 23:11:55', 1984481955388682242, '2026-04-15 23:11:55', '0', 'aquarius', '$2a$10$61WpjME6ZjkiGbL3gLWXGOmjG4O61DNo2K9xABRF1iVuXax32phW.', 'aquarius', '1015772292@qq.com', NULL, 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2053673508747608065, 1984481955388682242, '2026-05-11 11:08:15', 1984481955388682242, '2026-05-11 11:08:15', '0', 'test1', '$2a$10$f5OpL2zpprMKeVPY7jDwEuDgX3bQRsfOPyOuRRxioQvplSe6jwbei', 'test1', '1@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2053673771524947969, 1984481955388682242, '2026-05-11 11:09:17', 1984481955388682242, '2026-05-11 11:09:17', '0', 'test2', '$2a$10$HQUUzSht.0okAiCVkg393u.12XeUpx8hAS81lxncrvElmeI8FdOH2', 'test2', '3@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user
(id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar)
VALUES(2053673837916585985, 1984481955388682242, '2026-05-11 11:09:33', 1984481955388682242, '2026-05-11 11:09:33', '0', 'test3', '$2a$10$SPsW/zXLM2v85NiiUZ9sReU/21SxlRYMdnaAFtKdQkmLHzKPiGwyi', 'test3', '4@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);

INSERT INTO sys_user_role
(role_id, user_id)
VALUES(1984489247068725249, 1984481955388682242);
INSERT INTO sys_user_role
(role_id, user_id)
VALUES(1984489247123251201, 1984583825226477570);
INSERT INTO sys_user_role
(role_id, user_id)
VALUES(1984489247123251201, 2053673508747608065);
INSERT INTO sys_user_role
(role_id, user_id)
VALUES(1984489247123251201, 2053673771524947969);
INSERT INTO sys_user_role
(role_id, user_id)
VALUES(1984489247123251201, 2053673837916585985);