package com.soft.base.controller;

import com.soft.base.model.request.ListActuatorPageRequest;
import com.soft.base.model.vo.ListActuatorVO;
import com.soft.base.model.vo.PageVO;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysActuatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cyx
 * @description:
 * @date 2026-05-01
 */
@RestController
@RequestMapping(value = "/actuator")
@Slf4j
@Validated
@Tag(name = "监控")
@RequiredArgsConstructor
public class SysActuatorController {

    private final SysActuatorService sysActuatorService;

    @GetMapping(value = "/listActuator")
    @Operation(summary = "获取监控记录")
    public R<List<ListActuatorVO>> listActuator(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(sysActuatorService.listActuator(startTime, endTime));
    }

    @PostMapping(value = "/listActuatorPage")
    @Operation(summary = "获取监控记录")
    public R<PageVO<ListActuatorVO>> listActuatorPage(@RequestBody ListActuatorPageRequest request) {
        return R.ok(sysActuatorService.listActuatorPage(request));
    }
}
