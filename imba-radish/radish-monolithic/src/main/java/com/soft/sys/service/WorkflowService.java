package com.soft.sys.service;

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

import java.util.List;

/**
* @author cyx
* @description 工作流门面服务：业务层与页面只依赖本接口，不感知具体引擎
* @createDate 2026-11-19 11:30:00
*/
public interface WorkflowService {

    /**
     * 发起流程
     *
     * @param request 发起参数
     * @return 流程实例id
     */
    Long start(StartWorkflowDTO request);

    /**
     * 通过任务
     */
    void approve(HandleWorkflowDTO request);

    /**
     * 驳回任务（终止流程）
     */
    void reject(HandleWorkflowDTO request);

    /**
     * 转办任务
     */
    void transfer(TransferWorkflowDTO request);

    /**
     * 获取当前登录用户的待办任务
     */
    PageVO<WfTasksVO> getTodos(GetWorkflowTasksDTO request);

    /**
     * 获取当前登录用户的已办任务
     */
    PageVO<WfTasksVO> getDones(GetWorkflowTasksDTO request);

    /**
     * 获取当前登录用户发起的流程
     */
    PageVO<WfInstancesVO> getMyInstances(GetMyInstancesDTO request);

    /**
     * 流程跟踪（实例 + 任务 + 审批留痕）
     */
    WfTraceVO getTrace(Long instanceId);

    /**
     * 获取审批人下拉选项（启用用户）
     */
    List<WfUserOptionVO> getApproverOptions();
}
