package com.soft.sys.workflow;

import com.soft.sys.entity.WfInstance;
import com.soft.sys.entity.WfTask;
import com.soft.sys.enums.WorkflowTaskActionEnum;
import lombok.Data;

import java.util.List;

/**
 * 工作流执行引擎抽象。
 *
 * <p>门面层（WorkflowService）只依赖本接口完成流程推进，业务层与页面不感知具体引擎。
 * P0 阶段仅有内置线性引擎 {@link NoopWorkflowEngine}；未来接入 warm-flow 等引擎时，
 * 新实现标注 {@code @Primary} 顶替内置引擎即可，门面、表结构与页面均无需改动。</p>
 *
 * @Author: cyx
 * @Description: 工作流引擎接口
 * @DateTime: 2026/11/19 11:30
 **/
public interface WorkflowEngine {

    /**
     * 引擎标识，落库到 wf_instance.engine
     */
    String getName();

    /**
     * 发起流程后，解析第一批待办任务
     *
     * @param instance     流程实例（engineData 已写入引擎私有数据）
     * @param assigneeIds  发起时指定的审批人id序列
     * @return 第一批待办任务；为空表示流程直接结束
     */
    List<NextTask> resolveFirstTasks(WfInstance instance, List<Long> assigneeIds);

    /**
     * 任务办理完成后，解析下一批待办任务
     *
     * @param instance     流程实例
     * @param handledTask  刚办理完的任务
     * @param action       办理动作（通过/驳回/转办）
     * @return 下一批待办任务；为空表示当前动作之后流程结束（由门面层落 FINISH/TERMINATE 状态）
     */
    List<NextTask> resolveNextTasks(WfInstance instance, WfTask handledTask, WorkflowTaskActionEnum action);

    /**
     * 引擎产出的下一个待办任务描述
     */
    @Data
    class NextTask {

        /**
         * 节点序号；从1开始
         */
        private Integer nodeOrder;

        /**
         * 任务节点标识
         */
        private String taskKey;

        /**
         * 任务节点名称
         */
        private String taskName;

        /**
         * 办理人id
         */
        private Long assigneeId;
    }
}
