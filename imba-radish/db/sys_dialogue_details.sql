create table sys_dialogue_details
(
    id          bigint   not null comment '主键'
        primary key,
    parent_id   bigint   null comment '表sys_dialogue_history的id',
    create_by   bigint   null comment '创建人',
    create_time datetime null comment '创建时间',
    update_by   bigint   null comment '修改人',
    update_time datetime null comment '修改时间',
    del_flag    char     null comment '逻辑删除；1：存在；0：删除',
    tag         tinyint  null comment '标识；1：用户；0：AI',
    content     text     null comment '对话内容'
)
    comment '智能对话详情表';

