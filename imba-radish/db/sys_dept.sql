create table sys_dept
(
    id          bigint auto_increment comment '主键'
        primary key,
    sort_order  int         null comment '排序字段',
    create_by   bigint      null comment '创建人',
    create_time datetime    null comment '创建时间',
    update_by   bigint      null comment '修改人',
    update_time datetime    null comment '修改时间',
    del_flag    tinyint     null comment '逻辑删除',
    code        varchar(20) null comment '部门编码',
    name        varchar(20) null comment '部门名称',
    parent_id   bigint      null comment '父级id',
    level       tinyint     null comment '层级'
)
    comment '部门表';

create index idx_sys_dept_code
    on sys_dept (code)
    comment '部门编码普通索引';

INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (1984483212773847041, 1, 1984481955388682242, '2024-10-26 10:58:36', 1984481955388682242, '2025-05-16 23:49:42', 0, 'IT', '互联网公司', null, 1);
