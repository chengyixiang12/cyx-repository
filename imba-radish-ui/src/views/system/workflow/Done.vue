<template>
    <ListPage title="已办任务" :loading="loading" :count="doneList.length" :columns="9" :model="searchForm">
        <template #search>
            <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="流程名称/标题" clearable class="keyword-input" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
            </el-form-item>
        </template>

        <el-table :data="doneList" border size="small" style="width: 100%" v-loading="loading && doneList.length > 0"
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
            <el-table-column prop="taskName" align="center" label="办理任务" show-overflow-tooltip />
            <el-table-column label="办理动作" align="center">
                <template #default="scope">
                    {{ actionText(scope.row.action) }}
                </template>
            </el-table-column>
            <el-table-column prop="handleTime" min-width="150" align="center" label="办理时间" show-overflow-tooltip />
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

    <!-- 流程跟踪抽屉 -->
    <TraceDrawer v-model="traceVisible" :instance-id="traceInstanceId" />
</template>

<script lang="ts" setup>
import ListPage from '@/components/layout/ListPage.vue'
import TraceDrawer from './components/TraceDrawer.vue'
import { ref, onMounted } from 'vue'
import { getWorkflowDonesApi } from '@/api/workflow'
import type { WfTasksVo } from '@/types/workflow'

const loading = ref(false)
const doneList = ref<WfTasksVo[]>([])
const total = ref(0)

// 搜索表单
const searchForm = ref({
    keyword: '',
    pageNum: 1,
    pageSize: 10
})

const actionText = (action: number | null | undefined) => {
    switch (action) {
        case 1: return '通过'
        case 2: return '驳回'
        case 3: return '转办'
        default: return '-'
    }
}

// 加载已办列表
const loadDones = async () => {
    try {
        loading.value = true
        const res = await getWorkflowDonesApi(searchForm.value)
        doneList.value = res.records || []
        total.value = res.total || 0
    } catch (error) {
        console.error('加载已办列表失败:', error)
    } finally {
        loading.value = false
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
    loadDones()
}

const handleSizeChange = (size: number) => {
    searchForm.value.pageSize = size
    loadDones()
}

const handleSearch = () => {
    loadDones()
}

const resetSearch = () => {
    searchForm.value.keyword = ''
    loadDones()
}

onMounted(() => {
    loadDones()
})
</script>

<style scoped>

</style>
