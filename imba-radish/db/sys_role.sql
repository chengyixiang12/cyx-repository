create table sys_role
(
    id          bigint       not null comment '主键'
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

INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (1984489247068725249, 1, 1984481955388682242, '2024-10-25 15:07:55', 1984481955388682242, '2025-04-26 10:37:08', '0', 'ROLE_ADMIN', '管理员', '拥有所有权限', 1, 0, 1);
INSERT INTO radish_master.sys_role (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, description, status, is_default, fix_role) VALUES (1984489247123251201, 2, 1984481955388682242, '2024-10-25 15:08:15', 1984481955388682242, '2024-10-25 15:08:07', '0', 'ROLE_NORMAL', '普通用户', '没有任何权限', 1, 1, 1);
