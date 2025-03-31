<template>
  <div class="dashboard-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <div class="welcome-message">
        <h1>欢迎回来，{{ nickname }}！</h1>
        <p>您已成功登录萝卜系统</p>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { UserInfoVo } from '@/types/login';
import { computed, onMounted, ref } from 'vue'

const nickname = ref('');

const getNickname = () => {
  const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}');
  nickname.value = userInfo.nickname;
}


onMounted(() => {
  getNickname();
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-message {
  text-align: center;
  padding: 40px 0;
}

.welcome-message h1 {
  margin-bottom: 20px;
  color: #304156;
}
</style>