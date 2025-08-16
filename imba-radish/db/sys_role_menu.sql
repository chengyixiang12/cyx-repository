create table sys_role_menu
(
    role_id bigint not null comment '角色id',
    menu_id bigint not null comment '菜单id',
    constraint uk_sys_role_menu
        unique (role_id, menu_id)
)
    comment '角色菜单表';

INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 1);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 2);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 4);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 5);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 6);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 7);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 8);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 12);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 13);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 14);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 15);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 16);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 17);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 18);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1, 19);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (2, 2);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (2, 7);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (2, 8);
