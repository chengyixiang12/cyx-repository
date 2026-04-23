<template>
  <div class="permission-container container">
    <el-row :gutter="20">
      <!-- 右侧菜单表格 -->
      <el-col :span="24">
        <el-card class="permission-card">
          <template #header>
            <div class="list-header">
              <div class="header-title">
                <el-icon class="title-icon"><Key /></el-icon>
                <span>权限管理</span>
              </div>
              <div class="right-header">
                <el-button type="primary" @click="handleAdd" class="add-button">
                  <el-icon><Plus /></el-icon>
                  新增权限
                </el-button>
              </div>
            </div>
          </template>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="名称/编码" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="状态:">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>

              <el-form-item label="类型:">
                <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 100px">
                  <el-option label="菜单" value="1" />
                  <el-option label="按钮" value="2" />
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
            <el-table :data="permissionList" border size="small" style="width: 100%" v-loading="loading" :row-class-name="tableRowClassName">
              <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                  {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="name" align="center" label="名称" show-overflow-tooltip />
              <el-table-column prop="type" align="center" label="类型" show-overflow-tooltip>
                <template #default="scope">
                  {{ scope.row.type === 1 ? '菜单' : '按钮' }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" align="center" min-width="80">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
                </template>
              </el-table-column>
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
                      title="确认删除该权限吗？" 
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

  <!-- 新增菜单弹窗 -->
  <permission-from-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑菜单弹窗 -->
  <permission-from-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible"
    :permission-id="permissionId" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Key, Plus } from '@element-plus/icons-vue'
import {
  deletePermissionApi,
  editPermissionApi,
  enablePermissionApi,
  forbiddenPermissionApi,
  getPermissionsApi,
  savePermissionApi
} from '@/api/permission'
import PermissionFromDialog from './component/PermissionFormDialog.vue'
import type { EditPermissionRequest, PermissionsRequest, PermissionsVo, SavePermissionRequest } from '@/types/permission'

const loading = ref(false)
const permissionList = ref<PermissionsVo[]>([])

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const permissionId = ref<string>('')
const total = ref(0)

// 搜索表单
const searchForm = ref<PermissionsRequest>({
  keyword: '',
  status: null,
  type: '',
  pageNum: 1,
  pageSize: 10,
})

// 加载菜单列表
const loadPermissions = async () => {
  try {
    loading.value = true
    const res = await getPermissionsApi(searchForm.value)
    permissionList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载菜单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增菜单
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row: PermissionsVo) => {
  permissionId.value = row.id
  editDialogVisible.value = true
}

// 提交新增菜单
const handleAddSubmit = async (formData: SavePermissionRequest) => {
  await savePermissionApi(formData)
  loadPermissions()
}

// 提交编辑菜单
const handleEditSubmit = async (formData: EditPermissionRequest) => {
  await editPermissionApi(formData);
  loadPermissions();
}

// 改变权限状态
const handleStatusChange = async (row: PermissionsVo) => {
  if (row.status === 1) {
    await enablePermissionApi(row.id);
  } else {
    await forbiddenPermissionApi(row.id);
  }
  loadPermissions();
}

// 删除权限
const handleDelete = async (row: PermissionsVo) => {
  await deletePermissionApi(row.id);
  loadPermissions();
}

// 分页变化
const handlePageChange = (page: number) => {
  searchForm.value.pageNum = page
  loadPermissions()
}

const handleSizeChange = (size: number) => {
  searchForm.value.pageSize = size
  loadPermissions()
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadPermissions()
}

const resetSearch = () => {
  searchForm.value.status = null
  searchForm.value.type = ''
  searchForm.value.keyword = ''
  loadPermissions()
}

const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

onMounted(() => {
  loadPermissions()
})
</script>

<style scoped>
/* Permission页面特有样式 */

/* 权限卡片样式 */
.permission-card {
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
  padding: 0 12px 12px;
}
</style>
