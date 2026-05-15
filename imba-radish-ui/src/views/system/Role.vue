<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>角色管理</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleAdd" class="add-button">
          新增
        </el-button>
      </div>
    </div>

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
    <div class="table-wrapper">
      <el-table :data="roleList" border size="small" style="width: 100%" v-loading="loading" height="calc(100vh - 325px)">
        <el-table-column label="序号" min-width="50" align="center">
          <template #default="scope">
            {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" align="center" label="角色名称" show-overflow-tooltip />
        <el-table-column prop="code" align="center" label="角色编码" show-overflow-tooltip />
        <el-table-column prop="sortOrder" align="center" label="排序" min-width="70" sortable />
        <el-table-column prop="status" label="状态" align="center" min-width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#13ce66"
              inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认角色" align="center" min-width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.isDefault" :active-value="1" :inactive-value="0" active-color="#13ce66"
              inactive-color="#ff4949" @change="handleIsDefaultChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="fixRole" label="固定角色" align="center" min-width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.fixRole" :active-value="1" :inactive-value="0" active-color="#13ce66"
              inactive-color="#ff4949" @change="handleFixRoleChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="240" align="center">
          <template #default="scope">
            <div class="action-buttons-container">
              <el-button type="primary" @click="handleEdit(scope.row.id)" class="action-button">
                编辑
              </el-button>
              <el-button type="primary" @click="handleAssignPermission(scope.row.id)" class="action-button">
                权限
              </el-button>
              <el-button type="primary" @click="handleAssignMenu(scope.row.id)" class="action-button">
                菜单
              </el-button>
              <el-popconfirm title="确认删除该角色吗？" confirm-button-text="确认" cancel-button-text="取消"
                @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button type="danger" class="action-button">
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
    <div class="pagination">
      <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </div>
  </div>

  <!-- 新增角色弹窗 -->
  <role-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑角色弹窗 -->
  <role-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :roleId="roleId"
    @submit="handleEditSubmit" />

  <!--赋予权限弹窗-->
  <role-permission-dialog v-model="assignPermissionVisible" :role-id="roleId" v-if="assignPermissionVisible"
    @submit="handleAssignPermissionSubmit" />

  <!--赋予菜单弹窗-->
  <role-menu-dialog v-model="assignMenuVisible" :role-id="roleId" v-if="assignMenuVisible"
    @submit="handleAssignMenuSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { getRolesApi, addRoleApi, updateRoleApi, deleteRoleApi, enableRoleApi, forbiddenRoleApi, setDefaultRoleApi, setFixRoleApi, cancelFixRoleApi } from '@/api/role'
import { updateRoleMenusApi, updateRolePermissionsApi } from '@/api/role'
import RoleFormDialog from './component/RoleFormDialog.vue'
import { EditRoleRequest, GetRolesRequest, SaveRoleRequest, SysRolesVo, SysRoleVo } from '@/types/role'
import { showMessage } from '@/utils/message'
import RolePermissionDialog from './component/RolePermissionDialog.vue'
import RoleMenuDialog from './component/RoleMenuDialog.vue'
import { SetPermissionsRequest } from '@/types/permission'

const loading = ref(false)
const roleList = ref<SysRolesVo[]>([])

const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const assignPermissionVisible = ref(false)
const assignMenuVisible = ref(false)
const roleId = ref<string>('')

const searchForm = ref<GetRolesRequest>({
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})
const total = ref<number>(0)

// 加载角色列表
const loadRoles = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: searchForm.value.pageNum,
      pageSize: searchForm.value.pageSize,
      keyword: searchForm.value.keyword,
      status: searchForm.value.status
    }
    const res = await getRolesApi(params)
    roleList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存被赋予的权限
const handleAssignPermissionSubmit = async (request: SetPermissionsRequest) => {
  await updateRolePermissionsApi(request)
}

// 保存被赋予的菜单
const handleAssignMenuSubmit = async (data: { roleId: string, menuIds: string[] }) => {
  await updateRoleMenusApi(data)
  assignMenuVisible.value = false
  await loadRoles()
}

// 新增角色
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑角色
const handleEdit = (id: string) => {
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
const handleDelete = async (id: string) => {
  await deleteRoleApi(id)
  await loadRoles()
}

// 分页变化
const handlePageChange = (page: number) => {
  searchForm.value.pageNum = page
  loadRoles()
}

const handleSizeChange = (size: number) => {
  searchForm.value.pageSize = size
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
    showMessage('非法操作', 'warning');
    row.isDefault = 1;
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
const handleAssignPermission = (id: string) => {
  roleId.value = id;
  assignPermissionVisible.value = true;
}

// 赋予菜单
const handleAssignMenu = (id: string) => {
  roleId.value = id;
  assignMenuVisible.value = true;
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadRoles()
}

const resetSearch = () => {
  searchForm.value.status = null
  searchForm.value.keyword = ''
  loadRoles()
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>

</style>