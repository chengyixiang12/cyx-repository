<template>
  <div class="user-container container">
    <el-row :gutter="20">
      <!-- 左侧组织架构树 -->
      <el-col :span="4">
        <div class="tree-container">
          <div class="tree-header">
            <span>组织架构</span>
          </div>
          <div class="tree-content">
            <el-tree v-if="deptTree.length > 0" :data="deptTree" :props="treeProps" node-key="id" highlight-current
              @node-click="handleDeptClick" @expand-change="handleExpandChange" :default-expanded-keys="[firstNodeKey]"
              class="custom-tree" :expand-on-click-node="false" :indent="16">
              <template #default="{ node }">
                <el-tooltip effect="dark" :content="node.label" placement="right-start"
                  v-if="shouldShowTooltip(node.label)">
                  <span class="tree-node-label">{{ node.label }}</span>
                </el-tooltip>
                <span v-else class="tree-node-label">{{ node.label }}</span>
              </template>
            </el-tree>
          </div>
        </div>
      </el-col>

      <!-- 右侧用户表格 -->
      <el-col :span="20">
        <div class="user-right-container">
          <!-- 头部 -->
          <div class="list-header">
            <div class="header-title">
              <span>用户管理</span>
            </div>
            <div class="right-header">
              <el-button type="primary" @click="handleAdd">
                新增
              </el-button>
            </div>
          </div>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.nameLikeQry" placeholder="用户名/昵称" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="状态:">
                <el-select v-model="searchForm.enabled" placeholder="请选择" clearable style="width: 120px">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="账户状态:">
                <el-select v-model="searchForm.accountNonLocked" placeholder="请选择" clearable style="width: 120px">
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
          <div class="table-wrapper">
            <el-table :data="userList" border size="small" style="width: 100%" v-loading="loading"
              :row-class-name="rowClassName" highlight-current-row>
              <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="username" label="用户名" show-overflow-tooltip min-width="120" align="center">
                <template #default="scope">
                  <div class="user-info">
                    <span class="online-status"
                      :class="{ 'online': scope.row.isOnline === 1, 'offline': scope.row.isOnline === 0 }"></span>
                    <span class="username">{{ scope.row.username }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="nickname" label="昵称" align="center" show-overflow-tooltip min-width="100" />
              <el-table-column prop="phone" label="手机号码" align="center" show-overflow-tooltip min-width="120" />
              <el-table-column prop="email" label="邮箱" align="center" show-overflow-tooltip min-width="150" />
              <el-table-column prop="deptName" label="部门" align="center" show-overflow-tooltip min-width="100" />
              <el-table-column prop="enabled" label="状态" align="center" min-width="80">
                <template #default="scope">
                  <el-switch v-model="scope.row.enabled" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column prop="accountNonLocked" label="账户状态" align="center" min-width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.accountNonLocked" :active-value="1" :inactive-value="0"
                    active-color="#13ce66" inactive-color="#ff4949" @change="handleLockChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="340" align="center">
                <template #default="scope">
                  <div class="action-buttons-container">
                    <el-button type="primary" size="small" @click="handleEdit(scope.row)" class="action-button">
                      编辑
                    </el-button>
                    <el-button type="warning" size="small" @click="handleResetPassword(scope.row.id)" class="action-button">
                      重置密码
                    </el-button>
                    <el-popconfirm title="确认强制该用户下线吗？" confirm-button-text="确认" cancel-button-text="取消"
                      @confirm="forceOffline(scope.row)">
                      <template #reference>
                        <el-button v-show="scope.row.isOnline === 1" type="info" size="small" class="action-button">
                          强制下线
                        </el-button>
                      </template>
                    </el-popconfirm>
                    <el-popconfirm title="确认删除该用户吗？" confirm-button-text="确认" cancel-button-text="取消"
                      @confirm="handleDelete(scope.row.id)">
                      <template #reference>
                        <el-button type="danger" size="small" class="action-button">
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
            <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" @size-change="handleSizeChange" />
          </div>
        </div>
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
import { getDeptTreeApi } from '@/api/dept'
import {
  getUserList,
  addUser,
  updateUserApi,
  deleteUserById,
  lockUserApi,
  unlockUserApi,
  enableUserApi,
  forbiddenUser,
  resetPasswordApi
} from '@/api/user'
import type { AllUserVo, SaveUserRequest } from '@/types/user'
import type { DeptTreeVo } from '@/types/dept'
import UserFormDialog from './component/UserFormDialog.vue'
import { ElTooltip } from 'element-plus'
import { RSAUtil } from '@/utils/rsa'
import { getPublicKey } from '@/api/auth'
import { getWebSocketInstance } from '@/utils/websocket'

const loading = ref(false)
const userList = ref<AllUserVo[]>([])
const deptTree = ref<DeptTreeVo[]>([])
const selectedDept = ref<string | null>(null)

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const userId = ref<string>('')

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
const handleAddSubmit = async (formData: SaveUserRequest) => {
  const publicKey = await getPublicKey(0)
  formData.password = RSAUtil.encrypt(formData.password, publicKey);
  await addUser(formData)
  await loadUsers()
}

// 提交编辑用户
const handleEditSubmit = async (formData: SaveUserRequest) => {
  await updateUserApi({
    id: userId.value,
    nickname: formData.nickname,
    deptId: formData.deptId,
    email: formData.email,
    phone: formData.phone,
    roleIds: formData.roleIds
  })
  await loadUsers()
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
    const res = await getDeptTreeApi(null)
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

const handleDelete = async (id: string) => {
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

const forceOffline = async (row: AllUserVo) => {
  const wsInstance = getWebSocketInstance();
  wsInstance.send({ order: 'FORCE_OFFLINE', receiver: row.id, msg: '您已被强制下线' })
}

// 重置密码
const handleResetPassword = async (id: number) => {
  resetPasswordApi(id)
}

// 表格行样式
const rowClassName = ({ row, rowIndex }: { row: any; rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 初始化加载
onMounted(() => {
  loadDeptTree()
  loadUsers()
})
</script>

<style scoped>
.user-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  
}

.user-container :deep(.el-row) {
  display: flex;
  flex: 1;
  min-height: 0;
  margin: 0;
}

.user-container :deep(.el-col) {
  display: flex;
  min-height: 0;
}

/* 左侧部门树容器 */
.tree-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.tree-header {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  flex-shrink: 0;
}

.tree-content {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 10px;
}

/* 右侧用户容器 */
.user-right-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
  flex-shrink: 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.right-header {
  display: flex;
  gap: 8px;
}

.search-container {
  padding: 10px;
  background-color: #fafafa;
  border-radius: 6px;
  margin: 10px 5px 10px 5px;
  flex-shrink: 0;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.keyword-input {
  width: 180px !important;
}

.table-wrapper {
  flex: 1;
  min-height: 0;
  overflow: auto;
  border-radius: 6px;
  border: 1px solid #edeef1;
  margin: 0 5px 0 5px;
}

.table-wrapper :deep(.el-table) {
  height: 100%;
  min-height: 100%;
}

.table-wrapper :deep(.el-table__body-wrapper) {
  overflow-y: auto;
}

.table-wrapper :deep(.el-table th) {
  background-color: #f5f7fa !important;
  font-weight: 600;
  color: #606266;
}

.action-buttons-container {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: center;
}

.action-button {
  flex-shrink: 0;
}

.pagination {
  position: sticky;
  bottom: 0;
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
  z-index: 10;
}

.online-status {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  transition: all 0.3s;
  margin-right: 6px;
}

.online-status.online {
  background-color: #67C23A;
  box-shadow: 0 0 8px rgba(103, 194, 58, 0.5);
}

.online-status.offline {
  background-color: #C0C4CC;
  box-shadow: 0 0 8px rgba(192, 196, 204, 0.5);
}

.even-row {
  background-color: #fff;
}

.odd-row {
  background-color: #fafafa;
}

.action-buttons-container {
  display: flex;
  gap: 6px;
  /* justify-content: center; */
}

.action-button {
  padding: 5px 10px;
  font-size: 12px;
}
</style>