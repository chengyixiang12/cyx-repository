create table sys_file
(
    id            bigint auto_increment comment '主键'
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

INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (1, 1, '2025-02-18 14:30:32', 1, '2025-02-18 14:30:35', '1', '刘亦菲.jpg', 'ca22f9fdff0a44a4b5c430b7b30bf025', '1', 'cyx', '20250218/ca22f9fdff0a44a4b5c430b7b30bf025.jpg', '.jpg', 30852, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (2, 1, '2025-02-18 15:18:25', 1, '2025-02-18 15:18:25', '1', '吴彦祖.png', 'e5986c4ae1ed4c7e8fc87d17a2340516', '1', 'cyx', '20250218/e5986c4ae1ed4c7e8fc87d17a2340516.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (3, 1, '2025-02-28 11:37:09', 1, '2025-02-28 11:37:09', '1', 'openjdk-17.0.2_windows-x64_bin.zip', 'c13aee7bb33b4a56b86cef57efd6d67d', '1', 'cyx', '20250228/c13aee7bb33b4a56b86cef57efd6d67d.zip', '.zip', 186216309, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (4, 1, '2025-04-03 22:28:41', 1, '2025-04-03 22:28:41', '1', '吴彦祖.png', '6c31286234334103b3dfd9d52b2f679b', '1', 'cyx', '20250403/6c31286234334103b3dfd9d52b2f679b.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (5, 1, '2025-04-03 22:51:36', 1, '2025-04-03 22:51:36', '1', '吴彦祖.png', '884e7e1e340a4366ab5ae03f05de5aea', '1', 'cyx', '20250403/884e7e1e340a4366ab5ae03f05de5aea.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (6, 1, '2025-04-03 22:55:43', 1, '2025-04-03 22:55:43', '1', '吴彦祖.png', '37a388ed9c064179a0f08f10ada065bf', '1', 'cyx', '20250403/37a388ed9c064179a0f08f10ada065bf.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (7, 1, '2025-04-04 00:14:04', 1, '2025-04-04 00:14:04', '1', '吴彦祖.png', 'b008b734d81d4400a04302822b431d10', '1', 'cyx', '20250404/b008b734d81d4400a04302822b431d10.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (8, 1, '2025-04-04 00:17:40', 1, '2025-04-04 00:17:40', '1', '吴彦祖.png', '6278b086c32d41cb83bea7d4c1df05dd', '1', 'cyx', '20250404/6278b086c32d41cb83bea7d4c1df05dd.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (9, 1, '2025-04-04 00:35:55', 1, '2025-04-04 00:35:55', '1', '吴彦祖.png', '89bc71b7c3c4433cb50994c6337e7186', '1', 'cyx', '20250404/89bc71b7c3c4433cb50994c6337e7186.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (10, 1, '2025-05-29 21:19:55', 1, '2025-05-29 21:19:55', '1', '吴彦祖.png', '3e985f7e1aa24da9ba95a4f2170fb8cd', '1', 'cyx', '20250529/3e985f7e1aa24da9ba95a4f2170fb8cd.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (11, 1, '2025-05-29 21:23:42', 1, '2025-05-29 21:23:42', '1', '吴彦祖.png', '14f45f359e034c08ad95d3256fdb0df3', '1', 'avatar', '20250529/14f45f359e034c08ad95d3256fdb0df3.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (12, 1, '2025-05-29 21:42:49', 1, '2025-05-29 21:42:49', '1', '吴彦祖.png', 'a054583c707a4906964cde45e4439a35', '1', 'avatar', '20250529/a054583c707a4906964cde45e4439a35.png', '.png', 866889, null);
INSERT INTO radish_master.sys_file (id, create_by, create_time, update_by, update_time, del_flag, original_name, file_key, location, bucket, object_key, file_suffix, file_size, file_hash) VALUES (13, 1, '2025-08-07 16:46:39', 1, '2025-08-07 16:46:39', '1', '截至二季度用车大户清单-门禁联网情况0731.xlsx', '8cac9127cef14d369372cbbe99457461', '1', 'cyx', '20250807/8cac9127cef14d369372cbbe99457461.xlsx', '.xlsx', 5841881, null);
