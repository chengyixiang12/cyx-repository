create table act_ge_property
(
    NAME_  varchar(64)  not null
        primary key,
    VALUE_ varchar(300) null,
    REV_   int          null
)
    collate = utf8mb3_bin;

INSERT INTO radish_master.act_ge_property (NAME_, VALUE_, REV_) VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO radish_master.act_ge_property (NAME_, VALUE_, REV_) VALUES ('cfg.task-related-entities-count', 'true', 1);
INSERT INTO radish_master.act_ge_property (NAME_, VALUE_, REV_) VALUES ('common.schema.version', '7.1.0.2', 1);
INSERT INTO radish_master.act_ge_property (NAME_, VALUE_, REV_) VALUES ('next.dbid', '1', 1);
INSERT INTO radish_master.act_ge_property (NAME_, VALUE_, REV_) VALUES ('schema.history', 'create(7.1.0.2)', 1);
INSERT INTO radish_master.act_ge_property (NAME_, VALUE_, REV_) VALUES ('schema.version', '7.1.0.2', 1);
