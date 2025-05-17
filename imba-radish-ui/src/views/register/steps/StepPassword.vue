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

const rules: FormRules = {
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }]
}

const formData = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleNext = async () => {
  const valid = await formRef.value?.validate()
  if (valid) {
    if (formData.value.password === formData.value.confirmPassword) {
      emit('next')
    } else {
      showMessage('两次密码输入不一致', 'info')
    }
  }
}
</script>
