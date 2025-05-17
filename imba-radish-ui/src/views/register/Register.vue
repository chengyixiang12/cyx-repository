<template>
    <div class="register-container">
        <el-card class="register-card">
            <el-steps :active="activeStep" finish-status="success" simple>
                <el-step title="用户信息" />
                <el-step title="设置密码" />
                <el-step title="验证邮箱" />
                <el-step title="完成注册" />
            </el-steps>

            <div class="form-body">
                <component :is="currentStepComponent" v-model="formData" @next="handleNext" @prev="handlePrev"
                    @done="handleDone" />
            </div>

            <div class="bottom-text" v-if="activeStep === 3">
                <el-button type="primary" @click="goToLogin">前往登录</el-button>
            </div>
        </el-card>
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
    padding: 40px;
}

.register-card {
    width: 700px;
    padding: 20px;
}

.form-body {
    margin-top: 20px;
}

.bottom-text {
    margin-top: 30px;
    text-align: center;
}
</style>