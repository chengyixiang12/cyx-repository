-- ============================================================
-- V4: Redis 缓存监控模块
-- 菜单（系统管理下）+ 查看权限（sys_redis_view），并授予管理员角色
-- ============================================================

-- 1. 菜单：Redis缓存监控（挂在「系统管理」下）
INSERT INTO sys_menu
(id, parent_id, create_by, create_time, update_by, update_time, del_flag, name, `path`, component, icon, `type`, sort_order, status, visible, remark)
VALUES(2115000000000000001, 1984485400598355969, 1984481955388682242, '2026-11-27 16:40:00', 1984481955388682242, '2026-11-27 16:40:00', '0', 'Redis缓存', '/redis', 'system/RedisMonitor', 'Coin', '1', 9, '1', '1', 'Redis 缓存监控（只读，敏感值脱敏）');

-- 2. 查看权限
INSERT INTO sys_permission
(id, create_by, create_time, update_by, update_time, del_flag, name, code, `type`, status, description)
VALUES(2115000000000000002, 1984481955388682242, '2026-11-27 16:40:00', 1984481955388682242, '2026-11-27 16:40:00', '0', '查看Redis缓存', 'sys_redis_view', '2', '1', 'Redis缓存监控页面查看权限');

-- 3. 授予管理员角色菜单
INSERT INTO sys_role_menu
(role_id, menu_id)
VALUES(1984489247068725249, 2115000000000000001);

-- 4. 授予管理员角色权限
INSERT INTO sys_role_permission
(role_id, permission_id)
VALUES(1984489247068725249, 2115000000000000002);
