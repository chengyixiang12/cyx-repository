/*
 Navicat Premium Data Transfer

 Source Server         : 虚拟机mysql
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : 192.168.226.130:3306
 Source Schema         : mysql_study

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 18/02/2025 14:16:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序字段',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门编码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级id',
  `level` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '层级',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code_uk`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, 1, 1, '2024-10-26 10:58:36', 1, '2024-10-26 10:58:36', '1', '1', '互联网公司', NULL, '1');
INSERT INTO `sys_dept` VALUES (2, 2, 1, '2024-10-26 11:28:40', 1, '2024-10-26 11:28:40', '1', '2', '产品与研发部门', 1, '2');
INSERT INTO `sys_dept` VALUES (3, 3, 1, '2024-10-26 11:29:15', 1, '2024-10-26 11:29:15', '1', '3', '市场与运营部门', 1, '2');
INSERT INTO `sys_dept` VALUES (4, 4, 1, '2024-10-26 11:29:30', 1, '2024-10-26 11:29:30', '1', '4', '支持部门', 1, '2');
INSERT INTO `sys_dept` VALUES (5, 5, 1, '2024-10-26 11:29:42', 1, '2024-10-26 11:29:42', '1', '5', '战略与新业务部门', 1, '2');
INSERT INTO `sys_dept` VALUES (6, 6, 1, '2024-10-26 11:30:02', 1, '2024-10-26 11:30:02', '1', '6', '高级管理层', 1, '2');
INSERT INTO `sys_dept` VALUES (7, 7, 1, '2024-10-26 11:30:33', 1, '2024-10-26 11:30:33', '1', '7', '产品部', 2, '3');
INSERT INTO `sys_dept` VALUES (8, 8, 1, '2024-10-26 11:31:10', 1, '2024-10-26 11:31:10', '1', '8', '技术研发部', 2, '3');
INSERT INTO `sys_dept` VALUES (9, 9, 1, '2024-10-26 11:33:43', 1, '2024-10-26 11:33:43', '1', '9', '测试部', 2, '3');
INSERT INTO `sys_dept` VALUES (10, 10, 1, '2024-10-26 11:34:19', 1, '2024-10-26 11:34:19', '1', '10', '运维部', 2, '3');
INSERT INTO `sys_dept` VALUES (11, 11, 1, '2024-10-26 11:34:33', 1, '2024-10-26 11:34:33', '1', '11', '大数据与算法部', 2, '3');
INSERT INTO `sys_dept` VALUES (12, 12, 1, '2024-10-26 11:34:43', 1, '2024-10-26 11:34:43', '1', '12', 'AI与机器学习部', 2, '3');
INSERT INTO `sys_dept` VALUES (13, 13, 1, '2024-10-26 11:34:53', 1, '2024-10-26 11:34:53', '1', '13', '架构部', 2, '3');
INSERT INTO `sys_dept` VALUES (14, 14, 1, '2024-10-26 11:35:39', 1, '2024-10-26 11:35:39', '1', '14', '市场部', 3, '3');
INSERT INTO `sys_dept` VALUES (15, 15, 1, '2024-10-26 11:36:02', 1, '2024-10-26 11:36:02', '1', '15', '运营部', 3, '3');
INSERT INTO `sys_dept` VALUES (16, 16, 1, '2024-10-26 11:36:13', 1, '2024-10-26 11:36:13', '1', '16', '销售部', 3, '3');
INSERT INTO `sys_dept` VALUES (17, 17, 1, '2024-10-26 11:36:23', 1, '2024-10-26 11:36:23', '1', '17', '客户支持部', 3, '3');
INSERT INTO `sys_dept` VALUES (18, 18, 1, '2024-10-26 11:36:46', 1, '2024-10-26 11:36:46', '1', '18', '人力资源部（HR）', 4, '3');
INSERT INTO `sys_dept` VALUES (19, 19, 1, '2024-10-26 11:37:02', 1, '2024-10-26 11:37:02', '1', '19', '财务部', 4, '3');
INSERT INTO `sys_dept` VALUES (20, 20, 1, '2024-10-26 11:37:15', 1, '2024-10-26 11:37:15', '1', '20', '行政部', 4, '3');
INSERT INTO `sys_dept` VALUES (21, 21, 1, '2024-10-26 11:37:29', 1, '2024-10-26 11:37:29', '1', '21', '法务部', 4, '3');
INSERT INTO `sys_dept` VALUES (22, 22, 1, '2024-10-26 11:37:59', 1, '2024-10-26 11:37:59', '1', '22', '战略部', 5, '3');
INSERT INTO `sys_dept` VALUES (23, 23, 1, '2024-10-26 11:38:41', 1, '2024-10-26 11:38:41', '1', '23', '新业务拓展部', 5, '3');
INSERT INTO `sys_dept` VALUES (24, 24, 1, '2024-10-26 11:38:51', 1, '2024-10-26 11:38:51', '1', '24', '投资与并购部', 5, '3');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `css_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否默认；1：是 0：否',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态；1：启用；0：停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_dict_data_code`(`code` ASC) USING BTREE COMMENT '字典编码索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态；1：启用；0：停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_dict_type`(`dict_type` ASC) USING BTREE COMMENT '字典类型索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `original_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '源文件名',
  `file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储文件名',
  `location` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储地址；1：minio；2：磁盘',
  `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件后缀；示例：.txt、.jpg',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小；单位：B',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除',
  `log_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志级别',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ip',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问路径',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应结果',
  `operation_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '操作描述',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志来源',
  `execution_time` bigint NULL DEFAULT NULL COMMENT '耗时',
  `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块名称',
  `status_code` int NULL DEFAULT NULL COMMENT '状态码',
  `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `os_browser_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统/浏览器信息',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单唯一标识符，主键',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID，顶级菜单的父ID通常为0',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标（如字体图标类名）',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
  `order_num` int NULL DEFAULT 0 COMMENT '排序号，数字越小，排序越靠前',
  `status` tinyint NULL DEFAULT 1 COMMENT '菜单状态：0-禁用，1-启用',
  `visible` tinyint NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, NULL, 1, '2024-11-16 03:25:27', 1, '2024-11-16 04:48:48', '系统管理', NULL, NULL, 'setting', '0', 1, 1, 1, '系统管理顶级菜单', '1');
INSERT INTO `sys_menu` VALUES (2, NULL, 1, '2024-11-16 03:25:27', 1, '2024-11-16 04:48:48', '内容管理', NULL, NULL, 'folder', '0', 2, 1, 1, '内容管理顶级菜单', '1');
INSERT INTO `sys_menu` VALUES (3, NULL, 1, '2024-11-16 03:25:27', 1, '2024-11-16 04:48:49', '统计分析', NULL, NULL, 'chart', '0', 3, 1, 1, '统计分析顶级菜单', '1');
INSERT INTO `sys_menu` VALUES (4, 1, 1, '2024-11-16 03:25:27', 1, '2024-11-27 12:59:55', '用户管理', '/user', 'views/UserList.vue', 'user', '1', 1, 1, 1, '管理用户信息', '1');
INSERT INTO `sys_menu` VALUES (5, 1, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '角色管理', '/role/list', 'views/RoleList.vue', 'team', '1', 2, 1, 1, '管理角色信息', '1');
INSERT INTO `sys_menu` VALUES (6, 1, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '菜单管理', '/menu/list', 'views/MenuList.vue', 'menu', '1', 3, 1, 1, '管理菜单信息', '1');
INSERT INTO `sys_menu` VALUES (7, 2, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '文章管理', '/article/list', 'views/ArticleList.vue', 'file', '1', 1, 1, 1, '管理文章内容', '1');
INSERT INTO `sys_menu` VALUES (8, 2, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '分类管理', '/category/list', 'views/CategoryList.vue', 'tags', '1', 2, 1, 1, '管理文章分类', '1');
INSERT INTO `sys_menu` VALUES (9, 2, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '标签管理', '/tag/list', 'views/TagList.vue', 'tag', '1', 3, 1, 1, '管理文章标签', '1');
INSERT INTO `sys_menu` VALUES (10, 3, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '用户统计', '/stat/user', 'views/UserStat.vue', 'bar-chart', '1', 1, 1, 1, '统计用户相关数据', '1');
INSERT INTO `sys_menu` VALUES (11, 3, 1, '2024-11-16 03:25:27', 1, '2024-11-16 03:26:43', '系统访问统计', '/stat/system', 'views/SystemStat.vue', 'line-chart', '1', 2, 1, 1, '统计系统访问数据', '1');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限类型；1：菜单；2：按钮',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_permission_code`(`code` ASC) USING BTREE COMMENT '权限编码索引'
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 1, '2024-11-20 20:06:35', 1, '2024-11-20 20:06:41', '1', '添加菜单', 'sys_menu_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (2, 1, '2024-11-20 20:51:39', 1, '2024-11-20 20:51:39', '1', '添加角色', 'sys_role_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (3, 1, '2024-11-20 20:53:17', 1, '2024-11-20 20:53:17', '1', '添加权限', 'sys_pms_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (4, 1, '2024-11-20 20:53:44', 1, '2024-11-20 20:53:44', '1', '编辑角色', 'sys_role_edit', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (5, 1, '2024-11-20 20:55:08', 1, '2024-11-20 20:55:08', '1', '编辑菜单', 'sys_menu_edit', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (6, 1, '2024-11-20 20:55:18', 1, '2024-11-20 20:55:18', '1', '删除菜单', 'sys_menu_del', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (7, 1, '2024-11-20 21:39:14', 1, '2024-11-20 21:39:14', '1', '添加部门', 'sys_dept_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (8, 1, '2024-11-20 21:39:26', 1, '2024-11-20 21:39:26', '1', '编辑部门', 'sys_dept_edit', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (9, 1, '2024-11-20 21:39:52', 1, '2024-11-20 21:39:52', '1', '删除部门', 'sys_dept_del', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (10, 1, '2024-11-20 21:41:34', 1, '2024-11-20 21:41:34', '0', '用户赋予角色', 'sys_user_set_role', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (11, 1, '2024-11-20 21:42:27', 1, '2024-11-20 21:42:27', '1', '角色赋予菜单', 'sys_role_set_menu', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (12, 1, '2024-11-20 21:42:38', 1, '2024-11-20 21:42:38', '1', '角色赋予权限', 'sys_role_set_per', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (13, 1, '2024-11-20 21:47:29', 1, '2024-11-20 21:47:29', '1', '设置默认角色', 'sys_role_set_def', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (14, 1, '2024-11-20 21:48:11', 1, '2024-11-20 21:48:11', '1', '禁用角色', 'sys_role_fbn', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (15, 1, '2024-11-20 21:48:37', 1, '2024-11-20 21:48:37', '1', '启用角色', 'sys_role_enable', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (16, 1, '2024-11-20 21:51:21', 1, '2024-11-20 21:51:21', '1', '添加字典数据', 'sys_dict_data_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (17, 1, '2024-11-20 21:51:46', 1, '2024-11-20 21:51:46', '1', '编辑字典数据', 'sys_dict_data_edit', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (18, 1, '2024-11-20 21:52:10', 1, '2024-11-20 21:52:10', '1', '删除字典数据', 'sys_dict_data_del', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (19, 1, '2024-11-20 21:52:51', 1, '2024-11-20 21:52:51', '1', '添加字典类型', 'sys_dict_type_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (20, 1, '2024-11-20 21:53:21', 1, '2024-11-20 21:53:21', '1', '编辑字典类型', 'sys_dict_type_edit', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (21, 1, '2024-11-20 21:53:44', 1, '2024-11-20 21:53:44', '1', '删除字典类型', 'sys_dict_type_del', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (22, 1, '2024-11-20 21:54:18', 1, '2024-11-20 21:54:18', '1', '删除文件', 'sys_file_del', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (23, 1, '2024-11-20 21:57:41', 1, '2024-11-20 21:57:41', '1', '删除角色', 'sys_role_del', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (24, 1, '2024-11-20 21:58:58', 1, '2024-11-20 21:58:58', '1', '重置密码', 'sys_user_reset', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (25, 1, '2024-11-20 21:59:34', 1, '2024-11-20 21:59:34', '1', '添加用户', 'sys_user_add', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (27, 1, '2024-11-20 22:01:40', 1, '2024-11-20 22:01:40', '1', '编辑用户', 'sys_user_edit', '2', '1', NULL);
INSERT INTO `sys_permission` VALUES (28, 1, '2024-11-26 19:23:51', 1, '2024-11-26 19:23:51', '1', '生成密钥', 'sys_secret_key_generate', '2', '1', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序字段',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
  `is_default` tinyint NULL DEFAULT NULL COMMENT '是否标记为系统默认角色；1：是；0：不是',
  `fix_role` tinyint NULL DEFAULT NULL COMMENT '是否为固定角色（固定角色无法被删除）；1：是；0：不是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code_uk`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 1, 1, '2024-10-25 15:07:55', 1, '2024-10-25 15:08:05', '1', 'ROLE_ADMIN', '管理员', '拥有所有权限', 1, 0, 1);
INSERT INTO `sys_role` VALUES (2, 2, 1, '2024-10-25 15:08:15', 1, '2024-10-25 15:08:07', '1', 'ROLE_NORMAL', '普通用户', '没有任何权限', 1, 1, 1);
INSERT INTO `sys_role` VALUES (3, 3, 1, '2024-10-25 15:21:10', 1, '2024-10-25 15:21:18', '1', 'ROLE_DPEPT_HEADER', '部门负责人', '负责自己的部门', 1, 0, 0);
INSERT INTO `sys_role` VALUES (4, 4, 1, '2024-10-25 15:19:19', 1, '2024-10-25 15:21:21', '1', 'ROLE_TEAM_HEADER', '组长', '负责自己的小组', 1, 0, 0);
INSERT INTO `sys_role` VALUES (5, 5, 1, '2024-10-25 15:26:12', 1, '2024-10-25 15:28:01', '1', 'ROLE_PROGRAM_MANAGER', '项目经理', '负责项目研发、测试、部署、运维', 1, 0, 0);
INSERT INTO `sys_role` VALUES (6, 6, 1, '2024-10-25 15:29:03', 1, '2024-10-25 15:29:03', '1', 'ROLE_PROJECT_MANAGER', '产品经理', '负责项目设计、客户对接', 1, 0, 0);
INSERT INTO `sys_role` VALUES (7, 7, 1, '2024-10-25 16:18:56', 1, '2024-10-25 16:18:56', '1', 'ROLE_JAVA_ENGINEER', 'Java开发工程师', '负责java项目的开发', 1, 0, 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色id',
  `menu_id` bigint NOT NULL COMMENT '菜单id',
  UNIQUE INDEX `uk_sys_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (1, 5);
INSERT INTO `sys_role_menu` VALUES (1, 6);
INSERT INTO `sys_role_menu` VALUES (1, 7);
INSERT INTO `sys_role_menu` VALUES (1, 8);
INSERT INTO `sys_role_menu` VALUES (1, 9);
INSERT INTO `sys_role_menu` VALUES (1, 10);
INSERT INTO `sys_role_menu` VALUES (1, 11);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `role_id` bigint NOT NULL COMMENT '角色主键',
  `permission_id` bigint NOT NULL COMMENT '权限主键',
  UNIQUE INDEX `uk_sys_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1);
INSERT INTO `sys_role_permission` VALUES (1, 2);
INSERT INTO `sys_role_permission` VALUES (1, 3);
INSERT INTO `sys_role_permission` VALUES (1, 4);
INSERT INTO `sys_role_permission` VALUES (1, 5);
INSERT INTO `sys_role_permission` VALUES (1, 6);
INSERT INTO `sys_role_permission` VALUES (1, 7);
INSERT INTO `sys_role_permission` VALUES (1, 8);
INSERT INTO `sys_role_permission` VALUES (1, 9);
INSERT INTO `sys_role_permission` VALUES (1, 10);
INSERT INTO `sys_role_permission` VALUES (1, 11);
INSERT INTO `sys_role_permission` VALUES (1, 12);
INSERT INTO `sys_role_permission` VALUES (1, 13);
INSERT INTO `sys_role_permission` VALUES (1, 14);
INSERT INTO `sys_role_permission` VALUES (1, 15);
INSERT INTO `sys_role_permission` VALUES (1, 16);
INSERT INTO `sys_role_permission` VALUES (1, 17);
INSERT INTO `sys_role_permission` VALUES (1, 18);
INSERT INTO `sys_role_permission` VALUES (1, 19);
INSERT INTO `sys_role_permission` VALUES (1, 20);
INSERT INTO `sys_role_permission` VALUES (1, 21);
INSERT INTO `sys_role_permission` VALUES (1, 22);
INSERT INTO `sys_role_permission` VALUES (1, 23);
INSERT INTO `sys_role_permission` VALUES (1, 24);
INSERT INTO `sys_role_permission` VALUES (1, 25);
INSERT INTO `sys_role_permission` VALUES (1, 26);
INSERT INTO `sys_role_permission` VALUES (1, 27);
INSERT INTO `sys_role_permission` VALUES (1, 28);

-- ----------------------------
-- Table structure for sys_secret_key
-- ----------------------------
DROP TABLE IF EXISTS `sys_secret_key`;
CREATE TABLE `sys_secret_key`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `public_key` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公钥',
  `private_key` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '私钥',
  `type` tinyint NOT NULL COMMENT '类型',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_secret_key_type`(`type` ASC) USING BTREE COMMENT '类型唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '密钥' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_secret_key
-- ----------------------------
INSERT INTO `sys_secret_key` VALUES (1, 1, '2024-11-26 19:52:27', 1, '2024-11-26 11:53:23', '1', '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', 0, NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑删除；1：存在；0：不存在',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '用户是否被启用；1：启用；0：禁用',
  `account_non_locked` tinyint(1) NULL DEFAULT 1 COMMENT '账户是否被锁定；1：正常；0：锁定',
  `credentials_non_expired` tinyint(1) NULL DEFAULT 1 COMMENT '凭证是否过期；1：正常；0：过期',
  `account_non_expired` tinyint(1) NULL DEFAULT 1 COMMENT '账户是否过期；1：正常；0：过期',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 1, '2024-10-24 07:25:34', 1, '2024-11-16 04:29:52', '1', 'admin', '$2a$10$7GBeWjeLbS1OUOFl18nvMeiww0cAtVQRCnNOQJBVgzoJX.KMaezSy', '程益祥', '1907452460@qq.com', '13235423290', 1, 1, 1, 1, 1);
INSERT INTO `sys_user` VALUES (2, 1, '2024-11-09 23:31:22', 1, '2024-11-16 12:20:40', '1', 'zjy001', '$2a$10$toTWMK/I76kY2SwHPKX2k.EQqYcjG14PWjJ.H4dkGPIzE.joJ0x3a', '张佳耀', '3332951238@qq.com', NULL, 1, 1, 1, 1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `role_id` bigint NOT NULL COMMENT '角色主键',
  `user_id` bigint NOT NULL COMMENT '用户主键',
  UNIQUE INDEX `uk_role_user`(`role_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (1, 2);

SET FOREIGN_KEY_CHECKS = 1;
