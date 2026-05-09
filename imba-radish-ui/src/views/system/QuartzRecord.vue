<template>
    <div class="execution-record-container container">
        <!-- 头部 -->
        <div class="list-header">
            <div class="header-title">
                <span>执行记录</span>
            </div>
            <div class="right-header">
                <el-button link @click="goBack" class="go-back">
                    返回
                </el-button>
            </div>
        </div>

        <!-- 搜索条件区域 -->
        <div class="search-container">
            <el-form :model="searchForm" inline class="search-form">
                <el-form-item label="模糊查询">
                    <el-input v-model="searchForm.keyword" placeholder="请输入任务名称/任务组" clearable class="keyword-input" />
                </el-form-item>
                <el-form-item label="执行时间">
                    <el-date-picker v-model="searchForm.timeRange" type="datetimerange" range-separator="至"
                        start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">查询</el-button>
                    <el-button type="primary" @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 数据列表区域 -->
        <div class="table-wrapper">
            <el-table v-loading="loading" :data="tableData" border size="small" style="width: 100%">
                <el-table-column type="index" label="序号" min-width="50" align="center" />
                <el-table-column prop="jobName" label="任务名称" min-width="120" show-overflow-tooltip />
                <el-table-column prop="jobType" label="任务类型" min-width="120" />
                <el-table-column prop="jobGroup" label="任务组" min-width="120" />
                <el-table-column prop="status" label="运行状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag v-if="row.status === '0'" type="info">未执行</el-tag>
                        <el-tag v-else-if="row.status === '1'" type="warning">执行中</el-tag>
                        <el-tag v-else-if="row.status === '2'" type="success">执行成功</el-tag>
                        <el-tag v-else-if="row.status === '3'" type="danger">执行失败</el-tag>
                        <el-tag v-else type="info">未知</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="executionTime" label="耗时(ms)" min-width="90" />
                <el-table-column prop="startTime" label="开始时间" min-width="160" />
                <el-table-column prop="endTime" label="结束时间" min-width="160" />
                <el-table-column label="操作" min-width="140" align="center">
                    <template #default="{ row }">
                        <div class="action-buttons-container">
                            <el-button type="primary" class="action-button" @click="viewLog(row.id)">
                                日志
                            </el-button>
                            <el-button type="primary" class="action-button" @click="viewParams(row.id)">
                                参数
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination">
            <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
                :page-sizes="[10, 20, 50, 100]" :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                @current-change="handlePageChange" />
        </div>

        <!-- 执行日志弹窗 -->
        <el-dialog v-model="logDialogVisible" title="执行日志" width="800px">
            <pre class="log-content">{{ currentLog }}</pre>
        </el-dialog>

        <!-- 任务执行参数弹窗 -->
        <el-dialog v-model="paramsDialogVisible" title="任务执行参数" width="600px">
            <pre class="params-content">{{ currentParams }}</pre>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { getLogDetailApi, getQuartzRecordListApi } from '@/api/quartzRecord'
import { GetQuartzRecordListVo } from '@/types/quartzRecord'
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'


const route = useRoute()
const router = useRouter()

// 搜索表单
const searchForm = reactive({
    keyword: '',
    timeRange: []
})

const quartzId = route.query.id as string

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref<GetQuartzRecordListVo[]>([])

// 分页信息
const pagination = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
})

// 弹窗显示状态
const logDialogVisible = ref(false)
const paramsDialogVisible = ref(false)

// 当前查看的数据
const currentLog = ref('')
const currentParams = ref('')

// 获取列表数据
const fetchList = async () => {
    loading.value = true
    try {
        const params = {
            jobId: quartzId,
            pageNum: pagination.pageNum,
            pageSize: pagination.pageSize,
            keyword: searchForm.keyword,
            startTime: searchForm.timeRange?.[0],
            endTime: searchForm.timeRange?.[1]
        }

        const res = await getQuartzRecordListApi(params)
        tableData.value = res.records || []
        pagination.total = res.total || 0
    } finally {
        loading.value = false
    }
}

// 查询
const handleSearch = () => {
    pagination.pageNum = 1
    fetchList()
}

// 重置
const handleReset = () => {
    searchForm.keyword = ''
    searchForm.timeRange = []
    handleSearch()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
    pagination.pageSize = val
    fetchList()
}

// 页码变化
const handlePageChange = (val: number) => {
    pagination.pageNum = val
    fetchList()
}

// 查看执行日志
const viewLog = async (id: string) => {
    try {
        const log = await getLogDetailApi(id)
        currentLog.value = log || ''
        logDialogVisible.value = true
    } catch (error) {
        console.error('获取日志详情失败:', error)
        logDialogVisible.value = false
           }
}

// 查看任务执行参数
const viewParams = async (id: string) => {
    paramsDialogVisible.value = true
}

// 返回上一页
const goBack = () => {
    router.back()
}

onMounted(() => {
    fetchList()
})
</script>

<style scoped>
.execution-record-container {
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border-radius: 8px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    background-color: #fff;
}

.list-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    border-bottom: 1px solid #ebeef5;
    background-color: #fff;
    flex-shrink: 0;
}

.header-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
}

.title-icon {
    font-size: 18px;
    color: #409eff;
}

.right-header {
    display: flex;
    gap: 8px;
}

.search-container {
    padding: 10px;
    background-color: #fafafa;
    border-radius: 6px;
    margin: 10px 5px 10px 5px;
    flex-shrink: 0;
}

.search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
}

.search-form .el-form-item {
    margin-bottom: 0;
}

.keyword-input {
    width: 180px !important;
}

.table-wrapper {
    flex: 1;
    min-height: 0;
    overflow: auto;
    border-radius: 6px;
    border: 1px solid #edeef1;
    margin: 0 5px 0 5px;
}

.table-wrapper :deep(.el-table) {
    height: 100%;
    min-height: 100%;
}

.table-wrapper :deep(.el-table__body-wrapper) {
    overflow-y: auto;
}

.table-wrapper :deep(.el-table th) {
    background-color: #f5f7fa !important;
    font-weight: 600;
    color: #606266;
}

.pagination {
    position: sticky;
    bottom: 0;
    padding: 12px 16px;
    display: flex;
    justify-content: flex-end;
    flex-shrink: 0;
    background-color: #fff;
    z-index: 10;
}

.action-buttons-container {
    display: flex;
    gap: 6px;
    justify-content: center;
}

.action-button {
    padding: 5px 10px;
    font-size: 12px;
}

.go-back {
    font-size: 14px;
    color: #606266;
}

.go-back:hover {
    color: #409EFF;
}

.log-content,
.params-content {
    max-height: 400px;
    overflow: auto;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;
    font-family: monospace;
    font-size: 12px;
    line-height: 1.6;
}
</style>
