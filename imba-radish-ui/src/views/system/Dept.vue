<template>
  <div class="dept-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <div class="right-header">
                <el-button type="primary" @click="handleExport">导出</el-button>
                <el-button type="primary" @click="handleAdd">新增</el-button>
              </div>
            </div>
          </template>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="部门名称/编码" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="父级:">
                <el-input v-model="searchForm.parent" placeholder="部门名称/编码" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 角色表格 -->
          <div class="list-table">
            <el-table :data="deptList" border size="small" style="width: 100%" v-loading="loading"
              @selection-change="handleSelectionChange">
              <el-table-column type="selection" min-width="50" align="center" />
              <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                  {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="name" label="部门名称" align="center" show-overflow-tooltip />
              <el-table-column prop="code" label="部门编码" align="center" show-overflow-tooltip />
              <el-table-column prop="parentCode" label="父级部门编码" align="center" show-overflow-tooltip />
              <el-table-column prop="parentName" label="父级部门名称" align="center" show-overflow-tooltip />
              <el-table-column prop="level" align="center" label="部门层级" />
              <el-table-column prop="sortOrder" align="center" min-width="70" label="排序" sortable />
              <el-table-column label="操作" min-width="160" align="center">
                <template #default="scope">
                  <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)" circle />
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
              @current-change="handlePageChange" @size-change="handleSizeChange" size="small" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 新增部门弹窗 -->
  <dept-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑部门弹窗 -->
  <dept-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :roleId="deptId"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue'
import DeptFormDialog from '@/components/system/DeptFormDialog.vue'
import {
  getDeptsApi,
  deleteDeptApi,
  saveDeptApi,
  updateDeptApi,
  exportDept
} from '@/api/dept'
import type { GetDeptsVo, GetDeptsRequest, SaveDeptRequest } from '@/types/dept'
import { download } from '@/utils/download'
import { showMessage } from '@/utils/message'

const loading = ref(false)
const total = ref(0)
const deptId = ref<number | null>(null)
const deptList = ref<GetDeptsVo[]>([])
const selectedIds = ref<number[]>([])

const addDialogVisible = ref(false)
const editDialogVisible = ref(false)

const searchForm = ref<GetDeptsRequest>({
  keyword: '',
  parent: '',
  pageNum: 1,
  pageSize: 10
})

const loadDepts = async () => {
  try {
    loading.value = true
    const res = await getDeptsApi(searchForm.value)
    deptList.value = res.records
    total.value = res.total

  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadDepts()
}

const resetSearch = () => {
  searchForm.value.keyword = ''
  searchForm.value.parent = ''
  loadDepts()
}

const handleAdd = () => {
  addDialogVisible.value = true
}

const handleEdit = (row: GetDeptsVo) => {
  deptId.value = row.id
  editDialogVisible.value = true
}

const handleDelete = async (id: number) => {
  await deleteDeptApi(id)
  loadDepts()
}

const handleAddSubmit = async (data: SaveDeptRequest) => {
  await saveDeptApi(data)
  loadDepts()
}

const handleEditSubmit = async (data: SaveDeptRequest) => {
  await updateDeptApi({ ...data, id: deptId.value! })
  loadDepts()
}

const handlePageChange = (val: number) => {
  searchForm.value.pageNum = val
  loadDepts()
}

const handleSizeChange = (val: number) => {
  searchForm.value.pageSize = val
  loadDepts()
}

const handleSelectionChange = (selection: GetDeptsVo[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleExport = async () => {
  if (selectedIds.value.length === 0) {
    showMessage('请至少选择一项', 'warning')
    return
  }
  const { blob, filename } = await exportDept(selectedIds.value, '部门.xlsx')
  download(blob, filename)
}

onMounted(() => {
  loadDepts()
})
</script>

<style scoped>
.dept-container {
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
</style>