create table sys_dialogue
(
    id          bigint       not null comment '主键'
        primary key,
    sort_order  bigint       null comment '排序',
    create_by   bigint       null comment '创建人',
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '修改人',
    update_time datetime     null comment '修改时间',
    del_flag    tinyint      null comment '逻辑删除',
    title       varchar(100) null comment '标题',
    top_flag    tinyint      null comment '置顶标识；1：置顶；0：不置顶'
)
    comment '智能对话表';

