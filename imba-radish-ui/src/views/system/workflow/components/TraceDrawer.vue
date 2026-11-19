<template>
    <el-drawer v-model="visible" title="流程跟踪" size="560px" destroy-on-close>
        <div v-loading="loading" class="trace-drawer">
            <template v-if="trace">
                <el-descriptions title="流程信息" :column="2" size="small" border>
                    <el-descriptions-item label="流程名称" :span="2">{{ trace.processName }}</el-descriptions-item>
                    <el-descriptions-item label="流程标题" :span="2">{{ trace.title || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="业务标识">{{ trace.businessKey || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="statusTagType(trace.status)" size="small">{{ statusText(trace.status) }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="当前节点">{{ trace.currentNode || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="发起人">{{ trace.startUserNickname || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="发起时间" :span="2">{{ trace.createTime || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="结束时间" :span="2">{{ trace.endTime || '-' }}</el-descriptions-item>
                </el-descriptions>

                <el-divider content-position="left">审批留痕</el-divider>
                <el-timeline v-if="trace.records.length > 0">
                    <el-timeline-item v-for="(record, index) in trace.records" :key="index"
                        :timestamp="record.operateTime" :type="recordTimelineType(record.action)">
                        <div class="record-item">
                            <span class="record-title">
                                {{ record.operatorNickname }} · {{ recordActionText(record.action) }}（{{ record.nodeName }}）
                            </span>
                            <span v-if="record.comment" class="record-comment">{{ record.comment }}</span>
                        </div>
                    </el-timeline-item>
                </el-timeline>
                <el-empty v-else description="暂无留痕" :image-size="60" />

                <el-divider content-position="left">任务明细</el-divider>
                <el-table :data="trace.tasks" border size="small">
                    <el-table-column prop="taskName" label="节点" align="center" />
                    <el-table-column prop="assigneeNickname" label="办理人" align="center" show-overflow-tooltip />
                    <el-table-column label="任务状态" align="center">
                        <template #default="scope">
                            <el-tag :type="taskStatusTagType(scope.row.status ?? undefined)" size="small">
                                {{ taskStatusText(scope.row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="动作" align="center">
                        <template #default="scope">
                            {{ actionText(scope.row.action) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="handleTime" label="办理时间" min-width="110" align="center"
                        show-overflow-tooltip />
                </el-table>
            </template>
        </div>
    </el-drawer>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { getWorkflowTraceApi } from '@/api/workflow'
import type { WfTraceVo } from '@/types/workflow'

const visible = defineModel<boolean>({ default: false })
const props = defineProps<{
    /** 流程实例id；为空时抽屉只展示空态 */
    instanceId?: string
}>()

watch(
    () => [visible.value, props.instanceId] as const,
    ([show, instanceId]) => {
        if (show && instanceId) {
            loadTrace(instanceId)
        }
    }
)

watch(visible, (value) => {
    if (!value) {
        trace.value = null
    }
})

const loading = ref(false)
const trace = ref<WfTraceVo | null>(null)

const loadTrace = async (instanceId: string) => {
    try {
        loading.value = true
        trace.value = await getWorkflowTraceApi(instanceId)
    } catch (error) {
        console.error('加载流程跟踪失败:', error)
    } finally {
        loading.value = false
    }
}

// ---------- 文案与标签映射（Element Plus 语义化 type，跟随主题令牌） ----------
const statusText = (status: number | undefined) => {
    switch (status) {
        case 1: return '运行中'
        case 2: return '已完成'
        case 3: return '已终止'
        default: return '未知'
    }
}

const statusTagType = (status: number | undefined) => {
    switch (status) {
        case 1: return 'primary'
        case 2: return 'success'
        case 3: return 'danger'
        default: return 'info'
    }
}

const taskStatusText = (status: number | null | undefined) => {
    switch (status) {
        case 1: return '待办'
        case 2: return '已办'
        case 3: return '已取消'
        default: return '未知'
    }
}

const taskStatusTagType = (status: number | undefined) => {
    switch (status) {
        case 1: return 'warning'
        case 2: return 'success'
        default: return 'info'
    }
}

const actionText = (action: number | null | undefined) => {
    switch (action) {
        case 1: return '通过'
        case 2: return '驳回'
        case 3: return '转办'
        default: return '-'
    }
}

const recordActionText = (action: string) => {
    const map: Record<string, string> = {
        START: '发起流程',
        APPROVE: '通过',
        REJECT: '驳回',
        TRANSFER: '转办',
        FINISH: '流程完成',
        TERMINATE: '流程终止'
    }
    return map[action] || action
}

const recordTimelineType = (action: string) => {
    switch (action) {
        case APPROVE: return 'success'
        case REJECT:
        case TERMINATE: return 'danger'
        case START: return 'primary'
        default: return ''
    }
}

const APPROVE = 'APPROVE'
const REJECT = 'REJECT'
const TERMINATE = 'TERMINATE'
const START = 'START'
</script>

<style scoped>
.trace-drawer {
    min-height: 200px;
}

.record-item {
    display: flex;
    flex-direction: column;
    gap: var(--radish-space-1, 4px);
}

.record-title {
    font-size: var(--radish-font-sm, 13px);
    color: var(--radish-text-primary);
}

.record-comment {
    font-size: var(--radish-font-sm, 13px);
    color: var(--radish-text-secondary);
    word-break: break-all;
}
</style>
