create table act_hi_identitylink
(
    ID_                  varchar(64)  not null
        primary key,
    GROUP_ID_            varchar(255) null,
    TYPE_                varchar(255) null,
    USER_ID_             varchar(255) null,
    TASK_ID_             varchar(64)  null,
    CREATE_TIME_         datetime(3)  null,
    PROC_INST_ID_        varchar(64)  null,
    SCOPE_ID_            varchar(255) null,
    SUB_SCOPE_ID_        varchar(255) null,
    SCOPE_TYPE_          varchar(255) null,
    SCOPE_DEFINITION_ID_ varchar(255) null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_IDENT_LNK_SCOPE
    on act_hi_identitylink (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_SCOPE_DEF
    on act_hi_identitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_SUB_SCOPE
    on act_hi_identitylink (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_USER
    on act_hi_identitylink (USER_ID_);

