<template>
    <div class="container">
        <!-- 头部 -->
        <div class="list-header">
            <div class="header-title">
                <span>日志管理</span>
            </div>
            <div class="right-header">
            </div>
        </div>

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

        <!-- 日志表格 -->
        <div class="table-wrapper">
            <el-table :data="logList" border size="small" style="width: 100%" v-loading="loading" height="calc(100vh - 325px)">
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
                <el-table-column prop="executionTime" align="center" label="耗时（ms）" min-width="90" />
                <el-table-column prop="requestMethod" align="center" label="请求方法" />
                <el-table-column prop="statusCode" align="center" label="状态码" />
                <el-table-column prop="operationDesc" align="center" label="操作描述" show-overflow-tooltip />
                <el-table-column label="操作" min-width="140" align="center">
                    <template #default="scope">
                        <div class="action-buttons-container">
                            <el-button 
                                type="primary" 
                                @click="handleView(scope.row)"
                                class="action-button"
                            >
                                查看
                            </el-button>
                            <el-popconfirm 
                                title="确认删除该日志吗？" 
                                confirm-button-text="确认" 
                                cancel-button-text="取消"
                                @confirm="handleDelete(scope.row.id)"
                            >
                                <template #reference>
                                    <el-button 
                                        type="danger" 
                                        class="action-button"
                                    >
                                        删除
                                    </el-button>
                                </template>
                            </el-popconfirm>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination">
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize"
                :total="total" :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
                @size-change="handleSizeChange" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
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
    router.push({ name: 'logDetail', query: { id: row.id } })
}

// 删除菜单
const handleDelete = async (id: string) => {
    await deleteLogApi(id)
    loadLogs()
}

// 分页变化
const handlePageChange = (page: number) => {
    searchForm.value.pageNum = page
    handleSearch()
}

const handleSizeChange = (size: number) => {
    searchForm.value.pageSize = size
    handleSearch()
}

// 搜索
const handleSearch = () => {
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

</style>