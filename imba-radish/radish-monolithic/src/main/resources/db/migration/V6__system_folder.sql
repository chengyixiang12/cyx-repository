-- ============================================================
-- V6: 系统文件夹预置
-- 固定主键与 BaseConstant.SYSTEM_FOLDER_ID 对应：根目录上传的文件默认归类到该文件夹
-- 幂等插入：已存在同 id 记录（手工创建）时跳过，保留原名称
-- ============================================================

INSERT INTO sys_folder (id, create_by, create_time, update_by, update_time, del_flag, name, parent_id)
SELECT 2126672855014608898, 1984481955388682242, '2026-11-28 21:45:00', 1984481955388682242, '2026-11-28 21:45:00', '0', '系统', NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_folder WHERE id = 2126672855014608898);
