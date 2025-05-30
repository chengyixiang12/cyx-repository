package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/30 14:53
 **/
@EqualsAndHashCode(callSuper = true)
@Schema(description = "获取历史对话（复）请求参数")
@Data
@Alias(value = "GetDialoguesRequest")
public class GetDialoguesRequest extends PageRequest {

    @Schema(description = "关键字")
    private String keyword;
}
