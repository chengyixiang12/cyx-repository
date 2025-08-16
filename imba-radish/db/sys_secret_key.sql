create table sys_secret_key
(
    id          bigint auto_increment comment '主键'
        primary key,
    create_by   bigint        null comment '创建人',
    create_time datetime      null comment '创建时间',
    update_by   bigint        null comment '修改人',
    update_time datetime      null comment '修改时间',
    del_flag    char          null comment '逻辑删除；1：存在；0：删除',
    public_key  varchar(400)  null comment '公钥',
    private_key varchar(2000) null comment '私钥',
    type        tinyint       not null comment '类型',
    description varchar(500)  null comment '描述'
)
    comment '密钥';

create index idx_sys_secret_key_type
    on sys_secret_key (type)
    comment '类型普通索引';

INSERT INTO radish_master.sys_secret_key (id, create_by, create_time, update_by, update_time, del_flag, public_key, private_key, type, description) VALUES (1, 1, '2024-11-26 19:52:27', 1, '2024-11-26 11:53:23', '1', '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', 0, null);
