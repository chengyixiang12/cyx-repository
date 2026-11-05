<template>
  <el-form :model="formData" :rules="rules" ref="formRef">
    <el-form-item prop="username">
      <el-input v-model="formData.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
    </el-form-item>
    <el-form-item prop="nickname">
      <el-input v-model="formData.nickname" placeholder="请输入昵称（选填）" prefix-icon="UserFilled" size="large" />
    </el-form-item>
    <el-form-item prop="email">
      <el-input v-model="formData.email" placeholder="请输入邮箱" prefix-icon="Message" size="large" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="large" round class="next-btn" @click="handleNext">下一步</el-button>
    </el-form-item>
    <div class="login-link">
      <el-link type="primary" @click="goToLogin">已有帐号？前往登录</el-link>
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import type { RegisterRequest } from '@/types/register'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps<{ modelValue: RegisterRequest }>()
const emit = defineEmits<{
  (e: 'update:modelValue', val: RegisterRequest): void
  (e: 'next'): void
}>()

const formData = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formRef = ref<FormInstance>()

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const handleNext = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      emit('next')
    }
  } catch (error) {}
}

const router = useRouter()
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.next-btn {
  width: 100%;
  margin-top: 10px;
}

.login-link {
  margin-top: 10px;
  text-align: center;
  font-size: 14px;
}
</style>
