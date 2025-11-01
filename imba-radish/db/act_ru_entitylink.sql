create table act_ru_entitylink
(
    ID_                      varchar(64)  not null
        primary key,
    REV_                     int          null,
    CREATE_TIME_             datetime(3)  null,
    LINK_TYPE_               varchar(255) null,
    SCOPE_ID_                varchar(255) null,
    SUB_SCOPE_ID_            varchar(255) null,
    SCOPE_TYPE_              varchar(255) null,
    SCOPE_DEFINITION_ID_     varchar(255) null,
    PARENT_ELEMENT_ID_       varchar(255) null,
    REF_SCOPE_ID_            varchar(255) null,
    REF_SCOPE_TYPE_          varchar(255) null,
    REF_SCOPE_DEFINITION_ID_ varchar(255) null,
    ROOT_SCOPE_ID_           varchar(255) null,
    ROOT_SCOPE_TYPE_         varchar(255) null,
    HIERARCHY_TYPE_          varchar(255) null
)
    collate = utf8mb3_bin;

create index ACT_IDX_ENT_LNK_REF_SCOPE
    on act_ru_entitylink (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_ROOT_SCOPE
    on act_ru_entitylink (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_SCOPE
    on act_ru_entitylink (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_SCOPE_DEF
    on act_ru_entitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

