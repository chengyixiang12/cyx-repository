package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 *
 * @TableName wf_record
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value ="wf_record")
@Alias(value = "WfRecord")
public class WfRecord extends BaseEntity {

    /**
     * 流程实例id
     */
    @TableField(value = "instance_id")
    private Long instanceId;

    /**
     * 节点名称
     */
    @TableField(value = "node_name")
    private String nodeName;

    /**
     * 操作人id；系统动作可为空
     */
    @TableField(value = "operator_id")
    private Long operatorId;

    /**
     * 动作；START：发起；APPROVE：通过；REJECT：驳回；TRANSFER：转办；FINISH：完成；TERMINATE：终止
     */
    @TableField(value = "action")
    private String action;

    /**
     * 备注
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 操作时间
     */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;
}
