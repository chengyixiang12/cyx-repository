create table sys_permission
(
    id          bigint       not null comment '主键'
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

INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415355244546, 1984481955388682242, '2024-11-20 20:06:35', 1984481955388682242, '2024-11-20 20:06:41', '0', '添加菜单', 'sys_menu_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415409770497, 1984481955388682242, '2024-11-20 20:51:39', 1984481955388682242, '2024-11-20 20:51:39', '0', '添加角色', 'sys_role_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415409770498, 1984481955388682242, '2024-11-20 20:53:17', 1984481955388682242, '2024-11-20 20:53:17', '0', '添加权限', 'sys_pms_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415409770499, 1984481955388682242, '2024-11-20 20:53:44', 1984481955388682242, '2024-11-20 20:53:44', '0', '编辑角色', 'sys_role_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415409770500, 1984481955388682242, '2024-11-20 20:55:08', 1984481955388682242, '2024-11-20 20:55:08', '0', '编辑菜单', 'sys_menu_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415409770501, 1984481955388682242, '2024-11-20 20:55:18', 1984481955388682242, '2024-11-20 20:55:18', '0', '删除菜单', 'sys_menu_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415409770502, 1984481955388682242, '2024-11-20 21:39:14', 1984481955388682242, '2024-11-20 21:39:14', '0', '添加部门', 'sys_dept_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130625, 1984481955388682242, '2024-11-20 21:39:26', 1984481955388682242, '2024-11-20 21:39:26', '0', '编辑部门', 'sys_dept_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130626, 1984481955388682242, '2024-11-20 21:39:52', 1984481955388682242, '2024-11-20 21:39:52', '0', '删除部门', 'sys_dept_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130627, 1984481955388682242, '2024-11-20 21:42:27', 1984481955388682242, '2024-11-20 21:42:27', '0', '角色赋予菜单', 'sys_role_set_menu', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130628, 1984481955388682242, '2024-11-20 21:42:38', 1984481955388682242, '2024-11-20 21:42:38', '0', '角色赋予权限', 'sys_role_set_per', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130629, 1984481955388682242, '2024-11-20 21:47:29', 1984481955388682242, '2024-11-20 21:47:29', '0', '设置默认角色', 'sys_role_set_def', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130630, 1984481955388682242, '2024-11-20 21:48:11', 1984481955388682242, '2024-11-20 21:48:11', '0', '禁用角色', 'sys_role_fbn', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415439130631, 1984481955388682242, '2024-11-20 21:48:37', 1984481955388682242, '2024-11-20 21:48:37', '0', '启用角色', 'sys_role_enable', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490754, 1984481955388682242, '2024-11-20 21:51:21', 1984481955388682242, '2024-11-20 21:51:21', '0', '添加字典数据', 'sys_dict_data_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490755, 1984481955388682242, '2024-11-20 21:51:46', 1984481955388682242, '2024-11-20 21:51:46', '0', '编辑字典数据', 'sys_dict_data_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490756, 1984481955388682242, '2024-11-20 21:52:10', 1984481955388682242, '2024-11-20 21:52:10', '0', '删除字典数据', 'sys_dict_data_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490757, 1984481955388682242, '2024-11-20 21:52:51', 1984481955388682242, '2024-11-20 21:52:51', '0', '添加字典类型', 'sys_dict_type_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490758, 1984481955388682242, '2024-11-20 21:53:21', 1984481955388682242, '2024-11-20 21:53:21', '0', '编辑字典类型', 'sys_dict_type_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490759, 1984481955388682242, '2024-11-20 21:53:44', 1984481955388682242, '2024-11-20 21:53:44', '0', '删除字典类型', 'sys_dict_type_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490760, 1984481955388682242, '2024-11-20 21:54:18', 1984481955388682242, '2024-11-20 21:54:18', '0', '删除文件', 'sys_file_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490761, 1984481955388682242, '2024-11-20 21:57:41', 1984481955388682242, '2024-11-20 21:57:41', '0', '删除角色', 'sys_role_del', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415468490762, 1984481955388682242, '2024-11-20 21:58:58', 1984481955388682242, '2024-11-20 21:58:58', '0', '重置密码', 'sys_user_reset', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850881, 1984481955388682242, '2024-11-20 21:59:34', 1984481955388682242, '2024-11-20 21:59:34', '0', '添加用户', 'sys_user_add', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850882, 1984481955388682242, '2024-11-20 22:01:40', 1984481955388682242, '2024-11-20 22:01:40', '0', '编辑用户', 'sys_user_edit', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850883, 1984481955388682242, '2024-11-26 19:23:51', 1984481955388682242, '2024-11-26 19:23:51', '0', '生成密钥', 'sys_secret_key_generate', '2', '1', null);
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850884, 1984481955388682242, '2025-03-28 11:00:40', 1984481955388682242, '2025-03-28 11:00:40', '0', '删除用户', 'sys_user_delete', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850885, 1984481955388682242, '2025-04-17 18:30:21', 1984481955388682242, '2025-04-17 18:30:21', '0', '锁定用户', 'sys_user_lock', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850886, 1984481955388682242, '2025-04-17 18:31:31', 1984481955388682242, '2025-04-17 18:31:31', '0', '解锁用户', 'sys_user_unlock', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850887, 1984481955388682242, '2025-07-13 20:52:28', 1984481955388682242, '2025-07-13 20:52:28', '0', '启用用户', 'sys_user_enable', '2', '1', '');
INSERT INTO radish_master.sys_permission (id, create_by, create_time, update_by, update_time, del_flag, name, code, type, status, description) VALUES (1984481415497850888, 1984481955388682242, '2025-07-13 20:54:00', 1984481955388682242, '2025-07-13 20:54:00', '0', '禁用用户', 'sys_user_forbidden', '2', '1', '');
