package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.request.LogsRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysLogService;
import com.soft.base.vo.LogsVo;
import com.soft.base.vo.PageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 11:25
 **/
@RestController
@RequestMapping(value = "/log")
@Slf4j
@Tag(name = "日志")
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
}
