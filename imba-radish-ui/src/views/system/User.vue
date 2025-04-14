<template>
  <div class="unified-list-container">
    <el-row :gutter="20">
      <!-- 左侧组织架构树 -->
      <el-col :span="4">
        <el-card shadow="hover" class="tree-card">
          <template #header>
            <div class="dept-card-header">
              <span>组织架构</span>
            </div>
          </template>
          <el-tree v-if="deptTree.length > 0" :data="deptTree" :props="treeProps" node-key="id" highlight-current
            @node-click="handleDeptClick" :default-expanded-keys="[firstNodeKey]" class="custom-tree">
            <template #default="{ node }">
              <el-tooltip effect="dark" :content="node.label" placement="top-start"
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
                <el-input v-model="searchForm.nameLikeQry" placeholder="用户名/昵称" clearable  class="keyword-input"/>
              </el-form-item>
              <el-form-item label="状态:">
                <el-select v-model="searchForm.enabled" placeholder="请选择" clearable
                  style="width: 120px">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="账户状态:">
                <el-select v-model="searchForm.accountNonLocked" placeholder="请选择" clearable
                  style="width: 120px">
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
              <el-table-column prop="enabled" label="状态" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
                    {{ scope.row.enabled ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="accountNonLocked" label="账户" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
                    {{ scope.row.enabled ? '正常' : '锁定' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template #default="scope">
                  <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                  <el-popconfirm title="确认删除吗？" confirm-button-text="确认" cancel-button-text="取消"
                    @confirm="handleDelete(scope.row.id)">
                    <template #reference>
                      <el-button size="small" type="danger">
                        删除
                      </el-button>
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
import { getUserList, addUser, updateUser, deleteUserById } from '@/api/user'
import type { AllUserVo } from '@/types/user'
import type { DeptTreeVo } from '@/types/dept'
import UserFormDialog from '@/components/system/UserFormDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
  return label.length > 10
}

// 提交新增用户
const handleAddSubmit = async (formData: any) => {
  try {
    const publicKey = await getPublicKey(0)
    formData.password = RSAUtil.encrypt(formData.password, publicKey);
    await addUser(formData)
    ElMessage.success('新增用户成功')
    loadUsers()
  } catch (error) {
    console.error('新增用户失败:', error)
  }
}

// 提交编辑用户
const handleEditSubmit = async (formData: any) => {
  try {
    await updateUser({
      id: userId.value,
      nickname: formData.nickname,
      deptId: formData.deptId,
      email: formData.email,
      phone: formData.phone,
      avatar: formData.avatar,
      roleIds: formData.roleIds
    })
    ElMessage.success('更新用户成功')
    loadUsers()
  } catch (error) {
    console.error('更新用户失败:', error)
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
    userList.value = res.result || []
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

// 初始化加载
onMounted(() => {
  loadDeptTree()
  loadUsers()
})
</script>

<style scoped>
.unified-list-container {
  height: 100%;
  padding: 16px;
  background-color: #f5f7fa;
}

/* 树形卡片样式 */
.tree-card {
  height: 100%;
  border-radius: 6px;
}

/* 头部样式优化 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 32px;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

/* 卡片样式优化 */
.el-card {
  height: 100%;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

:deep(.el-card__header) {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

/* 搜索区域优化 */
.search-container {
  padding: 16px;
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

/* 树形节点样式优化 */
.custom-tree {
  padding: 8px 12px;
}

.tree-node-label {
  display: inline-block;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}

/* 分页样式优化 */
.list-pagination {
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
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

/* 按钮样式微调 */
.header-button {
  height: 32px;
  padding: 7px 15px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .search-form {
    gap: 12px;
  }
  
  :deep(.el-form-item) {
    margin-right: 12px;
  }
}
</style>