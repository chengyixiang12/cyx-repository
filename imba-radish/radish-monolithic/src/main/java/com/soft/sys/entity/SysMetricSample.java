package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 监控指标原始样本（30 秒一条，保留 3 天）
 * <p>
 * 时序数据不继承 BaseEntity：这里不需要 create_by / update_by / update_time，
 * 多一个字段就是每 30 秒多写一次无用数据。
 * @date 2026-11-05
 */
@Data
@TableName(value = "sys_metric_sample")
public class SysMetricSample {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 采样时间
     */
    private LocalDateTime collectTime;

    /**
     * 宿主机 cpu 使用率（%）
     */
    private Double cpuUsage;

    /**
     * 应用进程 cpu 使用率（%）
     */
    private Double processCpuUsage;

    /**
     * 堆已用字节
     */
    private Long heapUsed;

    /**
     * 堆上限字节
     */
    private Long heapMax;

    /**
     * 非堆已用字节
     */
    private Long nonheapUsed;

    /**
     * 活跃线程数
     */
    private Integer threadsLive;

    /**
     * 阻塞线程数
     */
    private Integer threadsBlocked;

    /**
     * 窗口内 gc 次数
     */
    private Integer gcCount;

    /**
     * 窗口内 gc 累计停顿（毫秒）
     */
    private Integer gcPauseMs;

    /**
     * 窗口内每秒请求数
     */
    private Double qps;

    /**
     * 窗口内 p95 延迟（毫秒）
     */
    private Integer p95Ms;

    /**
     * 窗口内 5xx 占比（%）
     */
    private Double errorRate;

    /**
     * 连接池活跃连接
     */
    private Integer poolActive;

    /**
     * 连接池等待连接
     */
    private Integer poolPending;

    /**
     * 连接池上限
     */
    private Integer poolMax;

    /**
     * 磁盘可用字节
     */
    private Long diskFree;
}
