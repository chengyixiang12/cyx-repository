create table sys_dept
(
    id          bigint auto_increment comment '主键'
        primary key,
    sort_order  int         null comment '排序字段',
    create_by   bigint      null comment '创建人',
    create_time datetime    null comment '创建时间',
    update_by   bigint      null comment '修改人',
    update_time datetime    null comment '修改时间',
    del_flag    char        null comment '逻辑删除；1：存在；0：删除',
    code        varchar(20) null comment '部门编码',
    name        varchar(20) null comment '部门名称',
    parent_id   bigint      null comment '父级id',
    level       tinyint     null comment '层级'
)
    comment '部门表';

create index idx_sys_dept_code
    on sys_dept (code)
    comment '部门编码普通索引';

INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (1, 1, 1, '2024-10-26 10:58:36', 1, '2025-05-16 23:49:42', '1', 'IT', '互联网公司', null, 1);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (2, 2, 1, '2024-10-26 11:28:40', 1, '2025-05-16 23:49:55', '1', 'product', '产品与研发部门', 1, 1);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (3, 3, 1, '2024-10-26 11:29:15', 1, '2025-05-16 23:51:11', '1', 'market', '市场与运营部门', 1, 1);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (4, 4, 1, '2024-10-26 11:29:30', 1, '2025-05-16 23:51:43', '1', 'support', '支持部门', 1, 1);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (5, 5, 1, '2024-10-26 11:29:42', 1, '2024-10-26 11:29:42', '1', '5', '战略与新业务部门', 1, 2);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (6, 6, 1, '2024-10-26 11:30:02', 1, '2024-10-26 11:30:02', '1', '6', '高级管理层', 1, 2);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (7, 7, 1, '2024-10-26 11:30:33', 1, '2024-10-26 11:30:33', '1', '7', '产品部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (8, 8, 1, '2024-10-26 11:31:10', 1, '2024-10-26 11:31:10', '1', '8', '技术研发部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (9, 9, 1, '2024-10-26 11:33:43', 1, '2024-10-26 11:33:43', '1', '9', '测试部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (10, 10, 1, '2024-10-26 11:34:19', 1, '2024-10-26 11:34:19', '1', '10', '运维部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (11, 11, 1, '2024-10-26 11:34:33', 1, '2024-10-26 11:34:33', '1', '11', '大数据与算法部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (12, 12, 1, '2024-10-26 11:34:43', 1, '2024-10-26 11:34:43', '1', '12', 'AI与机器学习部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (13, 13, 1, '2024-10-26 11:34:53', 1, '2024-10-26 11:34:53', '1', '13', '架构部', 2, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (14, 14, 1, '2024-10-26 11:35:39', 1, '2024-10-26 11:35:39', '1', '14', '市场部', 3, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (15, 15, 1, '2024-10-26 11:36:02', 1, '2024-10-26 11:36:02', '1', '15', '运营部', 3, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (16, 16, 1, '2024-10-26 11:36:13', 1, '2024-10-26 11:36:13', '1', '16', '销售部', 3, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (17, 17, 1, '2024-10-26 11:36:23', 1, '2024-10-26 11:36:23', '1', '17', '客户支持部', 3, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (18, 18, 1, '2024-10-26 11:36:46', 1, '2024-10-26 11:36:46', '1', '18', '人力资源部（HR）', 4, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (19, 19, 1, '2024-10-26 11:37:02', 1, '2024-10-26 11:37:02', '1', '19', '财务部', 4, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (20, 20, 1, '2024-10-26 11:37:15', 1, '2024-10-26 11:37:15', '1', '20', '行政部', 4, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (21, 21, 1, '2024-10-26 11:37:29', 1, '2024-10-26 11:37:29', '1', '21', '法务部', 4, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (22, 22, 1, '2024-10-26 11:37:59', 1, '2024-10-26 11:37:59', '1', '22', '战略部', 5, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (23, 23, 1, '2024-10-26 11:38:41', 1, '2024-10-26 11:38:41', '1', '23', '新业务拓展部', 5, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (24, 24, 1, '2024-10-26 11:38:51', 1, '2024-10-26 11:38:51', '1', '24', '投资与并购部', 5, 3);
INSERT INTO radish_master.sys_dept (id, sort_order, create_by, create_time, update_by, update_time, del_flag, code, name, parent_id, level) VALUES (25, null, 1, '2025-04-26 23:32:14', 1, '2025-04-26 23:32:48', '0', 'asdasd', 'asdas', 6, 3);
