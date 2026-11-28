-- ============================================================
-- V7: 移动文件权限码
-- ============================================================

INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
SELECT 2115000000000000014, 1984481955388682242, '2026-11-28 22:15:00', 1984481955388682242, '2026-11-28 22:15:00', '0', '移动文件', 'sys_file_move', '2', '1', NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'sys_file_move');

INSERT INTO sys_role_permission
(role_id, permission_id)
SELECT 1984489247068725249, 2115000000000000014
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id = 1984489247068725249 AND permission_id = 2115000000000000014);
