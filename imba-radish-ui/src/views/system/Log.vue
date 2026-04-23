<template>
    <div class="menu-container container">
        <el-row :gutter="20">
            <!-- 日志表格 -->
            <el-col :span="24">
                <el-card class="log-card">
                    <template #header>
                        <div class="list-header">
                            <div class="header-title">
                                <el-icon class="title-icon"><Document /></el-icon>
                                <span>日志管理</span>
                            </div>
                        </div>
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
                        <el-table :data="logList" border size="small" style="width: 100%" v-loading="loading" :row-class-name="tableRowClassName">
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
                            <el-table-column label="操作" min-width="200" align="center">
                                <template #default="scope">
                                    <div class="action-buttons-container">
                                        <el-button 
                                            size="small" 
                                            type="primary" 
                                            @click="handleView(scope.row)"
                                            class="action-button edit-button"
                                        >
                                            <el-icon><View /></el-icon>
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
                                                    size="small" 
                                                    type="danger" 
                                                    class="action-button delete-button"
                                                >
                                                    <el-icon><Delete /></el-icon>
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
                    <div class="list-pagination">
                        <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize"
                            :total="total" :page-sizes="[10, 20, 50, 100]"
                            layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
                            @size-change="handleSizeChange" size="default" />
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { View, Delete, Document } from '@element-plus/icons-vue'
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

const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
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
/* Log页面特有样式 */

/* 日志卡片样式 */
.log-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  height: 98%;
}

/* 列表头部样式 */
.list-header {
  height: 40px;
  padding: 0 12px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 12px;
}

/* 搜索区域样式 */
.search-container {
  padding: 12px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 12px;
}

/* 关键字输入框样式 */
.keyword-input {
  width: 180px !important;
}

/* 表格样式 */
.list-table {
  height: calc(100vh - 350px);
  overflow-y: auto;
  margin-bottom: 12px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #303133;
}

/* 表格行样式 */
.even-row {
  background-color: #ffffff;
}

.odd-row {
  background-color: #f9f9f9;
}

/* 分页样式 */
.list-pagination {
  padding: 0 16px 16px;
}
</style>