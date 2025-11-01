create table act_hi_varinst
(
    ID_                varchar(64)   not null
        primary key,
    REV_               int default 1 null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    TASK_ID_           varchar(64)   null,
    NAME_              varchar(255)  not null,
    VAR_TYPE_          varchar(100)  null,
    SCOPE_ID_          varchar(255)  null,
    SUB_SCOPE_ID_      varchar(255)  null,
    SCOPE_TYPE_        varchar(255)  null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    META_INFO_         varchar(4000) null,
    CREATE_TIME_       datetime(3)   null,
    LAST_UPDATED_TIME_ datetime(3)   null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_PROCVAR_NAME_TYPE
    on act_hi_varinst (NAME_, VAR_TYPE_);

create index ACT_IDX_HI_VAR_SCOPE_ID_TYPE
    on act_hi_varinst (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_VAR_SUB_ID_TYPE
    on act_hi_varinst (SUB_SCOPE_ID_, SCOPE_TYPE_);

