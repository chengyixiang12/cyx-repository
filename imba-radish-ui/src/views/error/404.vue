<template>
    <div class="not-found-container">
      <div class="not-found-content">
        <div class="error-code">404</div>
        <div class="error-message">页面不存在或已被移除</div>
        <div class="error-desc">抱歉，您访问的页面不存在或已被删除</div>
        <el-button 
          type="primary" 
          round 
          @click="goHome"
          class="back-btn"
        >
          返回首页
        </el-button>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  
  const goHome = () => {
    router.push('/dashboard')
  }
  </script>
  
  <style scoped>
  .not-found-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: var(--radish-fill);
  }
  
  .not-found-content {
    text-align: center;
    padding: 40px;
    background: white;
    border-radius: var(--radish-radius-xl);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .error-code {
    /* 404 巨型数字, 装饰性尺寸 */
  /* stylelint-disable-next-line declaration-property-value-disallowed-list */
  font-size: 120px;
    font-weight: bold;
    color: var(--radish-danger);
    line-height: 1;
    margin-bottom: 20px;
  }
  
  .error-message {
    font-size: var(--radish-font-2xl);
    color: var(--radish-text-primary);
    margin-bottom: 16px;
  }
  
  .error-desc {
    font-size: var(--radish-font-lg);
    color: var(--radish-text-secondary);
    margin-bottom: 24px;
  }
  
  .back-btn {
    padding: 12px 36px;
    font-size: var(--radish-font-lg);
  }
  </style>