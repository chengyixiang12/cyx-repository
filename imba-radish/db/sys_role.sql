create table sys_role
(
    id          bigint auto_increment comment '主键'
        primary key,
    sort_order  int          null comment '排序字段',
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    char         null comment '逻辑删除；1：存在；0：删除',
    code        varchar(20)  null comment '角色编码',
    name        varchar(20)  null comment '角色名称',
    description varchar(500) null comment '描述',
    status      tinyint      null comment '状态；1：启用；0：禁用',
    is_default  tinyint      null comment '是否标记为系统默认角色；1：是；0：不是',
    fix_role    tinyint      null comment '是否为固定角色（固定角色无法被删除）；1：是；0：不是'
)
    comment '角色表';

create index idx_code
    on sys_role (code)
    comment '角色编码普通索引';

INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (1, 1, 1, '2024-10-25 15:07:55', 1, '2025-04-26 10:37:08', '1', 'ROLE_ADMIN', '管理员', '拥有所有权限', 1, 0, 1);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (2, 2, 1, '2024-10-25 15:08:15', 1, '2024-10-25 15:08:07', '1', 'ROLE_NORMAL', '普通用户', '没有任何权限', 1, 1, 1);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (3, 3, 1, '2024-10-25 15:21:10', 1, '2024-10-25 15:21:18', '1', 'ROLE_DPEPT_HEADER', '部门负责人', '负责自己的部门', 1, 0, 0);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (4, 4, 1, '2024-10-25 15:19:19', 1, '2024-10-25 15:21:21', '1', 'ROLE_TEAM_HEADER', '组长', '负责自己的小组', 0, 0, 0);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (5, 5, 1, '2024-10-25 15:26:12', 1, '2024-10-25 15:28:01', '1', 'ROLE_PROGRAM_MANAGER', '项目经理', '负责项目研发、测试、部署、运维', 0, 0, 0);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (6, 6, 1, '2024-10-25 15:29:03', 1, '2024-10-25 15:29:03', '1', 'ROLE_PROJECT_MANAGER', '产品经理', '负责项目设计、客户对接', 0, 0, 0);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (7, 7, 1, '2024-10-25 16:18:56', 1, '2024-10-25 16:18:56', '1', 'ROLE_JAVA_ENGINEER', 'Java开发工程师', '负责java项目的开发', 1, 0, 0);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (8, 8, 1, '2025-04-20 21:52:02', 1, '2025-04-20 22:00:10', '0', 'ROLE_REACT_ENGINEER', 'React研发工程师', '从事于react项目研发', 0, 0, 0);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (9, 10, 1, '2025-04-21 20:29:54', 1, '2025-07-14 20:04:55', '0', 'ROLE_RECAT_ENGINEER', 'React开发工程师', null, 1, 0, 0);
