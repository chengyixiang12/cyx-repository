create table sys_role_permission
(
    role_id       bigint not null comment '角色主键',
    permission_id bigint not null comment '权限主键',
    constraint uk_sys_role_permission
        unique (role_id, permission_id)
)
    comment '角色权限表';

INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 1);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 2);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 3);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 4);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 5);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 6);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 7);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 8);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 9);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 10);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 11);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 12);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 13);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 14);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 15);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 16);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 17);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 18);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 19);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 20);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 21);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 22);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 23);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 24);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 25);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 26);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 27);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 28);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 29);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 30);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 31);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 32);
INSERT INTO radish_master.sys_role_permission (role_id, permission_id) VALUES (1, 33);
