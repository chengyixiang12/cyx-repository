package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/5/30 14:53
 **/
@EqualsAndHashCode(callSuper = true)
@Schema(description = "获取历史对话列表请求参数")
@Data
@Alias(value = "GetDialoguesDTO")
public class GetDialoguesDTO extends PageDTO {

    @Schema(description = "关键字")
    private String keyword;
}
