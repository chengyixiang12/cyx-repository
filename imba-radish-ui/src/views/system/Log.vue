<template>
    <div class="menu-container">
        <el-row :gutter="20">
            <!-- 日志表格 -->
            <el-col :span="24">
                <el-card>
                    <template #header>
                        <div class="list-header"></div>
                    </template>

                    <!-- 搜索条件 -->
                    <div class="search-container">
                        <el-form :inline="true" :model="searchForm" class="search-form">
                            <el-form-item label="关键字:">
                                <el-input v-model="searchForm.keyword" placeholder="模块名称/请求ip/访问路径" clearable
                                    class="keyword-input" />
                            </el-form-item>
                            <el-form-item label="日志级别:">
                                <el-select v-model="searchForm.logLevel" placeholder="请选择" clearable
                                    style="width: 100px">
                                    <el-option label="普通" :value="1" />
                                    <el-option label="警告" :value="2" />
                                    <el-option label="错误" :value="3" />
                                </el-select>
                            </el-form-item>

                            <el-form-item>
                                <el-button type="primary" @click="handleSearch">查询</el-button>
                                <el-button type="primary" @click="resetSearch">重置</el-button>
                            </el-form-item>
                        </el-form>
                    </div>

                    <!-- 菜单表格 -->
                    <div class="list-table">
                        <el-table :data="logList" border size="small" style="width: 100%" v-loading="loading">
                            <el-table-column label="序号" min-width="50" align="center">
                                <template #default="scope">
                                    {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                                </template>
                            </el-table-column>
                            <el-table-column prop="moduleName" align="center" label="模块名称" show-overflow-tooltip />
                            <el-table-column prop="nickname" align="center" label="创建人" show-overflow-tooltip />
                            <el-table-column prop="createTime" min-width="120" align="center" label="创建时间" show-overflow-tooltip />
                            <el-table-column prop="logLevel" align="center" label="日志级别" :formatter="logLevelType"
                                show-overflow-tooltip />
                            <el-table-column prop="executionTime" align="center" label="耗时（ms）" />
                            <el-table-column prop="requestMethod" align="center" label="请求方法" />
                            <el-table-column prop="statusCode" align="center" label="状态码" />
                            <el-table-column prop="operationDesc" align="center" label="操作描述" show-overflow-tooltip />
                            <el-table-column label="操作" min-width="120" align="center">
                                <template #default="scope">
                                    <el-button size="small" type="primary" @click="handleView(scope.row)" :icon="View"
                                        circle />
                                    <el-popconfirm title="确认删除？" confirm-button-text="确认" cancel-button-text="取消"
                                        @confirm="handleDelete(scope.row.id)">
                                        <template #reference>
                                            <el-button size="small" type="danger" :icon="Delete" circle />
                                        </template>
                                    </el-popconfirm>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>

                    <!-- 分页 -->
                    <div class="list-pagination">
                        <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize"
                            :total="total" :page-sizes="[10, 20, 50, 100]"
                            layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
                            @size-change="handleSizeChange" size="small" />
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { View, Delete } from '@element-plus/icons-vue'
import { getLogsApi, deleteLogApi } from '@/api/log'
import type { LogsRequest, LogsVo } from '@/types/log'
import { useRouter } from 'vue-router'

const loading = ref(false)
const logList = ref<LogsVo[]>([])
const total = ref(0)
const router = useRouter()

// 搜索表单
const searchForm = ref<LogsRequest>({
    keyword: '',
    requestMethod: '',
    statusCode: null,
    startTime: '',
    endTime: '',
    logLevel: '',
    pageNum: 1,
    pageSize: 10,
})

const logLevelType = (row: any, column: any, cellValue: string) => {
    switch (cellValue) {
        case '1':
            return '普通';
        case '2':
            return '警告';
        case '3':
            return '错误';
        default:
            return '未知';
    }
};

// 加载日志列表
const loadLogs = async () => {
    try {
        loading.value = true
        const res = await getLogsApi(searchForm.value)
        logList.value = res.records || []
        total.value = res.total || 0
    } catch (error) {
        console.error('加载菜单列表失败:', error)
    } finally {
        loading.value = false
    }
}

// 查看日志详情
const handleView = (row: LogsVo) => {
    router.push({ name: '/logdetail', query: { id: row.id } })
}

// 删除菜单
const handleDelete = async (id: number) => {
    await deleteLogApi(id)
    loadLogs()
}

// 分页变化
const handlePageChange = (page: number) => {
    searchForm.value.pageNum = page
    loadLogs()
}

const handleSizeChange = (size: number) => {
    searchForm.value.pageSize = size
    loadLogs()
}

// 搜索
const handleSearch = () => {
    searchForm.value.pageNum = 1
    loadLogs()
}

const resetSearch = () => {
    searchForm.value.requestMethod = ''
    searchForm.value.statusCode = null
    searchForm.value.keyword = ''
    searchForm.value.startTime = ''
    searchForm.value.endTime = ''
    searchForm.value.logLevel = ''
    loadLogs()
}

onMounted(() => {
    loadLogs()
})
</script>

<style scoped>
.menu-container {
    height: 100%;
    padding: 10px;
    background-color: #f5f7fa;
}

.list-table {
    width: 100%;
    height: 53vh;
    overflow: auto;
    padding-top: 12px;
}

.list-table::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}
.list-table::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 36px;
    padding: 0 12px;
}

.right-header {
    margin-left: auto;
}

.el-card {
    height: 100%;
    border-radius: 6px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

:deep(.el-card__header) {
    padding: 8px 12px !important;
    min-height: 36px !important;
    border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
    padding: 14px !important;
}

.search-container {
    padding: 12px;
    background-color: #fafafa;
    border-bottom: 1px solid #ebeef5;
}

.search-form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
}

.keyword-input {
    width: 200px !important;
}

:deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 16px;
}

:deep(.el-form-item__label) {
    padding-right: 8px;
    color: #606266;
}

.list-pagination {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
}

.header-button {
    padding: 4px 12px;
    font-size: 13px;
    height: 28px;
}
</style>