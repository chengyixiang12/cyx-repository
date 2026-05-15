<template>
  <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
    <el-form-item label="验证码" prop="verificationCode">
      <el-input v-model="formData.verificationCode" placeholder="输入验证码">
        <template #append>
          <el-button @click="sendCode">{{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}</el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <div style="display: flex; justify-content: space-between; width: 100%;">
        <el-button @click="emit('prev')">上一步</el-button>
        <el-button type="primary" @click="complete">完成注册</el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import type { RegisterRequest } from '@/types/register'
import { computed, ref, onUnmounted } from 'vue'
import { registerApi, sendRegistCaptchaApi } from '@/api/register'
import type { FormInstance, FormRules } from 'element-plus'
import { RSAUtil } from '@/utils/rsa'
import { getPublicKeyApi } from '@/api/auth'

const props = defineProps<{ modelValue: RegisterRequest }>()
const emit = defineEmits<{
  (e: 'update:modelValue', val: RegisterRequest): void
  (e: 'prev'): void
  (e: 'next'): void
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const countdown = ref(0)
let timer: number | null = null

const rules: FormRules = {
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

const startCountdown = (seconds: number = 300) => {
  countdown.value = seconds
  if (timer) clearInterval(timer)
  timer = window.setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      if (timer) clearInterval(timer)
      timer = null
    }
  }, 1000)
}

const formData = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const sendCode = async () => {
  await sendRegistCaptchaApi(formData.value.email)
  startCountdown()
}

const complete = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (valid) {
      const publicKey = await getPublicKeyApi()
      formData.value.password = RSAUtil.encrypt(formData.value.password, publicKey)
      await registerApi(formData.value)
      emit('next')
    }
  } catch (error) {
    console.log(error)
  } finally {
    submitting.value = false
  }
}

// 卸载页面时，清理定时器
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.el-form-item {
  margin-bottom: 20px;
}
</style>
