package com.soft.base.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description:
 * @date 2026-05-01
 */
@Data
@Schema(description = "监控记录")
@Alias(value = "ListActuatorVO")
public class ListActuatorVO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Double cpuUsage;

    private Integer cpuCount;

    private Long memoryUsed;

    private Long memoryMax;

    private Long uptime;

    private Long diskFree;

    private Long diskTotal;

    private String healthDb;

    private String healthDiskSpace;

    private String healthMail;

    private String healthPing;

    private String healthRabbit;

    private String healthRedis;

    private String healthSsl;

    private String health;
}
