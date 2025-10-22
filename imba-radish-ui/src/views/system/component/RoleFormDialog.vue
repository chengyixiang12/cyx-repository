<template>
  <el-dialog :title="isAdd ? '新增角色' : '编辑角色'" v-model="visible" width="50vw" :close-on-click-modal="false"
    :before-close="handleClose" class="custom-dialog">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="6vw" label-position="right"
        class="scrollable-form">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="角色编码" prop="code">
          <el-input v-model="formData.code" placeholder="角色编码ROLE_开头" />
        </el-form-item>

        <el-form-item label="角色状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="默认角色" prop="isDefault">
          <el-switch v-model="formData.isDefault" :active-value="1" :inactive-value="0" active-color="#13ce66"
            inactive-color="#ff4949" />
        </el-form-item>

        <el-form-item label="固定角色" prop="fixRole">
          <el-switch v-model="formData.fixRole" :active-value="1" :inactive-value="0" active-color="#13ce66"
            inactive-color="#ff4949" />
        </el-form-item>

        <el-form-item label="排序" prop="fixRole">
          <el-input v-model="formData.sortOrder" type="text" @input="handleSortInput" placeholder="请填写序号" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getRoleApi } from '@/api/role'
import { SysRoleVo } from '@/types/role'

interface FatherParam {
  visible: boolean;
  isAdd: boolean;
  roleId?: number | null
}

const props = withDefaults(defineProps<FatherParam>(), {
  visible: false,
  isAdd: false,
  roleId: null,
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref<FormInstance>()
const formData = ref<SysRoleVo>({
  id: 0,
  name: '',
  sortOrder: null,
  code: '',
  description: '',
  status: 1,
  isDefault: 0,
  fixRole: 0,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择角色状态', trigger: 'blur' }]
}

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 关闭弹窗事件
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate()
  if (valid) {
    emit('submit', formData.value)
    emit('update:visible', false)
  }
}

// 获取角色信息（编辑模式）
const getRole = async () => {
  if (!props.isAdd) {
    const res = await getRoleApi(props.roleId)
    formData.value = res
  }
}

// 排序表单输入校验
const handleSortInput = (value: string) => {
  // 替换掉所有非数字的字符，只保留数字
  const numericValue = value.replace(/\D/g, '')
  // 如果有输入，且转成了数字
  if (numericValue) {
    formData.value.sortOrder = parseInt(numericValue, 10)
  } else {
    formData.value.sortOrder = null
  }
}

onMounted(() => {
  getRole()
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