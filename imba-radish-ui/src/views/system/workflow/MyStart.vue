<template>
    <ListPage title="我发起的" :loading="loading" :count="instanceList.length" :columns="10" :model="searchForm">
        <template #actions>
            <el-button type="primary" @click="openStartDialog">发起流程</el-button>
        </template>
        <template #search>
            <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="流程名称/标题" clearable class="keyword-input" />
            </el-form-item>
            <el-form-item label="状态:">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
                    <el-option label="运行中" :value="1" />
                    <el-option label="已完成" :value="2" />
                    <el-option label="已终止" :value="3" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
            </el-form-item>
        </template>

        <el-table :data="instanceList" border size="small" style="width: 100%"
            v-loading="loading && instanceList.length > 0" height="100%">
            <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                    {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
            </el-table-column>
            <el-table-column prop="processName" align="center" label="流程名称" show-overflow-tooltip />
            <el-table-column prop="title" align="center" label="流程标题" show-overflow-tooltip />
            <el-table-column prop="businessKey" align="center" label="业务标识" show-overflow-tooltip />
            <el-table-column label="状态" align="center">
                <template #default="scope">
                    <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="currentNode" align="center" label="当前节点" show-overflow-tooltip>
                <template #default="scope">
                    {{ scope.row.currentNode || '-' }}
                </template>
            </el-table-column>
            <el-table-column prop="currentAssigneeNickname" align="center" label="当前待办人" show-overflow-tooltip>
                <template #default="scope">
                    {{ scope.row.currentAssigneeNickname || '-' }}
                </template>
            </el-table-column>
            <el-table-column prop="createTime" min-width="150" align="center" label="发起时间" show-overflow-tooltip />
            <el-table-column prop="endTime" min-width="150" align="center" label="结束时间" show-overflow-tooltip>
                <template #default="scope">
                    {{ scope.row.endTime || '-' }}
                </template>
            </el-table-column>
            <el-table-column label="操作" min-width="80" align="center">
                <template #default="scope">
                    <div class="action-buttons-container">
                        <el-button type="primary" class="action-button" @click="openTrace(scope.row)">跟踪</el-button>
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

    <!-- 发起流程弹窗 -->
    <el-dialog v-model="startDialogVisible" title="发起流程" width="520px" destroy-on-close>
        <el-form :model="startForm" label-width="90px">
            <el-form-item label="流程标识:" required>
                <el-input v-model="startForm.processKey" maxlength="64" placeholder="如 leave-apply" />
            </el-form-item>
            <el-form-item label="流程名称:" required>
                <el-input v-model="startForm.processName" maxlength="100" placeholder="如 请假申请" />
            </el-form-item>
            <el-form-item label="流程标题:">
                <el-input v-model="startForm.title" maxlength="200" placeholder="选填，默认同流程名称" />
            </el-form-item>
            <el-form-item label="业务标识:">
                <el-input v-model="startForm.businessKey" maxlength="128" placeholder="选填，业务单据id" />
            </el-form-item>
            <el-form-item label="审批人:" required>
                <el-select v-model="startForm.assigneeIds" multiple filterable placeholder="按顺序依次审批" style="width: 100%">
                    <el-option v-for="user in approverOptions" :key="user.id" :label="user.nickname" :value="user.id" />
                </el-select>
                <div class="form-tip">所选顺序即审批顺序：前一人通过后，后一人收到待办</div>
            </el-form-item>
            <el-form-item label="发起备注:">
                <el-input v-model="startForm.comment" type="textarea" :rows="3" maxlength="500" show-word-limit
                    placeholder="选填" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="startDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="submitStart">发起</el-button>
        </template>
    </el-dialog>

    <!-- 流程跟踪抽屉 -->
    <TraceDrawer v-model="traceVisible" :instance-id="traceInstanceId" />
</template>

<script lang="ts" setup>
import ListPage from '@/components/layout/ListPage.vue'
import TraceDrawer from './components/TraceDrawer.vue'
import { ref, onMounted } from 'vue'
import { getApproverOptionsApi, getMyWorkflowInstancesApi, startWorkflowApi } from '@/api/workflow'
import type { WfInstancesVo, WfUserOptionVo } from '@/types/workflow'
import { showMessage } from '@/utils/message'

const loading = ref(false)
const submitting = ref(false)
const instanceList = ref<WfInstancesVo[]>([])
const total = ref(0)

// 搜索表单
const searchForm = ref({
    keyword: '',
    status: null as number | null,
    pageNum: 1,
    pageSize: 10
})

const statusText = (status: number) => {
    switch (status) {
        case 1: return '运行中'
        case 2: return '已完成'
        case 3: return '已终止'
        default: return '未知'
    }
}

const statusTagType = (status: number) => {
    switch (status) {
        case 1: return 'primary'
        case 2: return 'success'
        case 3: return 'danger'
        default: return 'info'
    }
}

// 加载我发起的列表
const loadInstances = async () => {
    try {
        loading.value = true
        const res = await getMyWorkflowInstancesApi(searchForm.value)
        instanceList.value = res.records || []
        total.value = res.total || 0
    } catch (error) {
        console.error('加载流程列表失败:', error)
    } finally {
        loading.value = false
    }
}

// ---------- 发起流程 ----------
const startDialogVisible = ref(false)
const approverOptions = ref<WfUserOptionVo[]>([])
const startForm = ref({
    processKey: '',
    processName: '',
    title: '',
    businessKey: '',
    assigneeIds: [] as string[],
    comment: ''
})

const openStartDialog = async () => {
    startForm.value = {
        processKey: '',
        processName: '',
        title: '',
        businessKey: '',
        assigneeIds: [],
        comment: ''
    }
    if (approverOptions.value.length === 0) {
        try {
            approverOptions.value = await getApproverOptionsApi()
        } catch (error) {
            console.error('加载审批人选项失败:', error)
        }
    }
    startDialogVisible.value = true
}

const submitStart = async () => {
    if (!startForm.value.processKey.trim()) {
        showMessage('请填写流程标识', 'warning')
        return
    }
    if (!startForm.value.processName.trim()) {
        showMessage('请填写流程名称', 'warning')
        return
    }
    if (startForm.value.assigneeIds.length === 0) {
        showMessage('请选择审批人', 'warning')
        return
    }
    try {
        submitting.value = true
        await startWorkflowApi({
            processKey: startForm.value.processKey.trim(),
            processName: startForm.value.processName.trim(),
            title: startForm.value.title.trim() || undefined,
            businessKey: startForm.value.businessKey.trim() || undefined,
            assigneeIds: startForm.value.assigneeIds,
            comment: startForm.value.comment.trim() || undefined
        })
        startDialogVisible.value = false
        loadInstances()
    } catch (error) {
        console.error('发起流程失败:', error)
    } finally {
        submitting.value = false
    }
}

// ---------- 流程跟踪 ----------
const traceVisible = ref(false)
const traceInstanceId = ref<string | undefined>(undefined)

const openTrace = (row: WfInstancesVo) => {
    traceInstanceId.value = row.id
    traceVisible.value = true
}

// ---------- 搜索与分页 ----------
const handlePageChange = (page: number) => {
    searchForm.value.pageNum = page
    loadInstances()
}

const handleSizeChange = (size: number) => {
    searchForm.value.pageSize = size
    loadInstances()
}

const handleSearch = () => {
    loadInstances()
}

const resetSearch = () => {
    searchForm.value.keyword = ''
    searchForm.value.status = null
    loadInstances()
}

onMounted(() => {
    loadInstances()
})
</script>

<style scoped>
.form-tip {
    font-size: var(--radish-font-xs);
    color: var(--radish-text-secondary);
    line-height: 1.5;
    margin-top: var(--radish-space-1);
}
</style>
