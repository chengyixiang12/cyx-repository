package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 审批留痕行
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "审批留痕行")
@Alias(value = "WfRecordsVO")
public class WfRecordsVO {

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "操作人昵称")
    private String operatorNickname;

    @Schema(description = "动作；START：发起；APPROVE：通过；REJECT：驳回；TRANSFER：转办；FINISH：完成；TERMINATE：终止")
    private String action;

    @Schema(description = "备注")
    private String comment;

    @Schema(description = "操作时间")
    private LocalDateTime operateTime;
}
