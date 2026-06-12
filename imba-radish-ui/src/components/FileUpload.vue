<template>
  <div class="file-upload">
    <el-upload class="upload-demo" :action="''" :auto-upload="true" :show-file-list="false"
      :multiple="false" name="multipartFile" :http-request="customChunkUpload" ref="uploadRef"
      enctype="multipart/form-data" :limit="1">
      <el-button type="primary">上传</el-button>
    </el-upload>

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
        <p class="progress-desc">请等待上传完成</p>
      </div>
      <template #footer>
        <el-button type="danger" @click="cancelUpload">取消上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { uploadChunkApi, uploadFileApi, mergeChunkApi, getFileByMd5Api, getUploadProgressApi, cancelChunkApi } from '@/api/file'
import { showMessage } from '@/utils/message'
import { calculateFileMd5, chunkSize } from '@/utils/filemd5';
import { delay } from '@/utils/delay';

const emit = defineEmits(['upload-success'])

const uploadRef = ref();

const uploadProgress = ref<number>(0);
const maxChunkSize = 5 * 100 * 1024 * 1024;
const uploadDialogVisible = ref(false);
const isCancelled = ref(false);

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

/**
 * 取消上传
 */
const cancelUpload = async () => {
  isCancelled.value = true;
};

const customChunkUpload = async (options: any) => {
  const { file } = options;
  const fileMd5 = await calculateFileMd5(file)

  const res = await getFileByMd5Api(fileMd5, file.name);
  if (res) {
    showMessage("上传成功", 'success')
    return
  }

  if (file.size <= chunkSize) {
    const formData = new FormData()
    formData.append('multipartFile', file)
    formData.append('fileMd5', fileMd5)
    await uploadFileApi(formData)
  } else if (file.size <= maxChunkSize) {
    uploadDialogVisible.value = true
      isCancelled.value = false
      try {
        const progressRes = await getUploadProgressApi(fileMd5)
        const uploadedIndices = progressRes.uploadedIndices || []
        
        uploadProgress.value = 0
        const fileName = file.name

        const totalChunks = Math.ceil(file.size / chunkSize)

        for (let i = 0; i < totalChunks; i++) {
          if (isCancelled.value) {
            break
          }
          uploadProgress.value = Math.round((i + 1) / totalChunks * 100)
          if (uploadedIndices.includes(i)) {
            continue
          }
          const start = i * chunkSize;
          const end = Math.min(start + chunkSize, file.size)
          const chunk = file.slice(start, end)
          await uploadSingleChunk(chunk, i, fileMd5)
        }

        await delay(2000)

        if (!isCancelled.value) {
          await mergeChunks(fileMd5, fileName, totalChunks)
          showMessage("上传成功", 'success')
        } else {
          await cancelChunkApi(fileMd5)
          showMessage("上传已取消", 'info')
        }
      } catch (error) {
        uploadProgress.value = 0
        uploadDialogVisible.value = false
      } finally {
        uploadProgress.value = 0
        uploadDialogVisible.value = false
        isCancelled.value = false
      }
  } else {
    showMessage("文件限制500MB", 'warning')
  }
  
  if (uploadRef.value) {
    uploadRef.value.clearFiles();
  }
  
  // 触发上传成功事件
  emit('upload-success');
};
</script>

<style scoped>
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