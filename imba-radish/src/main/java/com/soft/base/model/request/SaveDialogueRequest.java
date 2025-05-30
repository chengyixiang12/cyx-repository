package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/30 16:25
 **/
@Schema(description = "新增对话请求参数")
@Data
@Alias(value = "SaveDialogueRequest")
public class SaveDialogueRequest {

    private String title;
}
