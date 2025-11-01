create table act_re_model
(
    ID_                           varchar(64)             not null
        primary key,
    REV_                          int                     null,
    NAME_                         varchar(255)            null,
    KEY_                          varchar(255)            null,
    CATEGORY_                     varchar(255)            null,
    CREATE_TIME_                  timestamp(3)            null,
    LAST_UPDATE_TIME_             timestamp(3)            null,
    VERSION_                      int                     null,
    META_INFO_                    varchar(4000)           null,
    DEPLOYMENT_ID_                varchar(64)             null,
    EDITOR_SOURCE_VALUE_ID_       varchar(64)             null,
    EDITOR_SOURCE_EXTRA_VALUE_ID_ varchar(64)             null,
    TENANT_ID_                    varchar(255) default '' null,
    constraint ACT_FK_MODEL_DEPLOYMENT
        foreign key (DEPLOYMENT_ID_) references act_re_deployment (ID_),
    constraint ACT_FK_MODEL_SOURCE
        foreign key (EDITOR_SOURCE_VALUE_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_MODEL_SOURCE_EXTRA
        foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_) references act_ge_bytearray (ID_)
)
    collate = utf8mb3_bin;

