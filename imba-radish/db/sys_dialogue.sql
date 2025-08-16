create table sys_dialogue
(
    id          bigint auto_increment comment '主键'
        primary key,
    sort_order  bigint       null comment '排序',
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    char         null comment '逻辑删除；1：存在；0：删除',
    title       varchar(100) null comment '标题',
    top_flag    tinyint      null comment '置顶标识；1：置顶；0：不置顶'
)
    comment '智能对话表';

INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (1, null, 1, '2025-05-30 17:50:40', 1, '2025-08-07 22:13:38', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (2, null, 1, '2025-05-30 18:22:08', 1, '2025-08-07 22:13:27', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (3, null, 1, '2025-05-30 18:23:16', 1, '2025-08-07 22:13:26', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (4, null, 1, '2025-05-30 18:24:43', 1, '2025-08-07 22:13:25', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (5, null, 1, '2025-05-30 18:28:16', 1, '2025-05-30 18:28:16', '1', '测试', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (6, null, 1, '2025-05-30 22:57:50', 1, '2025-08-07 22:13:23', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (7, null, 1, '2025-05-30 23:36:30', 1, '2025-08-07 22:13:21', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (8, null, 1, '2025-05-30 23:38:22', 1, '2025-08-07 22:13:19', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (9, null, 1, '2025-05-30 23:38:49', 1, '2025-08-07 22:13:18', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (10, null, 1, '2025-05-31 15:57:57', 1, '2025-08-07 22:13:16', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (11, null, 1, '2025-05-31 16:00:56', 1, '2025-08-07 22:13:14', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (12, null, 1, '2025-05-31 16:08:59', 1, '2025-08-07 22:13:13', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (13, null, 1, '2025-05-31 16:10:06', 1, '2025-08-07 22:13:11', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (14, null, 1, '2025-05-31 16:19:19', 1, '2025-08-07 22:13:09', '0', null, null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (15, null, 1, '2025-06-13 23:39:02', 1, '2025-06-13 23:39:02', '1', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (16, null, 1, '2025-06-13 23:40:35', 1, '2025-06-13 23:40:35', '1', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (17, null, 1, '2025-06-26 19:57:48', 1, '2025-06-26 19:57:48', '1', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (18, null, 1, '2025-07-12 12:26:21', 1, '2025-07-12 12:26:21', '1', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (19, null, 1, '2025-07-13 20:33:57', 1, '2025-07-13 20:34:52', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (20, null, 1, '2025-07-13 20:43:12', 1, '2025-08-07 21:17:27', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (21, null, 1, '2025-07-13 20:58:18', 1, '2025-08-07 21:01:40', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (22, null, 1, '2025-07-15 19:19:02', 1, '2025-08-07 21:01:05', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (23, null, 1, '2025-07-15 19:19:18', 1, '2025-08-07 20:59:55', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (24, null, 1, '2025-08-04 16:08:34', 1, '2025-08-07 20:58:54', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (25, null, 1, '2025-08-04 16:14:23', 1, '2025-08-07 20:58:06', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (26, null, 1, '2025-08-04 16:22:40', 1, '2025-08-07 20:56:31', '0', '新对话', null);
INSERT INTO radish_master.sys_dialogue (id, sort_order, create_by, create_time, update_by, update_time, del_flag, title, top_flag) VALUES (27, null, 1, '2025-08-04 16:30:40', 1, '2025-08-07 20:55:54', '0', '新对话', null);
