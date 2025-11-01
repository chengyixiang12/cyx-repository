create table sys_log
(
    id              bigint       not null comment '主键'
        primary key,
    create_by       varchar(20)  null comment '创建人',
    create_time     datetime     null comment '创建时间',
    del_flag        char         null comment '逻辑删除',
    log_level       varchar(10)  null comment '日志级别',
    ip_address      varchar(45)  null comment '请求ip',
    request_url     varchar(255) null comment '访问路径',
    request_method  varchar(10)  null comment '请求方法',
    request_params  text         null comment '请求参数',
    response_result text         null comment '响应结果',
    operation_desc  text         null comment '操作描述',
    source          varchar(50)  null comment '日志来源',
    execution_time  bigint       null comment '耗时',
    module_name     varchar(50)  null comment '模块名称',
    status_code     int          null comment '状态码',
    exception_info  text         null comment '异常信息',
    os_browser_info varchar(500) null comment '操作系统/浏览器信息',
    type            varchar(2)   null comment '日志类型',
    update_by       bigint       null comment '修改人',
    update_time     datetime     null comment '修改时间'
)
    comment '日志表';