package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: 审批人下拉选项
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "审批人下拉选项")
@Alias(value = "WfUserOptionVO")
public class WfUserOptionVO {

    @Schema(description = "用户id")
    private String id;

    @Schema(description = "昵称")
    private String nickname;
}
