create table act_hi_taskinst
(
    ID_                       varchar(64)             not null
        primary key,
    REV_                      int          default 1  null,
    PROC_DEF_ID_              varchar(64)             null,
    TASK_DEF_ID_              varchar(64)             null,
    TASK_DEF_KEY_             varchar(255)            null,
    PROC_INST_ID_             varchar(64)             null,
    EXECUTION_ID_             varchar(64)             null,
    SCOPE_ID_                 varchar(255)            null,
    SUB_SCOPE_ID_             varchar(255)            null,
    SCOPE_TYPE_               varchar(255)            null,
    SCOPE_DEFINITION_ID_      varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_ varchar(255)            null,
    STATE_                    varchar(255)            null,
    NAME_                     varchar(255)            null,
    PARENT_TASK_ID_           varchar(64)             null,
    DESCRIPTION_              varchar(4000)           null,
    OWNER_                    varchar(255)            null,
    ASSIGNEE_                 varchar(255)            null,
    START_TIME_               datetime(3)             not null,
    IN_PROGRESS_TIME_         datetime(3)             null,
    IN_PROGRESS_STARTED_BY_   varchar(255)            null,
    CLAIM_TIME_               datetime(3)             null,
    CLAIMED_BY_               varchar(255)            null,
    SUSPENDED_TIME_           datetime(3)             null,
    SUSPENDED_BY_             varchar(255)            null,
    END_TIME_                 datetime(3)             null,
    COMPLETED_BY_             varchar(255)            null,
    DURATION_                 bigint                  null,
    DELETE_REASON_            varchar(4000)           null,
    PRIORITY_                 int                     null,
    IN_PROGRESS_DUE_DATE_     datetime(3)             null,
    DUE_DATE_                 datetime(3)             null,
    FORM_KEY_                 varchar(255)            null,
    CATEGORY_                 varchar(255)            null,
    TENANT_ID_                varchar(255) default '' null,
    LAST_UPDATED_TIME_        datetime(3)             null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_TASK_SCOPE
    on act_hi_taskinst (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_TASK_SCOPE_DEF
    on act_hi_taskinst (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_TASK_SUB_SCOPE
    on act_hi_taskinst (SUB_SCOPE_ID_, SCOPE_TYPE_);

