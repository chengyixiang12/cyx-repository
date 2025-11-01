create table act_ru_deadletter_job
(
    ID_                  varchar(64)             not null
        primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES
        foreign key (CUSTOM_VALUES_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_DEADLETTER_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_DEADLETTER_JOB_EXECUTION
        foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_DEADLETTER_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_) references act_re_procdef (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_DEADLETTER_JOB_CORRELATION_ID
    on act_ru_deadletter_job (CORRELATION_ID_);

create index ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID
    on act_ru_deadletter_job (CUSTOM_VALUES_ID_);

create index ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID
    on act_ru_deadletter_job (EXCEPTION_STACK_ID_);

create index ACT_IDX_DJOB_SCOPE
    on act_ru_deadletter_job (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SCOPE_DEF
    on act_ru_deadletter_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SUB_SCOPE
    on act_ru_deadletter_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

