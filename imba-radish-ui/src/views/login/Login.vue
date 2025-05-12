<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 登录方式 Tabs 切换 -->
      <el-tabs v-model="currentLoginType" stretch class="login-tabs">
        <el-tab-pane label="密码登录" name="password" />
        <el-tab-pane label="邮箱验证码登录" name="email" />
      </el-tabs>

      <!-- 登录标题 -->
      <div class="login-header">
        <h2>欢迎登录</h2>
        <p>请选择登录方式并输入信息</p>
      </div>

      <!-- 登录表单 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" @submit.prevent="handleLogin">
        <!-- 密码登录表单 -->
        <template v-if="currentLoginType === 'password'">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="用户名/手机号" prefix-icon="User" size="large" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
          </el-form-item>

          <el-form-item prop="graphicsCaptcha">
            <div class="captcha-wrapper">
              <el-input v-model="loginForm.graphicsCaptcha" placeholder="验证码" prefix-icon="Picture" size="large" />
              <div class="captcha-img" @click="refreshCaptcha">
                <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" />
                <span v-else class="captcha-loading">加载中...</span>
              </div>
            </div>
          </el-form-item>
        </template>

        <!-- 邮箱验证码登录表单 -->
        <template v-else>
          <el-form-item prop="email">
            <el-input v-model="loginForm.email" placeholder="请输入邮箱" prefix-icon="Message" size="large" />
          </el-form-item>

          <el-form-item prop="emailCaptcha">
            <div class="captcha-wrapper">
              <el-input v-model="loginForm.emailCaptcha" placeholder="请输入验证码" prefix-icon="Key" size="large" />
              <el-button @click="sendEmailCaptcha" :disabled="countdown > 0" size="large">
                {{ countdown > 0 ? `${countdown}s 后重试` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </template>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" size="large" round class="login-btn">
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>


<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { FormInstance, FormRules } from 'element-plus';
import { getGraphicCaptcha, login, getUserInfo, sendCaptchaApi } from '@/api/login';
import { getPublicKey } from '@/api/auth';
import { RSAUtil } from '@/utils/rsa';
import { showMessage } from '@/utils/message';
import router from '@/router/routers';
import { LoginRequest } from '@/types/login';

const currentLoginType = ref<'password' | 'email'>('password');
const loginFormRef = ref<FormInstance>();
const loading = ref(false);
const captchaUrl = ref('');
const countdown = ref(0);
let timer: number;

const loginForm = ref({
  username: '',
  password: '',
  graphicsCaptcha: '',
  email: '',
  emailCaptcha: '',
  loginMethod: 'password',
  uuid: ''
});

const loginRules = computed<FormRules>(() => {
  return currentLoginType.value === 'password'
    ? {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        graphicsCaptcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    : {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        emailCaptcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      };
});

const refreshCaptcha = async () => {
  try {
    const { blob, uuid } = await getGraphicCaptcha(loginForm.value.uuid);
    loginForm.value.uuid = uuid;
    captchaUrl.value = URL.createObjectURL(blob);
  } catch (error) {
    console.error('获取验证码失败:', error);
  }
};

const sendEmailCaptcha = async () => {
  if (!loginForm.value.email) {
    showMessage('请先输入邮箱地址', 'warning');
    return;
  }
  try {
    await sendCaptchaApi(loginForm.value.email);
    showMessage('验证码已发送，请查收邮箱', 'success');
    countdown.value = 60;
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) clearInterval(timer);
    }, 1000);
  } catch {
    showMessage('发送验证码失败', 'error');
  }
};

const handleLogin = async () => {
  try {
    const valid = await loginFormRef.value?.validate();
    if (!valid) return;

    loading.value = true;
    const loginParam: LoginRequest = {
      loginMethod: currentLoginType.value
    };

    if (currentLoginType.value === 'password') {
      const publicKey = await getPublicKey(0);
      loginParam.username = loginForm.value.username;
      loginParam.password = RSAUtil.encrypt(loginForm.value.password, publicKey);
      loginParam.graphicsCaptcha = loginForm.value.graphicsCaptcha;
      loginParam.uuid = loginForm.value.uuid;
    } else {
      loginParam.email = loginForm.value.email;
      loginParam.emailCaptcha = loginForm.value.emailCaptcha;
    }

    const data = await login(loginParam);
    sessionStorage.setItem('Authorization', data.token);
    const userInfo = await getUserInfo();
    sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
    await router.push('/dashboard');
    showMessage('登录成功', 'success');
  } catch (error) {
    if (currentLoginType.value === 'password') refreshCaptcha();
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  if (currentLoginType.value === 'password') refreshCaptcha();
});
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.login-tabs {
  margin-bottom: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 10px 0;
  color: #303133;
  font-size: 24px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-img {
  width: 150px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-loading {
  color: #909399;
  font-size: 12px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}
</style>