package com.soft.sys.controller;

import com.soft.common.annotation.SysLock;
import com.soft.common.annotation.SysLog;
import com.soft.common.enums.LogModuleEnum;
import com.soft.common.resultapi.R;
import com.soft.sys.model.request.GetMyInstancesDTO;
import com.soft.sys.model.request.GetWorkflowTasksDTO;
import com.soft.sys.model.request.HandleWorkflowDTO;
import com.soft.sys.model.request.StartWorkflowDTO;
import com.soft.sys.model.request.TransferWorkflowDTO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.WfInstancesVO;
import com.soft.sys.model.vo.WfTasksVO;
import com.soft.sys.model.vo.WfTraceVO;
import com.soft.sys.model.vo.WfUserOptionVO;
import com.soft.sys.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工作流门面接口：待办中心 / 我发起的 / 已办 / 流程跟踪。
 * 业务层与前端只依赖本接口，不感知底层引擎。
 *
 * @Author: cyx
 * @Description: 工作流
 * @DateTime: 2026/11/19 11:30
 **/
@RestController
@RequestMapping(value = "/workflow")
@Slf4j
@Tag(name = "工作流")
@Validated
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @SysLock(name = "workflow")
    @SysLog(value = "发起流程", module = LogModuleEnum.WORKFLOW)
    @PostMapping(value = "/start")
    @Operation(summary = "发起流程")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_start')")
    public R<Object> start(@RequestBody @Valid StartWorkflowDTO request) {
        Long instanceId = workflowService.start(request);
        return R.ok("发起成功", instanceId);
    }

    @SysLock(name = "workflow")
    @SysLog(value = "通过任务", module = LogModuleEnum.WORKFLOW)
    @PostMapping(value = "/approve")
    @Operation(summary = "通过任务")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_approve')")
    public R<Object> approve(@RequestBody @Valid HandleWorkflowDTO request) {
        workflowService.approve(request);
        return R.ok("已通过");
    }

    @SysLock(name = "workflow")
    @SysLog(value = "驳回任务", module = LogModuleEnum.WORKFLOW)
    @PostMapping(value = "/reject")
    @Operation(summary = "驳回任务")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_approve')")
    public R<Object> reject(@RequestBody @Valid HandleWorkflowDTO request) {
        workflowService.reject(request);
        return R.ok("已驳回");
    }

    @SysLock(name = "workflow")
    @SysLog(value = "转办任务", module = LogModuleEnum.WORKFLOW)
    @PostMapping(value = "/transfer")
    @Operation(summary = "转办任务")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_transfer')")
    public R<Object> transfer(@RequestBody @Valid TransferWorkflowDTO request) {
        workflowService.transfer(request);
        return R.ok("已转办");
    }

    @PostMapping(value = "/getTodos")
    @Operation(summary = "获取待办任务列表")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_page')")
    public R<PageVO<WfTasksVO>> getTodos(@RequestBody GetWorkflowTasksDTO request) {
        PageVO<WfTasksVO> pageVo = workflowService.getTodos(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/getDones")
    @Operation(summary = "获取已办任务列表")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_page')")
    public R<PageVO<WfTasksVO>> getDones(@RequestBody GetWorkflowTasksDTO request) {
        PageVO<WfTasksVO> pageVo = workflowService.getDones(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/getMyInstances")
    @Operation(summary = "获取我发起的流程列表")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_page')")
    public R<PageVO<WfInstancesVO>> getMyInstances(@RequestBody GetMyInstancesDTO request) {
        PageVO<WfInstancesVO> pageVo = workflowService.getMyInstances(request);
        return R.ok(pageVo);
    }

    @GetMapping(value = "/getTrace")
    @Operation(summary = "流程跟踪")
    @Parameter(name = "id", description = "流程实例id", required = true, in = ParameterIn.QUERY)
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_page')")
    public R<WfTraceVO> getTrace(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        WfTraceVO traceVo = workflowService.getTrace(id);
        return R.ok(traceVo);
    }

    @GetMapping(value = "/getApproverOptions")
    @Operation(summary = "获取审批人下拉选项")
    @PreAuthorize(value = "@cps.hasPermission('sys_workflow_start')")
    public R<List<WfUserOptionVO>> getApproverOptions() {
        List<WfUserOptionVO> options = workflowService.getApproverOptions();
        return R.ok(options);
    }
}
