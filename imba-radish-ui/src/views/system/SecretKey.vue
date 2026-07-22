<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>密钥管理</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleOpenGenerateDialog">
          生成密钥
        </el-button>
      </div>
    </div>

    <!-- 密钥表格 -->
    <div class="table-wrapper">
      <el-table :data="keyList" border size="small" style="width: 100%" v-loading="loading"
        height="calc(100vh - 270px)" :row-class-name="tableRowClassName">
        <el-table-column label="序号" min-width="50" align="center">
          <template #default="scope">
            {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="密钥类型" align="center" min-width="120">
          <template #default="scope">
            {{ typeMap[scope.row.type] || '未知类型' }}
          </template>
        </el-table-column>
        <el-table-column prop="secretType" label="算法" align="center" min-width="80">
          <template #default="scope">
            <el-tag :type="scope.row.secretType === '1' ? 'primary' : 'success'" size="small">
              {{ SECRET_TYPE_MAP[scope.row.secretType] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="密钥" align="center" show-overflow-tooltip min-width="300">
          <template #default="scope">
            <el-tag v-if="scope.row.secretType === '1' && scope.row.publicKey"
              type="info" style="font-family: monospace; font-size: 12px; max-width: 280px; overflow: hidden; text-overflow: ellipsis;">
              {{ scope.row.publicKey }}
            </el-tag>
            <el-tag v-else-if="scope.row.secretType === '2' && scope.row.privateKey"
              type="warning" style="font-family: monospace; font-size: 12px; max-width: 280px; overflow: hidden; text-overflow: ellipsis;">
              {{ scope.row.privateKey }}
            </el-tag>
            <span v-else class="no-key">未生成</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" align="center" show-overflow-tooltip min-width="160" />
        <el-table-column prop="updateBy" label="更新人" align="center" min-width="100" />
        <el-table-column prop="updateTime" label="更新时间" align="center" min-width="160" />
        <el-table-column label="操作" min-width="140" align="center">
          <template #default="scope">
            <el-popconfirm title="确认重新生成该密钥？" confirm-button-text="确认" cancel-button-text="取消"
              @confirm="handleOpenGenerateDialog(scope.row.type)">
              <template #reference>
                <el-button type="primary" class="action-button">
                  生成
                </el-button>
              </template>
            </el-popconfirm>
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

    <!-- 生成密钥对话框 -->
    <el-dialog v-model="dialogVisible" title="生成密钥" width="480px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="generateForm" :rules="formRules" label-width="100px">
        <el-form-item label="密钥类型" prop="type">
          <el-select v-model="generateForm.type" placeholder="请选择密钥类型" style="width: 100%">
            <el-option v-for="(label, key) in typeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="算法" prop="secretType">
          <el-radio-group v-model="generateForm.secretType">
            <el-radio v-for="opt in SECRET_TYPE_OPTIONS" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="generateForm.description" placeholder="可选：输入描述信息" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="handleGenerate">
          确认生成
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { getSecretKeyListApi, generateKeyApi } from '@/api/secretKey'
import type { GetSecretKeyListRequest, SysSecretKeyVo, GenerateKeyRequest } from '@/types/secretKey'
import { SECRET_TYPE_MAP, SECRET_TYPE_OPTIONS } from '@/types/secretKey'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const keyList = ref<SysSecretKeyVo[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const generating = ref(false)
const formRef = ref<FormInstance>()

const searchForm = ref<GetSecretKeyListRequest>({
  pageNum: 1,
  pageSize: 10
})

// 密钥用途类型映射（与后端 KeyTypeEnum 对应）
const typeMap: Record<number, string> = {
  0: '用户密码密钥'
}

const generateForm = reactive<GenerateKeyRequest>({
  type: 0,
  secretType: '1'
})

const formRules: FormRules = {
  type: [{ required: true, message: '请选择密钥类型', trigger: 'change' }],
  secretType: [{ required: true, message: '请选择算法', trigger: 'change' }]
}

const loadKeyList = async () => {
  try {
    loading.value = true
    const data = await getSecretKeyListApi(searchForm.value)
    keyList.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleOpenGenerateDialog = (type?: number) => {
  if (type !== undefined) {
    // 单行生成：预填该行用途类型
    generateForm.type = type
  } else {
    // 点"生成密钥"按钮：默认选第一个类型
    const types = Object.keys(typeMap).map(Number)
    generateForm.type = types.length > 0 ? types[0] : 0
  }
  generateForm.secretType = '1' // 默认 RSA
  generateForm.description = ''
  dialogVisible.value = true
}

const handleGenerate = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    generating.value = true
    await generateKeyApi({
      type: generateForm.type,
      secretType: generateForm.secretType,
      description: generateForm.description || undefined
    })
    dialogVisible.value = false
    await loadKeyList()
  } finally {
    generating.value = false
  }
}

const handlePageChange = (page: number) => {
  searchForm.value.pageNum = page
  loadKeyList()
}

const handleSizeChange = (size: number) => {
  searchForm.value.pageSize = size
  loadKeyList()
}

const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => {
  loadKeyList()
})
</script>

<style scoped>
.no-key {
  color: #999;
  font-size: 12px;
}
</style>
