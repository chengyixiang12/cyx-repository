create table sys_permission
(
    id          bigint auto_increment comment '主键'
        primary key,
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    char         null comment '逻辑删除',
    name        varchar(50)  null comment '权限名称',
    code        varchar(50)  not null comment '权限编码',
    type        char         null comment '权限类型；1：菜单；2：按钮',
    status      char         null comment '状态；1：启用；0：禁用',
    description varchar(255) null comment '描述'
)
    comment '权限表';

create index idx_sys_permission_code
    on sys_permission (code)
    comment '权限编码普通索引';

INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1, 1, '2024-11-20 20:06:35', 1, '2024-11-20 20:06:41', '1', '添加菜单', 'sys_menu_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (2, 1, '2024-11-20 20:51:39', 1, '2024-11-20 20:51:39', '1', '添加角色', 'sys_role_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (3, 1, '2024-11-20 20:53:17', 1, '2024-11-20 20:53:17', '1', '添加权限', 'sys_pms_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (4, 1, '2024-11-20 20:53:44', 1, '2024-11-20 20:53:44', '1', '编辑角色', 'sys_role_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (5, 1, '2024-11-20 20:55:08', 1, '2024-11-20 20:55:08', '1', '编辑菜单', 'sys_menu_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (6, 1, '2024-11-20 20:55:18', 1, '2024-11-20 20:55:18', '1', '删除菜单', 'sys_menu_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (7, 1, '2024-11-20 21:39:14', 1, '2024-11-20 21:39:14', '1', '添加部门', 'sys_dept_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (8, 1, '2024-11-20 21:39:26', 1, '2024-11-20 21:39:26', '1', '编辑部门', 'sys_dept_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (9, 1, '2024-11-20 21:39:52', 1, '2024-11-20 21:39:52', '1', '删除部门', 'sys_dept_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (10, 1, '2024-11-20 21:41:34', 1, '2024-11-20 21:41:34', '0', '用户赋予角色', 'sys_user_set_role', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (11, 1, '2024-11-20 21:42:27', 1, '2024-11-20 21:42:27', '1', '角色赋予菜单', 'sys_role_set_menu', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (12, 1, '2024-11-20 21:42:38', 1, '2024-11-20 21:42:38', '1', '角色赋予权限', 'sys_role_set_per', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (13, 1, '2024-11-20 21:47:29', 1, '2024-11-20 21:47:29', '1', '设置默认角色', 'sys_role_set_def', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (14, 1, '2024-11-20 21:48:11', 1, '2024-11-20 21:48:11', '1', '禁用角色', 'sys_role_fbn', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (15, 1, '2024-11-20 21:48:37', 1, '2024-11-20 21:48:37', '1', '启用角色', 'sys_role_enable', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (16, 1, '2024-11-20 21:51:21', 1, '2024-11-20 21:51:21', '1', '添加字典数据', 'sys_dict_data_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (17, 1, '2024-11-20 21:51:46', 1, '2024-11-20 21:51:46', '1', '编辑字典数据', 'sys_dict_data_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (18, 1, '2024-11-20 21:52:10', 1, '2024-11-20 21:52:10', '1', '删除字典数据', 'sys_dict_data_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (19, 1, '2024-11-20 21:52:51', 1, '2024-11-20 21:52:51', '1', '添加字典类型', 'sys_dict_type_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (20, 1, '2024-11-20 21:53:21', 1, '2024-11-20 21:53:21', '1', '编辑字典类型', 'sys_dict_type_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (21, 1, '2024-11-20 21:53:44', 1, '2024-11-20 21:53:44', '1', '删除字典类型', 'sys_dict_type_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (22, 1, '2024-11-20 21:54:18', 1, '2024-11-20 21:54:18', '1', '删除文件', 'sys_file_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (23, 1, '2024-11-20 21:57:41', 1, '2024-11-20 21:57:41', '1', '删除角色', 'sys_role_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (24, 1, '2024-11-20 21:58:58', 1, '2024-11-20 21:58:58', '1', '重置密码', 'sys_user_reset', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (25, 1, '2024-11-20 21:59:34', 1, '2024-11-20 21:59:34', '1', '添加用户', 'sys_user_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (27, 1, '2024-11-20 22:01:40', 1, '2024-11-20 22:01:40', '1', '编辑用户', 'sys_user_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (28, 1, '2024-11-26 19:23:51', 1, '2024-11-26 19:23:51', '1', '生成密钥', 'sys_secret_key_generate', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (29, 1, '2025-03-28 11:00:40', 1, '2025-03-28 11:00:40', '1', '删除用户', 'sys_user_delete', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (30, 1, '2025-04-17 18:30:21', 1, '2025-04-17 18:30:21', '1', '锁定用户', 'sys_user_lock', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (31, 1, '2025-04-17 18:31:31', 1, '2025-04-17 18:31:31', '1', '解锁用户', 'sys_user_unlock', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (32, 1, '2025-07-13 20:52:28', 1, '2025-07-13 20:52:28', '1', '启用用户', 'sys_user_enable', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (33, 1, '2025-07-13 20:54:00', 1, '2025-07-13 20:54:00', '1', '禁用用户', 'sys_user_forbidden', '2', '1', '');
