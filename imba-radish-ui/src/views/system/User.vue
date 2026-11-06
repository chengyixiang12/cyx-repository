<template>
  <ListPage title="用户管理" :loading="loading" :count="userList.length" :columns="11" :model="searchForm">
    <template #aside>
      <!-- 左侧组织架构树: 可收起面板 -->
      <aside class="tree-panel" :class="{ 'is-collapsed': isTreeCollapsed }">
        <template v-if="!isTreeCollapsed">
          <div class="tree-header">
            <span class="tree-header-title">
              <el-icon><OfficeBuilding /></el-icon>组织架构
            </span>
            <el-tooltip content="收起面板" placement="top">
              <el-icon class="tree-toggle-btn" @click="isTreeCollapsed = true">
                <Fold />
              </el-icon>
            </el-tooltip>
          </div>
          <div class="tree-content">
            <el-tree v-if="deptTree.length > 0" ref="deptTreeRef" :data="deptTree" :props="treeProps"
              node-key="id" highlight-current @node-click="handleDeptClick"
              :default-expanded-keys="[firstNodeKey]" class="custom-tree" :expand-on-click-node="false"
              :indent="14">
              <template #default="{ node, data }">
                <span class="tree-node">
                  <el-icon class="tree-node-icon">
                    <component :is="node.expanded && data.children?.length ? 'FolderOpened' : 'Folder'" />
                  </el-icon>
                  <el-tooltip effect="dark" :content="node.label" placement="right-start"
                    v-if="shouldShowTooltip(node.label)" :show-after="400">
                    <span class="tree-node-label">{{ node.label }}</span>
                  </el-tooltip>
                  <span v-else class="tree-node-label">{{ node.label }}</span>
                </span>
              </template>
            </el-tree>
          </div>
        </template>
        <!-- 收起态: 窄条 + 竖排文字, 点击展开 -->
        <div v-else class="tree-collapsed-bar" @click="isTreeCollapsed = false">
          <el-tooltip content="展开组织架构" placement="right">
            <el-icon class="tree-toggle-btn">
              <Expand />
            </el-icon>
          </el-tooltip>
          <span class="tree-collapsed-label">组织架构</span>
        </div>
      </aside>
    </template>

    <template #actions>
      <el-button type="primary" @click="handleAdd">新增</el-button>
    </template>

    <template #search>
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
    </template>

    <el-table :data="userList" border size="small" style="width: 100%" v-loading="loading && userList.length > 0" height="100%">
              <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="username" label="用户名" show-overflow-tooltip min-width="120" align="center">
                <template #default="scope">
                  <div class="user-info">
                    <span class="username">{{ scope.row.username }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="nickname" label="昵称" align="center" show-overflow-tooltip min-width="100" />
              <el-table-column prop="phone" label="手机号码" align="center" show-overflow-tooltip min-width="120" />
              <el-table-column prop="email" label="邮箱" align="center" show-overflow-tooltip min-width="150" />
              <el-table-column prop="deptName" label="部门" align="center" show-overflow-tooltip min-width="100" />
              <el-table-column prop="webIsOnline" label="Web端状态" align="center" min-width="100">
                <template #default="scope">
                  <el-tooltip :content="scope.row.webIsOnline === 1 ? '点击强制下线该端' : '当前离线'"
                    :disabled="scope.row.webIsOnline !== 1" placement="top">
                    <span class="device-tag" :class="scope.row.webIsOnline === 1 ? 'is-online' : 'is-offline'"
                      @click="confirmForceOffline(scope.row, 'web', 'Web端')">
                      <i class="dot"></i>{{ scope.row.webIsOnline === 1 ? '在线' : '离线' }}
                    </span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column prop="miniProgramIsOnline" label="小程序端状态" align="center" min-width="110">
                <template #default="scope">
                  <el-tooltip :content="scope.row.miniProgramIsOnline === 1 ? '点击强制下线该端' : '当前离线'"
                    :disabled="scope.row.miniProgramIsOnline !== 1" placement="top">
                    <span class="device-tag"
                      :class="scope.row.miniProgramIsOnline === 1 ? 'is-online' : 'is-offline'"
                      @click="confirmForceOffline(scope.row, 'mini-program', '小程序端')">
                      <i class="dot"></i>{{ scope.row.miniProgramIsOnline === 1 ? '在线' : '离线' }}
                    </span>
                  </el-tooltip>
                </template>
              </el-table-column>
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
              <el-table-column label="操作" min-width="270" align="center">
                <template #default="scope">
                  <div class="action-buttons-container">
                    <el-button type="primary" @click="handleEdit(scope.row)" class="action-button">
                      编辑
                    </el-button>
                    <el-popconfirm title="确认重置该用户密码吗？" confirm-button-text="确认" cancel-button-text="取消"
                      @confirm="handleResetPassword(scope.row.id)">
                      <template #reference>
                        <el-button type="warning" class="action-button">
                          重置密码
                        </el-button>
                      </template>
                    </el-popconfirm>
                    <el-popconfirm title="确认删除该用户吗？其角色绑定会一并解除" width="280" confirm-button-text="确认"
                      cancel-button-text="取消" @confirm="handleDelete(scope.row.id)">
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

    <template #footer>
      <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </template>
  </ListPage>

  <user-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible" :deptTree="deptTree"
    @submit="handleAddSubmit" />

  <!-- 编辑用户弹窗 -->
  <user-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :userId="userId"
    :deptTree="deptTree" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import ListPage from '@/components/layout/ListPage.vue'
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
  resetPasswordApi,
  forceOfflineApi
} from '@/api/user'
import type { UsersVO, SaveUserRequest, GetUsersDTO } from '@/types/user'
import type { DeptTreeVo } from '@/types/dept'
import UserFormDialog from './component/UserFormDialog.vue'
import { ElTooltip, ElMessageBox } from 'element-plus'
import { showMessage } from '@/utils/message'
import { RSAUtil } from '@/utils/rsa'
import { getPublicKeyApi } from '@/api/auth'

