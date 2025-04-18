<template>
  <div class="list-container">
    <el-row :gutter="20">
      <!-- 左侧组织架构树 -->
      <el-col :span="4">
        <el-card shadow="hover" class="tree-card">
          <template #header>
            <div class="dept-card-header">
              <span>组织架构</span>
            </div>
          </template>
          <el-tree
            v-if="deptTree.length > 0"
            :data="deptTree"
            :props="treeProps"
            node-key="id"
            highlight-current
            @node-click="handleDeptClick"
            @expand-change="handleExpandChange"
            :default-expanded-keys="[firstNodeKey]"
            class="custom-tree"
            :expand-on-click-node="false"
          >
            <template #default="{ node }">
              <el-tooltip effect="dark" :content="node.label" placement="top-start" v-if="shouldShowTooltip(node.label)">
                <span class="tree-node-label">{{ node.label }}</span>
              </el-tooltip>
              <span v-else class="tree-node-label">{{ node.label }}</span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧用户表格 -->
      <el-col :span="20">
        <el-card>
          <template #header>
            <div class="list-header">
              <span class="header-title">用户管理</span>
              <div>
                <el-button type="primary" @click="handleAdd" class="header-button">新增用户</el-button>
              </div>
            </div>
          </template>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.nameLikeQry" placeholder="用户名/昵称" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="状态:">
                <el-select v-model="searchForm.enabled" placeholder="请选择" clearable style="width: 100px">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="账户状态:">
                <el-select v-model="searchForm.accountNonLocked" placeholder="请选择" clearable style="width: 100px">
                  <el-option label="正常" :value="1" />
                  <el-option label="锁定" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 用户表格 -->
          <div class="list-table">
            <el-table :data="userList" border size="small" style="width: 100%" v-loading="loading">
              <el-table-column label="序号" width="80" align="center">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="username" label="用户名" show-overflow-tooltip />
              <el-table-column prop="phone" label="手机号码" show-overflow-tooltip />
              <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
              <el-table-column prop="nickname" label="昵称" show-overflow-tooltip />
              <el-table-column prop="deptName" label="部门" show-overflow-tooltip />
              <el-table-column prop="enabled" label="状态" align="center" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.enabled" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
                </template>
              </el-table-column>

              <el-table-column prop="accountNonLocked" label="账户" align="center" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.accountNonLocked" :active-value="1" :inactive-value="0"
                    active-color="#13ce66" inactive-color="#ff4949" @change="handleLockChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="handleEdit(scope.row)" :icon="Edit" circle />
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

  <user-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible" :deptTree="deptTree"
    @submit="handleAddSubmit" />

  <!-- 编辑用户弹窗 -->
  <user-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :userId="userId"
    :deptTree="deptTree" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { getDeptTree } from '@/api/dept'
import { Edit, Delete } from '@element-plus/icons-vue'
import {
  getUserList,
  addUser,
  updateUser,
  deleteUserById,
  lockUserApi,
  unlockUserApi,
  enableUserApi,
  forbiddenUser
} from '@/api/user'
import type { AllUserVo } from '@/types/user'
import type { DeptTreeVo } from '@/types/dept'
import UserFormDialog from '@/components/system/UserFormDialog.vue'
import { ElTooltip } from 'element-plus'
import { RSAUtil } from '@/utils/rsa';
import { getPublicKey } from '@/api/auth'

const loading = ref(false)
const userList = ref<AllUserVo[]>([])
const deptTree = ref<DeptTreeVo[]>([])
const selectedDept = ref<string | number>('')

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const userId = ref<number>(0)

// 搜索表单
const searchForm = ref({
  nameLikeQry: '',
  enabled: undefined as number | undefined,
  accountNonLocked: undefined as number | undefined
})


// 新增用户
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑用户
const handleEdit = (row: AllUserVo) => {
  userId.value = row.id
  editDialogVisible.value = true
}

const shouldShowTooltip = (label: string) => {
  return label.length > 5
}

// 提交新增用户
const handleAddSubmit = async (formData: any) => {
  const publicKey = await getPublicKey(0)
  formData.password = RSAUtil.encrypt(formData.password, publicKey);
  await addUser(formData)
  loadUsers()
}

// 提交编辑用户
const handleEditSubmit = async (formData: any) => {
  await updateUser({
    id: userId.value,
    nickname: formData.nickname,
    deptId: formData.deptId,
    email: formData.email,
    phone: formData.phone,
    avatar: formData.avatar,
    roleIds: formData.roleIds
  })
  loadUsers()
}

const treeProps = {
  label: 'name',
  children: 'children'
}

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 加载部门树
const loadDeptTree = async () => {
  try {
    const res = await getDeptTree()
    deptTree.value = res || []
    if (deptTree.value.length > 0) {
      await nextTick();
    }
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

// 加载用户数据
const loadUsers = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      deptId: selectedDept.value,
      nameLikeQry: searchForm.value.nameLikeQry,
      enabled: searchForm.value.enabled,
      accountNonLocked: searchForm.value.accountNonLocked
    }
    const res = await getUserList(params)
    userList.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 部门树点击事件
const handleDeptClick = (data: DeptTreeVo) => {
  selectedDept.value = data.id
  pagination.value.current = 1
  loadUsers()
}

// 部门树展开/收起事件
const handleExpandChange = (node: DeptTreeVo, expanded: boolean) => {
  console.log(`节点 ${node.name} 的展开状态是：${expanded ? '展开' : '收起'}`);
  if (expanded) {
    // 展开时的处理逻辑
    console.log('展开了该节点');
  } else {
    // 收起时的处理逻辑
    console.log('收起了该节点');
  }
}

const handleDelete = async (id: number) => {
  await deleteUserById(id);
  await loadUsers();
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadUsers()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  loadUsers()
}

const firstNodeKey = computed(() => {
  return deptTree.value[0]?.id || 0;
})

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  loadUsers()
}

const resetSearch = () => {
  searchForm.value.accountNonLocked = undefined
  searchForm.value.enabled = undefined
  searchForm.value.nameLikeQry = ''
  loadUsers()
}

// 账户变更
const handleLockChange = async (row: AllUserVo) => {
  if (row.accountNonLocked === 1) {
    await unlockUserApi(row.id);
  } else {
    await lockUserApi(row.id);
  }
  await loadUsers()
}

// 用户状态
const handleStatusChange = async (row: AllUserVo) => {
  if (row.enabled === 1) {
    await enableUserApi(row.id);
  } else {
    await forbiddenUser(row.id);
  }
  await loadUsers()
}

// 初始化加载
onMounted(() => {
  loadDeptTree()
  loadUsers()
})
</script>

<style scoped>
.list-container {
  height: 100%;
  padding: 12px;
  background-color: #f5f7fa;
}

.list-table {
  width: 100%;
  overflow-x: auto;
  padding-top: 12px;
}

/* 优化左侧树样式 */
.tree-card {
  height: calc(100vh - 130px);
  overflow-y: auto;
  padding: 4px;
  box-sizing: border-box;
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

.custom-tree {
  max-height: 100%;
  overflow-y: auto;
}

.tree-node-label {
  display: inline-block;
  max-width: 130px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
  font-size: 13px;
  color: #303133;
}

.list-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 部门卡片头部优化 */
.dept-card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 32px;
  font-weight: 500;
  color: #303133;
}

.header-button {
  padding: 4px 12px;
  font-size: 13px;
  height: 28px;
}
</style>