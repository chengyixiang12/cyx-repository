<template>
  <div class="file-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <div class="right-header">
                <el-upload class="upload-demo" :action="''" :auto-upload="true" :show-file-list="false" multiple="false"
                  name="multipartFile" :http-request="customChunkUpload" ref="uploadRef" enctype="multipart/form-data">
                  <el-button type="primary">上传</el-button>
                </el-upload>
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
                  <el-popconfirm title="确认删除该部门？" @confirm="handleDelete(scope.row.id)">
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

    <!-- 上传进度弹窗 -->
    <el-dialog title="上传" v-model="uploadDialogVisible" :close-on-click-modal="false" :show-close="false" :modal="true"
      :destroy-on-close="true" width="240px">
      <div class="progress-container">
        <el-progress type="circle" :percentage="uploadProgress" :stroke-width="12" :width="120">
          <template #default>
            <span v-if="uploadProgress == 0">解析中...</span>
            <span v-if="uploadProgress > 0" class="progress-text">{{ uploadProgress }}%</span>
          </template>
        </el-progress>
        <p class="progress-desc">请等待上传完成，请勿关闭此窗口</p>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Delete, Download } from '@element-plus/icons-vue'
import { FilesRequest, FilesVo } from '@/types/file'
import { deleteFileApi, downloadFileApi, uploadChunkApi, getMyFilesApi, mergeChunkApi, uploadFileApi } from '@/api/file'
import { download } from '@/utils/download'
import { showMessage } from '@/utils/message'
import SparkMD5 from 'spark-md5';

const loading = ref(false);
const total = ref(0);
const fileList = ref<FilesVo[]>([]);

const uploadProgress = ref<number>(0);
const chunkSize = 5 * 1024 * 1024;
const uploadDialogVisible = ref(false);

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
const handleDelete = async (id: string) => {
  await deleteFileApi(id)
  await loadFiles()
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

/**
 * 计算文件MD5（用于分片唯一标识）
 */
const calculateFileMd5 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();
    const chunks = Math.ceil(file.size / chunkSize);
    let currentChunk = 0;

    fileReader.onload = (e) => {
      try {
        spark.append(e.target?.result as ArrayBuffer);
        currentChunk++;
        // 读取完所有块后计算MD5
        if (currentChunk >= chunks) {
          resolve(spark.end());
        } else {
          loadNextChunk();
        }
      } catch (err) {
        reject(err);
      }
    };

    fileReader.onerror = (err) => {
      reject(err);
      showMessage('文件上传失败', 'error');
    };

    // 读取下一块
    const loadNextChunk = () => {
      const start = currentChunk * chunkSize;
      const end = Math.min(start + chunkSize, file.size);
      fileReader.readAsArrayBuffer(file.slice(start, end));
    };

    // 开始读取第一块
    loadNextChunk();
  });
};

/**
 * 上传单个分片
 */
const uploadSingleChunk = async (
  chunk: Blob,
  chunkIndex: number,
  fileMd5: string
) => {
  const formData = new FormData();
  formData.append('chunk', chunk);
  formData.append('fileMd5', fileMd5);
  formData.append('chunkIndex', chunkIndex.toString());
  await uploadChunkApi(formData);
};

/**
 * 合并分片
 */
const mergeChunks = async (fileMd5: string, fileName: string, totalChunks: number) => {
  await mergeChunkApi(fileMd5, fileName, totalChunks);
};

const customChunkUpload = async (options: any) => {
  const { file } = options;

  if (file.size <= chunkSize) {
    const formData = new FormData();
    formData.append('multipartFile', file);
    await uploadFileApi(formData);
  } else {
    uploadDialogVisible.value = true;
    try {
      // 1. 重置进度
      uploadProgress.value = 0;
      const fileName = file.name;

      // 2. 计算文件MD5（唯一标识）
      const fileMd5 = await calculateFileMd5(file);

      // 3. 拆分文件分片
      const totalChunks = Math.ceil(file.size / chunkSize);

      // 4. 依次上传所有分片（也可改为并发上传，提升速度）
      for (let i = 0; i < totalChunks; i++) {
        const start = i * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);
        await uploadSingleChunk(chunk, i, fileMd5);
        uploadProgress.value = Math.round((i + 1) / totalChunks * 100);
      }

      // 5. 所有分片上传完成，请求合并
      await mergeChunks(fileMd5, fileName, totalChunks);

      // 6. 上传完成
      uploadProgress.value = 100;
      uploadDialogVisible.value = false;
    } catch (error) {
      uploadProgress.value = 0;
      uploadDialogVisible.value = false;
    }
  }
  loadFiles();
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

.progress-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
  height: 160px;
}

.progress-text {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-dialog__body) {
  padding: 10px 15px !important;
}

:deep(.el-dialog__header) {
  padding: 12px 15px !important;
}

:deep(.el-dialog__title) {
  font-size: 16px !important;
}
</style>