const loading = ref(false)
const userList = ref<UsersVO[]>([])
const deptTree = ref<DeptTreeVo[]>([])
const selectedDept = ref<string | null>(null)
const deptTreeRef = ref()
const isTreeCollapsed = ref(false)

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
const handleEdit = (row: UsersVO) => {
  userId.value = row.id
  editDialogVisible.value = true
}

const shouldShowTooltip = (label: string) => {
  return label.length > 5
}

// 提交新增用户
const handleAddSubmit = async (formData: SaveUserRequest) => {
  try {
    const publicKey = await getPublicKeyApi()
    formData.password = RSAUtil.encrypt(formData.password, publicKey);
    await addUser(formData)
    addDialogVisible.value = false
    await loadUsers()
  } catch (e) {
    // 失败时不关闭弹窗
  }
}

// 提交编辑用户
const handleEditSubmit = async (formData: SaveUserRequest) => {
  try {
    await updateUserApi({
      id: userId.value,
      nickname: formData.nickname,
      deptId: formData.deptId,
      email: formData.email,
      phone: formData.phone,
      roleIds: formData.roleIds
    })
    editDialogVisible.value = false
    await loadUsers()
  } catch (e) {
    // 失败时不关闭弹窗
  }
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
    const params: GetUsersDTO = {
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

// 部门树点击事件: 再次点击已选中的部门时取消筛选, 查看全部用户
const handleDeptClick = (data: DeptTreeVo) => {
  if (selectedDept.value === data.id) {
    selectedDept.value = null
    deptTreeRef.value?.setCurrentKey(null)
  } else {
    selectedDept.value = data.id
  }
  pagination.value.current = 1
  loadUsers()
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
const handleLockChange = async (row: UsersVO) => {
  if (row.accountNonLocked === 1) {
    await unlockUserApi(row.id);
  } else {
    await lockUserApi(row.id);
  }
  await loadUsers()
}

// 用户状态
const handleStatusChange = async (row: UsersVO) => {
  if (row.enabled === 1) {
    await enableUserApi(row.id);
  } else {
    await forbiddenUser(row.id);
  }
  await loadUsers()
}

type ClientType = 'web' | 'mini-program'

// 点击在线状态标签 -> 确认后强制下线对应端
const confirmForceOffline = async (row: UsersVO, deviceType: ClientType, deviceName: string) => {
  const isOnline = deviceType === 'web' ? row.webIsOnline : row.miniProgramIsOnline
  // 离线端标签不可操作（样式上也不可点, 这里兜底）
  if (isOnline !== 1) return
  try {
    await ElMessageBox.confirm(`确定强制下线该用户的 ${deviceName} 吗？`, '强制下线', {
      confirmButtonText: '强制下线',
      cancelButtonText: '取消',
      type: 'warning'
    })
    forceOffline(row, deviceType)
  } catch {
    // 用户取消
  }
}

const forceOffline = async (row: UsersVO, deviceType: ClientType) => {
  // 走 HTTP 管理接口, 由后端鉴权后从服务端下发下线指令(客户端直发 WS 指令存在越权风险)
  await forceOfflineApi({ id: row.id, deviceType })
  showMessage('已强制下线', 'success')
  await loadUsers()
}

// 重置密码
const handleResetPassword = async (id: number) => {
  resetPasswordApi(id)
}

// 初始化加载
onMounted(() => {
  loadDeptTree()
  loadUsers()
})
</script>

<style scoped>
/* ---------- 左侧组织架构面板: 可收起 ---------- */
.tree-panel {
  width: 230px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border);
  border-radius: var(--radish-radius);
  box-shadow: var(--radish-shadow-card);
  overflow: hidden;
  transition: width 0.25s ease;
}

.tree-panel.is-collapsed {
  width: 44px;
  cursor: pointer;
}

.tree-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 10px 10px 12px;
  border-bottom: 1px solid var(--radish-border-light);
  flex-shrink: 0;
}

.tree-header-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--radish-font-base);
  font-weight: 600;
  color: var(--radish-text-primary);
  white-space: nowrap;
}

