create table sys_role_menu
(
    role_id bigint not null comment '角色id',
    menu_id bigint not null comment '菜单id',
    constraint uk_sys_role_menu
        unique (role_id, menu_id)
)
    comment '角色菜单表';

INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400598355969);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400631910403);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400631910404);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400657076226);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400657076227);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400686436353);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400686436354);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400686436355);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400686436356);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400715796482);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400715796483);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400715796484);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400715796485);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400715796486);
INSERT INTO radish_master.sys_role_menu (role_id, menu_id) VALUES (1984489247068725249, 1984485400715796487);
