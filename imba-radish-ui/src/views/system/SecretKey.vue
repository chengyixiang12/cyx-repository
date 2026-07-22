<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>密钥管理</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleGenerateAll">
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
        <el-table-column prop="publicKey" label="公钥" align="center" show-overflow-tooltip>
          <template #default="scope">
            <el-tag type="info" style="font-family: monospace; font-size: 12px; max-width: 300px; overflow: hidden; text-overflow: ellipsis;">
              {{ scope.row.publicKey || '未生成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" align="center" show-overflow-tooltip min-width="160" />
        <el-table-column prop="updateBy" label="更新人" align="center" min-width="100" />
        <el-table-column prop="updateTime" label="更新时间" align="center" min-width="160" />
        <el-table-column label="操作" min-width="140" align="center">
          <template #default="scope">
            <el-popconfirm title="确认重新生成该密钥对？" confirm-button-text="确认" cancel-button-text="取消"
              @confirm="handleGenerate(scope.row.type)">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSecretKeyListApi, generateKeyApi } from '@/api/secretKey'
import type { GetSecretKeyListRequest, SysSecretKeyVo } from '@/types/secretKey'

const loading = ref(false)
const keyList = ref<SysSecretKeyVo[]>([])
const total = ref(0)

const searchForm = ref<GetSecretKeyListRequest>({
  pageNum: 1,
  pageSize: 10
})

// 密钥类型映射（与后端 SecretKeyEnum 对应）
const typeMap: Record<number, string> = {
  0: '用户密码密钥'
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

const handleGenerate = async (type: number) => {
  try {
    await generateKeyApi(type)
    await loadKeyList()
  } catch (e) {
    // 失败时不刷新
  }
}

const handleGenerateAll = async () => {
  try {
    // 对所有已知类型依次生成
    for (const type of Object.keys(typeMap).map(Number)) {
      await generateKeyApi(type)
    }
    await loadKeyList()
  } catch (e) {
    // 失败时不刷新
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

</style>
