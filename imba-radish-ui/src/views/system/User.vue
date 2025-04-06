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
          <el-tree :data="deptTree" :props="treeProps" node-key="id" highlight-current @node-click="handleDeptClick"
            class="custom-tree">
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
              <span>用户管理</span>
              <div>
                <el-button type="primary" @click="handleAdd">新增用户</el-button>
              </div>
            </div>
          </template>

          <!-- 用户表格 -->
          <div class="list-table">
            <el-table :data="userList" border size="small" style="width: 100%" v-loading="loading" use-virtual>
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
                  <el-button size="small" type="danger" @click="handleDelete(scope.row)">
                    删除
                  </el-button>
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
import { ref, onMounted, nextTick } from 'vue'
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
    nextTick(() => {
      // 确保 DOM 更新后能正确计算宽度
    })
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
      deptId: selectedDept.value
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

const handleDelete = (row: AllUserVo) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      beforeClose: (action, instance, done) => {
        if (action === 'confirm') {
          instance.confirmButtonLoading = true
          deleteUserById(row.id).then((res) => {
            loadUsers()
            done()
          }).catch(() => {
            done()
          }).finally(() => {
            instance.confirmButtonLoading = false
          })
        } else {
          done()
        }
      }
    }
  ).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadUsers()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  loadUsers()
}

// 初始化加载
onMounted(() => {
  loadDeptTree()
  loadUsers()
})
</script>

<style scoped>
.tree-card {
  height: 100%;
}

.custom-tree .tree-node-label {
  display: inline-block;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}

/* 覆盖全局样式中的部分样式以适应这个特定页面 */
:deep(.el-card__body) {
  flex: 1;
  overflow: auto;
  padding: 0 !important;
}

.el-col {
  height: 100%;
}

.el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.dept-card-header {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
  height: 40px;           /* 设置合适的高度 */
}
</style>