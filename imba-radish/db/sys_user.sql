create table sys_user
(
    id                      bigint auto_increment
        primary key,
    create_by               bigint               null comment '创建人',
    create_time             datetime             null comment '创建时间',
    update_by               bigint               null comment '修改人',
    update_time             datetime             null comment '修改时间',
    del_flag                char                 null comment '逻辑删除；1：存在；0：不存在',
    username                varchar(20)          not null comment '用户名',
    password                varchar(100)         not null comment '密码',
    nickname                varchar(20)          null comment '昵称',
    email                   varchar(50)          not null comment '邮箱',
    phone                   varchar(11)          null comment '电话',
    enabled                 tinyint(1) default 1 null comment '用户是否被启用；1：启用；0：禁用',
    account_non_locked      tinyint(1) default 1 null comment '账户是否被锁定；1：正常；0：锁定',
    credentials_non_expired tinyint(1) default 1 null comment '凭证是否过期；1：正常；0：过期',
    account_non_expired     tinyint(1) default 1 null comment '账户是否过期；1：正常；0：过期',
    dept_id                 bigint               null comment '部门id',
    avatar                  bigint               null comment '用户头像'
)
    comment '用户表';

create index idx_sys_user_email
    on sys_user (email)
    comment '邮箱普通索引';

create index idx_sys_user_username
    on sys_user (username)
    comment '用户名普通索引';

INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (1, 1, '2024-10-24 07:25:34', 1, '2025-05-29 13:24:29', '1', 'admin', '$2a$10$7GBeWjeLbS1OUOFl18nvMeiww0cAtVQRCnNOQJBVgzoJX.KMaezSy', '程益祥', '1907452460@qq.com', '13235423290', 1, 1, 1, 1, 1, 11);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (2, 1, '2024-11-09 23:31:22', 1, '2025-04-04 20:31:50', '0', 'zjy001', '$2a$10$toTWMK/I76kY2SwHPKX2k.EQqYcjG14PWjJ.H4dkGPIzE.joJ0x3a', '张佳耀', '3332951238@qq.com', null, 1, 1, 1, 1, 1, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (7, 1, '2025-03-28 10:04:54', 1, '2025-04-04 19:46:33', '0', 'lisi', '$2a$10$AT5yQG/hYRWRBMusTn.EjOIV3I4Hi/UEx0Eou1AL.v/VifNHwwk0W', '李四', '123@qq.com', '13245631345', 1, 1, 1, 1, 0, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (8, 1, '2025-04-04 11:49:06', 1, '2025-04-04 19:46:30', '0', 'alice', '$2a$10$l3shGrXf38J6PFeh1HZluO5j/nv1hJjFVvY2dHOizlcE6l9wFGh56', '爱丽丝', '1@qq.com', '12345678901', 1, 1, 1, 1, 17, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (9, 1, '2025-04-04 11:55:22', 1, '2025-04-04 19:42:06', '0', 'a1', '$2a$10$8WvumV4O8v/mz4H0v1ud9ecwkxrdiV1Qe7//YqwWaUzqoZIpntDJ2', 'a1', '1@163.com', '', 1, 1, 1, 1, 6, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (11, 1, '2025-04-04 20:39:55', 1, '2025-04-04 20:40:04', '0', 'a', '$2a$10$Mc5tVybz4OGjxbgQhBOytuyNQZ7bgPdEQK4RNeVNAgacWVZqqNWhS', 'a', 'a@qq.com', '', 1, 1, 1, 1, 6, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (12, 1, '2025-04-09 22:55:55', 1, '2025-04-09 22:58:49', '0', '1', '$2a$10$PFbiCf3w0Znunx.OaCsC7ulkkvSwOPzQeMfCHqrW03YVY7VYC0kTm', '1', '1@qq.com', '', 1, 1, 1, 1, 6, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (13, 1, '2025-04-15 21:56:02', 1, '2025-04-25 22:53:39', '1', 'radish', '$2a$10$zH.6/PKSj6KChbvW.IeWZO43/qSA/W81ZD/G8Kk0eJjpBQLeVnP9G', '萝卜', '862955299@qq.com', '12345678901', 1, 1, 1, 1, 8, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (14, 1, '2025-05-11 21:11:19', 1, '2025-05-11 22:09:27', '1', 'cc', '$2a$10$0S01wMuOSb4qcANkpXI1ROAjmZd1jvRPQOPYPgZpePReQOphDl3qO', '陈澄', '15061019560@163.com', '15061019560', 0, 1, 1, 1, 18, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (15, 1, '2025-05-17 21:45:46', 1, '2025-05-17 22:02:29', '0', 'csuser', '$2a$10$L1gQ4CXN8xC616eu0rTCPOHUm8aWNNbCbQ.S2Jbt64gOUWb6uhLWO', '测试用户', '13235423290@163.com', null, 1, 1, 1, 1, 1, null);
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (16, 1, '2025-05-17 22:03:23', 1, '2025-05-17 22:03:23', '1', 'csuser', '$2a$10$gbOHDbxAd69qY7DefIIgz.KmHMjS0XLc9ARrbVS/k1brngd8wQ//2', '测试用户', '13235423290@163.com', null, 0, 1, 1, 1, 1, null);
