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
    @TableField(value = "memory_used")
    private Long memoryUsed;

    /**
     * 最大内存容量
     */
    @TableField(value = "memory_max")
    private Long memoryMax;

    /**
     * 堆内存最大容量
     */
    @TableField(value = "memory_heap_max")
    private Long memoryHeapMax;

    /**
     * 堆内存使用量
     */
    @TableField(value = "memory_heap_used")
    private Long memoryHeapUsed;

    /**
     * 非堆内存最大容量
     */
    @TableField(value = "memory_noheap_max")
    private Long memoryNoheapMax;

    /**
     * 非堆内存使用量
     */
    @TableField(value = "memory_noheap_used")
    private Long memoryNoheapUsed;

    /**
     * G1的eden区内存使用量
     */
    @TableField(value = "memory_g1_eden_used")
    private Long memoryG1EdenUsed;

    /**
     * G1的survivor区内存使用量
     */
    @TableField(value = "memory_g1_survivor_used")
    private Long memoryG1SurvivorUsed;

    /**
     * G1的老年代内存使用量
     */
    @TableField(value = "memory_g1_old_used")
    private Long memoryG1OldUsed;

    /**
     * G1的老年代最大内存容量
     */
    @TableField(value = "memory_g1_old_max")
    private Long memoryG1OldMax;

    /**
     * 代码缓存最大内存容量
     */
    @TableField(value = "memory_code_cache_max")
    private Long memoryCodeCacheMax;

    /**
     * 代码缓存内存使用量
     */
    @TableField(value = "memory_code_cache_used")
    private Long memoryCodeCacheUsed;

    /**
     * 元空间内存使用量
     */
    @TableField(value = "memory_metaspace_used")
    private Long memoryMetaspaceUsed;

    /**
     * 压缩类空间内存使用容量
     */
    @TableField(value = "memory_compress_class_space_used")
    private Long memoryCompressClassSpaceUsed;

    /**
     * 压缩类空间最大内存容量
     */
    @TableField(value = "memory_compress_class_space_max")
    private Long memoryCompressClassSpaceMax;

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