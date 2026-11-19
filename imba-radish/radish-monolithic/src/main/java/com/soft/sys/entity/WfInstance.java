package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 *
 * @TableName wf_instance
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value ="wf_instance")
@Alias(value = "WfInstance")
public class WfInstance extends BaseEntity {

    /**
     * 流程标识
     */
    @TableField(value = "process_key")
    private String processKey;

    /**
     * 流程名称
     */
    @TableField(value = "process_name")
    private String processName;

    /**
     * 业务单据标识，业务表通过它零改造接入
     */
    @TableField(value = "business_key")
    private String businessKey;

    /**
     * 流程标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 实例状态；1：运行中；2：已完成；3：已终止
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 当前节点名称
     */
    @TableField(value = "current_node")
    private String currentNode;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private java.time.LocalDateTime endTime;

    /**
     * 执行引擎标识；noop：内置线性引擎
     */
    @TableField(value = "engine")
    private String engine;

    /**
     * 引擎私有数据；noop引擎存储串行审批人id序列
     */
    @TableField(value = "engine_data")
    private String engineData;
}
