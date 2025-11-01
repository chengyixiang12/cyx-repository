create table sys_dict_type
(
    id          bigint       not null comment '主键'
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

INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1984484774384603137, 2, 1984481955388682242, '2025-04-23 22:52:19', 1984481955388682242, '2025-04-23 23:04:57', '0', '性别', null, 1, '表示男女的性别');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1984484774405574658, 2, 1984481955388682242, '2025-04-25 22:48:38', 1984481955388682242, '2025-04-25 22:50:17', '0', '状态', null, 1, '');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1984484774405574659, 3, 1984481955388682242, '2025-07-13 16:34:09', 1984481955388682242, '2025-10-26 18:25:35', '0', '调度类型', null, 1, '');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1984484774405574660, 4, 1984481955388682242, '2025-09-06 22:16:11', 1984481955388682242, '2025-10-21 13:47:08', '0', '文件存储位置', null, 1, 'org.apache.catalina.webresources.Cache.getResource Unable to add the resource at [/uploadPath/home/NewCarPhoto/2025/10/21/4564117022241344294_vinpicture_1_20251021092127_12538.jpg] to the cache for web application [] because there was insufficient free space available after evicting expired cache entries - consider increasing the maximum size of the cache');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1984484774405574661, null, 1984481955388682242, '2025-10-26 18:24:18', 1984481955388682242, '2025-10-26 18:24:18', '0', '调度类型', null, 1, '');
INSERT INTO radish_master.sys_dict_type (id, sort_order, create_by, create_time, update_by, update_time, del_flag, dict_name, dict_type, status, remark) VALUES (1984484774405574662, null, 1984481955388682242, '2025-10-26 18:26:42', 1984481955388682242, '2025-10-26 18:26:42', '0', '作业类型', null, 1, '');
