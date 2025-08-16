create table sys_menu
(
    id          bigint auto_increment comment '菜单唯一标识符，主键'
        primary key,
    parent_id   bigint                             null comment '父菜单ID，顶级菜单的父ID通常为0',
    create_by   bigint                             null comment '创建人',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   bigint                             null comment '修改人',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    name        varchar(50)                        not null comment '菜单名称',
    path        varchar(255)                       null comment '前端路由路径',
    component   varchar(255)                       null comment '前端组件路径',
    icon        varchar(50)                        null comment '菜单图标（如字体图标类名）',
    type        char                               not null comment '菜单类型：0-目录，1-菜单，2-按钮',
    order_num   int      default 0                 null comment '排序号，数字越小，排序越靠前',
    status      tinyint  default 1                 null comment '菜单状态：0-禁用，1-启用',
    visible     tinyint  default 1                 null comment '是否显示：0-隐藏，1-显示',
    remark      varchar(255)                       null comment '备注信息',
    del_flag    char                               null comment '逻辑删除；1：存在；0：删除'
)
    comment '菜单信息表';

create index idx_sys_menu_parent_id
    on sys_menu (parent_id)
    comment '父级菜单普通索引';

create index idx_sys_menu_type
    on sys_menu (type)
    comment '菜单类型普通索引';

INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (1, null, 1, '2024-11-16 03:25:27', 1, '2025-04-24 10:37:03', '系统管理', '/system', null, 'setting', '0', 2, 1, 1, '系统管理顶级菜单', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (2, null, 1, '2024-11-16 03:25:27', 1, '2025-04-20 21:58:52', '内容管理', '/content', null, 'folder', '0', 1, 1, 1, '内容管理顶级菜单', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (3, null, 1, '2024-11-16 03:25:27', 1, '2025-04-09 22:00:39', '统计分析', '/total', null, 'chart', '0', 3, 1, 1, '统计分析顶级菜单', '0');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (4, 1, 1, '2024-11-16 03:25:27', 1, '2025-03-31 09:55:59', '用户管理', '/user', 'system/User', 'user', '1', 1, 1, 1, '管理用户信息', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (5, 1, 1, '2024-11-16 03:25:27', 1, '2025-04-20 11:46:09', '角色管理', '/role', 'system/Role', 'avatar', '1', 2, 1, 1, '管理角色信息', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (6, 1, 1, '2024-11-16 03:25:27', 1, '2025-04-20 10:26:56', '菜单管理', '/menu', 'system/Menu', 'menu', '1', 3, 1, 1, '管理菜单信息', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (7, 2, 1, '2024-11-16 03:25:27', 1, '2025-07-12 12:31:12', '文章管理', '/article/list', 'views/ArticleList', 'notebook', '1', 1, 1, 1, '管理文章内容', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (8, 2, 1, '2024-11-16 03:25:27', 1, '2025-07-12 12:33:02', '分类管理', '/category/list', 'views/CategoryList', 'Operation', '1', 2, 1, 1, '管理文章分类', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (9, 2, 1, '2024-11-16 03:25:27', 1, '2025-04-09 22:53:13', '标签管理', '/tag/list', 'views/TagList.vue', 'tag', '1', 3, 1, 1, '管理文章标签', '0');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (10, 3, 1, '2024-11-16 03:25:27', 1, '2025-04-09 22:00:39', '用户统计', '/stat/user', 'views/UserStat.vue', 'bar-chart', '1', 1, 1, 1, '统计用户相关数据', '0');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (11, 3, 1, '2024-11-16 03:25:27', 1, '2025-04-09 22:00:39', '系统访问统计', '/stat/system', 'views/SystemStat.vue', 'line-chart', '1', 2, 1, 1, '统计系统访问数据', '0');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (12, 1, 1, '2025-04-22 16:48:24', 1, '2025-04-24 02:33:58', '字典管理', '/dict', 'system/DictType', 'Collection', '1', 2, 1, 1, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (13, 1, 1, '2025-04-23 23:06:59', 1, '2025-04-23 15:18:13', '字典数据', '/dictData', 'system/DictData', 'Document', '1', 2, 1, 0, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (14, 1, 1, '2025-04-25 22:41:19', 1, '2025-04-25 22:41:19', '部门管理', '/dept', 'system/Dept', 'OfficeBuilding', '1', 4, 1, 1, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (15, 1, 1, '2025-04-25 22:42:14', 1, '2025-04-25 22:43:15', '日志管理', '/log', 'system/Log', 'Memo', '1', 5, 1, 1, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (16, 1, 1, '2025-04-27 16:47:48', 1, '2025-04-27 16:47:48', '日志详情', '/logDetail', 'system/LogDetail.vue', 'Document', '1', 7, 1, 0, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (17, null, 1, '2025-05-28 16:10:09', 1, '2025-05-29 14:31:24', '智能问答', '/ai', '', 'Tools', '0', 0, 1, 1, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (18, 17, 1, '2025-05-28 16:18:11', 1, '2025-05-29 14:32:06', '创建对话', '/dialog', 'chat/Chat', 'ChatDotSquare', '1', 0, 1, 1, '', '1');
INSERT INTO radish_master.sys_menu (id, parent_id, create_by, create_time, update_by, update_time, name, path, component, icon, type, order_num, status, visible, remark, del_flag) VALUES (19, 17, 1, '2025-05-29 15:35:28', 1, '2025-05-30 11:42:25', '历史对话', '/answerHistory', 'chat/AnswerHistory', 'Comment', '1', 2, 1, 1, '', '1');
