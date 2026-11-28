-- ============================================================
-- V5: 文件管理文件夹支持
-- sys_folder 文件夹表 + sys_file.folder_id 归属列 + 文件夹权限码
-- 旧数据兼容：folder_id 为 NULL 表示根目录，存量文件无需迁移
-- ============================================================

-- 1. 文件夹表
CREATE TABLE `sys_folder` (
                              `id` bigint NOT NULL COMMENT '主键',
                              `create_by` bigint DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `update_by` bigint DEFAULT NULL COMMENT '修改人',
                              `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '逻辑删除标记',
                              `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件夹名称',
                              `parent_id` bigint DEFAULT NULL COMMENT '父文件夹主键；NULL：根目录',
                              PRIMARY KEY (`id`),
                              KEY `idx_sys_folder_parent_id` (`parent_id`) COMMENT '父级文件夹普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件文件夹表';

-- 2. 文件归属文件夹
ALTER TABLE `sys_file`
    ADD COLUMN `folder_id` bigint DEFAULT NULL COMMENT '所属文件夹主键；NULL：根目录' AFTER `object_key`,
    ADD KEY `idx_sys_file_folder_id` (`folder_id`) COMMENT '文件所属文件夹普通索引';

-- 3. 文件夹权限码
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2115000000000000011, 1984481955388682242, '2026-11-28 20:40:00', 1984481955388682242, '2026-11-28 20:40:00', '0', '新建文件夹', 'sys_folder_add', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2115000000000000012, 1984481955388682242, '2026-11-28 20:40:00', 1984481955388682242, '2026-11-28 20:40:00', '0', '重命名文件夹', 'sys_folder_edit', '2', '1', NULL);
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2115000000000000013, 1984481955388682242, '2026-11-28 20:40:00', 1984481955388682242, '2026-11-28 20:40:00', '0', '删除文件夹', 'sys_folder_del', '2', '1', NULL);

-- 4. 授予管理员角色权限
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2115000000000000011);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2115000000000000012);
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2115000000000000013);
