<template>
  <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="formData.username" />
    </el-form-item>
    <el-form-item label="昵称" prop="nickname">
      <el-input v-model="formData.nickname" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="formData.email" />
    </el-form-item>
    <el-form-item>
      <div style="display: flex; justify-content: space-between; width: 100%;">
        <div class="register-link">
          <el-link type="primary" @click="goToLogin">已有帐号？前往登录</el-link>
        </div>
        <el-button type="primary" @click="handleNext">下一步</el-button>
      </div>
    </el-form-item>
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
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
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
.register-link {
  margin-top: 10px;
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>
