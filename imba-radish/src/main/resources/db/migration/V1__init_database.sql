-- radish_master.sys_announcement definition

CREATE TABLE `sys_announcement` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `sort_order` bigint DEFAULT NULL COMMENT '排序',
                                    `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                    `del_flag` tinyint DEFAULT NULL COMMENT '逻辑删除',
                                    `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公告标题',
                                    `publish_time` datetime DEFAULT NULL COMMENT '公布时间',
                                    `context` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公告内容',
                                    `status` tinyint DEFAULT NULL COMMENT '状态；1：发布；0：草稿',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';


-- radish_master.sys_dept definition

CREATE TABLE `sys_dept` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `sort_order` int DEFAULT NULL COMMENT '排序字段',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint DEFAULT NULL COMMENT '逻辑删除',
                            `code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门编码',
                            `name` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
                            `parent_id` bigint DEFAULT NULL COMMENT '父级id',
                            `level` tinyint DEFAULT NULL COMMENT '层级',
                            PRIMARY KEY (`id`),
                            KEY `idx_sys_dept_code` (`code`) COMMENT '部门编码普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';


-- radish_master.sys_dialogue definition

CREATE TABLE `sys_dialogue` (
                                `id` bigint NOT NULL COMMENT '主键',
                                `sort_order` bigint DEFAULT NULL COMMENT '排序',
                                `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                `del_flag` tinyint DEFAULT NULL COMMENT '逻辑删除',
                                `title` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
                                `top_flag` tinyint DEFAULT NULL COMMENT '置顶标识；1：置顶；0：不置顶',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能对话表';


-- radish_master.sys_dialogue_details definition

