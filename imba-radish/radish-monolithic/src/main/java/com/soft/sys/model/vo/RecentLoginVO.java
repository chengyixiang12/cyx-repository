package com.soft.sys.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 最近登录记录
 * @date 2026-11-05
 */
@Data
@Schema(description = "最近登录记录")
public class RecentLoginVO {

    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    @Schema(description = "登录IP")
    private String ipAddress;

    @Schema(description = "操作系统/浏览器信息")
    private String osBrowserInfo;
}
