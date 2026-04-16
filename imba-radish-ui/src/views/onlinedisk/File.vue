<template>
  <div class="file-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <div class="right-header">
                <FileUpload @upload-success="handleUploadSuccess" />
              </div>
            </div>
          </template>

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

          <div class="list-table">
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
              <el-table-column label="操作" min-width="160" align="center">
                <template #default="scope">
                  <el-popconfirm title="确认下载？" @confirm="handleDownload(scope.row)">
                    <template #reference>
                      <el-button size="small" type="primary" :icon="Download" circle />
                    </template>
                  </el-popconfirm>
                  <el-popconfirm title="确认删除该文件？" @confirm="handleDelete(scope.row.id)">
                    <template #reference>
                      <el-button type="danger" size="small" :icon="Delete" circle />
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 分页 -->
          <div class="list-pagination">
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" @size-change="handleSizeChange" size="default" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Delete, Download } from '@element-plus/icons-vue'
import { FilesRequest, FilesVo } from '@/types/file'
import { deleteFileApi, getMyFilesApi, getFileUrlApi } from '@/api/file'
import FileUpload from '@/components/FileUpload.vue';

const loading = ref(false);
const total = ref(0);
const fileList = ref<FilesVo[]>([]);

const searchForm = ref<FilesRequest>({
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

const loadFiles = async () => {
  try {
    loading.value = true
    const res = await getMyFilesApi(searchForm.value)
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
const handleDelete = (id: string) => {
  deleteFileApi(id)
  loadFiles()
}

const handlePageChange = (val: number) => {
  searchForm.value.pageNum = val
  loadFiles()
}

const handleSizeChange = (val: number) => {
  searchForm.value.pageSize = val
  loadFiles()
}

// 处理上传成功
const handleUploadSuccess = () => {
  loadFiles()
}

// 下载文件
const handleDownload = async (row: FilesVo) => {
  // showMessage('正在下载，请等待', 'success');
  // const blob = await downloadFileApi(row.id);
  // download(blob, row.originalName);
  const url = await getFileUrlApi(row.id, '0')
  window.location.href = url
};

onMounted(() => {
  loadFiles()
})
</script>

<style scoped>
.file-container {
  height: 100%;
  padding: 10px;
  background-color: #f5f7fa;
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

.list-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 36px;
  padding: 0 12px;
}

.right-header {
  margin-left: auto;
  display: flex;
  justify-content: flex-end;
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

.list-table {
  width: 100%;
  height: 52vh;
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

.list-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}


</style>