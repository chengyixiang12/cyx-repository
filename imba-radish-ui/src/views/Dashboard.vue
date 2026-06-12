<template>
  <div class="dashboard-container">
    <!-- 上部欢迎卡片 -->
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
        <el-card shadow="hover" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
              <el-button type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <div v-if="announcements.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <Bell />
            </el-icon>
            <p>暂无公告</p>
          </div>
          <div v-else class="content-list">
            <div v-for="item in announcements" :key="item.id" class="content-item"
              @click="showAnnouncementDetail(item)">
              <div class="content-title">{{ item.title }}</div>
              <div class="content-time">{{ formatTime(item.publishTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待办事项 -->
      <el-col :span="12">
        <el-card shadow="hover" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>我的待办</span>
              <el-button type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <div v-if="todos.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <CircleCheck />
            </el-icon>
            <p>暂无待办</p>
          </div>
          <div v-else class="content-list">
            <div v-for="item in todos" :key="item.id" class="content-item" @click="showTodoDetail(item)">
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

    <!-- 公告详情对话框 -->
    <el-dialog title="公告详情" :visible.sync="announcementDetailVisible" width="500px">
      <div class="detail-info">
        <h3>{{ currentAnnouncement.title }}</h3>
        <div class="detail-time">发布时间：{{ formatTime(currentAnnouncement.publishTime) }}</div>
        <div class="detail-content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>

    <!-- 待办详情对话框 -->
    <el-dialog title="待办详情" :visible.sync="todoDetailVisible" width="500px">
      <div class="detail-info">
        <div class="detail-header">
          <h3>{{ currentTodo.title }}</h3>
          <el-tag :type="currentTodo.status === '已完成' ? 'success' : 'warning'">
            {{ currentTodo.status }}
          </el-tag>
        </div>
        <div class="detail-time">截止时间：{{ formatTime(currentTodo.deadline) }}</div>
        <div class="detail-content">{{ currentTodo.description }}</div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="todoDetailVisible = false">关闭</el-button>
        <el-button type="primary" v-if="currentTodo.status !== '已完成'" @click="markTodoComplete">
          标记完成
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { UserInfoVo } from '@/types/login';
import { computed, onMounted, ref, reactive } from 'vue'
import dayjs from 'dayjs'
import { User, Files, Bell, Clock } from '@element-plus/icons-vue'
import { showMessage } from '@/utils/message';

// 用户信息
const nickname = ref('');

// 加载状态
const loading = ref(false);

// 日期显示
const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

// 统计数据
const stats = reactive({
  totalUsers: 128,
  totalFiles: 567,
  totalAnnouncements: 3,
  pendingTodos: 2
});

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
  try {
    const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}');
    nickname.value = userInfo.nickname ?? '用户';
  } catch (error) {
    console.error('获取用户信息失败:', error);
    nickname.value = '用户';
  }
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

const markTodoComplete = () => {
  const todo = todos.value.find(t => t.id === currentTodo.value.id);
  if (todo) {
    todo.status = '已完成';
    currentTodo.value.status = '已完成';
    stats.pendingTodos = todos.value.filter(t => t.status !== '已完成').length;
    showMessage('已标记为完成', 'success');
  }
}

// 模拟加载数据
const loadData = async () => {
  loading.value = true;
  try {
    // 这里可以替换为真实的 API 调用
    // await fetchAnnouncements();
    // await fetchTodos();
    // await fetchStats();
    await new Promise(resolve => setTimeout(resolve, 500));
  } catch (error) {
    console.error('加载数据失败:', error);
    showMessage('加载数据失败', 'error');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  getNickname();
  loadData();
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 120px);
}

/* 统计卡片区域 */
.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: #fff;
}

.stat-icon.bg-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.bg-green {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-icon.bg-purple {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
}

.stat-icon.bg-orange {
  background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* 上部欢迎卡片样式 */
.welcome-card {
  margin-bottom: 16px;
  padding: 16px 0;
  min-height: 120px;
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

/* 内容区域 */
.content-row {
  flex: 1;
  min-height: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  padding: 8px 0;
}

.content-list {
  padding: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.content-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.content-item:hover {
  background-color: #f5f7fa;
  padding-left: 8px;
}

.content-title {
  font-size: 14px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.content-time {
  font-size: 12px;
  color: #909399;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
}

.empty-icon {
  margin-bottom: 12px;
  color: #c0c4cc;
}

/* 详情对话框样式 */
.detail-info {
  padding: 8px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-header h3 {
  margin: 0;
  font-size: 16px;
}

.detail-time {
  color: #909399;
  margin-bottom: 16px;
  font-size: 14px;
}

.detail-content {
  line-height: 1.8;
  white-space: pre-line;
  color: #606266;
}

.dialog-footer {
  text-align: right;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .stats-row {
    margin-bottom: 12px;
  }

  .stat-card {
    padding: 12px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
    margin-right: 12px;
  }

  .stat-value {
    font-size: 20px;
  }

  .welcome-card {
    min-height: 100px;
  }

  .welcome-message h2 {
    font-size: 16px;
  }

  .content-list {
    max-height: 250px;
  }
}
</style>
