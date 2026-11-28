package com.soft.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.common.exception.GlobalException;
import com.soft.sys.entity.SysUser;
import com.soft.sys.entity.WfInstance;
import com.soft.sys.entity.WfRecord;
import com.soft.sys.entity.WfTask;
import com.soft.sys.enums.WorkflowInstanceStatusEnum;
import com.soft.sys.enums.WorkflowRecordActionEnum;
import com.soft.sys.enums.WorkflowTaskActionEnum;
import com.soft.sys.enums.WorkflowTaskStatusEnum;
import com.soft.sys.event.WorkflowEventPublisher;
import com.soft.sys.mapper.WfInstanceMapper;
import com.soft.sys.mapper.WfRecordMapper;
import com.soft.sys.mapper.WfTaskMapper;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.model.dto.WorkflowEventDTO;
import com.soft.sys.model.request.GetMyInstancesDTO;
import com.soft.sys.model.request.GetWorkflowTasksDTO;
import com.soft.sys.model.request.HandleWorkflowDTO;
import com.soft.sys.model.request.StartWorkflowDTO;
import com.soft.sys.model.request.TransferWorkflowDTO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.WfInstancesVO;
import com.soft.sys.model.vo.WfRecordsVO;
import com.soft.sys.model.vo.WfTasksVO;
import com.soft.sys.model.vo.WfTraceVO;
import com.soft.sys.model.vo.WfUserOptionVO;
import com.soft.sys.service.SysUsersService;
import com.soft.sys.service.WorkflowService;
import com.soft.sys.utils.SecurityUtil;
import com.soft.sys.workflow.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author cyx
* @description 工作流门面服务实现：编排实例/任务/留痕三张框架表，
* 流程推进委托给可插拔的 {@link WorkflowEngine}，并通过 Spring Event 桥接通知
* @createDate 2026-11-19 11:30:00
*/
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WfInstanceMapper wfInstanceMapper;

    private final WfTaskMapper wfTaskMapper;

    private final WfRecordMapper wfRecordMapper;

    private final WorkflowEngine workflowEngine;

    private final WorkflowEventPublisher workflowEventPublisher;

    private final SysUsersService sysUsersService;

    private final SecurityUtil securityUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long start(StartWorkflowDTO request) {
        UserDTO currentUser = securityUtil.getUserInfo();
        List<Long> assigneeIds = request.getAssigneeIds();
        validateAssignees(assigneeIds);

        WfInstance instance = new WfInstance();
        instance.setProcessKey(request.getProcessKey());
        instance.setProcessName(request.getProcessName());
        instance.setBusinessKey(request.getBusinessKey());
        instance.setTitle(StringUtils.defaultIfBlank(request.getTitle(), request.getProcessName()));
        instance.setStatus(WorkflowInstanceStatusEnum.RUNNING.getCode());
        instance.setEngine(workflowEngine.getName());
        // 引擎私有数据：no-op 引擎存串行审批人id序列
        instance.setEngineData(JSON.toJSONString(assigneeIds));
        wfInstanceMapper.insert(instance);

        List<WorkflowEngine.NextTask> firstTasks = workflowEngine.resolveFirstTasks(instance, assigneeIds);
        String currentNode = null;
        for (WorkflowEngine.NextTask nextTask : firstTasks) {
            WfTask task = insertTask(instance, nextTask);
            currentNode = task.getTaskName();
        }

        if (firstTasks.isEmpty()) {
            // 引擎判定无需审批，直接完成
            finishInstance(instance, WorkflowInstanceStatusEnum.FINISHED);
        } else {
            instance.setCurrentNode(currentNode);
            wfInstanceMapper.updateById(instance);
        }

        saveRecord(instance.getId(), "发起", currentUser.getId(), WorkflowRecordActionEnum.START, request.getComment());

        String subject = StringUtils.defaultIfBlank(instance.getTitle(), instance.getProcessName());
        for (WorkflowEngine.NextTask nextTask : firstTasks) {
            publishNotify(nextTask.getAssigneeId(), currentUser.getId(), "您有一条新的待办任务",
                    currentUser.getNickname() + " 发起的「" + subject + "」待您审批",
                    instance.getId(), null);
        }
        return instance.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(HandleWorkflowDTO request) {
        UserDTO currentUser = securityUtil.getUserInfo();
        WfTask task = getValidTodoTask(request.getTaskId(), currentUser.getId());
        WfInstance instance = getRunningInstance(task.getInstanceId());

        task.setStatus(WorkflowTaskStatusEnum.DONE.getCode());
        task.setAction(WorkflowTaskActionEnum.APPROVE.getCode());
        task.setComment(request.getComment());
        task.setHandleTime(LocalDateTime.now());
        wfTaskMapper.updateById(task);

        saveRecord(instance.getId(), task.getTaskName(), currentUser.getId(), WorkflowRecordActionEnum.APPROVE, request.getComment());

        List<WorkflowEngine.NextTask> nextTasks = workflowEngine.resolveNextTasks(instance, task, WorkflowTaskActionEnum.APPROVE);
        String subject = StringUtils.defaultIfBlank(instance.getTitle(), instance.getProcessName());
        if (nextTasks.isEmpty()) {
            finishInstance(instance, WorkflowInstanceStatusEnum.FINISHED);
            publishNotify(instance.getCreateBy(), currentUser.getId(), "您发起的流程已完成",
                    "「" + subject + "」已全部审批通过", instance.getId(), null);
            return;
        }

        StringBuilder currentNode = new StringBuilder();
        for (WorkflowEngine.NextTask nextTask : nextTasks) {
            WfTask newTask = insertTask(instance, nextTask);
            if (!currentNode.isEmpty()) {
                currentNode.append(",");
            }
            currentNode.append(newTask.getTaskName());
            publishNotify(nextTask.getAssigneeId(), currentUser.getId(), "您有一条新的待办任务",
                    currentUser.getNickname() + " 通过了「" + subject + "」的 " + task.getTaskName() + "，待您继续审批",
                    instance.getId(), newTask.getId());
        }
        instance.setCurrentNode(currentNode.toString());
        wfInstanceMapper.updateById(instance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(HandleWorkflowDTO request) {
        UserDTO currentUser = securityUtil.getUserInfo();
        WfTask task = getValidTodoTask(request.getTaskId(), currentUser.getId());
        WfInstance instance = getRunningInstance(task.getInstanceId());

        task.setStatus(WorkflowTaskStatusEnum.DONE.getCode());
        task.setAction(WorkflowTaskActionEnum.REJECT.getCode());
        task.setComment(request.getComment());
        task.setHandleTime(LocalDateTime.now());
        wfTaskMapper.updateById(task);

        saveRecord(instance.getId(), task.getTaskName(), currentUser.getId(), WorkflowRecordActionEnum.REJECT, request.getComment());
        finishInstance(instance, WorkflowInstanceStatusEnum.TERMINATED);

        String subject = StringUtils.defaultIfBlank(instance.getTitle(), instance.getProcessName());
        publishNotify(instance.getCreateBy(), currentUser.getId(), "您发起的流程已被驳回",
                "「" + subject + "」被 " + currentUser.getNickname() + " 驳回", instance.getId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(TransferWorkflowDTO request) {
        UserDTO currentUser = securityUtil.getUserInfo();
        WfTask task = getValidTodoTask(request.getTaskId(), currentUser.getId());
        WfInstance instance = getRunningInstance(task.getInstanceId());

        if (request.getTargetUserId().equals(currentUser.getId())) {
            throw new GlobalException("转办目标不能是自己");
        }
        SysUser targetUser = sysUsersService.getById(request.getTargetUserId());
        if (targetUser == null) {
            throw new GlobalException("转办目标用户不存在");
        }

        // 原任务以转办动作办结，同节点生成新待办
        task.setStatus(WorkflowTaskStatusEnum.DONE.getCode());
        task.setAction(WorkflowTaskActionEnum.TRANSFER.getCode());
        task.setComment(request.getComment());
        task.setHandleTime(LocalDateTime.now());
        wfTaskMapper.updateById(task);

        String transferComment = StringUtils.isBlank(request.getComment())
                ? "转办给 " + targetUser.getNickname()
                : request.getComment() + "；转办给 " + targetUser.getNickname();
        saveRecord(instance.getId(), task.getTaskName(), currentUser.getId(), WorkflowRecordActionEnum.TRANSFER, transferComment);

        WorkflowEngine.NextTask nextTask = new WorkflowEngine.NextTask();
        nextTask.setNodeOrder(task.getNodeOrder());
        nextTask.setTaskKey(task.getTaskKey());
        nextTask.setTaskName(task.getTaskName());
        nextTask.setAssigneeId(request.getTargetUserId());
        WfTask newTask = insertTask(instance, nextTask);

        String subject = StringUtils.defaultIfBlank(instance.getTitle(), instance.getProcessName());
        publishNotify(request.getTargetUserId(), currentUser.getId(), "您有一条转办任务",
                currentUser.getNickname() + " 将「" + subject + "」的 " + task.getTaskName() + " 转办给您",
                instance.getId(), newTask.getId());
    }

    @Override
    public PageVO<WfTasksVO> getTodos(GetWorkflowTasksDTO request) {
        Long userId = securityUtil.getUserInfo().getId();
        IPage<WfTasksVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = wfTaskMapper.getTodos(page, request, userId);
        PageVO<WfTasksVO> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public PageVO<WfTasksVO> getDones(GetWorkflowTasksDTO request) {
        Long userId = securityUtil.getUserInfo().getId();
        IPage<WfTasksVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = wfTaskMapper.getDones(page, request, userId);
        PageVO<WfTasksVO> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public PageVO<WfInstancesVO> getMyInstances(GetMyInstancesDTO request) {
        Long userId = securityUtil.getUserInfo().getId();
        IPage<WfInstancesVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = wfInstanceMapper.getMyInstances(page, request, userId);
        PageVO<WfInstancesVO> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public WfTraceVO getTrace(Long instanceId) {
        Long userId = securityUtil.getUserInfo().getId();
        WfInstance instance = wfInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new GlobalException("流程不存在");
        }

        // 发起人、参与者或管理员可查看
        boolean allowed = (instance.getCreateBy() != null && instance.getCreateBy().equals(userId))
                || securityUtil.isAdmin()
                || wfTaskMapper.exists(Wrappers.lambdaQuery(WfTask.class)
                .eq(WfTask::getInstanceId, instanceId)
                .eq(WfTask::getAssigneeId, userId));
        if (!allowed) {
            throw new GlobalException("无权查看该流程");
        }

        WfTraceVO traceVo = new WfTraceVO();
        traceVo.setId(String.valueOf(instance.getId()));
        traceVo.setProcessKey(instance.getProcessKey());
        traceVo.setProcessName(instance.getProcessName());
        traceVo.setTitle(instance.getTitle());
        traceVo.setBusinessKey(instance.getBusinessKey());
        traceVo.setStatus(instance.getStatus());
        traceVo.setCurrentNode(instance.getCurrentNode());
        traceVo.setEngine(instance.getEngine());
        traceVo.setCreateTime(instance.getCreateTime());
        traceVo.setEndTime(instance.getEndTime());

        // 收集涉及的用户id，一次性批量取昵称
        List<WfTask> tasks = wfTaskMapper.selectList(Wrappers.lambdaQuery(WfTask.class)
                .eq(WfTask::getInstanceId, instanceId)
                .orderByAsc(WfTask::getNodeOrder)
                .orderByAsc(WfTask::getArriveTime));
        List<WfRecord> records = wfRecordMapper.selectList(Wrappers.lambdaQuery(WfRecord.class)
                .eq(WfRecord::getInstanceId, instanceId)
                .orderByAsc(WfRecord::getOperateTime));
        Map<Long, String> nicknameMap = getNicknameMap(instance, tasks, records);

        traceVo.setStartUserNickname(nicknameMap.get(instance.getCreateBy()));
        List<WfTasksVO> taskVos = new ArrayList<>();
        for (WfTask task : tasks) {
            WfTasksVO taskVo = new WfTasksVO();
            taskVo.setId(String.valueOf(task.getId()));
            taskVo.setInstanceId(String.valueOf(task.getInstanceId()));
            taskVo.setProcessKey(instance.getProcessKey());
            taskVo.setProcessName(instance.getProcessName());
            taskVo.setTitle(instance.getTitle());
            taskVo.setBusinessKey(instance.getBusinessKey());
            taskVo.setCurrentNode(instance.getCurrentNode());
            taskVo.setTaskName(task.getTaskName());
            taskVo.setArriveTime(task.getArriveTime());
            taskVo.setHandleTime(task.getHandleTime());
            taskVo.setAction(task.getAction());
            taskVo.setComment(task.getComment());
            taskVo.setStartUserNickname(nicknameMap.get(instance.getCreateBy()));
            taskVo.setAssigneeNickname(nicknameMap.get(task.getAssigneeId()));
            taskVos.add(taskVo);
        }
        traceVo.setTasks(taskVos);

        List<WfRecordsVO> recordVos = new ArrayList<>();
        for (WfRecord record : records) {
            WfRecordsVO recordVo = new WfRecordsVO();
            recordVo.setNodeName(record.getNodeName());
            recordVo.setOperatorNickname(record.getOperatorId() == null ? "系统" : nicknameMap.get(record.getOperatorId()));
            recordVo.setAction(record.getAction());
            recordVo.setComment(record.getComment());
            recordVo.setOperateTime(record.getOperateTime());
            recordVos.add(recordVo);
        }
        traceVo.setRecords(recordVos);
        return traceVo;
    }

    @Override
    public List<WfUserOptionVO> getApproverOptions() {
        return sysUsersService.lambdaQuery()
                .eq(SysUser::getEnabled, true)
                .list()
                .stream()
                .map(user -> {
                    WfUserOptionVO optionVo = new WfUserOptionVO();
                    optionVo.setId(String.valueOf(user.getId()));
                    optionVo.setNickname(StringUtils.defaultIfBlank(user.getNickname(), user.getUsername()));
                    return optionVo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 校验审批人序列存在
     */
    private void validateAssignees(List<Long> assigneeIds) {
        for (Long assigneeId : assigneeIds) {
            if (sysUsersService.getById(assigneeId) == null) {
                throw new GlobalException("审批人不存在：" + assigneeId);
            }
        }
    }

    /**
     * 取有效待办任务并校验归属
     */
    private WfTask getValidTodoTask(Long taskId, Long userId) {
        WfTask task = wfTaskMapper.selectById(taskId);
        if (task == null) {
            throw new GlobalException("任务不存在");
        }
        if (!WorkflowTaskStatusEnum.TODO.getCode().equals(task.getStatus())) {
            throw new GlobalException("该任务已办理");
        }
        if (!task.getAssigneeId().equals(userId)) {
            throw new GlobalException("无权办理该任务");
        }
        return task;
    }

    /**
     * 取运行中的流程实例
     */
    private WfInstance getRunningInstance(Long instanceId) {
        WfInstance instance = wfInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new GlobalException("流程实例不存在");
        }
        if (!WorkflowInstanceStatusEnum.RUNNING.getCode().equals(instance.getStatus())) {
            throw new GlobalException("该流程已结束");
        }
        return instance;
    }

    /**
     * 按引擎产出插入待办任务
     */
    private WfTask insertTask(WfInstance instance, WorkflowEngine.NextTask nextTask) {
        WfTask task = new WfTask();
        task.setInstanceId(instance.getId());
        task.setNodeOrder(nextTask.getNodeOrder());
        task.setTaskKey(nextTask.getTaskKey());
        task.setTaskName(nextTask.getTaskName());
        task.setAssigneeId(nextTask.getAssigneeId());
        task.setStatus(WorkflowTaskStatusEnum.TODO.getCode());
        task.setArriveTime(LocalDateTime.now());
        wfTaskMapper.insert(task);
        return task;
    }

    /**
     * 结束流程实例并落系统留痕
     */
    private void finishInstance(WfInstance instance, WorkflowInstanceStatusEnum status) {
        instance.setStatus(status.getCode());
        instance.setEndTime(LocalDateTime.now());
        wfInstanceMapper.updateById(instance);
        saveRecord(instance.getId(), status == WorkflowInstanceStatusEnum.FINISHED ? "流程结束" : "流程终止",
                null, status == WorkflowInstanceStatusEnum.FINISHED ? WorkflowRecordActionEnum.FINISH : WorkflowRecordActionEnum.TERMINATE, null);
    }

    /**
     * 落审批留痕
     */
    private void saveRecord(Long instanceId, String nodeName, Long operatorId, WorkflowRecordActionEnum action, String comment) {
        WfRecord record = new WfRecord();
        record.setInstanceId(instanceId);
        record.setNodeName(nodeName);
        record.setOperatorId(operatorId);
        record.setAction(action.getCode());
        record.setComment(comment);
        record.setOperateTime(LocalDateTime.now());
        wfRecordMapper.insert(record);
    }

    /**
     * 发布工作流通知事件（自己操作不通知自己）
     */
    private void publishNotify(Long receiverId, Long operatorId, String title, String detail, Long instanceId, Long taskId) {
        if (receiverId == null || receiverId.equals(operatorId)) {
            return;
        }
        WorkflowEventDTO event = new WorkflowEventDTO();
        event.setReceiverId(receiverId);
        event.setTitle(title);
        event.setDetail(detail);
        event.setInstanceId(instanceId);
        event.setTaskId(taskId);
        workflowEventPublisher.send(event);
    }

    /**
     * 批量解析用户昵称
     */
    private Map<Long, String> getNicknameMap(WfInstance instance, List<WfTask> tasks, List<WfRecord> records) {
        List<Long> userIds = new ArrayList<>();
        if (instance.getCreateBy() != null) {
            userIds.add(instance.getCreateBy());
        }
        tasks.forEach(task -> userIds.add(task.getAssigneeId()));
        records.forEach(record -> {
            if (record.getOperatorId() != null) {
                userIds.add(record.getOperatorId());
            }
        });
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (distinctIds.isEmpty()) {
            return new HashMap<>();
        }
        return sysUsersService.listByIds(distinctIds).stream()
                .collect(Collectors.toMap(SysUser::getId, user -> StringUtils.defaultIfBlank(user.getNickname(), user.getUsername()), (a, b) -> a));
    }
}