CREATE TABLE `sys_dialogue_details` (
                                        `id` bigint NOT NULL COMMENT '主键',
                                        `parent_id` bigint DEFAULT NULL COMMENT '表sys_dialogue_history的id',
                                        `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                        `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                        `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                        `tag` tinyint DEFAULT NULL COMMENT '标识；1：用户；0：AI',
                                        `content` text COLLATE utf8mb4_general_ci COMMENT '对话内容',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能对话详情表';


-- radish_master.sys_dict_data definition

CREATE TABLE `sys_dict_data` (
                                 `id` bigint NOT NULL COMMENT '主键',
                                 `parent_id` bigint DEFAULT NULL COMMENT 'sys_dict_type的主键',
                                 `sort_order` int DEFAULT NULL COMMENT '排序',
                                 `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                 `label` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签',
                                 `value` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '键值',
                                 `css_class` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
                                 `list_class` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
                                 `is_default` tinyint DEFAULT NULL COMMENT '是否默认；1：是 0：否',
                                 `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：停用',
                                 `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';


-- radish_master.sys_dict_type definition

CREATE TABLE `sys_dict_type` (
                                 `id` bigint NOT NULL COMMENT '主键',
                                 `sort_order` int DEFAULT NULL COMMENT '排序',
                                 `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                 `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                 `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                 `dict_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典名称',
                                 `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：停用',
                                 `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';


-- radish_master.sys_file definition

CREATE TABLE `sys_file` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                            `original_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '源文件名',
                            `file_key` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储文件名',
                            `location` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储地址；1：minio；2：磁盘',
                            `bucket` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '桶名',
                            `object_key` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
                            `file_suffix` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件后缀；示例：.txt、.jpg',
                            `file_size` bigint DEFAULT NULL COMMENT '文件大小；单位：B',
                            `file_hash` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件hash',
                            PRIMARY KEY (`id`),
                            KEY `idx_sys_file_hash` (`file_hash`) COMMENT '文件hash普通索引',
                            KEY `idx_sys_file_key` (`file_key`) COMMENT '存储文件名普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件表';


-- radish_master.sys_log definition

CREATE TABLE `sys_log` (
                           `id` bigint NOT NULL COMMENT '主键',
                           `create_by` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                           `log_level` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志级别',
                           `ip_address` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求ip',
                           `request_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问路径',
                           `request_method` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求方法',
                           `request_params` text COLLATE utf8mb4_general_ci COMMENT '请求参数',
                           `response_result` text COLLATE utf8mb4_general_ci COMMENT '响应结果',
                           `operation_desc` text COLLATE utf8mb4_general_ci COMMENT '操作描述',
                           `source` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志来源',
                           `execution_time` bigint DEFAULT NULL COMMENT '耗时',
                           `module_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模块名称',
                           `status_code` int DEFAULT NULL COMMENT '状态码',
                           `exception_info` text COLLATE utf8mb4_general_ci COMMENT '异常信息',
                           `os_browser_info` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作系统/浏览器信息',
                           `type` varchar(2) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志类型',
                           `update_by` bigint DEFAULT NULL COMMENT '修改人',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日志表';


-- radish_master.sys_menu definition

CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL COMMENT '菜单唯一标识符，主键',
                            `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID，顶级菜单的父ID通常为0',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                            `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
                            `path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '前端路由路径',
                            `component` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '前端组件路径',
                            `icon` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单图标（如字体图标类名）',
                            `type` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
                            `order_num` int COMMENT '排序号，数字越小，排序越靠前',
                            `status` tinyint DEFAULT '1' COMMENT '菜单状态：0-禁用，1-启用',
                            `visible` tinyint DEFAULT '1' COMMENT '是否显示：0-隐藏，1-显示',
                            `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注信息',
                            PRIMARY KEY (`id`),
                            KEY `idx_sys_menu_parent_id` (`parent_id`) COMMENT '父级菜单普通索引',
                            KEY `idx_sys_menu_type` (`type`) COMMENT '菜单类型普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单信息表';


-- radish_master.sys_permission definition

CREATE TABLE `sys_permission` (
                                  `id` bigint NOT NULL COMMENT '主键',
                                  `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                                  `name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限名称',
                                  `code` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
                                  `type` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型；1：菜单；2：按钮',
                                  `status` int DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
                                  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_sys_permission_code` (`code`) COMMENT '权限编码普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';


-- radish_master.sys_role definition

CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `sort_order` int DEFAULT NULL COMMENT '排序字段',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                            `code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色编码',
                            `name` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色名称',
                            `description` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                            `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
                            `is_default` tinyint DEFAULT NULL COMMENT '是否标记为系统默认角色；1：是；0：不是',
                            `fix_role` tinyint DEFAULT NULL COMMENT '是否为固定角色（固定角色无法被删除）；1：是；0：不是',
                            PRIMARY KEY (`id`),
                            KEY `idx_code` (`code`) COMMENT '角色编码普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';


-- radish_master.sys_role_menu definition

CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL COMMENT '角色id',
                                 `menu_id` bigint NOT NULL COMMENT '菜单id',
                                 UNIQUE KEY `uk_sys_role_menu` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单表';


-- radish_master.sys_role_permission definition

CREATE TABLE `sys_role_permission` (
                                       `role_id` bigint NOT NULL COMMENT '角色主键',
                                       `permission_id` bigint NOT NULL COMMENT '权限主键',
                                       UNIQUE KEY `uk_sys_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限表';


-- radish_master.sys_schedule_job definition

CREATE TABLE `sys_schedule_job` (
                                    `id` bigint NOT NULL COMMENT '主键',
                                    `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                    `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                    `job_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
                                    `job_group` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务组',
                                    `cron` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cron表达式',
                                    `job_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务类型',
                                    `status` int DEFAULT NULL COMMENT '任务状态；1：启用；0：暂停',
                                    `job_param` text COLLATE utf8mb4_general_ci COMMENT '任务所需参数',
                                    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                    `job_interval` int DEFAULT NULL COMMENT '间隔',
                                    `interval_type` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '间隔类型；0：毫秒；1：秒；2：分钟；3：小时',
                                    `schedule_type` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '调度类型；0：简单调度；1：cron表达式调度',
                                    `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                    `job_class` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '作业执行类',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调度任务表';


-- radish_master.sys_secret_key definition

CREATE TABLE `sys_secret_key` (
                                  `id` bigint NOT NULL COMMENT '主键',
                                  `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
                                  `public_key` varchar(400) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公钥',
                                  `private_key` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '私钥',
                                  `type` tinyint NOT NULL COMMENT '类型',
                                  `description` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_sys_secret_key_type` (`type`) COMMENT '类型普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='密钥';


-- radish_master.sys_user definition

CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL,
                            `create_by` bigint DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：不存在',
                            `username` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
                            `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                            `nickname` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
                            `email` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
                            `phone` varchar(11) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电话',
                            `enabled` tinyint(1) DEFAULT '1' COMMENT '用户是否被启用；1：启用；0：禁用',
                            `account_non_locked` tinyint(1) DEFAULT '1' COMMENT '账户是否被锁定；1：正常；0：锁定',
                            `credentials_non_expired` tinyint(1) DEFAULT '1' COMMENT '凭证是否过期；1：正常；0：过期',
                            `account_non_expired` tinyint(1) DEFAULT '1' COMMENT '账户是否过期；1：正常；0：过期',
                            `dept_id` bigint DEFAULT NULL COMMENT '部门id',
                            `avatar` bigint DEFAULT NULL COMMENT '用户头像',
                            PRIMARY KEY (`id`),
                            KEY `idx_sys_user_email` (`email`) COMMENT '邮箱普通索引',
                            KEY `idx_sys_user_username` (`username`) COMMENT '用户名普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';


-- radish_master.sys_user_role definition

CREATE TABLE `sys_user_role` (
                                 `role_id` bigint NOT NULL COMMENT '角色主键',
                                 `user_id` bigint NOT NULL COMMENT '用户主键',
                                 UNIQUE KEY `uk_role_user` (`role_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色表';

INSERT INTO sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`) VALUES(1984483212773847041, 1, 1984481955388682242, '2024-10-26 10:58:36', 1984481955388682242, '2025-05-16 23:49:42', 0, 'IT', '互联网公司', NULL, 1);
INSERT INTO sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`) VALUES(1984567407013588993, 2, 1984481955388682242, '2025-11-01 18:25:16', 1984481955388682242, '2025-11-01 19:14:24', 0, 'TD', '技术研发部', 1984483212773847041, 2);
INSERT INTO sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`) VALUES(1984583320156778498, 3, 1984481955388682242, '2025-11-01 19:28:30', 1984481955388682242, '2025-11-01 19:28:30', 0, 'BD', '后端研发部', 1984567407013588993, 3);
INSERT INTO sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, `level`) VALUES(1984583475144699905, 4, 1984481955388682242, '2025-11-01 19:29:07', 1984481955388682242, '2025-11-01 19:29:07', 0, 'FD', '前端研发部', 1984567407013588993, 3);

INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867211083778, 1984484774384603137, 1, 1984481955388682242, '2025-04-24 15:03:44', 1984481955388682242, '2025-04-24 17:25:01', '0', '男', '1', '', '', 1, 1, '1');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867253026818, 1984484774384603137, 2, 1984481955388682242, '2025-04-24 17:12:48', 1984481955388682242, '2025-04-24 17:12:48', '0', '女', '0', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867253026819, 1984484774405574658, 1, 1984481955388682242, '2025-04-25 22:48:59', 1984481955388682242, '2025-04-25 22:48:59', '0', '启用', '1', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867253026820, 1984484774405574658, 2, 1984481955388682242, '2025-04-25 22:49:14', 1984481955388682242, '2025-04-25 22:49:14', '0', '禁用', '0', '', '', 1, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867253026821, 1984484774405574659, 1, 1984481955388682242, '2025-07-14 20:03:57', 1984481955388682242, '2025-07-14 20:03:57', '0', '存储文件扫描', 'storage_scan', '', '', 1, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867282386946, 1984484774405574660, 1, 1984481955388682242, '2025-09-06 22:16:42', 1984481955388682242, '2025-11-01 19:41:54', '0', 'minio存储', '1', '', '', 1, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867282386947, 1984484774405574660, 2, 1984481955388682242, '2025-09-06 22:16:58', 1984481955388682242, '2025-09-06 22:16:58', '0', '本地磁盘', '2', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867282386948, 1984484774405574661, NULL, 1984481955388682242, '2025-10-26 18:24:58', 1984481955388682242, '2025-10-26 18:24:58', '0', '简单调度', '0', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867282386949, 1984484774405574661, NULL, 1984481955388682242, '2025-10-26 18:25:11', 1984481955388682242, '2025-10-26 18:25:11', '0', 'cron调度', '1', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867311747074, 1984484774405574662, NULL, 1984481955388682242, '2025-10-26 18:28:43', 1984481955388682242, '2025-10-26 18:28:43', '0', '存储文件扫描', 'storage_scan', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984483867311747075, 1984484774405574662, NULL, 1984481955388682242, '2025-11-01 11:24:35', 1984481955388682242, '2025-11-01 11:24:35', '0', '修改主键', 'update_primary_key', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1984610087038955521, 1984484774405574662, NULL, 1984481955388682242, '2025-11-01 21:14:52', 1984481955388682242, '2025-11-01 21:14:52', '0', '清理文件', 'clear_deleted_file', '', '', 0, 1, '清理已删除的文件');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1985710533040242689, 1985710409769648130, NULL, 1984481955388682242, '2025-11-04 22:07:38', 1984481955388682242, '2025-11-04 22:07:38', '0', '菜单', '1', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(1985710563537027073, 1985710409769648130, NULL, 1984481955388682242, '2025-11-04 22:07:46', 1984481955388682242, '2025-11-04 22:07:46', '0', '按钮', '2', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269551443804161, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:33:38', 1984481955388682242, '2025-12-17 20:33:38', '0', 'image/jpeg', 'jpg', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269596801007617, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:33:48', 1984481955388682242, '2025-12-17 20:33:48', '0', 'image/jpeg', 'jpeg', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269648034430977, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:01', 1984481955388682242, '2025-12-17 20:34:01', '0', 'image/png', 'png', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269696017268737, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:12', 1984481955388682242, '2025-12-17 20:34:12', '0', 'image/gif', 'gif', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269744687972353, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:24', 1984481955388682242, '2025-12-17 20:34:24', '0', 'image/webp', 'webp', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269795946561538, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:36', 1984481955388682242, '2025-12-17 20:34:36', '0', 'image/svg+xml', 'svg', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269863076397058, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:34:52', 1984481955388682242, '2025-12-17 20:34:52', '0', 'application/pdf', 'pdf', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269906969788417, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:35:02', 1984481955388682242, '2025-12-17 20:35:02', '0', 'text/plain', 'txt', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269956429021186, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:35:14', 1984481955388682242, '2025-12-17 20:35:14', '0', 'text/html', 'html', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001269998841823234, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:35:24', 1984481955388682242, '2025-12-17 20:35:24', '0', 'application/json', 'json', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001270170804092930, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:05', 1984481955388682242, '2025-12-17 20:36:05', '0', 'text/csv', 'csv', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001270219969724417, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:17', 1984481955388682242, '2025-12-17 20:36:17', '0', 'audio/mpeg', 'mp3', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001270257131257857, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:26', 1984481955388682242, '2025-12-17 20:36:26', '0', 'video/mp4', 'mp4', '', '', 0, 1, '');
INSERT INTO sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, css_class, list_class, is_default, status, remark) VALUES(2001270296146673666, 2001266854774767618, NULL, 1984481955388682242, '2025-12-17 20:36:35', 1984481955388682242, '2025-12-17 20:36:35', '0', 'video/webm', 'webm', '', '', 0, 1, '');

INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1984484774384603137, 2, 1984481955388682242, '2025-04-23 22:52:19', 1984481955388682242, '2025-04-23 23:04:57', '0', '性别', 1, '表示男女的性别');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1984484774405574658, 2, 1984481955388682242, '2025-04-25 22:48:38', 1984481955388682242, '2025-04-25 22:50:17', '0', '状态', 1, '');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1984484774405574659, 3, 1984481955388682242, '2025-07-13 16:34:09', 1984481955388682242, '2025-10-26 18:25:35', '0', '调度类型', 1, '');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1984484774405574660, 4, 1984481955388682242, '2025-09-06 22:16:11', 1984481955388682242, '2025-10-21 13:47:08', '0', '文件存储位置', 1, 'org.apache.catalina.webresources.Cache.getResource Unable to add the resource at [/uploadPath/home/NewCarPhoto/2025/10/21/4564117022241344294_vinpicture_1_20251021092127_12538.jpg] to the cache for web application [] because there was insufficient free space available after evicting expired cache entries - consider increasing the maximum size of the cache');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1984484774405574661, 3, 1984481955388682242, '2025-10-26 18:24:18', 1984481955388682242, '2025-11-01 19:47:30', '0', '调度类型', 1, '');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1984484774405574662, 2, 1984481955388682242, '2025-10-26 18:26:42', 1984481955388682242, '2025-11-01 19:47:35', '0', '作业类型', 1, '');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(1985710409769648130, 1, 1984481955388682242, '2025-11-04 22:07:09', 1984481955388682242, '2025-12-20 19:56:06', '0', '权限类型', 1, '');
INSERT INTO sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, status, remark) VALUES(2001266854774767618, 2, 1984481955388682242, '2025-12-17 20:22:55', 1984481955388682242, '2025-12-20 19:56:02', '0', '在线预览文件类型', 1, '');

INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400598355969, NULL, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 04:48:45', '0', '系统管理', '/system', NULL, 'setting', '0', 2, 1, 1, '系统管理顶级菜单');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400631910402, NULL, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 04:48:45', '0', '内容管理', '/content', NULL, 'folder', '0', 1, 0, 1, '内容管理顶级菜单');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400631910403, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '用户管理', '/user', 'system/User', 'user', '1', 1, 1, 1, '管理用户信息');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400631910404, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '角色管理', '/role', 'system/Role', 'avatar', '1', 2, 1, 1, '管理角色信息');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400657076226, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '菜单管理', '/menu', 'system/Menu', 'menu', '1', 3, 1, 1, '管理菜单信息');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400657076227, 1984485400598355969, 1984481955388682242, '2025-04-22 16:48:24', 1984481955388682242, '2025-11-01 05:00:56', '0', '字典管理', '/dict', 'system/DictType', 'Collection', '1', 2, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400686436353, 1984485400598355969, 1984481955388682242, '2025-04-23 23:06:59', 1984481955388682242, '2025-11-01 05:00:56', '0', '字典数据', '/dictData', 'system/DictData', 'Document', '1', 2, 1, 0, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400686436354, 1984485400598355969, 1984481955388682242, '2025-04-25 22:41:19', 1984481955388682242, '2025-11-01 05:00:56', '0', '部门管理', '/dept', 'system/Dept', 'OfficeBuilding', '1', 4, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400686436355, 1984485400598355969, 1984481955388682242, '2025-04-25 22:42:14', 1984481955388682242, '2025-11-01 05:00:56', '0', '日志管理', '/log', 'system/Log', 'Memo', '1', 5, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400686436356, 1984485400598355969, 1984481955388682242, '2025-04-27 16:47:48', 1984481955388682242, '2025-11-01 15:29:03', '0', '日志详情', '/logDetail', 'system/LogDetail', 'Document', '1', 7, 1, 0, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400715796482, NULL, 1984481955388682242, '2025-05-28 16:10:09', 1984481955388682242, '2025-11-01 04:48:45', '0', '智能问答', '/ai', '', 'Tools', '0', 0, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400715796483, 1984485400715796482, 1984481955388682242, '2025-05-28 16:18:11', 1984481955388682242, '2025-11-01 05:00:56', '0', '新对话', '/dialog', 'chat/Chat', 'ChatDotSquare', '1', 0, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400715796484, 1984485400715796482, 1984481955388682242, '2025-05-29 15:35:28', 1984481955388682242, '2025-11-01 05:00:56', '0', '历史对话', '/answerHistory', 'chat/AnswerHistory', 'Comment', '1', 2, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400715796485, 1984485400598355969, 1984481955388682242, '2025-08-17 19:36:03', 1984481955388682242, '2025-11-01 05:00:56', '0', '定时任务', '/quartz', 'system/Quartz.vue', 'Clock', '1', 8, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400715796486, 1984485400598355969, 1984481955388682242, '2025-09-06 18:34:18', 1984481955388682242, '2025-11-01 05:00:56', '0', '文件管理', '/file', 'system/File', 'Files', '1', 3, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984485400715796487, 1984485400598355969, 1984481955388682242, '2025-10-22 13:58:44', 1984481955388682242, '2025-11-01 05:00:56', '0', 'cron表达式生成器', '/cron', 'system/component/CronGenerate', 'AlarmClock', '1', 20, 1, 0, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984595671614558209, NULL, 1984481955388682242, '2025-11-01 20:17:35', 1984481955388682242, '2025-11-01 20:30:23', '0', '网盘管理', '/onlineDisk', '', 'Platform', '0', 0, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984596219776536578, 1984595671614558209, 1984481955388682242, '2025-11-01 20:19:45', 1984481955388682242, '2025-11-01 20:28:43', '0', '我的文件', '/myFile', 'onlinedisk/File', 'Files', '1', 0, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1984999219198464002, 1984485400598355969, 1984481955388682242, '2025-11-02 23:01:08', 1984481955388682242, '2025-11-02 23:01:08', '0', '权限管理', '/permisson', 'system/Permission', 'Key', '1', 4, 1, 1, '');
INSERT INTO sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, order_num, status, visible, remark) VALUES(1988216780108926978, 1984485400598355969, 1984481955388682242, '2025-11-11 20:06:34', 1984481955388682242, '2025-11-16 13:42:40', '0', '系统监控', '/actuator', 'system/Actuator', 'Odometer', '1', 6, 1, 1, '');

INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415355244546, 1984481955388682242, '2024-11-20 20:06:35', 1984481955388682242, '2024-11-20 20:06:41', '0', '添加菜单', 'sys_menu_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415409770497, 1984481955388682242, '2024-11-20 20:51:39', 1984481955388682242, '2024-11-20 20:51:39', '0', '添加角色', 'sys_role_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415409770498, 1984481955388682242, '2024-11-20 20:53:17', 1984481955388682242, '2024-11-20 20:53:17', '0', '添加权限', 'sys_pms_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415409770499, 1984481955388682242, '2024-11-20 20:53:44', 1984481955388682242, '2024-11-20 20:53:44', '0', '编辑角色', 'sys_role_edit', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415409770500, 1984481955388682242, '2024-11-20 20:55:08', 1984481955388682242, '2024-11-20 20:55:08', '0', '编辑菜单', 'sys_menu_edit', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415409770501, 1984481955388682242, '2024-11-20 20:55:18', 1984481955388682242, '2024-11-20 20:55:18', '0', '删除菜单', 'sys_menu_del', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415409770502, 1984481955388682242, '2024-11-20 21:39:14', 1984481955388682242, '2024-11-20 21:39:14', '0', '添加部门', 'sys_dept_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130625, 1984481955388682242, '2024-11-20 21:39:26', 1984481955388682242, '2024-11-20 21:39:26', '0', '编辑部门', 'sys_dept_edit', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130626, 1984481955388682242, '2024-11-20 21:39:52', 1984481955388682242, '2024-11-20 21:39:52', '0', '删除部门', 'sys_dept_del', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130627, 1984481955388682242, '2024-11-20 21:42:27', 1984481955388682242, '2024-11-20 21:42:27', '0', '角色赋予菜单', 'sys_role_set_menu', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130628, 1984481955388682242, '2024-11-20 21:42:38', 1984481955388682242, '2024-11-20 21:42:38', '0', '角色赋予权限', 'sys_role_set_per', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130629, 1984481955388682242, '2024-11-20 21:47:29', 1984481955388682242, '2024-11-20 21:47:29', '0', '设置默认角色', 'sys_role_set_def', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130630, 1984481955388682242, '2024-11-20 21:48:11', 1984481955388682242, '2024-11-20 21:48:11', '0', '禁用角色', 'sys_role_fbn', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415439130631, 1984481955388682242, '2024-11-20 21:48:37', 1984481955388682242, '2024-11-20 21:48:37', '0', '启用角色', 'sys_role_enable', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490754, 1984481955388682242, '2024-11-20 21:51:21', 1984481955388682242, '2024-11-20 21:51:21', '0', '添加字典数据', 'sys_dict_data_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490755, 1984481955388682242, '2024-11-20 21:51:46', 1984481955388682242, '2024-11-20 21:51:46', '0', '编辑字典数据', 'sys_dict_data_edit', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490756, 1984481955388682242, '2024-11-20 21:52:10', 1984481955388682242, '2024-11-20 21:52:10', '0', '删除字典数据', 'sys_dict_data_del', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490757, 1984481955388682242, '2024-11-20 21:52:51', 1984481955388682242, '2024-11-20 21:52:51', '0', '添加字典类型', 'sys_dict_type_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490758, 1984481955388682242, '2024-11-20 21:53:21', 1984481955388682242, '2024-11-20 21:53:21', '0', '编辑字典类型', 'sys_dict_type_edit', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490759, 1984481955388682242, '2024-11-20 21:53:44', 1984481955388682242, '2024-11-20 21:53:44', '0', '删除字典类型', 'sys_dict_type_del', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490760, 1984481955388682242, '2024-11-20 21:54:18', 1984481955388682242, '2024-11-20 21:54:18', '0', '删除文件', 'sys_file_del', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490761, 1984481955388682242, '2024-11-20 21:57:41', 1984481955388682242, '2024-11-20 21:57:41', '0', '删除角色', 'sys_role_del', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415468490762, 1984481955388682242, '2024-11-20 21:58:58', 1984481955388682242, '2024-11-20 21:58:58', '0', '重置密码', 'sys_user_reset', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850881, 1984481955388682242, '2024-11-20 21:59:34', 1984481955388682242, '2024-11-20 21:59:34', '0', '添加用户', 'sys_user_add', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850882, 1984481955388682242, '2024-11-20 22:01:40', 1984481955388682242, '2024-11-20 22:01:40', '0', '编辑用户', 'sys_user_edit', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850883, 1984481955388682242, '2024-11-26 19:23:51', 1984481955388682242, '2024-11-26 19:23:51', '0', '生成密钥', 'sys_secret_key_generate', '2', 1, NULL);
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850884, 1984481955388682242, '2025-03-28 11:00:40', 1984481955388682242, '2025-03-28 11:00:40', '0', '删除用户', 'sys_user_delete', '2', 1, '');
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850885, 1984481955388682242, '2025-04-17 18:30:21', 1984481955388682242, '2025-04-17 18:30:21', '0', '锁定用户', 'sys_user_lock', '2', 1, '');
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850886, 1984481955388682242, '2025-04-17 18:31:31', 1984481955388682242, '2025-04-17 18:31:31', '0', '解锁用户', 'sys_user_unlock', '2', 1, '');
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850887, 1984481955388682242, '2025-07-13 20:52:28', 1984481955388682242, '2025-07-13 20:52:28', '0', '启用用户', 'sys_user_enable', '2', 1, '');
INSERT INTO sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description) VALUES(1984481415497850888, 1984481955388682242, '2025-07-13 20:54:00', 1984481955388682242, '2025-07-13 20:54:00', '0', '禁用用户', 'sys_user_forbidden', '2', 1, '');

