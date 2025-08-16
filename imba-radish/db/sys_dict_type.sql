create table sys_dict_type
(
    id          bigint auto_increment comment '主键'
        primary key,
    sort_order  int          null comment '排序',
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    char         null comment '逻辑删除；1：存在；0：删除',
    dict_name   varchar(100) null comment '字典名称',
    dict_type   varchar(100) null comment '字典类型',
    status      tinyint      null comment '状态；1：启用；0：停用',
    remark      varchar(500) null comment '备注'
)
    comment '字典类型表';

create index idx_sys_dict_type
    on sys_dict_type (dict_type)
    comment '字典类型索引';

INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1, 2, 1, '2025-04-23 22:52:19', 1, '2025-04-23 23:04:57', '1', '性别', 'gender', 1, '表示男女的性别');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (2, 2, 1, '2025-04-25 22:48:38', 1, '2025-04-25 22:50:17', '1', '状态', 'status', 1, '');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (3, 3, 1, '2025-07-13 16:34:09', 1, '2025-07-13 16:34:09', '1', '调度类型', 'schedule_type', 1, '');
