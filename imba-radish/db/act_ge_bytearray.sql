create table act_ge_bytearray
(
    ID_            varchar(64)  not null
        primary key,
    REV_           int          null,
    NAME_          varchar(255) null,
    DEPLOYMENT_ID_ varchar(64)  null,
    BYTES_         longblob     null,
    GENERATED_     tinyint      null,
    constraint ACT_FK_BYTEARR_DEPL
        foreign key (DEPLOYMENT_ID_) references act_re_deployment (ID_)
)
    collate = utf8mb3_bin;