.tree-header-title .el-icon {
  color: var(--radish-primary);
  font-size: var(--radish-font-lg);
}

/* 收起/展开按钮 */
.tree-toggle-btn {
  flex-shrink: 0;
  padding: 4px;
  border-radius: var(--radish-radius-sm);
  color: var(--radish-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.tree-toggle-btn:hover {
  background: var(--radish-fill);
  color: var(--radish-primary);
}

.tree-content {
  flex: 1;
  min-height: 0;
  overflow-x: auto;
  overflow-y: auto;
  padding: 8px;
}

/* ---------- 树节点样式 ---------- */
.tree-content :deep(.el-tree) {
  background: transparent;
  --el-tree-node-hover-bg-color: var(--radish-fill);
}

.tree-content :deep(.el-tree-node__content) {
  height: 32px;
  border-radius: var(--radish-radius-sm);
  margin-bottom: 2px;
  padding-right: 8px;
  transition: background-color 0.15s;
}

/* 选中节点: 主色浅底 */
.tree-content :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: var(--radish-primary-light-9);
  color: var(--radish-primary);
  font-weight: 600;
}

.tree-content :deep(.el-tree-node.is-current > .el-tree-node__content .tree-node-icon) {
  color: var(--radish-primary);
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  font-size: var(--radish-font-sm);
}

.tree-node-icon {
  flex-shrink: 0;
  font-size: var(--radish-font-base);
  color: var(--radish-text-secondary);
  transition: color 0.15s;
}

.tree-node:hover .tree-node-icon {
  color: var(--radish-primary);
}

.tree-node-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ---------- 收起态窄条 ---------- */
.tree-collapsed-bar {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
}

.tree-collapsed-bar .tree-toggle-btn {
  font-size: var(--radish-font-lg);
}

.tree-collapsed-label {
  writing-mode: vertical-lr;
  letter-spacing: 5px;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
  user-select: none;
}

/* ---------- 右侧用户容器 ---------- */
/* 设备在线状态标签: 在线时可点击强制下线 */
.device-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 10px;
  border-radius: var(--radish-radius-round);
  font-size: var(--radish-font-xs);
  user-select: none;
  transition: all 0.2s;
}

.device-tag .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.device-tag.is-online {
  background: var(--radish-success-light);
  color: var(--radish-success);
  cursor: pointer;
}

.device-tag.is-online .dot {
  background: var(--radish-success);
}

.device-tag.is-online:hover {
  background: var(--radish-success);
  color: var(--radish-bg-card);
  box-shadow: 0 2px 6px rgba(0, 180, 42, 0.35);
}

.device-tag.is-offline {
  background: var(--radish-bg-page);
  color: var(--radish-text-secondary);
  cursor: not-allowed;
}

.device-tag.is-offline .dot {
  background: var(--radish-text-placeholder);
}
</style>