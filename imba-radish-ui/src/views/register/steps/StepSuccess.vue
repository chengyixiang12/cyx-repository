<template>
  <div class="success-container">
    <el-result 
      icon="success" 
      title="注册成功" 
      :sub-title="subTitle"
    >
      <template #extra>
        <div class="success-extra">
          <p class="user-info-tip">
            欢迎您，{{ displayName }}！
          </p>
          <div class="action-buttons">
            <el-button type="primary" @click="goToLogin" size="large">
              立即登录
            </el-button>
            <el-button @click="goToHome" size="large">
              返回首页
            </el-button>
          </div>
          <div class="countdown-tip" v-if="countdown > 0">
            {{ countdown }} 秒后自动跳转登录页
          </div>
        </div>
      </template>
    </el-result>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import type { RegisterRequest } from '@/types/register'

const props = defineProps<{ 
  modelValue?: RegisterRequest 
}>()

const router = useRouter()
const countdown = ref(5)
let timer: number | null = null

// 使用 computed 处理显示名称，避免模板中的类型问题
const displayName = computed(() => {
  return props.modelValue?.nickname || props.modelValue?.username || '新用户'
})

const subTitle = computed(() => {
  const email = props.modelValue?.email
  if (email) {
    return `恭喜您，账号已创建完成！验证邮件已发送至 ${email}，请注意查收。`
  }
  return '恭喜您，账号已创建完成！'
})

const goToLogin = () => {
  if (timer) clearInterval(timer)
  router.push('/login')
}

const goToHome = () => {
  if (timer) clearInterval(timer)
  router.push('/')
}

const startAutoRedirect = () => {
  timer = window.setInterval(() => {
    if (countdown.value > 1) {
      countdown.value--
    } else {
      if (timer) clearInterval(timer)
      goToLogin()
    }
  }, 1000)
}

onMounted(() => {
  startAutoRedirect()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.success-container {
  text-align: center;
  padding: 20px;
}

.success-extra {
  margin-top: 20px;
}

.user-info-tip {
  font-size: 16px;
  color: #67c23a;
  margin: 20px 0;
  padding: 10px;
  background-color: #f0f9eb;
  border-radius: 8px;
  display: inline-block;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin: 24px 0 16px;
}

.countdown-tip {
  font-size: 14px;
  color: #909399;
  margin-top: 16px;
}

.success-container :deep(.el-result__icon) {
  animation: bounceIn 0.6s ease;
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>