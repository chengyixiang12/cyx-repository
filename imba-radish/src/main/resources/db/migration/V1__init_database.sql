CREATE TABLE act_evt_log
(
    LOG_NR_       BIGINT AUTO_INCREMENT    NOT NULL,
    TYPE_         VARCHAR(64)              NULL,
    PROC_DEF_ID_  VARCHAR(64)              NULL,
    PROC_INST_ID_ VARCHAR(64)              NULL,
    EXECUTION_ID_ VARCHAR(64)              NULL,
    TASK_ID_      VARCHAR(64)              NULL,
    TIME_STAMP_   timestamp DEFAULT NOW(3) NOT NULL,
    USER_ID_      VARCHAR(255)             NULL,
    DATA_         BLOB                     NULL,
    LOCK_OWNER_   VARCHAR(255)             NULL,
    LOCK_TIME_    timestamp                NULL,
    IS_PROCESSED_ TINYINT   DEFAULT 0      NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (LOG_NR_)
);

CREATE TABLE act_ge_bytearray
(
    ID_            VARCHAR(64)  NOT NULL,
    REV_           INT          NULL,
    NAME_          VARCHAR(255) NULL,
    DEPLOYMENT_ID_ VARCHAR(64)  NULL,
    BYTES_         BLOB         NULL,
    GENERATED_     TINYINT      NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ge_property
(
    NAME_  VARCHAR(64)  NOT NULL,
    VALUE_ VARCHAR(300) NULL,
    REV_   INT          NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (NAME_)
);

CREATE TABLE act_hi_entitylink
(
    ID_                      VARCHAR(64)  NOT NULL,
    LINK_TYPE_               VARCHAR(255) NULL,
    CREATE_TIME_             datetime     NULL,
    SCOPE_ID_                VARCHAR(255) NULL,
    SUB_SCOPE_ID_            VARCHAR(255) NULL,
    SCOPE_TYPE_              VARCHAR(255) NULL,
    SCOPE_DEFINITION_ID_     VARCHAR(255) NULL,
    PARENT_ELEMENT_ID_       VARCHAR(255) NULL,
    REF_SCOPE_ID_            VARCHAR(255) NULL,
    REF_SCOPE_TYPE_          VARCHAR(255) NULL,
    REF_SCOPE_DEFINITION_ID_ VARCHAR(255) NULL,
    ROOT_SCOPE_ID_           VARCHAR(255) NULL,
    ROOT_SCOPE_TYPE_         VARCHAR(255) NULL,
    HIERARCHY_TYPE_          VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_hi_identitylink
(
    ID_                  VARCHAR(64)  NOT NULL,
    GROUP_ID_            VARCHAR(255) NULL,
    TYPE_                VARCHAR(255) NULL,
    USER_ID_             VARCHAR(255) NULL,
    TASK_ID_             VARCHAR(64)  NULL,
    CREATE_TIME_         datetime     NULL,
    PROC_INST_ID_        VARCHAR(64)  NULL,
    SCOPE_ID_            VARCHAR(255) NULL,
    SUB_SCOPE_ID_        VARCHAR(255) NULL,
    SCOPE_TYPE_          VARCHAR(255) NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_hi_taskinst
(
    ID_                       VARCHAR(64)             NOT NULL,
    REV_                      INT          DEFAULT 1  NULL,
    PROC_DEF_ID_              VARCHAR(64)             NULL,
    TASK_DEF_ID_              VARCHAR(64)             NULL,
    TASK_DEF_KEY_             VARCHAR(255)            NULL,
    PROC_INST_ID_             VARCHAR(64)             NULL,
    EXECUTION_ID_             VARCHAR(64)             NULL,
    SCOPE_ID_                 VARCHAR(255)            NULL,
    SUB_SCOPE_ID_             VARCHAR(255)            NULL,
    SCOPE_TYPE_               VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_      VARCHAR(255)            NULL,
    PROPAGATED_STAGE_INST_ID_ VARCHAR(255)            NULL,
    STATE_                    VARCHAR(255)            NULL,
    NAME_                     VARCHAR(255)            NULL,
    PARENT_TASK_ID_           VARCHAR(64)             NULL,
    DESCRIPTION_              VARCHAR(4000)           NULL,
    OWNER_                    VARCHAR(255)            NULL,
    ASSIGNEE_                 VARCHAR(255)            NULL,
    START_TIME_               datetime                NOT NULL,
    IN_PROGRESS_TIME_         datetime                NULL,
    IN_PROGRESS_STARTED_BY_   VARCHAR(255)            NULL,
    CLAIM_TIME_               datetime                NULL,
    CLAIMED_BY_               VARCHAR(255)            NULL,
    SUSPENDED_TIME_           datetime                NULL,
    SUSPENDED_BY_             VARCHAR(255)            NULL,
    END_TIME_                 datetime                NULL,
    COMPLETED_BY_             VARCHAR(255)            NULL,
    DURATION_                 BIGINT                  NULL,
    DELETE_REASON_            VARCHAR(4000)           NULL,
    PRIORITY_                 INT                     NULL,
    IN_PROGRESS_DUE_DATE_     datetime                NULL,
    DUE_DATE_                 datetime                NULL,
    FORM_KEY_                 VARCHAR(255)            NULL,
    CATEGORY_                 VARCHAR(255)            NULL,
    TENANT_ID_                VARCHAR(255) DEFAULT '' NULL,
    LAST_UPDATED_TIME_        datetime                NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_hi_tsk_log
(
    ID_                  BIGINT AUTO_INCREMENT   NOT NULL,
    TYPE_                VARCHAR(64)             NULL,
    TASK_ID_             VARCHAR(64)             NOT NULL,
    TIME_STAMP_          timestamp               NOT NULL,
    USER_ID_             VARCHAR(255)            NULL,
    DATA_                VARCHAR(4000)           NULL,
    EXECUTION_ID_        VARCHAR(64)             NULL,
    PROC_INST_ID_        VARCHAR(64)             NULL,
    PROC_DEF_ID_         VARCHAR(64)             NULL,
    SCOPE_ID_            VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255)            NULL,
    SUB_SCOPE_ID_        VARCHAR(255)            NULL,
    SCOPE_TYPE_          VARCHAR(255)            NULL,
    TENANT_ID_           VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_hi_varinst
(
    ID_                VARCHAR(64)   NOT NULL,
    REV_               INT DEFAULT 1 NULL,
    PROC_INST_ID_      VARCHAR(64)   NULL,
    EXECUTION_ID_      VARCHAR(64)   NULL,
    TASK_ID_           VARCHAR(64)   NULL,
    NAME_              VARCHAR(255)  NOT NULL,
    VAR_TYPE_          VARCHAR(100)  NULL,
    SCOPE_ID_          VARCHAR(255)  NULL,
    SUB_SCOPE_ID_      VARCHAR(255)  NULL,
    SCOPE_TYPE_        VARCHAR(255)  NULL,
    BYTEARRAY_ID_      VARCHAR(64)   NULL,
    DOUBLE_            DOUBLE        NULL,
    LONG_              BIGINT        NULL,
    TEXT_              VARCHAR(4000) NULL,
    TEXT2_             VARCHAR(4000) NULL,
    META_INFO_         VARCHAR(4000) NULL,
    CREATE_TIME_       datetime      NULL,
    LAST_UPDATED_TIME_ datetime      NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_procdef_info
(
    ID_           VARCHAR(64) NOT NULL,
    PROC_DEF_ID_  VARCHAR(64) NOT NULL,
    REV_          INT         NULL,
    INFO_JSON_ID_ VARCHAR(64) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_re_deployment
(
    ID_                   VARCHAR(64)             NOT NULL,
    NAME_                 VARCHAR(255)            NULL,
    CATEGORY_             VARCHAR(255)            NULL,
    KEY_                  VARCHAR(255)            NULL,
    TENANT_ID_            VARCHAR(255) DEFAULT '' NULL,
    DEPLOY_TIME_          timestamp               NULL,
    DERIVED_FROM_         VARCHAR(64)             NULL,
    DERIVED_FROM_ROOT_    VARCHAR(64)             NULL,
    PARENT_DEPLOYMENT_ID_ VARCHAR(255)            NULL,
    ENGINE_VERSION_       VARCHAR(255)            NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_re_model
(
    ID_                           VARCHAR(64)             NOT NULL,
    REV_                          INT                     NULL,
    NAME_                         VARCHAR(255)            NULL,
    KEY_                          VARCHAR(255)            NULL,
    CATEGORY_                     VARCHAR(255)            NULL,
    CREATE_TIME_                  timestamp               NULL,
    LAST_UPDATE_TIME_             timestamp               NULL,
    VERSION_                      INT                     NULL,
    META_INFO_                    VARCHAR(4000)           NULL,
    DEPLOYMENT_ID_                VARCHAR(64)             NULL,
    EDITOR_SOURCE_VALUE_ID_       VARCHAR(64)             NULL,
    EDITOR_SOURCE_EXTRA_VALUE_ID_ VARCHAR(64)             NULL,
    TENANT_ID_                    VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_re_procdef
(
    ID_                     VARCHAR(64)             NOT NULL,
    REV_                    INT                     NULL,
    CATEGORY_               VARCHAR(255)            NULL,
    NAME_                   VARCHAR(255)            NULL,
    KEY_                    VARCHAR(255)            NOT NULL,
    VERSION_                INT                     NOT NULL,
    DEPLOYMENT_ID_          VARCHAR(64)             NULL,
    RESOURCE_NAME_          VARCHAR(4000)           NULL,
    DGRM_RESOURCE_NAME_     VARCHAR(4000)           NULL,
    DESCRIPTION_            VARCHAR(4000)           NULL,
    HAS_START_FORM_KEY_     TINYINT                 NULL,
    HAS_GRAPHICAL_NOTATION_ TINYINT                 NULL,
    SUSPENSION_STATE_       INT                     NULL,
    TENANT_ID_              VARCHAR(255) DEFAULT '' NULL,
    ENGINE_VERSION_         VARCHAR(255)            NULL,
    DERIVED_FROM_           VARCHAR(64)             NULL,
    DERIVED_FROM_ROOT_      VARCHAR(64)             NULL,
    DERIVED_VERSION_        INT          DEFAULT 0  NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_actinst
(
    ID_                VARCHAR(64)             NOT NULL,
    REV_               INT          DEFAULT 1  NULL,
    PROC_DEF_ID_       VARCHAR(64)             NOT NULL,
    PROC_INST_ID_      VARCHAR(64)             NOT NULL,
    EXECUTION_ID_      VARCHAR(64)             NOT NULL,
    ACT_ID_            VARCHAR(255)            NOT NULL,
    TASK_ID_           VARCHAR(64)             NULL,
    CALL_PROC_INST_ID_ VARCHAR(64)             NULL,
    ACT_NAME_          VARCHAR(255)            NULL,
    ACT_TYPE_          VARCHAR(255)            NOT NULL,
    ASSIGNEE_          VARCHAR(255)            NULL,
    START_TIME_        datetime                NOT NULL,
    END_TIME_          datetime                NULL,
    DURATION_          BIGINT                  NULL,
    TRANSACTION_ORDER_ INT                     NULL,
    DELETE_REASON_     VARCHAR(4000)           NULL,
    TENANT_ID_         VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_deadletter_job
(
    ID_                  VARCHAR(64)             NOT NULL,
    REV_                 INT                     NULL,
    CATEGORY_            VARCHAR(255)            NULL,
    TYPE_                VARCHAR(255)            NOT NULL,
    EXCLUSIVE_           TINYINT(1)              NULL,
    EXECUTION_ID_        VARCHAR(64)             NULL,
    PROCESS_INSTANCE_ID_ VARCHAR(64)             NULL,
    PROC_DEF_ID_         VARCHAR(64)             NULL,
    ELEMENT_ID_          VARCHAR(255)            NULL,
    ELEMENT_NAME_        VARCHAR(255)            NULL,
    SCOPE_ID_            VARCHAR(255)            NULL,
    SUB_SCOPE_ID_        VARCHAR(255)            NULL,
    SCOPE_TYPE_          VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255)            NULL,
    CORRELATION_ID_      VARCHAR(255)            NULL,
    EXCEPTION_STACK_ID_  VARCHAR(64)             NULL,
    EXCEPTION_MSG_       VARCHAR(4000)           NULL,
    DUEDATE_             timestamp               NULL,
    REPEAT_              VARCHAR(255)            NULL,
    HANDLER_TYPE_        VARCHAR(255)            NULL,
    HANDLER_CFG_         VARCHAR(4000)           NULL,
    CUSTOM_VALUES_ID_    VARCHAR(64)             NULL,
    CREATE_TIME_         timestamp               NULL,
    TENANT_ID_           VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_entitylink
(
    ID_                      VARCHAR(64)  NOT NULL,
    REV_                     INT          NULL,
    CREATE_TIME_             datetime     NULL,
    LINK_TYPE_               VARCHAR(255) NULL,
    SCOPE_ID_                VARCHAR(255) NULL,
    SUB_SCOPE_ID_            VARCHAR(255) NULL,
    SCOPE_TYPE_              VARCHAR(255) NULL,
    SCOPE_DEFINITION_ID_     VARCHAR(255) NULL,
    PARENT_ELEMENT_ID_       VARCHAR(255) NULL,
    REF_SCOPE_ID_            VARCHAR(255) NULL,
    REF_SCOPE_TYPE_          VARCHAR(255) NULL,
    REF_SCOPE_DEFINITION_ID_ VARCHAR(255) NULL,
    ROOT_SCOPE_ID_           VARCHAR(255) NULL,
    ROOT_SCOPE_TYPE_         VARCHAR(255) NULL,
    HIERARCHY_TYPE_          VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_event_subscr
(
    ID_                   VARCHAR(64)                 NOT NULL,
    REV_                  INT                         NULL,
    EVENT_TYPE_           VARCHAR(255)                NOT NULL,
    EVENT_NAME_           VARCHAR(255)                NULL,
    EXECUTION_ID_         VARCHAR(64)                 NULL,
    PROC_INST_ID_         VARCHAR(64)                 NULL,
    ACTIVITY_ID_          VARCHAR(64)                 NULL,
    CONFIGURATION_        VARCHAR(255)                NULL,
    CREATED_              timestamp    DEFAULT NOW(3) NOT NULL,
    PROC_DEF_ID_          VARCHAR(64)                 NULL,
    SUB_SCOPE_ID_         VARCHAR(64)                 NULL,
    SCOPE_ID_             VARCHAR(64)                 NULL,
    SCOPE_DEFINITION_ID_  VARCHAR(64)                 NULL,
    SCOPE_DEFINITION_KEY_ VARCHAR(255)                NULL,
    SCOPE_TYPE_           VARCHAR(64)                 NULL,
    LOCK_TIME_            timestamp                   NULL,
    LOCK_OWNER_           VARCHAR(255)                NULL,
    TENANT_ID_            VARCHAR(255) DEFAULT ''     NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_execution
(
    ID_                        VARCHAR(64)             NOT NULL,
    REV_                       INT                     NULL,
    PROC_INST_ID_              VARCHAR(64)             NULL,
    BUSINESS_KEY_              VARCHAR(255)            NULL,
    PARENT_ID_                 VARCHAR(64)             NULL,
    PROC_DEF_ID_               VARCHAR(64)             NULL,
    SUPER_EXEC_                VARCHAR(64)             NULL,
    ROOT_PROC_INST_ID_         VARCHAR(64)             NULL,
    ACT_ID_                    VARCHAR(255)            NULL,
    IS_ACTIVE_                 TINYINT                 NULL,
    IS_CONCURRENT_             TINYINT                 NULL,
    IS_SCOPE_                  TINYINT                 NULL,
    IS_EVENT_SCOPE_            TINYINT                 NULL,
    IS_MI_ROOT_                TINYINT                 NULL,
    SUSPENSION_STATE_          INT                     NULL,
    CACHED_ENT_STATE_          INT                     NULL,
    TENANT_ID_                 VARCHAR(255) DEFAULT '' NULL,
    NAME_                      VARCHAR(255)            NULL,
    START_ACT_ID_              VARCHAR(255)            NULL,
    START_TIME_                datetime                NULL,
    START_USER_ID_             VARCHAR(255)            NULL,
    LOCK_TIME_                 timestamp               NULL,
    LOCK_OWNER_                VARCHAR(255)            NULL,
    IS_COUNT_ENABLED_          TINYINT                 NULL,
    EVT_SUBSCR_COUNT_          INT                     NULL,
    TASK_COUNT_                INT                     NULL,
    JOB_COUNT_                 INT                     NULL,
    TIMER_JOB_COUNT_           INT                     NULL,
    SUSP_JOB_COUNT_            INT                     NULL,
    DEADLETTER_JOB_COUNT_      INT                     NULL,
    EXTERNAL_WORKER_JOB_COUNT_ INT                     NULL,
    VAR_COUNT_                 INT                     NULL,
    ID_LINK_COUNT_             INT                     NULL,
    CALLBACK_ID_               VARCHAR(255)            NULL,
    CALLBACK_TYPE_             VARCHAR(255)            NULL,
    REFERENCE_ID_              VARCHAR(255)            NULL,
    REFERENCE_TYPE_            VARCHAR(255)            NULL,
    PROPAGATED_STAGE_INST_ID_  VARCHAR(255)            NULL,
    BUSINESS_STATUS_           VARCHAR(255)            NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_external_job
(
    ID_                  VARCHAR(64)             NOT NULL,
    REV_                 INT                     NULL,
    CATEGORY_            VARCHAR(255)            NULL,
    TYPE_                VARCHAR(255)            NOT NULL,
    LOCK_EXP_TIME_       timestamp               NULL,
    LOCK_OWNER_          VARCHAR(255)            NULL,
    EXCLUSIVE_           TINYINT(1)              NULL,
    EXECUTION_ID_        VARCHAR(64)             NULL,
    PROCESS_INSTANCE_ID_ VARCHAR(64)             NULL,
    PROC_DEF_ID_         VARCHAR(64)             NULL,
    ELEMENT_ID_          VARCHAR(255)            NULL,
    ELEMENT_NAME_        VARCHAR(255)            NULL,
    SCOPE_ID_            VARCHAR(255)            NULL,
    SUB_SCOPE_ID_        VARCHAR(255)            NULL,
    SCOPE_TYPE_          VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255)            NULL,
    CORRELATION_ID_      VARCHAR(255)            NULL,
    RETRIES_             INT                     NULL,
    EXCEPTION_STACK_ID_  VARCHAR(64)             NULL,
    EXCEPTION_MSG_       VARCHAR(4000)           NULL,
    DUEDATE_             timestamp               NULL,
    REPEAT_              VARCHAR(255)            NULL,
    HANDLER_TYPE_        VARCHAR(255)            NULL,
    HANDLER_CFG_         VARCHAR(4000)           NULL,
    CUSTOM_VALUES_ID_    VARCHAR(64)             NULL,
    CREATE_TIME_         timestamp               NULL,
    TENANT_ID_           VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_history_job
(
    ID_                 VARCHAR(64)             NOT NULL,
    REV_                INT                     NULL,
    LOCK_EXP_TIME_      timestamp               NULL,
    LOCK_OWNER_         VARCHAR(255)            NULL,
    RETRIES_            INT                     NULL,
    EXCEPTION_STACK_ID_ VARCHAR(64)             NULL,
    EXCEPTION_MSG_      VARCHAR(4000)           NULL,
    HANDLER_TYPE_       VARCHAR(255)            NULL,
    HANDLER_CFG_        VARCHAR(4000)           NULL,
    CUSTOM_VALUES_ID_   VARCHAR(64)             NULL,
    ADV_HANDLER_CFG_ID_ VARCHAR(64)             NULL,
    CREATE_TIME_        timestamp               NULL,
    SCOPE_TYPE_         VARCHAR(255)            NULL,
    TENANT_ID_          VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_identitylink
(
    ID_                  VARCHAR(64)  NOT NULL,
    REV_                 INT          NULL,
    GROUP_ID_            VARCHAR(255) NULL,
    TYPE_                VARCHAR(255) NULL,
    USER_ID_             VARCHAR(255) NULL,
    TASK_ID_             VARCHAR(64)  NULL,
    PROC_INST_ID_        VARCHAR(64)  NULL,
    PROC_DEF_ID_         VARCHAR(64)  NULL,
    SCOPE_ID_            VARCHAR(255) NULL,
    SUB_SCOPE_ID_        VARCHAR(255) NULL,
    SCOPE_TYPE_          VARCHAR(255) NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_job
(
    ID_                  VARCHAR(64)             NOT NULL,
    REV_                 INT                     NULL,
    CATEGORY_            VARCHAR(255)            NULL,
    TYPE_                VARCHAR(255)            NOT NULL,
    LOCK_EXP_TIME_       timestamp               NULL,
    LOCK_OWNER_          VARCHAR(255)            NULL,
    EXCLUSIVE_           TINYINT(1)              NULL,
    EXECUTION_ID_        VARCHAR(64)             NULL,
    PROCESS_INSTANCE_ID_ VARCHAR(64)             NULL,
    PROC_DEF_ID_         VARCHAR(64)             NULL,
    ELEMENT_ID_          VARCHAR(255)            NULL,
    ELEMENT_NAME_        VARCHAR(255)            NULL,
    SCOPE_ID_            VARCHAR(255)            NULL,
    SUB_SCOPE_ID_        VARCHAR(255)            NULL,
    SCOPE_TYPE_          VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255)            NULL,
    CORRELATION_ID_      VARCHAR(255)            NULL,
    RETRIES_             INT                     NULL,
    EXCEPTION_STACK_ID_  VARCHAR(64)             NULL,
    EXCEPTION_MSG_       VARCHAR(4000)           NULL,
    DUEDATE_             timestamp               NULL,
    REPEAT_              VARCHAR(255)            NULL,
    HANDLER_TYPE_        VARCHAR(255)            NULL,
    HANDLER_CFG_         VARCHAR(4000)           NULL,
    CUSTOM_VALUES_ID_    VARCHAR(64)             NULL,
    CREATE_TIME_         timestamp               NULL,
    TENANT_ID_           VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_suspended_job
(
    ID_                  VARCHAR(64)             NOT NULL,
    REV_                 INT                     NULL,
    CATEGORY_            VARCHAR(255)            NULL,
    TYPE_                VARCHAR(255)            NOT NULL,
    EXCLUSIVE_           TINYINT(1)              NULL,
    EXECUTION_ID_        VARCHAR(64)             NULL,
    PROCESS_INSTANCE_ID_ VARCHAR(64)             NULL,
    PROC_DEF_ID_         VARCHAR(64)             NULL,
    ELEMENT_ID_          VARCHAR(255)            NULL,
    ELEMENT_NAME_        VARCHAR(255)            NULL,
    SCOPE_ID_            VARCHAR(255)            NULL,
    SUB_SCOPE_ID_        VARCHAR(255)            NULL,
    SCOPE_TYPE_          VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255)            NULL,
    CORRELATION_ID_      VARCHAR(255)            NULL,
    RETRIES_             INT                     NULL,
    EXCEPTION_STACK_ID_  VARCHAR(64)             NULL,
    EXCEPTION_MSG_       VARCHAR(4000)           NULL,
    DUEDATE_             timestamp               NULL,
    REPEAT_              VARCHAR(255)            NULL,
    HANDLER_TYPE_        VARCHAR(255)            NULL,
    HANDLER_CFG_         VARCHAR(4000)           NULL,
    CUSTOM_VALUES_ID_    VARCHAR(64)             NULL,
    CREATE_TIME_         timestamp               NULL,
    TENANT_ID_           VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_task
(
    ID_                       VARCHAR(64)             NOT NULL,
    REV_                      INT                     NULL,
    EXECUTION_ID_             VARCHAR(64)             NULL,
    PROC_INST_ID_             VARCHAR(64)             NULL,
    PROC_DEF_ID_              VARCHAR(64)             NULL,
    TASK_DEF_ID_              VARCHAR(64)             NULL,
    SCOPE_ID_                 VARCHAR(255)            NULL,
    SUB_SCOPE_ID_             VARCHAR(255)            NULL,
    SCOPE_TYPE_               VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_      VARCHAR(255)            NULL,
    PROPAGATED_STAGE_INST_ID_ VARCHAR(255)            NULL,
    STATE_                    VARCHAR(255)            NULL,
    NAME_                     VARCHAR(255)            NULL,
    PARENT_TASK_ID_           VARCHAR(64)             NULL,
    DESCRIPTION_              VARCHAR(4000)           NULL,
    TASK_DEF_KEY_             VARCHAR(255)            NULL,
    OWNER_                    VARCHAR(255)            NULL,
    ASSIGNEE_                 VARCHAR(255)            NULL,
    DELEGATION_               VARCHAR(64)             NULL,
    PRIORITY_                 INT                     NULL,
    CREATE_TIME_              timestamp               NULL,
    IN_PROGRESS_TIME_         datetime                NULL,
    IN_PROGRESS_STARTED_BY_   VARCHAR(255)            NULL,
    CLAIM_TIME_               datetime                NULL,
    CLAIMED_BY_               VARCHAR(255)            NULL,
    SUSPENDED_TIME_           datetime                NULL,
    SUSPENDED_BY_             VARCHAR(255)            NULL,
    IN_PROGRESS_DUE_DATE_     datetime                NULL,
    DUE_DATE_                 datetime                NULL,
    CATEGORY_                 VARCHAR(255)            NULL,
    SUSPENSION_STATE_         INT                     NULL,
    TENANT_ID_                VARCHAR(255) DEFAULT '' NULL,
    FORM_KEY_                 VARCHAR(255)            NULL,
    IS_COUNT_ENABLED_         TINYINT                 NULL,
    VAR_COUNT_                INT                     NULL,
    ID_LINK_COUNT_            INT                     NULL,
    SUB_TASK_COUNT_           INT                     NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_timer_job
(
    ID_                  VARCHAR(64)             NOT NULL,
    REV_                 INT                     NULL,
    CATEGORY_            VARCHAR(255)            NULL,
    TYPE_                VARCHAR(255)            NOT NULL,
    LOCK_EXP_TIME_       timestamp               NULL,
    LOCK_OWNER_          VARCHAR(255)            NULL,
    EXCLUSIVE_           TINYINT(1)              NULL,
    EXECUTION_ID_        VARCHAR(64)             NULL,
    PROCESS_INSTANCE_ID_ VARCHAR(64)             NULL,
    PROC_DEF_ID_         VARCHAR(64)             NULL,
    ELEMENT_ID_          VARCHAR(255)            NULL,
    ELEMENT_NAME_        VARCHAR(255)            NULL,
    SCOPE_ID_            VARCHAR(255)            NULL,
    SUB_SCOPE_ID_        VARCHAR(255)            NULL,
    SCOPE_TYPE_          VARCHAR(255)            NULL,
    SCOPE_DEFINITION_ID_ VARCHAR(255)            NULL,
    CORRELATION_ID_      VARCHAR(255)            NULL,
    RETRIES_             INT                     NULL,
    EXCEPTION_STACK_ID_  VARCHAR(64)             NULL,
    EXCEPTION_MSG_       VARCHAR(4000)           NULL,
    DUEDATE_             timestamp               NULL,
    REPEAT_              VARCHAR(255)            NULL,
    HANDLER_TYPE_        VARCHAR(255)            NULL,
    HANDLER_CFG_         VARCHAR(4000)           NULL,
    CUSTOM_VALUES_ID_    VARCHAR(64)             NULL,
    CREATE_TIME_         timestamp               NULL,
    TENANT_ID_           VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE act_ru_variable
(
    ID_           VARCHAR(64)   NOT NULL,
    REV_          INT           NULL,
    TYPE_         VARCHAR(255)  NOT NULL,
    NAME_         VARCHAR(255)  NOT NULL,
    EXECUTION_ID_ VARCHAR(64)   NULL,
    PROC_INST_ID_ VARCHAR(64)   NULL,
    TASK_ID_      VARCHAR(64)   NULL,
    SCOPE_ID_     VARCHAR(255)  NULL,
    SUB_SCOPE_ID_ VARCHAR(255)  NULL,
    SCOPE_TYPE_   VARCHAR(255)  NULL,
    BYTEARRAY_ID_ VARCHAR(64)   NULL,
    DOUBLE_       DOUBLE        NULL,
    LONG_         BIGINT        NULL,
    TEXT_         VARCHAR(4000) NULL,
    TEXT2_        VARCHAR(4000) NULL,
    META_INFO_    VARCHAR(4000) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE flw_ru_batch
(
    ID_            VARCHAR(64)             NOT NULL,
    REV_           INT                     NULL,
    TYPE_          VARCHAR(64)             NOT NULL,
    SEARCH_KEY_    VARCHAR(255)            NULL,
    SEARCH_KEY2_   VARCHAR(255)            NULL,
    CREATE_TIME_   datetime                NOT NULL,
    COMPLETE_TIME_ datetime                NULL,
    STATUS_        VARCHAR(255)            NULL,
    BATCH_DOC_ID_  VARCHAR(64)             NULL,
    TENANT_ID_     VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE flw_ru_batch_part
(
    ID_            VARCHAR(64)             NOT NULL,
    REV_           INT                     NULL,
    BATCH_ID_      VARCHAR(64)             NULL,
    TYPE_          VARCHAR(64)             NOT NULL,
    SCOPE_ID_      VARCHAR(64)             NULL,
    SUB_SCOPE_ID_  VARCHAR(64)             NULL,
    SCOPE_TYPE_    VARCHAR(64)             NULL,
    SEARCH_KEY_    VARCHAR(255)            NULL,
    SEARCH_KEY2_   VARCHAR(255)            NULL,
    CREATE_TIME_   datetime                NOT NULL,
    COMPLETE_TIME_ datetime                NULL,
    STATUS_        VARCHAR(255)            NULL,
    RESULT_DOC_ID_ VARCHAR(64)             NULL,
    TENANT_ID_     VARCHAR(255) DEFAULT '' NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (ID_)
);

CREATE TABLE sys_announcement
(
    id           BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键',
    sort_order   BIGINT                NULL COMMENT '排序',
    create_by    BIGINT                NULL COMMENT '创建人',
    create_time  datetime              NULL COMMENT '创建时间',
    update_by    BIGINT                NULL COMMENT '修改人',
    update_time  datetime              NULL COMMENT '修改时间',
    del_flag     TINYINT               NULL COMMENT '逻辑删除',
    title        VARCHAR(255)          NULL COMMENT '公告标题',
    publish_time datetime              NULL COMMENT '公布时间',
    context      VARCHAR(1000)         NULL COMMENT '公告内容',
    status       TINYINT               NULL COMMENT '状态；1：发布；0：草稿',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='公告表';

CREATE TABLE sys_dept
(
    id          BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键',
    sort_order  INT                   NULL COMMENT '排序字段',
    create_by   BIGINT                NULL COMMENT '创建人',
    create_time datetime              NULL COMMENT '创建时间',
    update_by   BIGINT                NULL COMMENT '修改人',
    update_time datetime              NULL COMMENT '修改时间',
    del_flag    TINYINT               NULL COMMENT '逻辑删除',
    code        VARCHAR(20)           NULL COMMENT '部门编码',
    name        VARCHAR(20)           NULL COMMENT '部门名称',
    parent_id   BIGINT                NULL COMMENT '父级id',
    level       TINYINT               NULL COMMENT '层级',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='部门表';

CREATE TABLE sys_dialogue
(
    id          BIGINT       NOT NULL COMMENT '主键',
    sort_order  BIGINT       NULL COMMENT '排序',
    create_by   BIGINT       NULL COMMENT '创建人',
    create_time datetime     NULL COMMENT '创建时间',
    update_by   BIGINT       NULL COMMENT '修改人',
    update_time datetime     NULL COMMENT '修改时间',
    del_flag    TINYINT      NULL COMMENT '逻辑删除',
    title       VARCHAR(100) NULL COMMENT '标题',
    top_flag    TINYINT      NULL COMMENT '置顶标识；1：置顶；0：不置顶',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='智能对话表';

CREATE TABLE sys_dialogue_details
(
    id          BIGINT   NOT NULL COMMENT '主键',
    parent_id   BIGINT   NULL COMMENT '表sys_dialogue_history的id',
    create_by   BIGINT   NULL COMMENT '创建人',
    create_time datetime NULL COMMENT '创建时间',
    update_by   BIGINT   NULL COMMENT '修改人',
    update_time datetime NULL COMMENT '修改时间',
    del_flag    CHAR(1)  NULL COMMENT '逻辑删除；1：存在；0：删除',
    tag         TINYINT  NULL COMMENT '标识；1：用户；0：AI',
    content     LONGTEXT NULL COMMENT '对话内容',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='智能对话详情表';

CREATE TABLE sys_dict_data
(
    id          BIGINT       NOT NULL COMMENT '主键',
    parent_id   BIGINT       NULL COMMENT 'sys_dict_type的主键',
    sort_order  INT          NULL COMMENT '排序',
    create_by   BIGINT       NULL COMMENT '创建人',
    create_time datetime     NULL COMMENT '创建时间',
    update_by   BIGINT       NULL COMMENT '修改人',
    update_time datetime     NULL COMMENT '修改时间',
    del_flag    CHAR(1)      NULL COMMENT '逻辑删除；1：存在；0：删除',
    label       VARCHAR(100) NULL COMMENT '标签',
    value       VARCHAR(255) NULL COMMENT '键值',
    dict_type   VARCHAR(100) NULL COMMENT '字典类型',
    css_class   VARCHAR(255) NULL COMMENT '样式属性（其他样式扩展）',
    list_class  VARCHAR(255) NULL COMMENT '表格回显样式',
    is_default  TINYINT      NULL COMMENT '是否默认；1：是 0：否',
    status      TINYINT      NULL COMMENT '状态；1：启用；0：停用',
    remark      VARCHAR(500) NULL COMMENT '备注',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='字典数据表';

CREATE TABLE sys_dict_type
(
    id          BIGINT       NOT NULL COMMENT '主键',
    sort_order  INT          NULL COMMENT '排序',
    create_by   BIGINT       NULL COMMENT '创建人',
    create_time datetime     NULL COMMENT '创建时间',
    update_by   BIGINT       NULL COMMENT '修改人',
    update_time datetime     NULL COMMENT '修改时间',
    del_flag    CHAR(1)      NULL COMMENT '逻辑删除；1：存在；0：删除',
    dict_name   VARCHAR(100) NULL COMMENT '字典名称',
    dict_type   VARCHAR(100) NULL COMMENT '字典类型',
    status      TINYINT      NULL COMMENT '状态；1：启用；0：停用',
    remark      VARCHAR(500) NULL COMMENT '备注',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='字典类型表';

CREATE TABLE sys_file
(
    id            BIGINT       NOT NULL COMMENT '主键',
    create_by     BIGINT       NULL COMMENT '创建人',
    create_time   datetime     NULL COMMENT '创建时间',
    update_by     BIGINT       NULL COMMENT '修改人',
    update_time   datetime     NULL COMMENT '修改时间',
    del_flag      CHAR(1)      NULL COMMENT '逻辑删除；1：存在；0：删除',
    original_name VARCHAR(100) NULL COMMENT '源文件名',
    file_key      VARCHAR(100) NULL COMMENT '存储文件名',
    location      CHAR(1)      NULL COMMENT '存储地址；1：minio；2：磁盘',
    bucket        VARCHAR(100) NULL COMMENT '桶名',
    object_key    VARCHAR(500) NULL COMMENT '文件路径',
    file_suffix   VARCHAR(10)  NULL COMMENT '文件后缀；示例：.txt、.jpg',
    file_size     BIGINT       NULL COMMENT '文件大小；单位：B',
    file_hash     VARCHAR(100) NULL COMMENT '文件hash',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='文件表';

CREATE TABLE sys_log
(
    id              BIGINT       NOT NULL COMMENT '主键',
    create_by       VARCHAR(20)  NULL COMMENT '创建人',
    create_time     datetime     NULL COMMENT '创建时间',
    del_flag        CHAR(1)      NULL COMMENT '逻辑删除',
    log_level       VARCHAR(10)  NULL COMMENT '日志级别',
    ip_address      VARCHAR(45)  NULL COMMENT '请求ip',
    request_url     VARCHAR(255) NULL COMMENT '访问路径',
    request_method  VARCHAR(10)  NULL COMMENT '请求方法',
    request_params  LONGTEXT     NULL COMMENT '请求参数',
    response_result LONGTEXT     NULL COMMENT '响应结果',
    operation_desc  LONGTEXT     NULL COMMENT '操作描述',
    source          VARCHAR(50)  NULL COMMENT '日志来源',
    execution_time  BIGINT       NULL COMMENT '耗时',
    module_name     VARCHAR(50)  NULL COMMENT '模块名称',
    status_code     INT          NULL COMMENT '状态码',
    exception_info  LONGTEXT     NULL COMMENT '异常信息',
    os_browser_info VARCHAR(500) NULL COMMENT '操作系统/浏览器信息',
    type            VARCHAR(2)   NULL COMMENT '日志类型',
    update_by       BIGINT       NULL COMMENT '修改人',
    update_time     datetime     NULL COMMENT '修改时间',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='日志表';

CREATE TABLE sys_menu
(
    id          BIGINT                 NOT NULL COMMENT '菜单唯一标识符，主键',
    parent_id   BIGINT                 NULL COMMENT '父菜单ID，顶级菜单的父ID通常为0',
    create_by   BIGINT                 NULL COMMENT '创建人',
    create_time datetime DEFAULT NOW() NULL COMMENT '创建时间',
    update_by   BIGINT                 NULL COMMENT '修改人',
    update_time datetime DEFAULT NOW() NULL COMMENT '更新时间',
    del_flag    CHAR(1)                NULL COMMENT '逻辑删除；1：存在；0：删除',
    name        VARCHAR(50)            NOT NULL COMMENT '菜单名称',
    `path`      VARCHAR(255)           NULL COMMENT '前端路由路径',
    `component` VARCHAR(255)           NULL COMMENT '前端组件路径',
    icon        VARCHAR(50)            NULL COMMENT '菜单图标（如字体图标类名）',
    type        CHAR(1)                NOT NULL COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
    order_num   INT                    NULL COMMENT '排序号，数字越小，排序越靠前',
    status      TINYINT  DEFAULT 1     NULL COMMENT '菜单状态：0-禁用，1-启用',
    `visible`   TINYINT  DEFAULT 1     NULL COMMENT '是否显示：0-隐藏，1-显示',
    remark      VARCHAR(255)           NULL COMMENT '备注信息',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='菜单信息表';

CREATE TABLE sys_permission
(
    id            BIGINT       NOT NULL COMMENT '主键',
    create_by     BIGINT       NULL COMMENT '创建人',
    create_time   datetime     NULL COMMENT '创建时间',
    update_by     BIGINT       NULL COMMENT '修改人',
    update_time   datetime     NULL COMMENT '修改时间',
    del_flag      CHAR(1)      NULL COMMENT '逻辑删除',
    name          VARCHAR(50)  NULL COMMENT '权限名称',
    code          VARCHAR(50)  NOT NULL COMMENT '权限编码',
    type          CHAR(1)      NULL COMMENT '权限类型；1：菜单；2：按钮',
    status        CHAR(1)      NULL COMMENT '状态；1：启用；0：禁用',
    `description` VARCHAR(255) NULL COMMENT '描述',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='权限表';

CREATE TABLE sys_role
(
    id            BIGINT       NOT NULL COMMENT '主键',
    sort_order    INT          NULL COMMENT '排序字段',
    create_by     BIGINT       NULL COMMENT '创建人',
    create_time   datetime     NULL COMMENT '创建时间',
    update_by     BIGINT       NULL COMMENT '修改人',
    update_time   datetime     NULL COMMENT '修改时间',
    del_flag      CHAR(1)      NULL COMMENT '逻辑删除；1：存在；0：删除',
    code          VARCHAR(20)  NULL COMMENT '角色编码',
    name          VARCHAR(20)  NULL COMMENT '角色名称',
    `description` VARCHAR(500) NULL COMMENT '描述',
    status        TINYINT      NULL COMMENT '状态；1：启用；0：禁用',
    is_default    TINYINT      NULL COMMENT '是否标记为系统默认角色；1：是；0：不是',
    fix_role      TINYINT      NULL COMMENT '是否为固定角色（固定角色无法被删除）；1：是；0：不是',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='角色表';

CREATE TABLE sys_role_menu
(
    role_id BIGINT NOT NULL COMMENT '角色id',
    menu_id BIGINT NOT NULL COMMENT '菜单id'
) COMMENT ='角色菜单表';

CREATE TABLE sys_role_permission
(
    role_id       BIGINT NOT NULL COMMENT '角色主键',
    permission_id BIGINT NOT NULL COMMENT '权限主键'
) COMMENT ='角色权限表';

CREATE TABLE sys_schedule_job
(
    id            BIGINT       NOT NULL COMMENT '主键',
    create_by     BIGINT       NULL COMMENT '创建人',
    create_time   datetime     NULL COMMENT '创建时间',
    update_by     BIGINT       NULL COMMENT '修改人',
    update_time   datetime     NULL COMMENT '修改时间',
    del_flag      CHAR(1)      NULL COMMENT '逻辑删除；1：存在；0：删除',
    job_name      VARCHAR(100) NULL COMMENT '任务名称',
    job_group     VARCHAR(100) NULL COMMENT '任务组',
    cron          VARCHAR(100) NULL COMMENT 'cron表达式',
    job_type      VARCHAR(100) NULL COMMENT '任务类型',
    status        INT          NULL COMMENT '任务状态；1：启用；0：暂停',
    job_param     LONGTEXT     NULL COMMENT '任务所需参数',
    start_time    datetime     NULL COMMENT '开始时间',
    end_time      datetime     NULL COMMENT '结束时间',
    job_interval  INT          NULL COMMENT '间隔',
    interval_type CHAR(1)      NULL COMMENT '间隔类型；0：毫秒；1：秒；2：分钟；3：小时',
    schedule_type CHAR(1)      NULL COMMENT '调度类型；0：简单调度；1：cron表达式调度',
    remark        VARCHAR(500) NULL COMMENT '备注',
    job_class     VARCHAR(200) NULL COMMENT '作业执行类',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='调度任务表';

CREATE TABLE sys_secret_key
(
    id            BIGINT AUTO_INCREMENT NOT NULL COMMENT '主键',
    create_by     BIGINT                NULL COMMENT '创建人',
    create_time   datetime              NULL COMMENT '创建时间',
    update_by     BIGINT                NULL COMMENT '修改人',
    update_time   datetime              NULL COMMENT '修改时间',
    del_flag      CHAR(1)               NULL COMMENT '逻辑删除；1：存在；0：删除',
    public_key    VARCHAR(400)          NULL COMMENT '公钥',
    private_key   VARCHAR(2000)         NULL COMMENT '私钥',
    type          TINYINT               NOT NULL COMMENT '类型',
    `description` VARCHAR(500)          NULL COMMENT '描述',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='密钥';

CREATE TABLE sys_user
(
    id                      BIGINT               NOT NULL,
    create_by               BIGINT               NULL COMMENT '创建人',
    create_time             datetime             NULL COMMENT '创建时间',
    update_by               BIGINT               NULL COMMENT '修改人',
    update_time             datetime             NULL COMMENT '修改时间',
    del_flag                CHAR(1)              NULL COMMENT '逻辑删除；1：存在；0：不存在',
    username                VARCHAR(20)          NOT NULL COMMENT '用户名',
    password                VARCHAR(100)         NOT NULL COMMENT '密码',
    nickname                VARCHAR(20)          NULL COMMENT '昵称',
    email                   VARCHAR(50)          NOT NULL COMMENT '邮箱',
    phone                   VARCHAR(11)          NULL COMMENT '电话',
    enabled                 TINYINT(1) DEFAULT 1 NULL COMMENT '用户是否被启用；1：启用；0：禁用',
    account_non_locked      TINYINT(1) DEFAULT 1 NULL COMMENT '账户是否被锁定；1：正常；0：锁定',
    credentials_non_expired TINYINT(1) DEFAULT 1 NULL COMMENT '凭证是否过期；1：正常；0：过期',
    account_non_expired     TINYINT(1) DEFAULT 1 NULL COMMENT '账户是否过期；1：正常；0：过期',
    dept_id                 BIGINT               NULL COMMENT '部门id',
    avatar                  BIGINT               NULL COMMENT '用户头像',
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
) COMMENT ='用户表';

CREATE TABLE sys_user_role
(
    role_id BIGINT NOT NULL COMMENT '角色主键',
    user_id BIGINT NOT NULL COMMENT '用户主键'
) COMMENT ='用户角色表';

ALTER TABLE act_procdef_info
    ADD CONSTRAINT ACT_UNIQ_INFO_PROCDEF UNIQUE (PROC_DEF_ID_);

ALTER TABLE act_re_procdef
    ADD CONSTRAINT ACT_UNIQ_PROCDEF UNIQUE (KEY_, VERSION_, DERIVED_VERSION_, TENANT_ID_);

ALTER TABLE sys_user_role
    ADD CONSTRAINT uk_role_user UNIQUE (role_id, user_id);

ALTER TABLE sys_role_menu
    ADD CONSTRAINT uk_sys_role_menu UNIQUE (role_id, menu_id);

ALTER TABLE sys_role_permission
    ADD CONSTRAINT uk_sys_role_permission UNIQUE (role_id, permission_id);

CREATE INDEX ACT_IDC_EXEC_ROOT ON act_ru_execution (ROOT_PROC_INST_ID_);

CREATE INDEX ACT_IDX_ACT_HI_TSK_LOG_TASK ON act_hi_tsk_log (TASK_ID_);

CREATE INDEX ACT_IDX_DEADLETTER_JOB_CORRELATION_ID ON act_ru_deadletter_job (CORRELATION_ID_);

CREATE INDEX ACT_IDX_DJOB_SCOPE ON act_ru_deadletter_job (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_DJOB_SCOPE_DEF ON act_ru_deadletter_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_DJOB_SUB_SCOPE ON act_ru_deadletter_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_EJOB_SCOPE ON act_ru_external_job (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_EJOB_SCOPE_DEF ON act_ru_external_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_EJOB_SUB_SCOPE ON act_ru_external_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_ENT_LNK_REF_SCOPE ON act_ru_entitylink (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_ENT_LNK_ROOT_SCOPE ON act_ru_entitylink (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_ENT_LNK_SCOPE ON act_ru_entitylink (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_ENT_LNK_SCOPE_DEF ON act_ru_entitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_EVENT_SUBSCR_CONFIG_ ON act_ru_event_subscr (CONFIGURATION_);

CREATE INDEX ACT_IDX_EVENT_SUBSCR_PROC_ID ON act_ru_event_subscr (PROC_INST_ID_);

CREATE INDEX ACT_IDX_EVENT_SUBSCR_SCOPEREF_ ON act_ru_event_subscr (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_EXEC_BUSKEY ON act_ru_execution (BUSINESS_KEY_);

CREATE INDEX ACT_IDX_EXEC_REF_ID_ ON act_ru_execution (REFERENCE_ID_);

CREATE INDEX ACT_IDX_EXTERNAL_JOB_CORRELATION_ID ON act_ru_external_job (CORRELATION_ID_);

CREATE INDEX ACT_IDX_HI_ENT_LNK_REF_SCOPE ON act_hi_entitylink (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_HI_ENT_LNK_ROOT_SCOPE ON act_hi_entitylink (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_HI_ENT_LNK_SCOPE ON act_hi_entitylink (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_HI_ENT_LNK_SCOPE_DEF ON act_hi_entitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

CREATE INDEX ACT_IDX_HI_IDENT_LNK_SCOPE ON act_hi_identitylink (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_IDENT_LNK_SCOPE_DEF ON act_hi_identitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_IDENT_LNK_SUB_SCOPE ON act_hi_identitylink (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_IDENT_LNK_USER ON act_hi_identitylink (USER_ID_);

CREATE INDEX ACT_IDX_HI_PROCVAR_NAME_TYPE ON act_hi_varinst (NAME_, VAR_TYPE_);

CREATE INDEX ACT_IDX_HI_TASK_SCOPE ON act_hi_taskinst (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_TASK_SCOPE_DEF ON act_hi_taskinst (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_TASK_SUB_SCOPE ON act_hi_taskinst (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_VAR_SCOPE_ID_TYPE ON act_hi_varinst (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_HI_VAR_SUB_ID_TYPE ON act_hi_varinst (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_IDENT_LNK_GROUP ON act_ru_identitylink (GROUP_ID_);

CREATE INDEX ACT_IDX_IDENT_LNK_SCOPE ON act_ru_identitylink (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_IDENT_LNK_SCOPE_DEF ON act_ru_identitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_IDENT_LNK_SUB_SCOPE ON act_ru_identitylink (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_IDENT_LNK_USER ON act_ru_identitylink (USER_ID_);

CREATE INDEX ACT_IDX_JOB_CORRELATION_ID ON act_ru_job (CORRELATION_ID_);

CREATE INDEX ACT_IDX_JOB_SCOPE ON act_ru_job (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_JOB_SCOPE_DEF ON act_ru_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_JOB_SUB_SCOPE ON act_ru_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_RU_ACTI_END ON act_ru_actinst (END_TIME_);

CREATE INDEX ACT_IDX_RU_ACTI_EXEC ON act_ru_actinst (EXECUTION_ID_);

CREATE INDEX ACT_IDX_RU_ACTI_EXEC_ACT ON act_ru_actinst (EXECUTION_ID_, ACT_ID_);

CREATE INDEX ACT_IDX_RU_ACTI_PROC ON act_ru_actinst (PROC_INST_ID_);

CREATE INDEX ACT_IDX_RU_ACTI_PROC_ACT ON act_ru_actinst (PROC_INST_ID_, ACT_ID_);

CREATE INDEX ACT_IDX_RU_ACTI_START ON act_ru_actinst (START_TIME_);

CREATE INDEX ACT_IDX_RU_ACTI_TASK ON act_ru_actinst (TASK_ID_);

CREATE INDEX ACT_IDX_RU_VAR_SCOPE_ID_TYPE ON act_ru_variable (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_RU_VAR_SUB_ID_TYPE ON act_ru_variable (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_SJOB_SCOPE ON act_ru_suspended_job (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_SJOB_SCOPE_DEF ON act_ru_suspended_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_SJOB_SUB_SCOPE ON act_ru_suspended_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_SUSPENDED_JOB_CORRELATION_ID ON act_ru_suspended_job (CORRELATION_ID_);

CREATE INDEX ACT_IDX_TASK_CREATE ON act_ru_task (CREATE_TIME_);

CREATE INDEX ACT_IDX_TASK_SCOPE ON act_ru_task (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_TASK_SCOPE_DEF ON act_ru_task (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_TASK_SUB_SCOPE ON act_ru_task (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_TIMER_JOB_CORRELATION_ID ON act_ru_timer_job (CORRELATION_ID_);

CREATE INDEX ACT_IDX_TIMER_JOB_DUEDATE ON act_ru_timer_job (DUEDATE_);

CREATE INDEX ACT_IDX_TJOB_SCOPE ON act_ru_timer_job (SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_TJOB_SCOPE_DEF ON act_ru_timer_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_TJOB_SUB_SCOPE ON act_ru_timer_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

CREATE INDEX ACT_IDX_VARIABLE_TASK_ID ON act_ru_variable (TASK_ID_);

CREATE INDEX idx_code ON sys_role (code);

CREATE INDEX idx_sys_dept_code ON sys_dept (code);

CREATE INDEX idx_sys_dict_data_dict_type ON sys_dict_data (dict_type);

CREATE INDEX idx_sys_dict_type ON sys_dict_type (dict_type);

CREATE INDEX idx_sys_file_hash ON sys_file (file_hash);

CREATE INDEX idx_sys_file_key ON sys_file (file_key);

CREATE INDEX idx_sys_menu_parent_id ON sys_menu (parent_id);

CREATE INDEX idx_sys_menu_type ON sys_menu (type);

CREATE INDEX idx_sys_permission_code ON sys_permission (code);

CREATE INDEX idx_sys_secret_key_type ON sys_secret_key (type);

CREATE INDEX idx_sys_user_email ON sys_user (email);

CREATE INDEX idx_sys_user_username ON sys_user (username);

ALTER TABLE act_ru_identitylink
    ADD CONSTRAINT ACT_FK_ATHRZ_PROCEDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_ATHRZ_PROCEDEF ON act_ru_identitylink (PROC_DEF_ID_);

ALTER TABLE act_ge_bytearray
    ADD CONSTRAINT ACT_FK_BYTEARR_DEPL FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES act_re_deployment (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_BYTEARR_DEPL ON act_ge_bytearray (DEPLOYMENT_ID_);

ALTER TABLE act_ru_deadletter_job
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES FOREIGN KEY (CUSTOM_VALUES_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID ON act_ru_deadletter_job (CUSTOM_VALUES_ID_);

ALTER TABLE act_ru_deadletter_job
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID ON act_ru_deadletter_job (EXCEPTION_STACK_ID_);

ALTER TABLE act_ru_deadletter_job
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_DEADLETTER_JOB_EXECUTION ON act_ru_deadletter_job (EXECUTION_ID_);

ALTER TABLE act_ru_deadletter_job
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE ON act_ru_deadletter_job (PROCESS_INSTANCE_ID_);

ALTER TABLE act_ru_deadletter_job
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_DEADLETTER_JOB_PROC_DEF ON act_ru_deadletter_job (PROC_DEF_ID_);

ALTER TABLE act_ru_event_subscr
    ADD CONSTRAINT ACT_FK_EVENT_EXEC FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_EVENT_SUBSCR_EXEC_ID ON act_ru_event_subscr (EXECUTION_ID_);

ALTER TABLE act_ru_execution
    ADD CONSTRAINT ACT_FK_EXE_PARENT FOREIGN KEY (PARENT_ID_) REFERENCES act_ru_execution (ID_) ON DELETE CASCADE;

CREATE INDEX ACT_FK_EXE_PARENT ON act_ru_execution (PARENT_ID_);

ALTER TABLE act_ru_execution
    ADD CONSTRAINT ACT_FK_EXE_PROCDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_EXE_PROCDEF ON act_ru_execution (PROC_DEF_ID_);

ALTER TABLE act_ru_execution
    ADD CONSTRAINT ACT_FK_EXE_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES act_ru_execution (ID_) ON DELETE CASCADE;

CREATE INDEX ACT_FK_EXE_PROCINST ON act_ru_execution (PROC_INST_ID_);

ALTER TABLE act_ru_execution
    ADD CONSTRAINT ACT_FK_EXE_SUPER FOREIGN KEY (SUPER_EXEC_) REFERENCES act_ru_execution (ID_) ON DELETE CASCADE;

CREATE INDEX ACT_FK_EXE_SUPER ON act_ru_execution (SUPER_EXEC_);

ALTER TABLE act_ru_external_job
    ADD CONSTRAINT ACT_FK_EXTERNAL_JOB_CUSTOM_VALUES FOREIGN KEY (CUSTOM_VALUES_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_EXTERNAL_JOB_CUSTOM_VALUES_ID ON act_ru_external_job (CUSTOM_VALUES_ID_);

ALTER TABLE act_ru_external_job
    ADD CONSTRAINT ACT_FK_EXTERNAL_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_EXTERNAL_JOB_EXCEPTION_STACK_ID ON act_ru_external_job (EXCEPTION_STACK_ID_);

ALTER TABLE act_ru_identitylink
    ADD CONSTRAINT ACT_FK_IDL_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_IDL_PROCINST ON act_ru_identitylink (PROC_INST_ID_);

ALTER TABLE act_procdef_info
    ADD CONSTRAINT ACT_FK_INFO_JSON_BA FOREIGN KEY (INFO_JSON_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_INFO_JSON_BA ON act_procdef_info (INFO_JSON_ID_);

ALTER TABLE act_procdef_info
    ADD CONSTRAINT ACT_FK_INFO_PROCDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

ALTER TABLE act_ru_job
    ADD CONSTRAINT ACT_FK_JOB_CUSTOM_VALUES FOREIGN KEY (CUSTOM_VALUES_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_JOB_CUSTOM_VALUES_ID ON act_ru_job (CUSTOM_VALUES_ID_);

ALTER TABLE act_ru_job
    ADD CONSTRAINT ACT_FK_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_JOB_EXCEPTION_STACK_ID ON act_ru_job (EXCEPTION_STACK_ID_);

ALTER TABLE act_ru_job
    ADD CONSTRAINT ACT_FK_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_JOB_EXECUTION ON act_ru_job (EXECUTION_ID_);

ALTER TABLE act_ru_job
    ADD CONSTRAINT ACT_FK_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_JOB_PROCESS_INSTANCE ON act_ru_job (PROCESS_INSTANCE_ID_);

ALTER TABLE act_ru_job
    ADD CONSTRAINT ACT_FK_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_JOB_PROC_DEF ON act_ru_job (PROC_DEF_ID_);

ALTER TABLE act_re_model
    ADD CONSTRAINT ACT_FK_MODEL_DEPLOYMENT FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES act_re_deployment (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_MODEL_DEPLOYMENT ON act_re_model (DEPLOYMENT_ID_);

ALTER TABLE act_re_model
    ADD CONSTRAINT ACT_FK_MODEL_SOURCE FOREIGN KEY (EDITOR_SOURCE_VALUE_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_MODEL_SOURCE ON act_re_model (EDITOR_SOURCE_VALUE_ID_);

ALTER TABLE act_re_model
    ADD CONSTRAINT ACT_FK_MODEL_SOURCE_EXTRA FOREIGN KEY (EDITOR_SOURCE_EXTRA_VALUE_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_MODEL_SOURCE_EXTRA ON act_re_model (EDITOR_SOURCE_EXTRA_VALUE_ID_);

ALTER TABLE act_ru_suspended_job
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES FOREIGN KEY (CUSTOM_VALUES_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID ON act_ru_suspended_job (CUSTOM_VALUES_ID_);

ALTER TABLE act_ru_suspended_job
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID ON act_ru_suspended_job (EXCEPTION_STACK_ID_);

ALTER TABLE act_ru_suspended_job
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_SUSPENDED_JOB_EXECUTION ON act_ru_suspended_job (EXECUTION_ID_);

ALTER TABLE act_ru_suspended_job
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE ON act_ru_suspended_job (PROCESS_INSTANCE_ID_);

ALTER TABLE act_ru_suspended_job
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_SUSPENDED_JOB_PROC_DEF ON act_ru_suspended_job (PROC_DEF_ID_);

ALTER TABLE act_ru_task
    ADD CONSTRAINT ACT_FK_TASK_EXE FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TASK_EXE ON act_ru_task (EXECUTION_ID_);

ALTER TABLE act_ru_task
    ADD CONSTRAINT ACT_FK_TASK_PROCDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TASK_PROCDEF ON act_ru_task (PROC_DEF_ID_);

ALTER TABLE act_ru_task
    ADD CONSTRAINT ACT_FK_TASK_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TASK_PROCINST ON act_ru_task (PROC_INST_ID_);

ALTER TABLE act_ru_timer_job
    ADD CONSTRAINT ACT_FK_TIMER_JOB_CUSTOM_VALUES FOREIGN KEY (CUSTOM_VALUES_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID ON act_ru_timer_job (CUSTOM_VALUES_ID_);

ALTER TABLE act_ru_timer_job
    ADD CONSTRAINT ACT_FK_TIMER_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID ON act_ru_timer_job (EXCEPTION_STACK_ID_);

ALTER TABLE act_ru_timer_job
    ADD CONSTRAINT ACT_FK_TIMER_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TIMER_JOB_EXECUTION ON act_ru_timer_job (EXECUTION_ID_);

ALTER TABLE act_ru_timer_job
    ADD CONSTRAINT ACT_FK_TIMER_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TIMER_JOB_PROCESS_INSTANCE ON act_ru_timer_job (PROCESS_INSTANCE_ID_);

ALTER TABLE act_ru_timer_job
    ADD CONSTRAINT ACT_FK_TIMER_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES act_re_procdef (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TIMER_JOB_PROC_DEF ON act_ru_timer_job (PROC_DEF_ID_);

ALTER TABLE act_ru_identitylink
    ADD CONSTRAINT ACT_FK_TSKASS_TASK FOREIGN KEY (TASK_ID_) REFERENCES act_ru_task (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_TSKASS_TASK ON act_ru_identitylink (TASK_ID_);

ALTER TABLE act_ru_variable
    ADD CONSTRAINT ACT_FK_VAR_BYTEARRAY FOREIGN KEY (BYTEARRAY_ID_) REFERENCES act_ge_bytearray (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_VAR_BYTEARRAY ON act_ru_variable (BYTEARRAY_ID_);

ALTER TABLE act_ru_variable
    ADD CONSTRAINT ACT_FK_VAR_EXE FOREIGN KEY (EXECUTION_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_VAR_EXE ON act_ru_variable (EXECUTION_ID_);

ALTER TABLE act_ru_variable
    ADD CONSTRAINT ACT_FK_VAR_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES act_ru_execution (ID_) ON DELETE NO ACTION;

CREATE INDEX ACT_FK_VAR_PROCINST ON act_ru_variable (PROC_INST_ID_);

ALTER TABLE flw_ru_batch_part
    ADD CONSTRAINT FLW_FK_BATCH_PART_PARENT FOREIGN KEY (BATCH_ID_) REFERENCES flw_ru_batch (ID_) ON DELETE NO ACTION;

CREATE INDEX FLW_IDX_BATCH_PART ON flw_ru_batch_part (BATCH_ID_);