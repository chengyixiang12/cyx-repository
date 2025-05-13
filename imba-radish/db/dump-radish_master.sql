-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: 192.168.226.130    Database: radish_master
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_announcement`
--

DROP TABLE IF EXISTS `sys_announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int DEFAULT NULL COMMENT '排序',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公告标题',
  `publish_time` datetime DEFAULT NULL COMMENT '公布时间',
  `context` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公告内容',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态；1：发布；0：草稿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int DEFAULT NULL COMMENT '排序字段',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门编码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父级id',
  `level` tinyint DEFAULT NULL COMMENT '层级',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_dept_code` (`code`) USING BTREE COMMENT '部门编码普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (1,1,1,'2024-10-26 10:58:36',1,'2024-10-26 10:58:36','1','1','互联网公司',NULL,1),
                              (2,2,1,'2024-10-26 11:28:40',1,'2024-10-26 11:28:40','1','2','产品与研发部门',1,2),
                              (3,3,1,'2024-10-26 11:29:15',1,'2024-10-26 11:29:15','1','3','市场与运营部门',1,2),
                              (4,4,1,'2024-10-26 11:29:30',1,'2024-10-26 11:29:30','1','4','支持部门',1,2),
                              (5,5,1,'2024-10-26 11:29:42',1,'2024-10-26 11:29:42','1','5','战略与新业务部门',1,2),
                              (6,6,1,'2024-10-26 11:30:02',1,'2024-10-26 11:30:02','1','6','高级管理层',1,2),
                              (7,7,1,'2024-10-26 11:30:33',1,'2024-10-26 11:30:33','1','7','产品部',2,3),
                              (8,8,1,'2024-10-26 11:31:10',1,'2024-10-26 11:31:10','1','8','技术研发部',2,3),
                              (9,9,1,'2024-10-26 11:33:43',1,'2024-10-26 11:33:43','1','9','测试部',2,3),
                              (10,10,1,'2024-10-26 11:34:19',1,'2024-10-26 11:34:19','1','10','运维部',2,3),
                              (11,11,1,'2024-10-26 11:34:33',1,'2024-10-26 11:34:33','1','11','大数据与算法部',2,3),
                              (12,12,1,'2024-10-26 11:34:43',1,'2024-10-26 11:34:43','1','12','AI与机器学习部',2,3),
                              (13,13,1,'2024-10-26 11:34:53',1,'2024-10-26 11:34:53','1','13','架构部',2,3),
                              (14,14,1,'2024-10-26 11:35:39',1,'2024-10-26 11:35:39','1','14','市场部',3,3),
                              (15,15,1,'2024-10-26 11:36:02',1,'2024-10-26 11:36:02','1','15','运营部',3,3),
                              (16,16,1,'2024-10-26 11:36:13',1,'2024-10-26 11:36:13','1','16','销售部',3,3),
                              (17,17,1,'2024-10-26 11:36:23',1,'2024-10-26 11:36:23','1','17','客户支持部',3,3),
                              (18,18,1,'2024-10-26 11:36:46',1,'2024-10-26 11:36:46','1','18','人力资源部（HR）',4,3),
                              (19,19,1,'2024-10-26 11:37:02',1,'2024-10-26 11:37:02','1','19','财务部',4,3),
                              (20,20,1,'2024-10-26 11:37:15',1,'2024-10-26 11:37:15','1','20','行政部',4,3),
                              (21,21,1,'2024-10-26 11:37:29',1,'2024-10-26 11:37:29','1','21','法务部',4,3),
                              (22,22,1,'2024-10-26 11:37:59',1,'2024-10-26 11:37:59','1','22','战略部',5,3),
                              (23,23,1,'2024-10-26 11:38:41',1,'2024-10-26 11:38:41','1','23','新业务拓展部',5,3),
                              (24,24,1,'2024-10-26 11:38:51',1,'2024-10-26 11:38:51','1','24','投资与并购部',5,3),
                              (25,NULL,1,'2025-04-26 23:32:14',1,'2025-04-26 23:32:48','0','asdasd','asdas',6,3);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int DEFAULT NULL COMMENT '排序',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编码',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典类型',
  `css_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` tinyint DEFAULT NULL COMMENT '是否默认；1：是 0：否',
  `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_dict_data_code` (`code`) USING BTREE COMMENT '字典编码普通索引',
  KEY `idx_sys_dict_data_dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,1,'2025-04-24 15:03:44',1,'2025-04-24 17:25:01','1','male','男','1','gender','','',1,1,'1'),
                                   (2,2,1,'2025-04-24 17:12:48',1,'2025-04-24 17:12:48','1','female','女','0','gender','','',0,1,''),
                                   (3,1,1,'2025-04-25 22:48:59',1,'2025-04-25 22:48:59','1','enable','启用','1','status','','',0,1,''),
                                   (4,2,1,'2025-04-25 22:49:14',1,'2025-04-25 22:49:14','1','forbbien','禁用','0','status','','',1,1,'');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int DEFAULT NULL COMMENT '排序',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典类型',
  `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_dict_type` (`dict_type`) USING BTREE COMMENT '字典类型索引'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,2,1,'2025-04-23 22:52:19',1,'2025-04-23 23:04:57','1','性别','gender',1,'表示男女的性别'),
                                   (2,2,1,'2025-04-25 22:48:38',1,'2025-04-25 22:50:17','1','状态','status',1,'');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_file`
--

DROP TABLE IF EXISTS `sys_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `original_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '源文件名',
  `file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储文件名',
  `location` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '存储地址；1：minio；2：磁盘',
  `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
  `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件后缀；示例：.txt、.jpg',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小；单位：B',
  `file_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件hash',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_file_key` (`file_key`) USING BTREE COMMENT '存储文件名普通索引',
  KEY `idx_sys_file_hash` (`file_hash`) USING BTREE COMMENT '文件hash普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
  `log_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志级别',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求ip',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问路径',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求方法',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '响应结果',
  `operation_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '操作描述',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志来源',
  `execution_time` bigint DEFAULT NULL COMMENT '耗时',
  `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模块名称',
  `status_code` int DEFAULT NULL COMMENT '状态码',
  `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常信息',
  `os_browser_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作系统/浏览器信息',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单唯一标识符，主键',
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID，顶级菜单的父ID通常为0',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '前端路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单图标（如字体图标类名）',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
  `order_num` int DEFAULT '0' COMMENT '排序号，数字越小，排序越靠前',
  `status` tinyint DEFAULT '1' COMMENT '菜单状态：0-禁用，1-启用',
  `visible` tinyint DEFAULT '1' COMMENT '是否显示：0-隐藏，1-显示',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_menu_parent_id` (`parent_id`) USING BTREE COMMENT '父级菜单普通索引',
  KEY `idx_sys_menu_type` (`type`) USING BTREE COMMENT '菜单类型普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,NULL,1,'2024-11-16 03:25:27',1,'2025-04-24 10:37:03','系统管理','/system',NULL,'setting','0',2,1,1,'系统管理顶级菜单','1'),(2,NULL,1,'2024-11-16 03:25:27',1,'2025-04-20 21:58:52','内容管理','/content',NULL,'folder','0',1,1,1,'内容管理顶级菜单','1'),(3,NULL,1,'2024-11-16 03:25:27',1,'2025-04-09 22:00:39','统计分析','/total',NULL,'chart','0',3,1,1,'统计分析顶级菜单','0'),(4,1,1,'2024-11-16 03:25:27',1,'2025-03-31 09:55:59','用户管理','/user','system/User','user','1',1,1,1,'管理用户信息','1'),(5,1,1,'2024-11-16 03:25:27',1,'2025-04-20 11:46:09','角色管理','/role','system/Role','avatar','1',2,1,1,'管理角色信息','1'),(6,1,1,'2024-11-16 03:25:27',1,'2025-04-20 10:26:56','菜单管理','/menu','system/Menu','menu','1',3,1,1,'管理菜单信息','1'),(7,2,1,'2024-11-16 03:25:27',1,'2025-04-21 10:06:53','文章管理','/article/list','views/ArticleList.vue','notebook','1',1,1,1,'管理文章内容','1'),(8,2,1,'2024-11-16 03:25:27',1,'2025-04-21 18:07:35','分类管理','/category/list','views/CategoryList.vue','Operation','1',2,1,1,'管理文章分类','1'),(9,2,1,'2024-11-16 03:25:27',1,'2025-04-09 22:53:13','标签管理','/tag/list','views/TagList.vue','tag','1',3,1,1,'管理文章标签','0'),(10,3,1,'2024-11-16 03:25:27',1,'2025-04-09 22:00:39','用户统计','/stat/user','views/UserStat.vue','bar-chart','1',1,1,1,'统计用户相关数据','0'),(11,3,1,'2024-11-16 03:25:27',1,'2025-04-09 22:00:39','系统访问统计','/stat/system','views/SystemStat.vue','line-chart','1',2,1,1,'统计系统访问数据','0'),(12,1,1,'2025-04-22 16:48:24',1,'2025-04-24 02:33:58','字典管理','/dict','system/DictType','Collection','1',2,1,1,'','1'),(13,1,1,'2025-04-23 23:06:59',1,'2025-04-23 15:18:13','字典数据','/dictData','system/DictData','Document','1',2,1,0,'','1'),(14,1,1,'2025-04-25 22:41:19',1,'2025-04-25 22:41:19','部门管理','/dept','system/Dept','OfficeBuilding','1',4,1,1,'','1'),(15,1,1,'2025-04-25 22:42:14',1,'2025-04-25 22:43:15','日志管理','/log','system/Log','Memo','1',5,1,1,'','1'),(16,1,1,'2025-04-27 16:47:48',1,'2025-04-27 16:47:48','日志详情','/logDetail','system/LogDetail.vue','Document','1',7,1,0,'','1');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限类型；1：菜单；2：按钮',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_permission_code` (`code`) USING BTREE COMMENT '权限编码普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,1,'2024-11-20 20:06:35',1,'2024-11-20 20:06:41','1','添加菜单','sys_menu_add','2','1',NULL),(2,1,'2024-11-20 20:51:39',1,'2024-11-20 20:51:39','1','添加角色','sys_role_add','2','1',NULL),(3,1,'2024-11-20 20:53:17',1,'2024-11-20 20:53:17','1','添加权限','sys_pms_add','2','1',NULL),(4,1,'2024-11-20 20:53:44',1,'2024-11-20 20:53:44','1','编辑角色','sys_role_edit','2','1',NULL),(5,1,'2024-11-20 20:55:08',1,'2024-11-20 20:55:08','1','编辑菜单','sys_menu_edit','2','1',NULL),(6,1,'2024-11-20 20:55:18',1,'2024-11-20 20:55:18','1','删除菜单','sys_menu_del','2','1',NULL),(7,1,'2024-11-20 21:39:14',1,'2024-11-20 21:39:14','1','添加部门','sys_dept_add','2','1',NULL),(8,1,'2024-11-20 21:39:26',1,'2024-11-20 21:39:26','1','编辑部门','sys_dept_edit','2','1',NULL),(9,1,'2024-11-20 21:39:52',1,'2024-11-20 21:39:52','1','删除部门','sys_dept_del','2','1',NULL),(10,1,'2024-11-20 21:41:34',1,'2024-11-20 21:41:34','0','用户赋予角色','sys_user_set_role','2','1',NULL),(11,1,'2024-11-20 21:42:27',1,'2024-11-20 21:42:27','1','角色赋予菜单','sys_role_set_menu','2','1',NULL),(12,1,'2024-11-20 21:42:38',1,'2024-11-20 21:42:38','1','角色赋予权限','sys_role_set_per','2','1',NULL),(13,1,'2024-11-20 21:47:29',1,'2024-11-20 21:47:29','1','设置默认角色','sys_role_set_def','2','1',NULL),(14,1,'2024-11-20 21:48:11',1,'2024-11-20 21:48:11','1','禁用角色','sys_role_fbn','2','1',NULL),(15,1,'2024-11-20 21:48:37',1,'2024-11-20 21:48:37','1','启用角色','sys_role_enable','2','1',NULL),(16,1,'2024-11-20 21:51:21',1,'2024-11-20 21:51:21','1','添加字典数据','sys_dict_data_add','2','1',NULL),(17,1,'2024-11-20 21:51:46',1,'2024-11-20 21:51:46','1','编辑字典数据','sys_dict_data_edit','2','1',NULL),(18,1,'2024-11-20 21:52:10',1,'2024-11-20 21:52:10','1','删除字典数据','sys_dict_data_del','2','1',NULL),(19,1,'2024-11-20 21:52:51',1,'2024-11-20 21:52:51','1','添加字典类型','sys_dict_type_add','2','1',NULL),(20,1,'2024-11-20 21:53:21',1,'2024-11-20 21:53:21','1','编辑字典类型','sys_dict_type_edit','2','1',NULL),(21,1,'2024-11-20 21:53:44',1,'2024-11-20 21:53:44','1','删除字典类型','sys_dict_type_del','2','1',NULL),(22,1,'2024-11-20 21:54:18',1,'2024-11-20 21:54:18','1','删除文件','sys_file_del','2','1',NULL),(23,1,'2024-11-20 21:57:41',1,'2024-11-20 21:57:41','1','删除角色','sys_role_del','2','1',NULL),(24,1,'2024-11-20 21:58:58',1,'2024-11-20 21:58:58','1','重置密码','sys_user_reset','2','1',NULL),(25,1,'2024-11-20 21:59:34',1,'2024-11-20 21:59:34','1','添加用户','sys_user_add','2','1',NULL),(27,1,'2024-11-20 22:01:40',1,'2024-11-20 22:01:40','1','编辑用户','sys_user_edit','2','1',NULL),(28,1,'2024-11-26 19:23:51',1,'2024-11-26 19:23:51','1','生成密钥','sys_secret_key_generate','2','1',NULL),(29,1,'2025-03-28 11:00:40',1,'2025-03-28 11:00:40','1','删除用户','sys_user_delete','2','1',''),(30,1,'2025-04-17 18:30:21',1,'2025-04-17 18:30:21','1','锁定用户','sys_user_lock','2','1',''),(31,1,'2025-04-17 18:31:31',1,'2025-04-17 18:31:31','1','解锁用户','sys_user_unlock','2','1','');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sort_order` int DEFAULT NULL COMMENT '排序字段',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色编码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT NULL COMMENT '状态；1：启用；0：禁用',
  `is_default` tinyint DEFAULT NULL COMMENT '是否标记为系统默认角色；1：是；0：不是',
  `fix_role` tinyint DEFAULT NULL COMMENT '是否为固定角色（固定角色无法被删除）；1：是；0：不是',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE COMMENT '角色编码普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,1,1,'2024-10-25 15:07:55',1,'2025-04-26 10:37:08','1','ROLE_ADMIN','管理员','拥有所有权限',1,0,1),(2,2,1,'2024-10-25 15:08:15',1,'2024-10-25 15:08:07','1','ROLE_NORMAL','普通用户','没有任何权限',1,1,1),(3,3,1,'2024-10-25 15:21:10',1,'2024-10-25 15:21:18','1','ROLE_DPEPT_HEADER','部门负责人','负责自己的部门',1,0,0),(4,4,1,'2024-10-25 15:19:19',1,'2024-10-25 15:21:21','1','ROLE_TEAM_HEADER','组长','负责自己的小组',0,0,0),(5,5,1,'2024-10-25 15:26:12',1,'2024-10-25 15:28:01','1','ROLE_PROGRAM_MANAGER','项目经理','负责项目研发、测试、部署、运维',0,0,0),(6,6,1,'2024-10-25 15:29:03',1,'2024-10-25 15:29:03','1','ROLE_PROJECT_MANAGER','产品经理','负责项目设计、客户对接',0,0,0),(7,7,1,'2024-10-25 16:18:56',1,'2024-10-25 16:18:56','1','ROLE_JAVA_ENGINEER','Java开发工程师','负责java项目的开发',0,0,0),(8,8,1,'2025-04-20 21:52:02',1,'2025-04-20 22:00:10','0','ROLE_REACT_ENGINEER','React研发工程师','从事于react项目研发',0,0,0),(9,10,1,'2025-04-21 20:29:54',1,'2025-04-22 15:38:14','1','ROLE_RECAT_ENGINEER','React开发工程师',NULL,1,0,0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色id',
  `menu_id` bigint NOT NULL COMMENT '菜单id',
  UNIQUE KEY `uk_sys_role_menu` (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,1),(1,2),(1,4),(1,5),(1,6),(1,7),(1,8),(1,12),(1,13),(1,14),(1,15),(1,16),(2,2),(2,7),(2,8);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色主键',
  `permission_id` bigint NOT NULL COMMENT '权限主键',
  UNIQUE KEY `uk_sys_role_permission` (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31);
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_secret_key`
--

DROP TABLE IF EXISTS `sys_secret_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_secret_key` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：删除',
  `public_key` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公钥',
  `private_key` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '私钥',
  `type` tinyint NOT NULL COMMENT '类型',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_secret_key_type` (`type`) USING BTREE COMMENT '类型普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='密钥';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_secret_key`
