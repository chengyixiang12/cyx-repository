create table flw_ru_batch_part
(
    ID_            varchar(64)             not null
        primary key,
    REV_           int                     null,
    BATCH_ID_      varchar(64)             null,
    TYPE_          varchar(64)             not null,
    SCOPE_ID_      varchar(64)             null,
    SUB_SCOPE_ID_  varchar(64)             null,
    SCOPE_TYPE_    varchar(64)             null,
    SEARCH_KEY_    varchar(255)            null,
    SEARCH_KEY2_   varchar(255)            null,
    CREATE_TIME_   datetime(3)             not null,
    COMPLETE_TIME_ datetime(3)             null,
    STATUS_        varchar(255)            null,
    RESULT_DOC_ID_ varchar(64)             null,
    TENANT_ID_     varchar(255) default '' null,
    constraint FLW_FK_BATCH_PART_PARENT
        foreign key (BATCH_ID_) references flw_ru_batch (ID_)
)
    collate = utf8mb3_bin;

create index FLW_IDX_BATCH_PART
    on flw_ru_batch_part (BATCH_ID_);

