create table act_re_deployment
(
    ID_                   varchar(64)             not null
        primary key,
    NAME_                 varchar(255)            null,
    CATEGORY_             varchar(255)            null,
    KEY_                  varchar(255)            null,
    TENANT_ID_            varchar(255) default '' null,
    DEPLOY_TIME_          timestamp(3)            null,
    DERIVED_FROM_         varchar(64)             null,
    DERIVED_FROM_ROOT_    varchar(64)             null,
    PARENT_DEPLOYMENT_ID_ varchar(255)            null,
    ENGINE_VERSION_       varchar(255)            null
)
    collate = utf8mb3_bin;

