<template>
  <div class="user-container container">
    <el-row :gutter="20">
      <!-- 左侧组织架构树 -->
      <el-col :span="4">
        <el-card shadow="hover" class="tree-card">
          <template #header>
            <div class="dept-card-header">
              <el-icon class="header-icon"><Folder /></el-icon>
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
            :indent="16"
          >
            <template #default="{ node }">
              <el-tooltip effect="dark" :content="node.label" placement="right-start"
                v-if="shouldShowTooltip(node.label)">
                <span class="tree-node-label">{{ node.label }}</span>
              </el-tooltip>
              <span v-else class="tree-node-label">{{ node.label }}</span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧用户表格 -->
      <el-col :span="20">
        <el-card class="user-card">
          <template #header>
            <div class="list-header">
              <div class="header-title">
                <el-icon class="title-icon"><User /></el-icon>
                <span>用户管理</span>
              </div>
              <div class="right-header">
                <el-button type="primary" @click="handleAdd" class="add-button">
                  <el-icon><Plus /></el-icon>
                  新增用户
                </el-button>
              </div>
            </div>
          </template>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input 
                  v-model="searchForm.nameLikeQry" 
                  placeholder="用户名/昵称" 
                  clearable 
                  class="keyword-input"
                  size="default"
                />
              </el-form-item>
              <el-form-item label="状态:">
                <el-select 
                  v-model="searchForm.enabled" 
                  placeholder="请选择" 
                  clearable 
                  style="width: 120px"
                  size="default"
                >
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="账户状态:">
                <el-select 
                  v-model="searchForm.accountNonLocked" 
                  placeholder="请选择" 
                  clearable 
                  style="width: 120px"
                  size="default"
                >
                  <el-option label="正常" :value="1" />
                  <el-option label="锁定" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch" size="default">
                  <el-icon><Search /></el-icon>
                  查询
                </el-button>
                <el-button @click="resetSearch" size="default">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 用户表格 -->
          <div class="list-table">
            <el-table 
              :data="userList" 
              border 
              style="width: 100%" 
              v-loading="loading"
              :row-class-name="rowClassName"
              highlight-current-row
            >
              <el-table-column label="序号" min-width="60" align="center" type="index" />
              <el-table-column prop="username" label="用户名" show-overflow-tooltip min-width="120" align="center">
                <template #default="scope">
                  <div class="user-info">
                    <span 
                      class="online-status" 
                      :class="{ 'online': scope.row.isOnline === 1, 'offline': scope.row.isOnline === 0 }"
                    ></span>
                    <span class="username">{{ scope.row.username }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="nickname" label="昵称" align="center" show-overflow-tooltip min-width="100" />
              <el-table-column prop="phone" label="手机号码" align="center" show-overflow-tooltip min-width="120" />
              <el-table-column prop="email" label="邮箱" align="center" show-overflow-tooltip min-width="150" />
              <el-table-column prop="deptName" label="部门" align="center" show-overflow-tooltip min-width="100" />
              <el-table-column prop="enabled" label="状态" align="center" min-width="100">
                <template #default="scope">
                  <el-switch 
                    v-model="scope.row.enabled" 
                    :active-value="1" 
                    :inactive-value="0" 
                    active-color="#409EFF" 
                    inactive-color="#C0C4CC" 
                    @change="handleStatusChange(scope.row)" 
                  />
                </template>
              </el-table-column>
              <el-table-column prop="accountNonLocked" label="账户状态" align="center" min-width="120">
                <template #default="scope">
                  <el-switch 
                    v-model="scope.row.accountNonLocked" 
                    :active-value="1" 
                    :inactive-value="0" 
                    active-color="#67C23A" 
                    inactive-color="#F56C6C" 
                    @change="handleLockChange(scope.row)" 
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="300" align="center">
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
                    <el-button 
                      size="small" 
                      type="warning" 
                      @click="handleResetPassword(scope.row.id)"
                      class="action-button reset-button"
                    >
                      <el-icon><RefreshLeft /></el-icon>
                      重置密码
                    </el-button>
                    <el-button 
                      v-show="scope.row.isOnline === 1" 
                      size="small" 
                      type="info" 
                      @click="forceOffline(scope.row)"
                      class="action-button offline-button"
                    >
                      <el-icon><RemoveFilled /></el-icon>
                      强制下线
                    </el-button>
                    <el-popconfirm 
                      title="确认删除该用户吗？" 
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
            <el-pagination 
              :current-page="pagination.current" 
              :page-size="pagination.size" 
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]" 
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" 
              @size-change="handleSizeChange" 
              size="default" 
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <user-form-dialog 
    v-model:visible="addDialogVisible" 
    :is-add="true" 
    v-if="addDialogVisible" 
    :deptTree="deptTree"
    @submit="handleAddSubmit" 
  />

  <!-- 编辑用户弹窗 -->
  <user-form-dialog 
    v-model:visible="editDialogVisible" 
    :is-add="false" 
    v-if="editDialogVisible" 
    :userId="userId"
    :deptTree="deptTree" 
    @submit="handleEditSubmit" 
  />
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { getDeptTreeApi } from '@/api/dept'
import { Edit, Delete, RemoveFilled, RefreshLeft, User, Folder, Plus, Search, Refresh } from '@element-plus/icons-vue'
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
/* 用户页面特有样式 */

/* 列表头部样式 */
.list-header {
  height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

/* 用户卡片样式 */
.user-card {
  border-radius: 6px;
  box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.08);
  overflow: hidden;
  height: 95%;
}

/* 搜索区域样式 */
.search-container {
  padding: 12px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 12px;
}

/* 关键字输入框样式 */
.keyword-input {
  width: 180px !important;
}

/* 表格样式 */
.list-table {
  height: calc(100vh - 350px);
  overflow-y: auto;
  margin-bottom: 12px;
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