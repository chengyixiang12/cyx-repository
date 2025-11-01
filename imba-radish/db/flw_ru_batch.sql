create table flw_ru_batch
(
    ID_            varchar(64)             not null
        primary key,
    REV_           int                     null,
    TYPE_          varchar(64)             not null,
    SEARCH_KEY_    varchar(255)            null,
    SEARCH_KEY2_   varchar(255)            null,
    CREATE_TIME_   datetime(3)             not null,
    COMPLETE_TIME_ datetime(3)             null,
    STATUS_        varchar(255)            null,
    BATCH_DOC_ID_  varchar(64)             null,
    TENANT_ID_     varchar(255) default '' null
)
    collate = utf8mb3_bin;

