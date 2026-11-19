-- ============================================================
-- V2: 工作流框架（P0：引擎解耦门面层 + 统一待办中心）
-- 三张框架表：wf_instance（实例索引）/ wf_task（任务）/ wf_record（留痕）
-- 全部通过 business_key 关联业务单据，不包含任何业务审批规则
-- ============================================================

-- radish_master.wf_instance 定义
CREATE TABLE `wf_instance` (
                                         `id` bigint NOT NULL COMMENT '主键',
                                         `create_by` bigint DEFAULT NULL COMMENT '创建人（发起人）',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间（发起时间）',
                                         `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                         `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                         `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                                         `process_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程标识',
                                         `process_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程名称',
                                         `business_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单据标识，业务表通过它零改造接入',
                                         `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程标题',
                                         `status` tinyint NOT NULL DEFAULT '1' COMMENT '实例状态；1：运行中；2：已完成；3：已终止',
                                         `current_node` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '当前节点名称',
                                         `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                         `engine` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'noop' COMMENT '执行引擎标识；noop：内置线性引擎',
                                         `engine_data` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '引擎私有数据；noop引擎存储串行审批人id序列',
                                         PRIMARY KEY (`id`),
                                         KEY `idx_wf_instance_business_key` (`business_key`) COMMENT '业务单据标识普通索引',
                                         KEY `idx_wf_instance_status` (`status`) COMMENT '实例状态普通索引',
                                         KEY `idx_wf_instance_create_by` (`create_by`) COMMENT '发起人普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作流实例表';

-- radish_master.wf_task 定义
CREATE TABLE `wf_task` (
                                     `id` bigint NOT NULL COMMENT '主键',
                                     `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                     `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                     `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                                     `instance_id` bigint NOT NULL COMMENT '流程实例id',
                                     `node_order` int NOT NULL DEFAULT '1' COMMENT '节点序号；从1开始',
                                     `task_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务节点标识',
                                     `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务节点名称',
                                     `assignee_id` bigint NOT NULL COMMENT '办理人id',
                                     `status` tinyint NOT NULL DEFAULT '1' COMMENT '任务状态；1：待办；2：已办；3：已取消',
                                     `action` tinyint DEFAULT NULL COMMENT '办理动作；1：通过；2：驳回；3：转办',
                                     `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审批意见',
                                     `arrive_time` datetime DEFAULT NULL COMMENT '任务到达时间',
                                     `handle_time` datetime DEFAULT NULL COMMENT '办理时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_wf_task_instance` (`instance_id`) COMMENT '流程实例普通索引',
                                     KEY `idx_wf_task_assignee_status` (`assignee_id`, `status`) COMMENT '办理人与任务状态组合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作流任务表';

-- radish_master.wf_record 定义
CREATE TABLE `wf_record` (
                                       `id` bigint NOT NULL COMMENT '主键',
                                       `create_by` bigint DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_by` bigint DEFAULT NULL COMMENT '修改人',
                                       `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                       `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
                                       `instance_id` bigint NOT NULL COMMENT '流程实例id',
                                       `node_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点名称',
                                       `operator_id` bigint DEFAULT NULL COMMENT '操作人id；系统动作可为空',
                                       `action` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动作；START：发起；APPROVE：通过；REJECT：驳回；TRANSFER：转办；FINISH：完成；TERMINATE：终止',
                                       `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
                                       `operate_time` datetime NOT NULL COMMENT '操作时间',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_wf_record_instance` (`instance_id`) COMMENT '流程实例普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作流审批留痕表';

-- ============================================================
-- 菜单：工作流顶级目录 + 待办/已办/我发起的 三个页面
-- （流程跟踪为列表页内抽屉，不单独占菜单）
-- ============================================================
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2119250000000000001, NULL, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '工作流', '/workflow', NULL, 'Connection', '0', 3, '1', '1', '工作流框架顶级菜单');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2119250000000000002, 2119250000000000001, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '待办任务', '/todo', 'system/workflow/Todo', 'Tickets', '1', 1, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2119250000000000003, 2119250000000000001, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '已办任务', '/done', 'system/workflow/Done', 'CircleCheck', '1', 2, '1', '1', '');
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2119250000000000004, 2119250000000000001, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '我发起的', '/myStart', 'system/workflow/MyStart', 'Promotion', '1', 3, '1', '1', '');

-- ============================================================
-- 权限码
-- ============================================================
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2119250000000000011, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '查看工作流', 'sys_workflow_page', '2', '1', '待办/已办/我发起的/流程跟踪');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2119250000000000012, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '发起流程', 'sys_workflow_start', '2', '1', '');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2119250000000000013, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '审批流程', 'sys_workflow_approve', '2', '1', '通过/驳回');
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2119250000000000014, 1984481955388682242, '2026-11-19 11:30:00', 1984481955388682242, '2026-11-19 11:30:00', '0', '转办任务', 'sys_workflow_transfer', '2', '1', '');

-- ============================================================
-- 角色关联：管理员与普通角色均开放（工作流为框架基础能力）
-- ============================================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 2119250000000000001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 2119250000000000002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 2119250000000000003);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247068725249, 2119250000000000004);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 2119250000000000001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 2119250000000000002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 2119250000000000003);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES(1984489247123251201, 2119250000000000004);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 2119250000000000011);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 2119250000000000012);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 2119250000000000013);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247068725249, 2119250000000000014);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247123251201, 2119250000000000011);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247123251201, 2119250000000000012);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247123251201, 2119250000000000013);
INSERT INTO sys_role_permission (role_id, permission_id) VALUES(1984489247123251201, 2119250000000000014);
