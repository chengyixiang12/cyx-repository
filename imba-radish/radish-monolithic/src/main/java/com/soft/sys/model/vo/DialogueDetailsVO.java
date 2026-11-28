package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 对话明细响应
 * @DateTime: 2026/11/05
 **/
@Schema(description = "对话明细")
@Data
@Alias(value = "DialogueDetailsVO")
public class DialogueDetailsVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "标识；1：用户；0：AI")
    private Integer tag;

    @Schema(description = "对话内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
