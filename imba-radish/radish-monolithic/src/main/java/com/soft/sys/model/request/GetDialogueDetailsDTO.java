package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 获取对话明细请求参数
 * @DateTime: 2026/11/05
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取对话明细")
public class GetDialogueDetailsDTO extends PageDTO {

    @NotNull(message = "对话id不能为空")
    @Schema(description = "历史对话id")
    private Long dialogueId;
}
