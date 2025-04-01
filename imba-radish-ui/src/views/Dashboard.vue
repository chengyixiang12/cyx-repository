<template>
  <div class="dashboard-container">
    <!-- 上部欢迎卡片 - 高度缩小 -->
    <el-card shadow="hover" class="welcome-card">
      <div class="welcome-message">
        <h2>欢迎回来，{{ nickname }}！</h2>
        <p class="welcome-subtext">您已成功登录萝卜系统 · 今天是{{ currentDate }}</p>
      </div>
    </el-card>

    <!-- 下部内容区域 -->
    <el-row :gutter="20" class="content-row">
      <!-- 公告板 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <div class="content-list">
            <div 
              v-for="item in announcements" 
              :key="item.id" 
              class="content-item"
              @click="showAnnouncementDetail(item)"
            >
              <div class="content-title">{{ item.title }}</div>
              <div class="content-time">{{ formatTime(item.publishTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待办事项 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>我的待办</span>
            </div>
          </template>
          <div class="content-list">
            <div 
              v-for="item in todos" 
              :key="item.id" 
              class="content-item"
              @click="showTodoDetail(item)"
            >
              <div class="content-title">
                <el-tag :type="item.status === '已完成' ? 'success' : 'warning'" size="small">
                  {{ item.status }}
                </el-tag>
                {{ item.title }}
              </div>
              <div class="content-time">截止: {{ formatTime(item.deadline) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 对话框部分保持不变 -->
    <!-- ... -->
  </div>
</template>

<script lang="ts" setup>
import { UserInfoVo } from '@/types/login';
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'

// 用户信息
const nickname = ref('');

// 日期显示
const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

// 公告相关
const announcements = ref([
  { id: 1, title: '系统维护通知', content: '系统将于本周五凌晨2:00-4:00进行维护升级，请提前保存好您的工作内容。升级期间系统将无法访问，给您带来的不便敬请谅解。', publishTime: '2023-05-10 09:00:00' },
  { id: 2, title: '新功能上线', content: '新增数据导出功能，支持Excel和PDF格式导出，欢迎使用并提供宝贵意见。', publishTime: '2023-05-08 14:30:00' },
  { id: 3, title: '五一假期安排', content: '五一假期期间客服服务时间调整为9:00-18:00，紧急问题请联系值班电话：13800138000。', publishTime: '2023-04-28 16:00:00' }
])

const announcementDetailVisible = ref(false)
const currentAnnouncement = ref({
  id: 0,
  title: '',
  content: '',
  publishTime: ''
})

// 待办事项相关
const todos = ref([
  { id: 1, title: '完成项目报告', deadline: '2023-05-15 18:00:00', status: '未完成', description: '需要完成季度项目总结报告，包括项目进度、问题和下一步计划' },
  { id: 2, title: '参加团队会议', deadline: '2023-05-12 10:00:00', status: '已完成', description: '每周团队例会，需要准备上周工作汇报和本周计划' },
  { id: 3, title: '提交周工作计划', deadline: '2023-05-08 17:00:00', status: '未完成', description: '向主管提交下周工作计划，包括主要任务和时间安排' }
])

const todoDetailVisible = ref(false)
const currentTodo = ref({
  id: 0,
  title: '',
  deadline: '',
  status: '',
  description: ''
})

// 方法
const getNickname = () => {
  const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}');
  nickname.value = userInfo.nickname;
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const showAnnouncementDetail = (item: any) => {
  currentAnnouncement.value = { ...item }
  announcementDetailVisible.value = true
}

const showTodoDetail = (item: any) => {
  currentTodo.value = { ...item }
  todoDetailVisible.value = true
}

onMounted(() => {
  getNickname();
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 上部欢迎卡片样式调整 */
.welcome-card {
  margin-bottom: 16px;
  padding: 16px 0;
}

.welcome-message {
  text-align: center;
  padding: 8px 0;
}

.welcome-message h2 {
  margin-bottom: 8px;
  color: #304156;
  font-size: 20px;
}

.welcome-subtext {
  color: #909399;
  font-size: 14px;
}

/* 内容区域调整 */
.content-row {
  flex: 1;
  min-height: 0; /* 防止内容溢出 */
}

.card-header {
  font-weight: 500;
  padding: 8px 16px;
}

.content-list {
  padding: 8px;
  height: calc(100% - 56px); /* 减去标题栏高度 */
  overflow-y: auto;
}

.content-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.content-item:hover {
  background-color: #f5f7fa;
}

.content-title {
  font-size: 14px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.content-time {
  font-size: 12px;
  color: #909399;
}

.detail-time {
  color: #909399;
  margin-bottom: 16px;
  font-size: 14px;
}

.detail-content {
  line-height: 1.6;
  white-space: pre-line;
}

.detail-info p {
  margin-bottom: 12px;
}
</style>