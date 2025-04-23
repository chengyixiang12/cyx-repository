<template>
  <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" :close-on-click-modal="false" @closed="handleClose">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" label-position="right">
      <!-- 用户名 -->
      <el-form-item label="用户名" prop="username" v-if="isAdd">
        <el-input v-model="formData.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="用户名" v-else>
        <el-input v-model="formData.username" disabled />
      </el-form-item>

      <!-- 部门 -->
      <el-form-item label="部门">
        <el-popover placement="bottom-start" :width="300" trigger="click" popper-class="dept-tree-popper">
          <template #reference>
            <el-input :model-value="deptName" placeholder="请选择部门" readonly />
          </template>
          <el-tree
            :data="deptTree"
            :props="treeProps"
            node-key="id"
            highlight-current
            style="max-height: 300px; overflow-y: auto;"
            @node-click="handleDeptSelect"
          />
        </el-popover>
      </el-form-item>

      <!-- 密码 -->
      <el-form-item label="密码" prop="password" v-if="isAdd">
        <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>

      <!-- 昵称 -->
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入昵称" />
      </el-form-item>

      <!-- 邮箱 -->
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>

      <!-- 手机号 -->
      <el-form-item label="手机号">
        <el-input v-model="formData.phone" placeholder="请输入手机号" />
      </el-form-item>

      <!-- 角色 -->
      <el-form-item label="角色">
        <el-select
          v-model="formData.roleIds"
          multiple
          placeholder="请选择角色"
          filterable
          collapse-tags
          collapse-tags-tooltip
        >
          <el-option v-for="role in roleOptions" :key="role.id" :label="role.name" :value="role.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button class="btn-cancel" @click="dialogVisible = false">取消</el-button>
      <el-button class="btn-submit" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>


<script lang="ts" setup>
import { ref, computed, defineProps, defineEmits, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DeptTreeVo } from '@/types/dept'
import { SaveUserRequest } from '@/types/user'
import { getUser } from '@/api/user'
import { getRoleSelectApi } from '@/api/role'
import { GetRoleSelectVo } from '@/types/role'

interface FatherParam {
  visible: boolean;
  isAdd: boolean;
  deptTree: DeptTreeVo[];
  userId?: number | null;
}

const props = withDefaults(defineProps<FatherParam>(),{
  visible: false,
  isAdd: false,
  deptTree: () => [],
  userId: null
})

const emit = defineEmits(['update:visible', 'submit'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const dialogTitle = computed(() => props.isAdd ? '新增用户' : '编辑用户')

const treeProps = { label: 'name', children: 'children' }

const formRef = ref()
const formData = ref<SaveUserRequest>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  deptId: 0,
  roleIds: []
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const deptTree = ref<DeptTreeVo[]>([])
const roleOptions = ref<GetRoleSelectVo[]>([])

const handleDeptSelect = (node: DeptTreeVo) => {
  formData.value.deptId = node.id
}

const handleClose = () => {
  formRef.value?.resetFields()
}

// 提交处理
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    console.log('aaa', formData.value)
    emit('submit', formData.value)
    dialogVisible.value = false
  } catch (e) {
    ElMessage.error('请完善表单信息')
  }
}

const deptName = computed(() => {
  const findDept = (tree: DeptTreeVo[], id: number): string => {
    for (const node of tree) {
      if (node.id === id) return node.name
      if (node.children?.length) {
        const name = findDept(node.children, id)
        if (name) return name
      }
    }
    return ''
  }
  return findDept(props.deptTree, formData.value.deptId)
})

// 查找部门名称
const findDeptById = (tree: DeptTreeVo[], id: number): DeptTreeVo | null => {
  for (const node of tree) {
    if (node.id === id) return node
    if (node.children?.length) {
      const found = findDeptById(node.children, id)
      if (found) return found
    }
  }
  return null
}

// 获取用户详情
const getUserById = async (id: number) => {
  const userInfo = await getUser(id)
  const deptNode = findDeptById(props.deptTree, userInfo.deptId)

  formData.value = {
    username: userInfo.username,
    password: '',
    nickname: userInfo.nickname,
    email: userInfo.email,
    phone: userInfo.phone || '',
    deptId: userInfo.deptId || 0,
    roleIds: userInfo.roleIds || []
  }
}

// 加载角色
const loadRoles = async () => {
  try {
    const res = await getRoleSelectApi()
    roleOptions.value = res
  } catch (e) {
    console.error('加载角色失败', e)
  }
}

onMounted(async () => {
  deptTree.value = props.deptTree
  await loadRoles()
  if (props.userId) {
    await getUserById(props.userId)
  }
})
</script>

<style scoped>
.el-form-item {
  margin-bottom: 20px;
}

.el-input, .el-button {
  border-radius: 8px;
}

.el-input::placeholder {
  color: #999;
}

.el-button {
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.el-button:hover {
  transform: translateY(-2px);
}

.el-button:active {
  background-color: #409EFF;
  transform: translateY(1px);
}

.dept-tree-popper {
  max-height: 60vh;
  overflow-y: auto;
  border-radius: 8px;
}

.btn-cancel {
  background-color: #f0f0f0;
  color: #555;
  border-radius: 8px;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-submit {
  background-color: #409EFF;
  color: white;
  border-radius: 8px;
}

.btn-submit:hover {
  background-color: #3386c5;
}
</style>
