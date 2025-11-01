create table sys_dict_data
(
    id          bigint       not null comment '主键'
        primary key,
    parent_id   bigint       null comment 'sys_dict_type的主键',
    sort_order  int          null comment '排序',
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    char         null comment '逻辑删除；1：存在；0：删除',
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

create index idx_sys_dict_data_dict_type
    on sys_dict_data (dict_type);

INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867211083778, 1984484774384603137, 1, 1984481955388682242, '2025-04-24 15:03:44', 1984481955388682242, '2025-04-24 17:25:01', '0', '男', '1', 'gender', '', '', 1, 1, '1');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867253026818, 1984484774384603137, 2, 1984481955388682242, '2025-04-24 17:12:48', 1984481955388682242, '2025-04-24 17:12:48', '0', '女', '0', 'gender', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867253026819, 1984484774405574658, 1, 1984481955388682242, '2025-04-25 22:48:59', 1984481955388682242, '2025-04-25 22:48:59', '0', '启用', '1', 'status', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867253026820, 1984484774405574658, 2, 1984481955388682242, '2025-04-25 22:49:14', 1984481955388682242, '2025-04-25 22:49:14', '0', '禁用', '0', 'status', '', '', 1, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867253026821, 1984484774405574659, 1, 1984481955388682242, '2025-07-14 20:03:57', 1984481955388682242, '2025-07-14 20:03:57', '0', '存储文件扫描', 'storage_scan', 'schedule_type', '', '', 1, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867282386946, 1984484774405574660, 1, 1984481955388682242, '2025-09-06 22:16:42', 1984481955388682242, '2025-09-06 22:16:42', '0', 'minio存储', '1', 'file_storage_location', '', '', 1, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867282386947, 1984484774405574660, 2, 1984481955388682242, '2025-09-06 22:16:58', 1984481955388682242, '2025-09-06 22:16:58', '0', '本地磁盘', '2', 'file_storage_location', '', '', 0, 0, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867282386948, 1984484774405574661, null, 1984481955388682242, '2025-10-26 18:24:58', 1984481955388682242, '2025-10-26 18:24:58', '0', '简单调度', '0', 'quartz_type', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867282386949, 1984484774405574661, null, 1984481955388682242, '2025-10-26 18:25:11', 1984481955388682242, '2025-10-26 18:25:11', '0', 'cron调度', '1', 'quartz_type', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867311747074, 1984484774405574662, null, 1984481955388682242, '2025-10-26 18:28:43', 1984481955388682242, '2025-10-26 18:28:43', '0', '存储文件扫描', 'storage_scan', 'job_type', '', '', 0, 1, '');
INSERT INTO radish_master.sys_dict_data (id, parent_id, sort_order, create_by, create_time, update_by, update_time, del_flag, label, value, dict_type, css_class, list_class, is_default, status, remark) VALUES (1984483867311747075, 1984484774405574662, null, 1984481955388682242, '2025-11-01 11:24:35', 1984481955388682242, '2025-11-01 11:24:35', '0', '修改主键', 'update_primary_key', null, '', '', 0, 1, '');
