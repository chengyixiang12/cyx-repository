create table act_re_procdef
(
    ID_                     varchar(64)             not null
        primary key,
    REV_                    int                     null,
    CATEGORY_               varchar(255)            null,
    NAME_                   varchar(255)            null,
    KEY_                    varchar(255)            not null,
    VERSION_                int                     not null,
    DEPLOYMENT_ID_          varchar(64)             null,
    RESOURCE_NAME_          varchar(4000)           null,
    DGRM_RESOURCE_NAME_     varchar(4000)           null,
    DESCRIPTION_            varchar(4000)           null,
    HAS_START_FORM_KEY_     tinyint                 null,
    HAS_GRAPHICAL_NOTATION_ tinyint                 null,
    SUSPENSION_STATE_       int                     null,
    TENANT_ID_              varchar(255) default '' null,
    ENGINE_VERSION_         varchar(255)            null,
    DERIVED_FROM_           varchar(64)             null,
    DERIVED_FROM_ROOT_      varchar(64)             null,
    DERIVED_VERSION_        int          default 0  not null,
    constraint ACT_UNIQ_PROCDEF
        unique (KEY_, VERSION_, DERIVED_VERSION_, TENANT_ID_)
)
    collate = utf8mb3_bin;

