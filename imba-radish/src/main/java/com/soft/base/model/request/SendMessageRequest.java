package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/15 19:04
 **/

@Data
@Schema(description = "发送消息请求参数")
@Alias(value = "SendMessageRequest")
public class SendMessageRequest {

    @Schema(description = "邮箱")
    private String email;
}
