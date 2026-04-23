<template>
  <div class="dept-container container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="dept-card">
          <template #header>
            <div class="list-header">
              <div class="header-title">
                <el-icon class="title-icon"><Folder /></el-icon>
                <span>部门管理</span>
              </div>
              <div class="right-header">
                <el-button type="primary" @click="handleExport" class="add-button">
                  <el-icon><Download /></el-icon>
                  导出
                </el-button>
                <el-button type="primary" @click="handleAdd" class="add-button">
                  <el-icon><Plus /></el-icon>
                  新增部门
                </el-button>
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

          <!-- 部门表格 -->
          <div class="list-table">
            <el-table :data="deptList" border size="small" style="width: 100%" v-loading="loading"
              @selection-change="handleSelectionChange" :row-class-name="tableRowClassName">
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
              <el-table-column label="操作" min-width="200" align="center">
                <template #default="scope">
                  <div class="action-buttons-container">
                    <el-button 
                      size="small" 
                      type="primary" 
                      @click="handleEdit(scope.row)"
                      class="action-button edit-button"
                    >
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-popconfirm 
                      title="确认删除该部门吗？" 
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
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" @size-change="handleSizeChange" size="default" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 新增部门弹窗 -->
  <dept-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑部门弹窗 -->
  <dept-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :deptId="deptId"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Folder, Download, Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import DeptFormDialog from './component/DeptFormDialog.vue'
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
const deptId = ref<string | null>(null)
const deptList = ref<GetDeptsVo[]>([])
const selectedIds = ref<string[]>([])

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

const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

const handleExport = async () => {
  if (selectedIds.value.length === 0) {
    showMessage('请至少选择一项', 'warning')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要导出选中的 ${selectedIds.value.length} 个部门吗？`,
      '导出确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
        title: '导出确认'
      }
    )
    const { blob, filename } = await exportDept(selectedIds.value, '部门.xlsx')
    download(blob, filename)
  } catch {
    // 用户取消导出
  }
}

onMounted(() => {
  loadDepts()
})
</script>

<style scoped>
/* 部门页面特有样式 */

/* 部门卡片样式 */
.dept-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  height: 98%;
}

/* 列表头部样式 */
.list-header {
  height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

/* 搜索区域样式 */
.search-container {
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 16px;
}

/* 关键字输入框样式 */
.keyword-input {
  width: 240px !important;
}

/* 表格样式 */
.list-table {
  height: calc(100vh - 400px);
  overflow-y: auto;
  margin-bottom: 16px;
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