<template>
  <div class="file-container container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>文件管理</span>
      </div>
      <div class="right-header">
      </div>
    </div>

    <!-- 搜索条件 -->
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键字:">
          <el-input v-model="searchForm.keyword" placeholder="文件名" clearable class="keyword-input" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 文件表格 -->
    <div class="table-wrapper">
      <el-table :data="fileList" border size="small" style="width: 100%" v-loading="loading">
        <el-table-column type="selection" min-width="20" align="center" />
        <el-table-column label="序号" min-width="50" align="center">
          <template #default="scope">
            {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="originalName" label="源文件名" align="center" show-overflow-tooltip />
        <el-table-column prop="locationName" label="存储地址" align="center" show-overflow-tooltip />
        <el-table-column prop="createTime" label="上传时间" align="center" show-overflow-tooltip />
        <el-table-column prop="createBy" label="上传人" align="center" show-overflow-tooltip />
        <el-table-column prop="fileSize" align="center" label="文件大小">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="140" align="center">
          <template #default="scope">
            <div class="action-buttons-container">
              <el-popconfirm title="确认下载？" @confirm="handleDownload(scope.row)">
                <template #reference>
                  <el-button type="primary" class="action-button">
                    下载
                  </el-button>
                </template>
              </el-popconfirm>
              <el-popconfirm title="确认删除该文件？" @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button type="danger" class="action-button">
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
      <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { FilesRequest, FilesVo } from '@/types/file'
import { deleteFileApi, getFilesApi, downloadFileApi } from '@/api/file'
import { download } from '@/utils/download'
import { showMessage } from '@/utils/message'

const loading = ref(false)
const total = ref(0)
const fileList = ref<FilesVo[]>([])

const searchForm = ref<FilesRequest>({
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

const loadFiles = async () => {
  try {
    loading.value = true
    const res = await getFilesApi(searchForm.value)
    fileList.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

// 添加一个格式化文件大小的函数
const formatFileSize = (size: number): string => {
  if (size === 0) return '0 B'
  
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const k = 1024
  const i = Math.floor(Math.log(size) / Math.log(k))
  
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + units[i]
}

const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadFiles()
}

const resetSearch = () => {
  searchForm.value.keyword = ''
  loadFiles()
}

// 删除文件
const handleDelete = async (id: string) => {
  await deleteFileApi(id);
  await loadFiles();
}

const handlePageChange = (val: number) => {
  searchForm.value.pageNum = val
  loadFiles()
}

const handleSizeChange = (val: number) => {
  searchForm.value.pageSize = val
  loadFiles()
}

// 下载文件
const handleDownload = async (row: FilesVo) => {
  showMessage('正在下载，请等待', 'success');
  const blob = await downloadFileApi(row.id);
  download(blob, row.originalName);
}

onMounted(() => {
  loadFiles()
})
</script>

<style scoped>
.file-container {
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

.even-row {
  background-color: #fff;
}

.odd-row {
  background-color: #fafafa;
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
</style>