create table sys_dict_data
(
    id          bigint auto_increment comment '主键'
        primary key,
    sort_order  int          null comment '排序',
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    char         null comment '逻辑删除；1：存在；0：删除',
    code        varchar(100) null comment '编码',
    label       varchar(100) null comment '标签',
    value       varchar(255) null comment '键值',
    dict_type   varchar(100) null comment '字典类型',
    css_class   varchar(255) null comment '样式属性（其他样式扩展）',
    list_class  varchar(255) null comment '表格回显样式',
    is_default  tinyint      null comment '是否默认；1：是 0：否',
    status      tinyint      null comment '状态；1：启用；0：停用',
    remark      varchar(500) null comment '备注'
)
    comment '字典数据表';

create index idx_sys_dict_data_code
    on sys_dict_data (code)
    comment '字典编码普通索引';

create index idx_sys_dict_data_dict_type
    on sys_dict_data (dict_type);

INSERT INTO radish_master.sys_dict_data (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1, 1, 1, '2025-04-24 15:03:44', 1, '2025-04-24 17:25:01', '1', 'male', '男', '1', 'gender', '', '', 1, 1, '1');
INSERT INTO radish_master.sys_dict_data (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (2, 2, 1, '2025-04-24 17:12:48', 1, '2025-04-24 17:12:48', '1', 'female', '女', '0', 'gender', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (3, 1, 1, '2025-04-25 22:48:59', 1, '2025-04-25 22:48:59', '1', 'enable', '启用', '1', 'status', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (4, 2, 1, '2025-04-25 22:49:14', 1, '2025-04-25 22:49:14', '1', 'forbbien', '禁用', '0', 'status', '', '', 1, 1, '');
INSERT INTO radish_master.sys_dict_data (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (5, 1, 1, '2025-07-14 20:03:57', 1, '2025-07-14 20:03:57', '1', 'storage_scan', '存储文件扫描', 'storage_scan', 'schedule_type', '', '', 0, 1, '');
