<template>
  <div class="dictData-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <el-button type="text" icon="ArrowLeft" @click="goBack">返回</el-button>
              <span class="header-title">{{ route.query.dictName }}</span>
              <el-button type="primary" @click="handleAddData">新增</el-button>
            </div>
          </template>

          <!-- 搜索 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="标签/值" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="状态:">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 数据表格 -->
          <div class="list-table">
            <el-table :data="dictDataList" border size="small" style="width: 100%" v-loading="loading">
              <el-table-column label="序号" width="80" align="center">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="code" label="编码" />
              <el-table-column prop="label" label="标签" />
              <el-table-column prop="value" label="值" />
              <el-table-column prop="dictType" label="字典类型" />
              <el-table-column prop="isDefault" label="默认" width="80" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.isDefault" :active-value="1" :inactive-value="0" />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
                    @change="changeStatus(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column prop="sortOrder" label="排序" width="80" sortable />
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-button size="small" type="primary" :icon="Edit" circle @click="editData(scope.row)" />
                  <el-popconfirm title="确认删除该数据？" @confirm="deleteData(scope.row.id)">
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
            <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
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
import { useRouter, useRoute } from 'vue-router'
import { Edit, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)

const dictDataList = ref<any[]>([])
const pagination = ref({ current: 1, size: 10, total: 0 })
const searchForm = ref({ keyword: '', status: undefined })

const goBack = () => {
  router.push({ name: '/dict' })
}

const handleAddData = () => { /* 新增数据弹窗 */ }
const editData = (row: any) => { /* 编辑数据弹窗 */ }
const deleteData = async (id: number) => { /* 删除数据接口 */ }
const changeStatus = async (row: any) => { /* 状态切换接口 */ }

const handleSearch = () => { /* 查询 */ }
const resetSearch = () => {
  searchForm.value = { keyword: '', status: undefined }
}
const handlePageChange = (val: number) => { pagination.value.current = val }
const handleSizeChange = (val: number) => { pagination.value.size = val }

onMounted(() => {
  // loadDictDataList(route.query.dictType)
})
</script>
<style scoped>
.dictData-container {
  height: 100%;
  padding: 12px;
  background-color: #f5f7fa;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 36px;
  padding: 0 12px;
}

.header-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.header-button {
  padding: 4px 12px;
  font-size: 13px;
  height: 28px;
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
  overflow-x: auto;
  padding-top: 12px;
}

.list-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
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
</style>