INSERT INTO sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES(1984489247068725249, 1, 1984481955388682242, '2024-10-25 15:07:55', 1984481955388682242, '2025-11-16 13:18:22', '0', 'ROLE_ADMIN', '管理员', '拥有所有权限', 1, 0, 1);
INSERT INTO sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES(1984489247123251201, 2, 1984481955388682242, '2024-10-25 15:08:15', 1984481955388682242, '2024-10-25 15:08:07', '0', 'ROLE_NORMAL', '普通用户', '没有任何权限', 1, 1, 1);

INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400598355969);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400631910403);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400631910404);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400657076226);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400657076227);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400686436353);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400686436354);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400686436355);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400686436356);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400715796482);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400715796483);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400715796484);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400715796485);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400715796486);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984485400715796487);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984595671614558209);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984596219776536578);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1984999219198464002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 1988216780108926978);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 1984485400715796482);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 1984485400715796483);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 1984485400715796484);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 1984595671614558209);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 1984596219776536578);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415355244546);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415409770497);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415409770498);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415409770499);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415409770500);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415409770501);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415409770502);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130625);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130626);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130627);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130628);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130629);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130630);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415439130631);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490754);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490755);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490756);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490757);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490758);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490759);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490760);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490761);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415468490762);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850881);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850882);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850883);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850884);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850885);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850886);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850887);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 1984481415497850888);

