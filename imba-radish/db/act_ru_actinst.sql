create table act_ru_actinst
(
    ID_                varchar(64)             not null
        primary key,
    REV_               int          default 1  null,
    PROC_DEF_ID_       varchar(64)             not null,
    PROC_INST_ID_      varchar(64)             not null,
    EXECUTION_ID_      varchar(64)             not null,
    ACT_ID_            varchar(255)            not null,
    TASK_ID_           varchar(64)             null,
    CALL_PROC_INST_ID_ varchar(64)             null,
    ACT_NAME_          varchar(255)            null,
    ACT_TYPE_          varchar(255)            not null,
    ASSIGNEE_          varchar(255)            null,
    START_TIME_        datetime(3)             not null,
    END_TIME_          datetime(3)             null,
    DURATION_          bigint                  null,
    TRANSACTION_ORDER_ int                     null,
    DELETE_REASON_     varchar(4000)           null,
    TENANT_ID_         varchar(255) default '' null
)
    collate = utf8mb3_bin;

create index ACT_IDX_RU_ACTI_END
    on act_ru_actinst (END_TIME_);

create index ACT_IDX_RU_ACTI_EXEC
    on act_ru_actinst (EXECUTION_ID_);

create index ACT_IDX_RU_ACTI_EXEC_ACT
    on act_ru_actinst (EXECUTION_ID_, ACT_ID_);

create index ACT_IDX_RU_ACTI_PROC
    on act_ru_actinst (PROC_INST_ID_);

create index ACT_IDX_RU_ACTI_PROC_ACT
    on act_ru_actinst (PROC_INST_ID_, ACT_ID_);

create index ACT_IDX_RU_ACTI_START
    on act_ru_actinst (START_TIME_);

create index ACT_IDX_RU_ACTI_TASK
    on act_ru_actinst (TASK_ID_);

