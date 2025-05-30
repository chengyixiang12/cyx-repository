package com.soft.base.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/30 14:44
 **/
@Schema(description = "获取历史对话（复）响应参数")
@Data
@Alias(value = "GetDialoguesVo")
public class GetDialoguesVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "标题")
    private String title;
}
