<template>
  <div class="role-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <span class="header-title">角色管理</span>
              <div>
                <el-button type="primary" @click="handleAdd" class="header-button">新增角色</el-button>
              </div>
            </div>
          </template>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="角色名称/编码" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="角色状态:">
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

          <!-- 角色表格 -->
          <div class="list-table">
            <el-table :data="roleList" border size="small" style="width: 100%" v-loading="loading">
              <el-table-column label="序号" width="80" align="center">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="name" label="角色名称" show-overflow-tooltip />
              <el-table-column prop="code" label="角色编码" show-overflow-tooltip />
              <el-table-column prop="status" label="状态" align="center" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column prop="isDefault" label="默认角色" align="center" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.isDefault" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleIsDefaultChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column prop="fixRole" label="固定角色" align="center" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.fixRole" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleFixRoleChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="handleEdit(scope.row.id)" :icon="Edit" circle />
                  <el-tooltip class="item" effect="dark" content="赋予权限" placement="top">
                    <el-button size="small" type="primary" @click="handleAssignPermission(scope.row.id)" :icon="Key"
                    circle />
                  </el-tooltip>
                  <el-tooltip class="item" effect="dark" content="赋予菜单" placement="top">
                    <el-button size="small" type="primary" @click="handleAssignMenu(scope.row.id)" :icon="Menu" circle />
                  </el-tooltip>
                  <el-popconfirm title="确认删除吗？" confirm-button-text="确认" cancel-button-text="取消"
                    @confirm="handleDelete(scope.row.id)">
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
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" @size-change="handleSizeChange" size="small" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 新增角色弹窗 -->
  <role-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑角色弹窗 -->
  <role-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :roleId="roleId"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Menu, Key } from '@element-plus/icons-vue'
import { getRolesApi, addRoleApi, updateRoleApi, deleteRoleApi, enableRoleApi, forbiddenRoleApi, setDefaultRoleApi, setFixRoleApi, cancelFixRoleApi } from '@/api/role'
import RoleFormDialog from '@/components/system/RoleFormDialog.vue'
import { EditRoleRequest, GetRolesRequest, SaveRoleRequest, SysRolesVo, SysRoleVo } from '@/types/role'
import { showMessage } from '@/utils/message'

const loading = ref(false)
const roleList = ref<SysRolesVo[]>([])

const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const roleId = ref<number>()

const searchForm = ref<GetRolesRequest>({
  keyword: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 加载角色列表
const loadRoles = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      keyword: searchForm.value.keyword,
      status: searchForm.value.status
    }
    const res = await getRolesApi(params)
    roleList.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    console.error('加载角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增角色
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑角色
const handleEdit = (id: number) => {
  roleId.value = id
  editDialogVisible.value = true
}

// 提交新增角色
const handleAddSubmit = async (formData: SaveRoleRequest) => {
  await addRoleApi(formData)
  await loadRoles()
}

// 提交编辑角色
const handleEditSubmit = async (formData: EditRoleRequest) => {
  await updateRoleApi(formData)
  await loadRoles()
}

// 删除角色
const handleDelete = async (id: number) => {
  await deleteRoleApi(id)
  await loadRoles()
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadRoles()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  loadRoles()
}

// 状态变更
const handleStatusChange = async (row: SysRoleVo) => {
  if (row.status === 1) {
    await enableRoleApi(row.id)
  } else {
    await forbiddenRoleApi(row.id)
  }
  await loadRoles();
}

// 默认角色变更
const handleIsDefaultChange = async (row: SysRoleVo) => {
  if (row.isDefault === 1) {
    await setDefaultRoleApi(row.id);
  } else {
    showMessage('非法操作', 'error');
    row.isDefault = 0;
  }
  await loadRoles();
}

// 固定角色变更
const handleFixRoleChange = async (row: SysRoleVo) => {
  if (row.fixRole === 1) {
    await setFixRoleApi(row.id);
  } else {
    await cancelFixRoleApi(row.id);
  }
  await loadRoles();
}

// 赋予权限
const handleAssignPermission = (id: number) => {

}

// 赋予菜单
const handleAssignMenu = (id: number) => {

}

onMounted(() => {
  loadRoles()
})

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  loadRoles()
}

const resetSearch = () => {
  searchForm.value.status = null
  searchForm.value.keyword = null
  loadRoles()
}
</script>

<style scoped>
.role-container {
  height: 100%;
  padding: 12px;
  background-color: #f5f7fa;
}

.list-table {
  width: 100%;
  overflow-x: auto;
  padding-top: 12px;
}

.header-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 36px;
  padding: 0 12px;
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

.header-button {
  padding: 4px 12px;
  font-size: 13px;
  height: 28px;
}
</style>