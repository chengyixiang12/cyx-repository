package com.soft.base.controller;

import com.soft.base.model.request.LogsRequest;
import com.soft.base.model.vo.GetLogVo;
import com.soft.base.model.vo.LogsVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 11:25
 **/
@RestController
@RequestMapping(value = "/log")
@Slf4j
@Tag(name = "日志")
@Validated
public class SysLogController {

    private final SysLogService sysLogService;

    @Autowired
    public SysLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @PostMapping(value = "/getLogs")
    @Operation(summary = "获取日志（复）")
    public R<PageVo<LogsVo>> getLogs(@RequestBody LogsRequest request) {
        try {
            PageVo<LogsVo> pageVo = sysLogService.getLogs(request);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getLog")
    @Operation(summary = "获取日志")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<GetLogVo> getLog(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        try {
            GetLogVo getLogVo = sysLogService.getLog(id);
            return R.ok(getLogVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PreAuthorize(value = "@cps.hasPermission('sys_log_delete')")
    @DeleteMapping
    @Operation(summary = "删除日志")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteLog(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysLogService.deleteLog(id);
        return R.ok("删除成功", null);
    }
}
