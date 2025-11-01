create table act_ru_history_job
(
    ID_                 varchar(64)             not null
        primary key,
    REV_                int                     null,
    LOCK_EXP_TIME_      timestamp(3)            null,
    LOCK_OWNER_         varchar(255)            null,
    RETRIES_            int                     null,
    EXCEPTION_STACK_ID_ varchar(64)             null,
    EXCEPTION_MSG_      varchar(4000)           null,
    HANDLER_TYPE_       varchar(255)            null,
    HANDLER_CFG_        varchar(4000)           null,
    CUSTOM_VALUES_ID_   varchar(64)             null,
    ADV_HANDLER_CFG_ID_ varchar(64)             null,
    CREATE_TIME_        timestamp(3)            null,
    SCOPE_TYPE_         varchar(255)            null,
    TENANT_ID_          varchar(255) default '' null
)
    collate = utf8mb3_bin;

