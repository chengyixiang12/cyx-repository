<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <!-- <img src="@/assets/logo.png" alt="Logo" class="logo"> -->
        <h2>欢迎登录</h2>
        <p>请输入您的账号和密码</p>
      </div>

      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" @submit.prevent="handleLogin">
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名/手机号" prefix-icon="User" size="large" @input="forceUpdate" />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password prefix-icon="Lock"
            size="large" @input="forceUpdate" />
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item prop="graphicsCaptcha">
          <div class="captcha-wrapper">
            <el-input v-model="loginForm.graphicsCaptcha" placeholder="请输入验证码" prefix-icon="Picture" size="large" @input="forceUpdate" />
            <div class="captcha-img" @click="refreshCaptcha">
              <img v-if="captchaUrl" :src="captchaUrl" alt="验证码">
              <span v-else class="captcha-loading">加载中...</span>
            </div>
          </div>
        </el-form-item>

        <!-- 记住我 & 忘记密码 -->
        <!-- <div class="login-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-link type="primary" @click="showForgetDialog">忘记密码?</el-link>
        </div> -->

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" size="large" round class="login-btn">
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 其他登录方式 -->
      <!-- <div class="other-login">
        <el-divider>其他登录方式</el-divider>
        <div class="login-methods">
          <el-tooltip content="手机验证码登录">
            <el-button circle @click="switchToSMS">
              <el-icon><Message /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="微信登录">
            <el-button circle>
              <el-icon><Iphone /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </div>-->
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { FormInstance, FormRules } from 'element-plus';
import { getGraphicCaptcha, login, getUserInfo } from '@/api/login';
import { RSAUtil } from '@/utils/rsa';
import { getPublicKey } from '@/api/auth';
import router from '@/router/routers';
import { fetchMenuList } from '@/api/dashboard';

const loginForm = ref({
  username: '',
  password: '',
  graphicsCaptcha: '',
  loginMethod: 'password', // 默认密码登录
  uuid: ''
})

// const rememberMe = ref(false)
const loading = ref(false);
const captchaUrl = ref('');
const loginFormRef = ref<FormInstance>();

// 验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    // { min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    // { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ],
  graphicsCaptcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    // { len: 4, message: '验证码为4位字符', trigger: 'blur' }
  ]
}

const forceUpdate = () => {
  // 强制更新视图
  loginForm.value = {...loginForm.value};
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const { blob, uuid } = await getGraphicCaptcha(loginForm.value.uuid);
    loginForm.value.uuid = uuid;
    captchaUrl.value = URL.createObjectURL(blob);
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

// 登录提交
const handleLogin = async () => {
  try {
    const valid = await loginFormRef.value?.validate();
    if (!valid) return // 验证不通过时停止执行
    
    loading.value = true

    const publicKey: string = await getPublicKey(0);
    // 加密密码
    loginForm.value.password = RSAUtil.encrypt(loginForm.value.password, publicKey);
    // 登录
    const data = await login({
      ...loginForm.value,
    });

    // 存储token
    sessionStorage.setItem('Authorization', data.token);

    // 查询登录用户信息
    const userInfo = await getUserInfo();

    // 存储用户信息
    sessionStorage.setItem('userInfo', JSON.stringify(userInfo))

    // 跳转首页
    router.push('/dashboard')

  } catch (error: any) {
    console.error('登录失败:', error)
    if (error.message?.includes('验证码')) {
      refreshCaptcha();
      loginForm.value.graphicsCaptcha = '';
      loginForm.value.password = ''
    }
  } finally {
    loading.value = false
  }
}

// 初始化加载验证码
onMounted(refreshCaptcha)
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

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
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

.login-options {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.other-login {
  margin-top: 30px;
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.el-divider__text {
  background-color: white;
  padding: 0 10px;
  color: #909399;
}
</style>