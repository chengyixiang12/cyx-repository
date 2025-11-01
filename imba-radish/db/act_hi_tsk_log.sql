create table act_hi_tsk_log
(
    ID_                  bigint auto_increment
        primary key,
    TYPE_                varchar(64)             null,
    TASK_ID_             varchar(64)             not null,
    TIME_STAMP_          timestamp(3)            not null,
    USER_ID_             varchar(255)            null,
    DATA_                varchar(4000)           null,
    EXECUTION_ID_        varchar(64)             null,
    PROC_INST_ID_        varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    SCOPE_ID_            varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    TENANT_ID_           varchar(255) default '' null
)
    collate = utf8mb3_bin;

create index ACT_IDX_ACT_HI_TSK_LOG_TASK
    on act_hi_tsk_log (TASK_ID_);

