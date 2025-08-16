create table sys_user_role
(
    role_id bigint not null comment '角色主键',
    user_id bigint not null comment '用户主键',
    constraint uk_role_user
        unique (role_id, user_id)
)
    comment '用户角色表';

INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (1, 1);
INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (1, 2);
INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (2, 11);
INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (2, 12);
INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (2, 13);
INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (2, 14);
