create table sys_user_role
(
    role_id bigint not null comment '角色主键',
    user_id bigint not null comment '用户主键',
    constraint uk_role_user
        unique (role_id, user_id)
)
    comment '用户角色表';

INSERT INTO radish_master.sys_user_role (role_id, user_id) VALUES (1984489247068725249, 1984481955388682242);
