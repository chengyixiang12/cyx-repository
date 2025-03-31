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
    router.push('/')
  }
  </script>
  
  <style scoped>
  .not-found-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f7fa;
  }
  
  .not-found-content {
    text-align: center;
    padding: 40px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .error-code {
    font-size: 120px;
    font-weight: bold;
    color: #f56c6c;
    line-height: 1;
    margin-bottom: 20px;
  }
  
  .error-message {
    font-size: 24px;
    color: #303133;
    margin-bottom: 16px;
  }
  
  .error-desc {
    font-size: 16px;
    color: #909399;
    margin-bottom: 24px;
  }
  
  .back-btn {
    padding: 12px 36px;
    font-size: 16px;
  }
  </style>