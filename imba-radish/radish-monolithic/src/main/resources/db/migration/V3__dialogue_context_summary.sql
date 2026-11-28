-- 对话上下文压缩：摘要字段与查询索引
ALTER TABLE `sys_dialogue`
    ADD COLUMN `context_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '历史上下文摘要',
    ADD COLUMN `summary_up_to_id` bigint DEFAULT NULL COMMENT '摘要已覆盖到的最后一条对话详情ID',
    ADD COLUMN `summary_version` int NOT NULL DEFAULT 0 COMMENT '摘要版本号';

CREATE INDEX `idx_sys_dialogue_details_parent_id_id` ON `sys_dialogue_details` (`parent_id`, `id`);
