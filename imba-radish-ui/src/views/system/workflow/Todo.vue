<template>
    <ListPage title="待办任务" :loading="loading" :count="todoList.length" :columns="9" :model="searchForm">
        <template #search>
            <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="流程名称/标题" clearable class="keyword-input" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
            </el-form-item>
        </template>

        <el-table :data="todoList" border size="small" style="width: 100%" v-loading="loading && todoList.length > 0"
            height="100%">
            <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                    {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
            </el-table-column>
            <el-table-column prop="processName" align="center" label="流程名称" show-overflow-tooltip />
            <el-table-column prop="title" align="center" label="流程标题" show-overflow-tooltip />
            <el-table-column prop="startUserNickname" align="center" label="发起人" show-overflow-tooltip />
            <el-table-column prop="businessKey" align="center" label="业务标识" show-overflow-tooltip />
            <el-table-column prop="taskName" align="center" label="当前任务" show-overflow-tooltip />
            <el-table-column prop="arriveTime" min-width="150" align="center" label="到达时间" show-overflow-tooltip />
            <el-table-column label="操作" min-width="200" align="center">
                <template #default="scope">
                    <div class="action-buttons-container">
                        <el-button type="primary" class="action-button" @click="openHandleDialog('approve', scope.row)">
                            通过
                        </el-button>
                        <el-button type="danger" class="action-button" @click="openHandleDialog('reject', scope.row)">
                            驳回
                        </el-button>
                        <el-button type="warning" class="action-button" @click="openHandleDialog('transfer', scope.row)">
                            转办
                        </el-button>
                        <el-button class="action-button" @click="openTrace(scope.row)">跟踪</el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <template #footer>
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
                :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
                @current-change="handlePageChange" @size-change="handleSizeChange" />
        </template>
    </ListPage>

    <!-- 办理弹窗：通过 / 驳回 / 转办 -->
    <el-dialog v-model="handleDialogVisible" :title="handleDialogTitle" width="480px" destroy-on-close>
        <el-form :model="handleForm" label-width="80px">
            <el-form-item v-if="handleMode === 'transfer'" label="转办给:">
                <el-select v-model="handleForm.targetUserId" placeholder="请选择转办目标" filterable style="width: 100%">
                    <el-option v-for="user in approverOptions" :key="user.id" :label="user.nickname" :value="user.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="审批意见:">
                <el-input v-model="handleForm.comment" type="textarea" :rows="3" maxlength="500" show-word-limit
                    placeholder="选填" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="handleDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="submitHandle">确定</el-button>
        </template>
    </el-dialog>

    <!-- 流程跟踪抽屉 -->
    <TraceDrawer v-model="traceVisible" :instance-id="traceInstanceId" />
</template>

<script lang="ts" setup>
import ListPage from '@/components/layout/ListPage.vue'
import TraceDrawer from './components/TraceDrawer.vue'
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
    approveWorkflowApi,
    getApproverOptionsApi,
    getWorkflowTodosApi,
    rejectWorkflowApi,
    transferWorkflowApi
} from '@/api/workflow'
import type { WfTasksVo, WfUserOptionVo } from '@/types/workflow'
import { showMessage } from '@/utils/message'

const loading = ref(false)
const submitting = ref(false)
const todoList = ref<WfTasksVo[]>([])
const total = ref(0)

// 搜索表单
const searchForm = ref({
    keyword: '',
    pageNum: 1,
    pageSize: 10
})

// 加载待办列表
const loadTodos = async () => {
    try {
        loading.value = true
        const res = await getWorkflowTodosApi(searchForm.value)
        todoList.value = res.records || []
        total.value = res.total || 0
    } catch (error) {
        console.error('加载待办列表失败:', error)
    } finally {
        loading.value = false
    }
}

// ---------- 办理弹窗 ----------
type HandleMode = 'approve' | 'reject' | 'transfer'

const handleDialogVisible = ref(false)
const handleMode = ref<HandleMode>('approve')
const handleForm = ref({
    taskId: '',
    targetUserId: '',
    comment: ''
})
const approverOptions = ref<WfUserOptionVo[]>([])

const handleDialogTitle = computed(() => {
    switch (handleMode.value) {
        case 'approve': return '通过任务'
        case 'reject': return '驳回任务'
        case 'transfer': return '转办任务'
    }
})

const openHandleDialog = async (mode: HandleMode, row: WfTasksVo) => {
    handleMode.value = mode
    handleForm.value = {
        taskId: row.id,
        targetUserId: '',
        comment: ''
    }
    if (mode === 'transfer' && approverOptions.value.length === 0) {
        try {
            approverOptions.value = await getApproverOptionsApi()
        } catch (error) {
            console.error('加载审批人选项失败:', error)
        }
    }
    handleDialogVisible.value = true
}

const submitHandle = async () => {
    if (handleMode.value === 'transfer' && !handleForm.value.targetUserId) {
        showMessage('请选择转办目标', 'warning')
        return
    }
    try {
        submitting.value = true
        if (handleMode.value === 'approve') {
            await approveWorkflowApi({ taskId: handleForm.value.taskId, comment: handleForm.value.comment })
        } else if (handleMode.value === 'reject') {
            await rejectWorkflowApi({ taskId: handleForm.value.taskId, comment: handleForm.value.comment })
        } else {
            await transferWorkflowApi({
                taskId: handleForm.value.taskId,
                targetUserId: handleForm.value.targetUserId,
                comment: handleForm.value.comment
            })
        }
        handleDialogVisible.value = false
        loadTodos()
    } catch (error) {
        console.error('办理任务失败:', error)
    } finally {
        submitting.value = false
    }
}

// ---------- 流程跟踪 ----------
const traceVisible = ref(false)
const traceInstanceId = ref<string | undefined>(undefined)

const openTrace = (row: WfTasksVo) => {
    traceInstanceId.value = row.instanceId
    traceVisible.value = true
}

// ---------- 搜索与分页 ----------
const handlePageChange = (page: number) => {
    searchForm.value.pageNum = page
    loadTodos()
}

const handleSizeChange = (size: number) => {
    searchForm.value.pageSize = size
    loadTodos()
}

const handleSearch = () => {
    loadTodos()
}

const resetSearch = () => {
    searchForm.value.keyword = ''
    loadTodos()
}

onMounted(() => {
    loadTodos()
})
</script>

<style scoped>

</style>
