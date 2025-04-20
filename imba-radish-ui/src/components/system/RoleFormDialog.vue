<template>
  <el-dialog :title="isAdd ? '新增角色' : '编辑角色'" v-model="visible" width="700px" :close-on-click-modal="false"
    :before-close="handleClose" class="custom-dialog">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="right"
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
          <el-input v-model="formData.sortOrder" placeholder="请填写序号" />
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
import { ref, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getRoleApi } from '@/api/role'
import { SysRoleVo } from '@/types/role'

const props = defineProps({
  visible: { type: Boolean, required: true },
  isAdd: { type: Boolean, default: false, required: true },
  roleId: { type: Number, default: null }
})

const emit = defineEmits(['update:visible', 'submit'])

const visible = ref(props.visible)
const formRef = ref<FormInstance>()
const formData = ref<SysRoleVo>({
  id: 0,
  name: null,
  sortOrder: null,
  code: null,
  description: null,
  status: null,
  isDefault: null,
  fixRole: null,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择角色状态', trigger: 'blur' }]
}

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

onMounted(() => {
  getRole()
})
</script>

<style scoped>
.custom-dialog {
  display: flex;
  flex-direction: column;
  max-height: 80vh;
  margin-top: 5vh;
  overflow: hidden;
}

.dialog-body-wrapper {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.scrollable-form {
  max-height: 50vh;
  overflow-y: auto;
  padding: 25px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>