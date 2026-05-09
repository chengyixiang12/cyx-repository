package com.soft.base.controller;

import cn.hutool.core.util.StrUtil;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysScheduleJob;
import com.soft.base.model.request.CreateJobRequest;
import com.soft.base.model.request.EditJobRequest;
import com.soft.base.model.request.GetQuartzTasksRequest;
import com.soft.base.model.vo.GetJobVo;
import com.soft.base.model.vo.GetQuartzTasksVo;
import com.soft.base.model.vo.PageVO;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysScheduleJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/7/14 13:44
 **/
@RestController
@Slf4j
@RequestMapping(value = "/scheduleJob")
@Tag(name = "定时任务")
@Validated
public class SysScheduleJobController {

    private final SysScheduleJobService sysScheduleJobService;

    private final Scheduler scheduler;

    @Autowired
    public SysScheduleJobController(SysScheduleJobService sysScheduleJobService, Scheduler scheduler) {
        this.sysScheduleJobService = sysScheduleJobService;
        this.scheduler = scheduler;
    }

    @PostMapping(value = "/createJob")
    @Operation(summary = "创建定时任务")
    public R<Object> createJob(@RequestBody @Valid CreateJobRequest request) {
        if (sysScheduleJobService.existJobType(request.getJobType(), request.getJobGroup())) {
            return R.fail(StrUtil.format("“{}”任务类型已存在于“{}”组中", request.getJobType(), request.getJobGroup()));
        }
        sysScheduleJobService.createJob(request);
        return R.ok("创建成功", null);
    }

    @PostMapping(value = "/getQuartzTasks")
    @Operation(summary = "获取定时任务（复）")
    public R<PageVO<GetQuartzTasksVo>> getQuartzTasks(@RequestBody @Valid GetQuartzTasksRequest request) {
        PageVO<GetQuartzTasksVo> pageVo = sysScheduleJobService.getQuartzTasks(request);
        return R.ok(pageVo);
    }

    @GetMapping(value = "/startJob")
    @Operation(summary = "启用任务")
    public R<Object> startJob(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysScheduleJobService.startJob(id);
        return R.ok("任务已启动", null);
    }

    @GetMapping(value = "/stopJob")
    @Operation(summary = "停止任务")
    public R<Object> stopJob(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysScheduleJobService.stopJob(id);
        return R.ok("任务已停止", null);
    }

    @GetMapping(value = "/parseCron")
    @Operation(summary = "解析cron表达式")
    @Parameter(name = "cronExpress", description = "cron表达式", required = true, in = ParameterIn.QUERY)
    public R<List<String>> parseCron(@RequestParam(value = "cronExpress", required = false) @NotBlank String cronExpress) {
        List<String> resultList = new ArrayList<>();

        CronExpression expression = CronExpression.parse(cronExpress);
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> nextTimes = new ArrayList<>();

        LocalDateTime next = expression.next(now);
        for (int i = 0; i < 5 && next != null; i++) {
            nextTimes.add(next);
            next = expression.next(next); // 获取下一个时间
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (LocalDateTime time : nextTimes) {
            resultList.add(time.format(formatter));
        }
        return R.ok(resultList);
    }

    @GetMapping(value = "/getJob")
    @Operation(summary = "获取job详情")
    public R<GetJobVo> getJob(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        GetJobVo getJobVo = sysScheduleJobService.getJob(id);
        return R.ok(getJobVo);
    }

    @PutMapping(value = "/editJob")
    @Operation(summary = "修改job")
    public R<Object> editJob(@RequestBody @Valid EditJobRequest request) {
        sysScheduleJobService.editJob(request);
        return R.ok("修改成功", null);
    }

    @DeleteMapping(value = "/deleteJob")
    public R<Object> deleteJob(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysScheduleJobService.deleteJob(id);
        return R.ok("删除成功");
    }

    @GetMapping(value = "/execImmediately")
    @Operation(summary = "立即执行")
    public R<Object> execImmediately(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        SysScheduleJob sysScheduleJob = sysScheduleJobService.getById(id);
        if (BaseConstant.Status.STATUS_BAN.equals(sysScheduleJob.getStatus())) {
            return R.fail("任务还未启动");
        }

        String jobType = sysScheduleJob.getJobType();
        String jobGroup = sysScheduleJob.getJobGroup();

        try {
            JobKey jobKey = new JobKey(jobType, jobGroup);

            // 检查任务是否存在
            if (scheduler.checkExists(jobKey)) {
                // 立即触发任务执行
                scheduler.triggerJob(jobKey);
                return R.ok("任务执行成功", null);
            } else {
                log.warn("任务不存在: {}.{}", jobGroup, jobType);
                return R.fail("任务还未启动");
            }
        } catch (SchedulerException e) {
            log.error("立即执行任务失败: {}.{}", jobGroup, jobType, e);
            return R.fail("立即执行任务失败");
        }
    }
}
