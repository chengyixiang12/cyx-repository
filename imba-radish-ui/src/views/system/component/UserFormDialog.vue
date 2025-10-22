<template>
  <el-dialog :title="dialogTitle" v-model="dialogVisible" width="50vw" :close-on-click-modal="false"
    @closed="handleClose">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="5vw" label-position="right" class="scrollable-form">

      <el-form-item label="用户名" prop="username" v-if="isAdd">
        <el-input v-model="formData.username" placeholder="请输入用户名" />
      </el-form-item>

      <el-form-item label="用户名" v-else>
        <el-input v-model="formData.username" disabled />
      </el-form-item>

      <el-form-item label="部门" prop="deptId">
        <el-tree-select v-model="formData.deptId" :data="deptTree" :props="treeProps" check-strictly placeholder="请选择部门"
          clearable filterable :filter-node-method="filterNode" style="width: 100%;" />
      </el-form-item>

      <el-form-item label="密码" prop="password" v-if="isAdd">
        <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入昵称" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item label="手机号">
        <el-input v-model="formData.phone" placeholder="请输入手机号" />
      </el-form-item>

      <el-form-item label="角色">
        <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" filterable collapse-tags
          collapse-tags-tooltip>
          <el-option v-for="role in roleOptions" :key="role.id" :label="role.name" :value="role.id" />
        </el-select>
      </el-form-item>
    </el-form>

    </div>
    <template #footer>
      <el-button class="btn-cancel" @click="dialogVisible = false">取消</el-button>
      <el-button class="btn-submit" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref, computed, defineEmits, onMounted } from 'vue'
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

const props = withDefaults(defineProps<FatherParam>(), {
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

const treeProps = { label: 'name', children: 'children', value: 'id' }

const formRef = ref()
const formData = ref<SaveUserRequest>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  deptId: null,
  roleIds: []
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const deptTree = ref<DeptTreeVo[]>([])
const roleOptions = ref<GetRoleSelectVo[]>([])

const handleClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    emit('submit', formData.value)
    dialogVisible.value = false
  } catch (e) {
    //
  }
}

// 获取用户
const getUserById = async (id: number) => {
  const userInfo = await getUser(id)
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

// 加载角色数组
const loadRoles = async () => {
  try {
    const res = await getRoleSelectApi()
    roleOptions.value = res
  } catch (e) {
    console.error('加载角色失败', e)
  }
}

// 部门搜索过滤
const filterNode = (value: string, data: DeptTreeVo) => {
  if (!value) return true
  return data.name.toLowerCase().includes(value.toLowerCase())
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
.dialog-body-wrapper {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
  padding-right: 10px;
}

.dialog-body-wrapper::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}
.dialog-body-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.scrollable-form {
  padding: 25px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
