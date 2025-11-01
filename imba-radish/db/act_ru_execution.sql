create table act_ru_execution
(
    ID_                        varchar(64)             not null
        primary key,
    REV_                       int                     null,
    PROC_INST_ID_              varchar(64)             null,
    BUSINESS_KEY_              varchar(255)            null,
    PARENT_ID_                 varchar(64)             null,
    PROC_DEF_ID_               varchar(64)             null,
    SUPER_EXEC_                varchar(64)             null,
    ROOT_PROC_INST_ID_         varchar(64)             null,
    ACT_ID_                    varchar(255)            null,
    IS_ACTIVE_                 tinyint                 null,
    IS_CONCURRENT_             tinyint                 null,
    IS_SCOPE_                  tinyint                 null,
    IS_EVENT_SCOPE_            tinyint                 null,
    IS_MI_ROOT_                tinyint                 null,
    SUSPENSION_STATE_          int                     null,
    CACHED_ENT_STATE_          int                     null,
    TENANT_ID_                 varchar(255) default '' null,
    NAME_                      varchar(255)            null,
    START_ACT_ID_              varchar(255)            null,
    START_TIME_                datetime(3)             null,
    START_USER_ID_             varchar(255)            null,
    LOCK_TIME_                 timestamp(3)            null,
    LOCK_OWNER_                varchar(255)            null,
    IS_COUNT_ENABLED_          tinyint                 null,
    EVT_SUBSCR_COUNT_          int                     null,
    TASK_COUNT_                int                     null,
    JOB_COUNT_                 int                     null,
    TIMER_JOB_COUNT_           int                     null,
    SUSP_JOB_COUNT_            int                     null,
    DEADLETTER_JOB_COUNT_      int                     null,
    EXTERNAL_WORKER_JOB_COUNT_ int                     null,
    VAR_COUNT_                 int                     null,
    ID_LINK_COUNT_             int                     null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_  varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null,
    constraint ACT_FK_EXE_PARENT
        foreign key (PARENT_ID_) references act_ru_execution (ID_)
            on delete cascade,
    constraint ACT_FK_EXE_PROCDEF
        foreign key (PROC_DEF_ID_) references act_re_procdef (ID_),
    constraint ACT_FK_EXE_PROCINST
        foreign key (PROC_INST_ID_) references act_ru_execution (ID_)
            on update cascade on delete cascade,
    constraint ACT_FK_EXE_SUPER
        foreign key (SUPER_EXEC_) references act_ru_execution (ID_)
            on delete cascade
)
    collate = utf8mb3_bin;

create index ACT_IDC_EXEC_ROOT
    on act_ru_execution (ROOT_PROC_INST_ID_);

create index ACT_IDX_EXEC_BUSKEY
    on act_ru_execution (BUSINESS_KEY_);

create index ACT_IDX_EXEC_REF_ID_
    on act_ru_execution (REFERENCE_ID_);