--

LOCK TABLES `sys_secret_key` WRITE;
/*!40000 ALTER TABLE `sys_secret_key` DISABLE KEYS */;
INSERT INTO `sys_secret_key` VALUES (1,1,'2024-11-26 19:52:27',1,'2024-11-26 11:53:23','1','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',0,NULL);
/*!40000 ALTER TABLE `sys_secret_key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除；1：存在；0：不存在',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电话',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '用户是否被启用；1：启用；0：禁用',
  `account_non_locked` tinyint(1) DEFAULT '1' COMMENT '账户是否被锁定；1：正常；0：锁定',
  `credentials_non_expired` tinyint(1) DEFAULT '1' COMMENT '凭证是否过期；1：正常；0：过期',
  `account_non_expired` tinyint(1) DEFAULT '1' COMMENT '账户是否过期；1：正常；0：过期',
  `dept_id` bigint DEFAULT NULL COMMENT '部门id',
  `avatar` bigint DEFAULT NULL COMMENT '用户头像',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_user_username` (`username`) USING BTREE COMMENT '用户名普通索引',
  KEY `idx_sys_user_email` (`email`) USING BTREE COMMENT '邮箱普通索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO radish_master.sys_user (id, create_by, create_time, update_by, update_time, del_flag, username, password, nickname, email, phone, enabled, account_non_locked, credentials_non_expired, account_non_expired, dept_id, avatar) VALUES(1, 1, '2025-04-15 21:56:02', 1, '2025-04-25 22:53:39', '1', 'radish', '$2a$10$zH.6/PKSj6KChbvW.IeWZO43/qSA/W81ZD/G8Kk0eJjpBQLeVnP9G', '萝卜', '862955299@qq.com', '12345678901', 1, 1, 1, 1, 8, NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_role` (
  `role_id` bigint NOT NULL COMMENT '角色主键',
  `user_id` bigint NOT NULL COMMENT '用户主键',
  UNIQUE KEY `uk_role_user` (`role_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'radish_master'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-13 11:40:54
