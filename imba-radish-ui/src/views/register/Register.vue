<template>
  <div class="register-container">
    <div class="register-card">
      <!-- 注册标题 -->
      <div class="register-header">
        <h2>创建账号</h2>
        <p>填写信息，几步完成注册</p>
      </div>

      <el-steps :active="activeStep" finish-status="success" simple class="register-steps">
        <el-step title="用户信息" />
        <el-step title="设置密码" />
        <el-step title="验证邮箱" />
        <el-step title="完成注册" />
      </el-steps>

      <div class="form-body">
        <component :is="currentStepComponent" v-model="formData" @next="handleNext" @prev="handlePrev"
            @done="handleDone" />
      </div>

      <div class="bottom-actions" v-if="activeStep === 3">
        <el-button type="primary" size="large" round class="goto-login-btn" @click="goToLogin">前往登录</el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import StepUserInfo from './steps/StepUserInfo.vue'
import StepPassword from './steps/StepPassword.vue'
import StepVerify from './steps/StepVerify.vue'
import StepSuccess from './steps/StepSuccess.vue'
import type { RegisterRequest } from '@/types/register'

const router = useRouter()
const activeStep = ref(0)

const formData = ref<RegisterRequest>({
    username: '',
    nickname: '',
    email: '',
    password: '',
    confirmPassword: '',
    verificationCode: ''
})

const currentStepComponent = computed(() => {
    return [StepUserInfo, StepPassword, StepVerify, StepSuccess][activeStep.value]
})

const handleNext = () => {
    if (activeStep.value < 3) activeStep.value++
}

const handlePrev = () => {
    if (activeStep.value > 0) activeStep.value--
}

const handleDone = () => {
    activeStep.value = 3
}

const goToLogin = () => {
    router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #EDF1FE 0%, #E4E9FD 45%, #F3F0FE 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
}

.register-header h2 {
  margin: 10px 0;
  color: #303133;
  font-size: 24px;
}

.register-header p {
  color: #909399;
  font-size: 14px;
}

.register-steps {
  margin-bottom: 24px;
}

.register-steps :deep(.el-step__title) {
  font-size: 13px;
}

.form-body {
  min-height: 230px;
}

.register-card :deep(.el-form-item) {
  margin-bottom: 20px;
}

.bottom-actions {
  margin-top: 10px;
}

.goto-login-btn {
  width: 100%;
}
</style>