INSERT INTO sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type, remark, job_class) VALUES(1984492295652417538, 1984481955388682242, '2025-07-15 19:06:19', 1984481955388682242, '2025-10-29 19:20:31', '0', 'nmap扫描', 'default', NULL, 'storage_scan', 0, NULL, '2025-10-26 09:38:46', '2025-10-30 16:00:00', 30, '2', '0', NULL, 'com.soft.base.quartz.job.NmapScanScheduler');
INSERT INTO sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type, remark, job_class) VALUES(1984492295711137793, 1984481955388682242, '2025-11-01 11:27:01', 1984481955388682242, '2025-11-01 11:27:01', '0', '修改主键', 'default', '', 'update_primary_key', 0, '', '2025-11-27 00:00:00', '2025-11-29 00:00:00', 1, '3', '0', '', 'com.soft.base.quartz.job.UpdatePrimaryKey');
INSERT INTO sys_schedule_job (id, create_by, create_time, update_by, update_time, del_flag, job_name, job_group, cron, job_type, status, job_param, start_time, end_time, job_interval, interval_type, schedule_type, remark, job_class) VALUES(1984613110888849409, 1984481955388682242, '2025-11-01 21:26:53', 1984481955388682242, '2025-11-01 21:26:53', '0', '清理文件', 'default', '0 0 0 * * ?', 'clear_deleted_file', 1, '', NULL, NULL, NULL, '2', '1', '', 'com.soft.base.quartz.job.ClearDeletedFile');

