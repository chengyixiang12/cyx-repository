package com.soft.sys.workflow;

import com.alibaba.fastjson2.JSON;
import com.soft.sys.entity.WfInstance;
import com.soft.sys.entity.WfTask;
import com.soft.sys.enums.WorkflowTaskActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 内置线性串行审批引擎（no-op）。
 *
 * <p>语义：发起时指定的审批人序列按顺序逐个审批——
 * 第一个人通过后产生第二个人的待办，依次类推；任何人驳回则流程终止。
 * 转办由门面层在任务维度换人，不产生新节点。</p>
 *
 * <p>引擎私有数据存放在 wf_instance.engine_data（JSON 数组的审批人id序列）。
 * 该实现不含任何业务审批规则，仅保证框架链路（待办/已办/留痕/通知）可独立运转。</p>
 *
 * <p>作为默认实现注册；未来引入真实引擎（如 warm-flow adapter）时，
 * 新实现标注 {@code @Primary} 即可顶替本实现，门面层无需改动。</p>
 *
 * @Author: cyx
 * @Description: 内置线性工作流引擎
 * @DateTime: 2026/11/19 11:30
 **/
@Slf4j
@Component
public class NoopWorkflowEngine implements WorkflowEngine {

    /**
     * 引擎标识
     */
    public static final String ENGINE_NAME = "noop";

    @Override
    public String getName() {
        return ENGINE_NAME;
    }

    @Override
    public List<NextTask> resolveFirstTasks(WfInstance instance, List<Long> assigneeIds) {
        List<Long> sequence = parseSequence(instance);
        if (sequence == null || sequence.isEmpty()) {
            log.warn("no-op引擎未找到审批人序列，实例 {} 直接结束", instance.getId());
            return Collections.emptyList();
        }
        return List.of(buildTask(1, sequence.get(0)));
    }

    @Override
    public List<NextTask> resolveNextTasks(WfInstance instance, WfTask handledTask, WorkflowTaskActionEnum action) {
        if (action != WorkflowTaskActionEnum.APPROVE) {
            // 驳回由门面层落终止状态；转办在任务维度换人，均不产生新节点
            return Collections.emptyList();
        }
        List<Long> sequence = parseSequence(instance);
        if (sequence == null || sequence.isEmpty()) {
            return Collections.emptyList();
        }
        int nextOrder = handledTask.getNodeOrder() + 1;
        if (nextOrder > sequence.size()) {
            // 序列走完，流程完成
            return Collections.emptyList();
        }
        return List.of(buildTask(nextOrder, sequence.get(nextOrder - 1)));
    }

    /**
     * 从实例引擎私有数据解析串行审批人序列
     */
    private List<Long> parseSequence(WfInstance instance) {
        try {
            return JSON.parseArray(instance.getEngineData(), Long.class);
        } catch (Exception e) {
            log.error("解析引擎私有数据失败，实例 {}", instance.getId(), e);
            return null;
        }
    }

    /**
     * 构造线性节点任务
     */
    private NextTask buildTask(int nodeOrder, Long assigneeId) {
        NextTask nextTask = new NextTask();
        nextTask.setNodeOrder(nodeOrder);
        nextTask.setTaskKey("node_" + nodeOrder);
        nextTask.setTaskName("节点" + nodeOrder);
        nextTask.setAssigneeId(assigneeId);
        return nextTask;
    }
}
