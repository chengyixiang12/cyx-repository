package com.soft.base.enums;

import com.soft.base.quartz.job.NmapScanScheduler;
import lombok.Getter;
import org.quartz.Job;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/7/14 14:20
 **/
@Getter
public enum JobEnum {

    STORAGE_SCAN("storage_scan", NmapScanScheduler.class);

    private final String jobType;

    private final Class<? extends Job> jobClass;

    JobEnum(String jobType, Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
        this.jobType = jobType;
    }

    public static Class<? extends Job> getJobClass(String jobType) {
        for (JobEnum value : JobEnum.values()) {
            if (value.getJobType().equals(jobType)) {
                return value.jobClass;
            }
        }
        return null;
    }
}
