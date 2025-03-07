package com.soft.base.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: 日志模块枚举
 * @DateTime: 2024/11/24 20:28
 **/
@Getter
public enum LogModuleEnum {

    DEFAULT(""),
    AUTHORIZATION("鉴权"),
    MESSAGE_QUEUE("消息队列"),
    DEPT("部门"),
    DICT_DATA("字典数据"),
    DICT_TYPE("字典类型"),
    FILE("文件"),
    LOG("日志"),
    MENU("菜单"),
    PERMISSION("权限"),
    ROLE("角色"),
    USER("用户"),
    SECRET_KEY("密钥"),
    ;

    private final String name;

    LogModuleEnum(String name) {
        this.name = name;
    }
}
