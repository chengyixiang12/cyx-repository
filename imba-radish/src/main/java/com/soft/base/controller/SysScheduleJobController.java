package com.soft.base.controller;

import com.soft.base.model.request.CreateJobRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysScheduleJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/7/14 13:44
 **/
@RestController
@Slf4j
@RequestMapping(value = "/scheduleJob")
@Tag(name = "定时任务")
@Validated
public class SysScheduleJobController {

    private final SysScheduleJobService sysScheduleJobService;

    @Autowired
    public SysScheduleJobController(SysScheduleJobService sysScheduleJobService) {
        this.sysScheduleJobService = sysScheduleJobService;
    }

    @PostMapping(value = "/createJob")
    @Operation(summary = "创建定时任务")
    public R<Object> createJob(@RequestBody @Valid CreateJobRequest request) {
        sysScheduleJobService.createJob(request);
        return R.ok("创建成功", null);
    }
}
