create table sys_file
(
    id            bigint       not null comment '主键'
        primary key,
    create_by     bigint       null comment '创建人',
    create_time   datetime     null comment '创建时间',
    update_by     bigint       null comment '修改人',
    update_time   datetime     null comment '修改时间',
    del_flag      char         null comment '逻辑删除；1：存在；0：删除',
    original_name varchar(100) null comment '源文件名',
    file_key      varchar(100) null comment '存储文件名',
    location      char         null comment '存储地址；1：minio；2：磁盘',
    bucket        varchar(100) null comment '桶名',
    object_key    varchar(500) null comment '文件路径',
    file_suffix   varchar(10)  null comment '文件后缀；示例：.txt、.jpg',
    file_size     bigint       null comment '文件大小；单位：B',
    file_hash     varchar(100) null comment '文件hash'
)
    comment '文件表';

create index idx_sys_file_hash
    on sys_file (file_hash)
    comment '文件hash普通索引';

create index idx_sys_file_key
    on sys_file (file_key)
    comment '存储文件名普通索引';

