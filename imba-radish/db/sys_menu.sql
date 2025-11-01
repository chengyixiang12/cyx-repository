create table sys_menu
(
    id          bigint                             not null comment '菜单唯一标识符，主键'
        primary key,
    parent_id   bigint                             null comment '父菜单ID，顶级菜单的父ID通常为0',
    create_by   bigint                             null comment '创建人',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   bigint                             null comment '修改人',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    char                               null comment '逻辑删除；1：存在；0：删除',
    name        varchar(50)                        not null comment '菜单名称',
    path        varchar(255)                       null comment '前端路由路径',
    component   varchar(255)                       null comment '前端组件路径',
    icon        varchar(50)                        null comment '菜单图标（如字体图标类名）',
    type        char                               not null comment '菜单类型：0-目录，1-菜单，2-按钮',
    order_num   int      default 0                 null comment '排序号，数字越小，排序越靠前',
    status      tinyint  default 1                 null comment '菜单状态：0-禁用，1-启用',
    visible     tinyint  default 1                 null comment '是否显示：0-隐藏，1-显示',
    remark      varchar(255)                       null comment '备注信息'
)
    comment '菜单信息表';

create index idx_sys_menu_parent_id
    on sys_menu (parent_id)
    comment '父级菜单普通索引';

create index idx_sys_menu_type
    on sys_menu (type)
    comment '菜单类型普通索引';

INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400598355969, null, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 04:48:45', '0', '系统管理', '/system', null, 'setting', '0', 2, 1, 1, '系统管理顶级菜单');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400631910402, null, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 04:48:45', '0', '内容管理', '/content', null, 'folder', '0', 1, 0, 1, '内容管理顶级菜单');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400631910403, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '用户管理', '/user', 'system/User', 'user', '1', 1, 1, 1, '管理用户信息');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400631910404, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '角色管理', '/role', 'system/Role', 'avatar', '1', 2, 1, 1, '管理角色信息');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400657076226, 1984485400598355969, 1984481955388682242, '2024-11-16 03:25:27', 1984481955388682242, '2025-11-01 05:00:56', '0', '菜单管理', '/menu', 'system/Menu', 'menu', '1', 3, 1, 1, '管理菜单信息');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400657076227, 1984485400598355969, 1984481955388682242, '2025-04-22 16:48:24', 1984481955388682242, '2025-11-01 05:00:56', '0', '字典管理', '/dict', 'system/DictType', 'Collection', '1', 2, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400686436353, 1984485400598355969, 1984481955388682242, '2025-04-23 23:06:59', 1984481955388682242, '2025-11-01 05:00:56', '0', '字典数据', '/dictData', 'system/DictData', 'Document', '1', 2, 1, 0, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400686436354, 1984485400598355969, 1984481955388682242, '2025-04-25 22:41:19', 1984481955388682242, '2025-11-01 05:00:56', '0', '部门管理', '/dept', 'system/Dept', 'OfficeBuilding', '1', 4, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400686436355, 1984485400598355969, 1984481955388682242, '2025-04-25 22:42:14', 1984481955388682242, '2025-11-01 05:00:56', '0', '日志管理', '/log', 'system/Log', 'Memo', '1', 5, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400686436356, 1984485400598355969, 1984481955388682242, '2025-04-27 16:47:48', 1984481955388682242, '2025-11-01 05:00:56', '0', '日志详情', '/logDetail', 'system/LogDetail.vue', 'Document', '1', 7, 1, 0, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400715796482, null, 1984481955388682242, '2025-05-28 16:10:09', 1984481955388682242, '2025-11-01 04:48:45', '0', '智能问答', '/ai', '', 'Tools', '0', 0, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400715796483, 1984485400715796482, 1984481955388682242, '2025-05-28 16:18:11', 1984481955388682242, '2025-11-01 05:00:56', '0', '新对话', '/dialog', 'chat/Chat', 'ChatDotSquare', '1', 0, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400715796484, 1984485400715796482, 1984481955388682242, '2025-05-29 15:35:28', 1984481955388682242, '2025-11-01 05:00:56', '0', '历史对话', '/answerHistory', 'chat/AnswerHistory', 'Comment', '1', 2, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400715796485, 1984485400598355969, 1984481955388682242, '2025-08-17 19:36:03', 1984481955388682242, '2025-11-01 05:00:56', '0', '定时任务', '/quartz', 'system/Quartz.vue', 'Clock', '1', 8, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400715796486, 1984485400598355969, 1984481955388682242, '2025-09-06 18:34:18', 1984481955388682242, '2025-11-01 05:00:56', '0', '文件管理', '/file', 'system/File', 'Files', '1', 3, 1, 1, '');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, path, component, icon, type, order_num, status, visible, remark) VALUES (1984485400715796487, 1984485400598355969, 1984481955388682242, '2025-10-22 13:58:44', 1984481955388682242, '2025-11-01 05:00:56', '0', 'cron表达式生成器', '/cron', 'system/component/CronGenerate', 'AlarmClock', '1', 20, 1, 0, '');