INSERT INTO sys_secret_key (id, create_by, create_time, update_by, update_time, del_flag, public_key, private_key, `type`, description) VALUES(1984491955066576897, 1984481955388682242, '2024-11-26 19:52:27', 1984481955388682242, '2024-11-26 11:53:23', '0', 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5zuUH2yI4yOlaEUJ6PBk9mme52FmtSvchu+8MNqd3ALJXPauXTAdmzGMqCur8OscE5XQR+UEBtibDM6NtvBlxt89DHpgHZPQpZrC9HtSDIsFk/ck2Bs5oM/clPwYXKumRTkk/SgDx+7oq55fezV6XfAc7rMoKKXyF/g591cKmFlDdmMdHEh6Fbz9kvFQ0kfdpikXuaoPpC3O32I+xXx77uIAO2W+vhyYRsuy6TozFs3Ba/nveg27gVZcvlWAWPXZY/cG6ggeuuTBSd3kR9Tf0TfHu1xIr8aOOFu82gY5+B4h9eWhdjhmboBZvIBHwlMgiBvEkkr+qWunltxeicNe1wIDAQAB', 'MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDnO5QfbIjjI6VoRQno8GT2aZ7nYWa1K9yG77ww2p3cAslc9q5dMB2bMYyoK6vw6xwTldBH5QQG2JsMzo228GXG3z0MemAdk9ClmsL0e1IMiwWT9yTYGzmgz9yU/Bhcq6ZFOST9KAPH7uirnl97NXpd8BzusygopfIX+Dn3VwqYWUN2Yx0cSHoVvP2S8VDSR92mKRe5qg+kLc7fYj7FfHvu4gA7Zb6+HJhGy7LpOjMWzcFr+e96DbuBVly+VYBY9dlj9wbqCB665MFJ3eRH1N/RN8e7XEivxo44W7zaBjn4HiH15aF2OGZugFm8gEfCUyCIG8SSSv6pa6eW3F6Jw17XAgMBAAECggEAMFD0rHRDTiLepyD15ySEFDERsQtbKLQXimKBkju8DILQjIpG+NXa+diqqWEmtlqKLVV6hetGoh+UlmJ6niUxPxLacMcJWmTOjiv+XJOAG3rZGYfkvPtDWWTVlJPwizyaq5A7OGKqF5bGK0YWcWpFPWe0w/PPil7SbUvC4PnhDuACTiQmmW4jZBZ9X6IjXPx95nZ2TRCRHdQ9vFMBz5oY55+gV5efrgBibkg8MKzoOnmjcvfzutuy62RzUtYefKv6WQRVRni8ZOlcp3DjJ59t5zkP95unlLF8tQfri27cEq5d4tmrxgQZUWZnThWqsa79ejIdX/TKxznIsaOqYqHJQQKBgQD6hBPSPyLPnVu+z7E67MIjFVZmFZtdreUTQFxbB0yg5lBM3Y/ObQSKpvvHdTtp0z1jrAWJmta5d3M5tziYIcwrsRl0k/UAYLRlvwFey7OgGaNCQft98JCYLDT/26bHIgZ+IAEfLhwuA8bcj1loGdgVyWCT6+0tcV9vWOwY6fnX5wKBgQDsS2+LhEKzlr3d7H/G8rkosd4obvMGMPV5/lmjPHM4b315Wp70Iy30cwIWIkfTodK7gllyRRJ7wXCaH7mdkq52wZV7GlPSdb+2t+LKpPKFnyW3c0Dgk0vrYcwFzSluZUhYxhXjPHwY6FsINzD0O+jxX5Wyf9DPnGNpWBEGIfijkQKBgAPsAWtvNZpOels4YSvs/PUTpnCesfn7ePSeM1Pxf0+di3BIn7G5nzKUfqiWu0Fi3zkqPkPzOp1Ys2MZ7TbkgI/GjAF5N4K0AN7+6ISVZ9B/1kB5S/iixYC8YHAI/klrzPI4igv06tgFkx1s2Rd6IBnnNy3ZqbLmbXoOyFNzhkfNAoGAcM1aRKoxBXay0Ryzqw/4YHr46Sh+D7iTl1dbB1g2UPy4U5R1SWr55zZ4CoT28QrRhP4nISvkNPwVex4mCBkb/ElRyOC6nz/i86E5PTAdLrjY0ojMsejfV1DqiuJ0IuVq8iYuELqxK1rRCkz+q7ll7MSKvBnUXyfzNTj7d4gEIGECgYEAz8hNR5zW0915qJk/TMQFpjwAfvLi1g686eRnjwbp721ADFA6Wvw4w6ie7wBVrQTkMIcGYMLqNUZ3f9Y4iyle/6Z6CMJOwcErdhHxkAXOxG+jgCxbRpmzqicBpB0YPopOG6QhsUS0dcJHUkMPlEijb3WfikQcRTTYChg3G5o7nm0=', 0, NULL);

INSERT INTO sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES(1984481955388682242, 1984481955388682242, '2024-10-24 07:25:34', 1984481955388682242, '2026-01-10 18:03:45', '0', 'admin', '$2a$10$IRLlmhCyCxgHp/Kmzsb9ceqijkfDdAwGRFAeRndKkwewXZ4bsv8yi', '程益祥', '1907452460@qq.com', '13235423290', 1, 1, 1, 1, 1984483212773847041, 11);
INSERT INTO sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES(1984583825226477570, 1984481955388682242, '2025-11-01 19:30:30', 1984481955388682242, '2025-11-01 19:30:30', '0', 'radish', '$2a$10$wJ3aGId3/NhXiHtOJ6OBPuImLA1LByIO6t7p4vASyd9KBa5ejKeTO', '萝卜', '1@qq.com', '', 1, 1, 1, 1, 1984583320156778498, NULL);
INSERT INTO sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES(2002379807972450306, 1984481955388682242, '2025-12-20 22:05:23', 1984481955388682242, '2025-12-20 22:10:36', '0', 'yys1999', '$2a$10$2Ok21kIYYbrkm4KJtGteuuKAoB1d0X2DW8tlrMYtK29jcgNWUEvx.', '杨永胜', '2210263330@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);
INSERT INTO sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES(2004934672951070721, 1984481955388682242, '2025-12-27 23:17:31', 1984481955388682242, '2025-12-27 23:48:38', '1', '1', '$2a$10$zZdAiavPptGm5XD5270uZeXLsiIyjJHAYLJg/P4iZr8wvp6eFD3s2', '1', '132354232@qq.com', '', 1, 1, 1, 1, 1984483212773847041, NULL);

INSERT INTO sys_user_role (role_id, user_id) VALUES(1984489247068725249, 1984481955388682242);
INSERT INTO sys_user_role (role_id, user_id) VALUES(1984489247068725249, 2004934672951070721);
INSERT INTO sys_user_role (role_id, user_id) VALUES(1984489247123251201, 1984583825226477570);
INSERT INTO sys_user_role (role_id, user_id) VALUES(1984489247123251201, 2002379807972450306);
INSERT INTO sys_user_role (role_id, user_id) VALUES(1984489247123251201, 2004934672951070721);