create table sys_announcement
(
    id           bigint auto_increment comment '主键'
        primary key,
    sort_order   int           null comment '排序',
    create_by    bigint        null comment '创建人',
    create_time  datetime      null comment '创建时间',
    update_by    bigint        null comment '修改人',
    update_time  datetime      null comment '修改时间',
    del_flag     char          null comment '逻辑删除；1：存在；0：删除',
    title        varchar(255)  null comment '公告标题',
    publish_time datetime      null comment '公布时间',
    context      varchar(1000) null comment '公告内容',
    status       char          null comment '状态；1：发布；0：草稿'
)
    comment '公告表';

