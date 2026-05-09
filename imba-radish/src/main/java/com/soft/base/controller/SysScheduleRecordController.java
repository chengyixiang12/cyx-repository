package com.soft.base.controller;

import com.soft.base.model.request.GetQuartzRecordListRequest;
import com.soft.base.model.vo.GetQuartzRecordListVo;
import com.soft.base.model.vo.PageVO;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysScheduleRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: cyx
 * @Description:
 * @DateTime: 2026/04/24
 **/
@RestController
@RequestMapping(value = "/scheduleRecord")
@Tag(name = "定时任务执行记录")
@Slf4j
@Validated
@RequiredArgsConstructor
public class SysScheduleRecordController {

    private final SysScheduleRecordService sysScheduleRecordService;

    @PostMapping(value = "/getQuartzRecordList")
    @Operation(summary = "获取定时任务执行记录列表")
    public R<PageVO<GetQuartzRecordListVo>> getQuartzRecordList(@RequestBody GetQuartzRecordListRequest request) {
        PageVO<GetQuartzRecordListVo> pageVo = sysScheduleRecordService.getQuartzRecordList(request);
        return R.ok(pageVo);
    }

    @GetMapping(value = "/getLogDetail")
    @Operation(summary = "获取定时任务详情")
    public R<String> getLogDetail(@RequestParam(value = "id") @NotNull(message = "id不能为空") Long id) {
        String logDetail = sysScheduleRecordService.getLogDetail(id);
        return R.ok(logDetail);
    }
}
