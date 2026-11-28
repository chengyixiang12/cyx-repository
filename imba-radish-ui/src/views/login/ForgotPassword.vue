<template>
  <div class="forgot-container">
    <div class="forgot-card">
      <div class="forgot-header">
        <h2>重置密码</h2>
        <p>通过注册邮箱验证身份</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleSubmit">
        <!-- 第一步：验证邮箱 -->
        <template v-if="!emailVerified">
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="请输入注册邮箱" prefix-icon="Message" size="large" />
          </el-form-item>

          <el-form-item prop="verificationCode">
            <div class="captcha-wrapper">
              <el-input v-model="form.verificationCode" placeholder="请输入邮箱验证码" prefix-icon="Key" size="large" />
              <el-button @click="sendCaptcha" :disabled="countdown > 0" size="large">
                {{ countdown > 0 ? `${countdown}s 后重试` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="verifying" size="large" round class="submit-btn">
              {{ verifying ? '验证中...' : '下一步' }}
            </el-button>
          </el-form-item>
        </template>

        <!-- 第二步：设置新密码 -->
        <template v-else>
          <el-alert type="success" :closable="false" class="verified-tip" :title="`邮箱 ${form.email} 验证通过，请设置新密码`" />

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入新密码（6-20位）" prefix-icon="Lock" size="large" show-password />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" prefix-icon="Lock" size="large" show-password />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="submitting" size="large" round class="submit-btn">
              {{ submitting ? '提交中...' : '重置密码' }}
            </el-button>
          </el-form-item>
        </template>

        <div class="back-link">
          <el-link type="primary" @click="router.push('/login')">
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onUnmounted } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { sendForgotPasswordCaptchaApi, forgotPasswordApi } from '@/api/login';
import { getPublicKeyApi } from '@/api/auth';
import { RSAUtil } from '@/utils/rsa';
import router from '@/router/routers';

const formRef = ref<FormInstance>();
const emailVerified = ref(false);
const verifying = ref(false);
const submitting = ref(false);
const countdown = ref(0);
let timer: number | null = null;

const form = reactive({
  email: '',
  verificationCode: '',
  password: '',
  confirmPassword: ''
});

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请再次输入新密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const rules = computed<FormRules>(() => {
  const base: FormRules = {
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
    ],
    verificationCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
  };
  if (emailVerified.value) {
    base.password = [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
    ];
    base.confirmPassword = [
      { required: true, message: '请再次输入新密码', trigger: 'blur' },
      { validator: validateConfirmPassword, trigger: 'blur' }
    ];
  }
  return base;
});

const startCountdown = (seconds: number = 60) => {
  countdown.value = seconds;
  if (timer) clearInterval(timer);
  timer = window.setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(timer!);
      timer = null;
    }
  }, 1000);
};

const sendCaptcha = async () => {
  if (!form.email) {
    ElMessage.warning('请先输入邮箱地址');
    return;
  }
  try {
    const msg = await sendForgotPasswordCaptchaApi(form.email);
    startCountdown();
    ElMessage.success(msg || '验证码已发送，请留意您的邮箱');
  } catch (error) {
    console.error('发送验证码失败:', error);
  }
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  if (!emailVerified.value) {
    // 第一步只校验邮箱和验证码格式，真实验证放到重置接口（验证码一次性）
    emailVerified.value = true;
    return;
  }

  try {
    submitting.value = true;
    const publicKey = await getPublicKeyApi();
    const encrypted = RSAUtil.encrypt(form.password, publicKey);
    const msg = await forgotPasswordApi(form.email, form.verificationCode, encrypted);
    ElMessage.success(msg || '密码重置成功，请使用新密码登录');
    router.push('/login');
  } catch (error: any) {
    console.error('重置密码失败:', error);
    // 验证码错误/过期时退回第一步，让用户重新输入
    if (error?.message?.includes('验证码')) {
      emailVerified.value = false;
    }
  } finally {
    submitting.value = false;
  }
};

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.forgot-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, var(--radish-primary-light-9) 0%, var(--radish-primary-light-8) 45%, var(--radish-deco-purple) 100%);
  padding: 20px;
}

.forgot-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: white;
  border-radius: var(--radish-radius-xl);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.forgot-card .el-form-item {
  margin-bottom: 20px;
}

.forgot-header {
  text-align: center;
  margin-bottom: 30px;
}

.forgot-header h2 {
  margin: 10px 0;
  color: var(--radish-text-primary);
  font-size: var(--radish-font-2xl);
}

.forgot-header p {
  color: var(--radish-text-secondary);
  font-size: var(--radish-font-base);
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
}

.captcha-wrapper .el-button {
  flex-shrink: 0;
  width: 130px;
}

.verified-tip {
  margin-bottom: 20px;
}

.submit-btn {
  width: 100%;
  margin-top: 6px;
}

.back-link {
  margin-top: 10px;
  text-align: center;
}
</style>
