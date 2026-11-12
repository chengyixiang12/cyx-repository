package com.soft.sys.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 当前用户最近对话（首页）
 * @date 2026-11-12
 */
@Data
@Schema(description = "当前用户最近对话")
public class RecentDialogueVO {

    @Schema(description = "对话ID")
    private Long id;

    @Schema(description = "对话标题")
    private String title;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
