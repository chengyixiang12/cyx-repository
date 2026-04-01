<template>
  <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
    <el-form-item label="密码" prop="password">
      <el-input type="password" v-model="formData.password" show-password />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input type="password" v-model="formData.confirmPassword" show-password />
    </el-form-item>
    <el-form-item>
      <div style="display: flex; justify-content: space-between; width: 100%;">
        <el-button @click="emit('prev')">上一步</el-button>
        <el-button type="primary" @click="handleNext">下一步</el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import type { RegisterRequest } from '@/types/register'
import type { FormInstance, FormRules } from 'element-plus'
import { computed, ref } from 'vue'
import { showMessage } from '@/utils/message'

const props = defineProps<{ modelValue: RegisterRequest }>()
const emit = defineEmits<{
  (e: 'update:modelValue', val: RegisterRequest): void
  (e: 'prev'): void
  (e: 'next'): void
}>()

const formRef = ref<FormInstance>()

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== formData.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const formData = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleNext = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      emit('next')
    }
  } catch (error) {
    // 校验失败时不执行下一步
  }
}
</script>
