package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统监控表
 * @TableName sys_actuator
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_actuator")
@Data
public class SysActuator extends BaseEntity {

    /**
     * cpu使用率
     */
    @TableField(value = "cpu_usage")
    private Double cpuUsage;

    /**
     * cpu核心数
     */
    @TableField(value = "cpu_count")
    private Integer cpuCount;

    /**
     * 内存使用量
     */
    @TableField(value = "memery_used")
    private Long memeryUsed;

    /**
     * 最大内存容量
     */
    @TableField(value = "memery_max")
    private Long memeryMax;

    /**
     * 系统运行时长
     */
    @TableField(value = "uptime")
    private Long uptime;

    /**
     * 磁盘空闲空间
     */
    @TableField(value = "disk_free")
    private Long diskFree;

    /**
     * 磁盘总量
     */
    @TableField(value = "disk_total")
    private Long diskTotal;

    /**
     * 数据库状态；up：正常；down：异常
     */
    @TableField(value = "health_db")
    private String healthDb;

    /**
     * 磁盘状态；up：正常；down：异常
     */
    @TableField(value = "health_disk_space")
    private String healthDiskSpace;

    /**
     * 邮箱状态；up：正常；down：异常
     */
    @TableField(value = "health_mail")
    private String healthMail;

    /**
     * 系统响应状态；up：正常；down：异常
     */
    @TableField(value = "health_ping")
    private String healthPing;

    /**
     * rabbitmq状态；up：正常；down：异常
     */
    @TableField(value = "health_rabbit")
    private String healthRabbit;

    /**
     * redis状态；up：正常；down：异常
     */
    @TableField(value = "health_redis")
    private String healthRedis;

    /**
     * ssl证书状态；up：正常；down：异常
     */
    @TableField(value = "health_ssl")
    private String healthSsl;

    /**
     * 健康状态；up：正常；down：异常
     */
    @TableField(value = "health")
    private String health;
}