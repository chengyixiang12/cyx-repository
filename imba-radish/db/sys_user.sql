create table sys_user
(
    id                      bigint               not null
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

INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES (1984481955388682242, 1984481955388682242, '2024-10-24 07:25:34', 1984481955388682242, '2025-05-29 13:24:29', '0', 'admin', '$2a$10$7GBeWjeLbS1OUOFl18nvMeiww0cAtVQRCnNOQJBVgzoJX.KMaezSy', '小程', '', '', 1, 1, 1, 1, 1984483212773847041, 11);
