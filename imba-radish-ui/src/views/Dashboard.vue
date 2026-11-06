<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片：渐变背景 + 头像 + 时段问候 -->
    <div class="welcome-card">
      <div class="welcome-left">
        <el-avatar :size="56" :src="avatarUrl" class="welcome-avatar">
          {{ nickname.charAt(0) }}
        </el-avatar>
        <div class="welcome-text">
          <h2>{{ greeting }}，{{ nickname }}！</h2>
          <p>今天是 {{ currentDate }}，祝您工作顺利</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="welcome-date">
          <div class="date-day">{{ nowDay }}</div>
          <div class="date-detail">{{ nowMonth }}<br />{{ nowWeek }}</div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div v-for="card in statCards" :key="card.label" class="stat-card">
        <div class="stat-icon" :class="card.bgClass">
          <el-icon :size="24">
            <component :is="card.icon" />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ card.value }}<span v-if="card.unit" class="stat-unit">{{ card.unit }}</span></div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- 迷你趋势图 + 最近登录 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="14">
        <el-card shadow="hover" class="fill-card">
          <template #header>
            <div class="card-header">
              <span>请求趋势</span>
              <span class="card-hint">最近 1 小时 QPS</span>
            </div>
          </template>
          <div ref="trendChartRef" class="trend-chart"></div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="hover" class="fill-card">
          <template #header>
            <div class="card-header">
              <span>最近登录</span>
            </div>
          </template>
          <div v-if="recentLogins.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <Clock />
            </el-icon>
            <p>暂无登录记录</p>
          </div>
          <div v-else class="content-list">
            <div v-for="(item, index) in recentLogins" :key="index" class="login-item">
              <div class="login-main">
                <div class="login-device">{{ item.osBrowserInfo || '未知设备' }}</div>
                <div class="login-ip">{{ item.ipAddress || '未知 IP' }}</div>
              </div>
              <div class="login-time">{{ formatTime(item.loginTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告 + 待办 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
              <el-button type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <ListSkeleton :loading="loading" :count="announcements.length" />
          <div v-if="!loading && announcements.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <Bell />
            </el-icon>
            <p>暂无公告</p>
          </div>
          <div v-else-if="!loading" class="content-list">
            <div v-for="item in announcements" :key="item.id" class="content-item"
              @click="showAnnouncementDetail(item)">
              <div class="content-title">{{ item.title }}</div>
              <div class="content-time">{{ formatTime(item.publishTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>我的待办</span>
              <el-button type="primary" size="small">查看全部</el-button>
            </div>
          </template>
          <ListSkeleton :loading="loading" :count="todos.length" />
          <div v-if="!loading && todos.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <CircleCheck />
            </el-icon>
            <p>暂无待办</p>
          </div>
          <div v-else-if="!loading" class="content-list">
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
    <el-dialog v-model="announcementDetailVisible" title="公告详情" width="500px">
      <div class="detail-info">
        <h3>{{ currentAnnouncement.title }}</h3>
        <div class="detail-time">发布时间：{{ formatTime(currentAnnouncement.publishTime) }}</div>
        <div class="detail-content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>

    <!-- 待办详情对话框 -->
    <el-dialog v-model="todoDetailVisible" title="待办详情" width="500px">
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
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="todoDetailVisible = false">关闭</el-button>
          <el-button type="primary" v-if="currentTodo.status !== '已完成'" @click="markTodoComplete">
            标记完成
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { UserInfoVo } from '@/types/login';
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { Bell, Clock, CircleCheck } from '@element-plus/icons-vue'
import { showMessage } from '@/utils/message';
import ListSkeleton from '@/components/skeleton/ListSkeleton.vue'
import { downloadFileApi } from '@/api/file';
import { getDashboardStatsApi, getRecentLoginsApi } from '@/api/dashboard';
import { getMonitorTrendApi } from '@/api/actuator';
import type { RecentLogin } from '@/types/dashboard';

// 用户信息
const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}');
const nickname = ref(userInfo.nickname ?? '用户');
const avatarUrl = ref('');

// 加载状态
const loading = ref(false);

// ==================== 欢迎卡片 ====================

const weekTextMap = ['日', '一', '二', '三', '四', '五', '六'];

const greeting = computed(() => {
  const hour = dayjs().hour();
  if (hour < 6) return '夜深了';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  return '晚上好';
});

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日'));
const nowDay = computed(() => dayjs().format('DD'));
const nowMonth = computed(() => `${dayjs().month() + 1} 月`);
const nowWeek = computed(() => `星期${weekTextMap[dayjs().day()]}`);

// ==================== 统计卡片 ====================

const stats = ref({ userCount: 0, fileCount: 0, jobCount: 0, qps: 0 });

const statCards = computed(() => [
  { label: '用户总数', value: stats.value.userCount ?? 0, unit: '', icon: 'Coin', bgClass: 'bg-blue' },
  { label: '文件总数', value: stats.value.fileCount ?? 0, unit: '', icon: 'Folder', bgClass: 'bg-green' },
  { label: '当前 QPS', value: (stats.value.qps ?? 0).toFixed(2), unit: '次/秒', icon: 'DataLine', bgClass: 'bg-orange' },
  { label: '定时任务', value: stats.value.jobCount ?? 0, unit: '', icon: 'Timer', bgClass: 'bg-purple' }
]);

// ==================== 迷你趋势图 ====================

const trendChartRef = ref<HTMLElement | null>(null);
let trendChart: echarts.ECharts | null = null;

const renderTrendChart = async () => {
  try {
    const seriesList = await getMonitorTrendApi('1h', 'qps');
    const points = seriesList?.[0]?.points ?? [];
    await nextTick();
    if (!trendChartRef.value) return;
    trendChart = trendChart || echarts.init(trendChartRef.value);
    trendChart.setOption({
      grid: { left: 8, right: 8, top: 16, bottom: 6, containLabel: true },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'line', lineStyle: { color: '#C9CDD4' } },
        formatter: (params: any) => {
          const rows = Array.isArray(params) ? params : [params];
          if (rows.length === 0) return '';
          const time = dayjs(rows[0].value[0]).format('HH:mm:ss');
          return `${time}<br/>${rows[0].marker}QPS：${rows[0].value[1]}`;
        }
      },
      xAxis: {
        type: 'time',
        axisLine: { lineStyle: { color: '#E5E7EB' } },
        axisTick: { show: false },
        axisLabel: { color: '#8A8F99', fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        splitLine: { lineStyle: { color: '#F1F2F4' } },
        axisLabel: { color: '#8A8F99', fontSize: 11 }
      },
      series: [{
        name: 'QPS',
        type: 'line',
        smooth: true,
        showSymbol: false,
        lineStyle: { width: 1.6, color: '#4F6EF2' },
        areaStyle: { opacity: 0.12, color: '#4F6EF2' },
        data: points.map((point: { t: number; v: number }) => [point.t, point.v])
      }]
    }, true);
  } catch (error) {
    console.error('加载趋势图失败:', error);
  }
};

const handleResize = () => {
  trendChart?.resize();
};

// ==================== 最近登录 ====================

const recentLogins = ref<RecentLogin[]>([]);

// ==================== 公告相关（待接真实接口） ====================

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

// ==================== 待办事项相关（待接真实接口） ====================

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

// ==================== 方法 ====================

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
    showMessage('已标记为完成', 'success');
  }
}

// 获取用户头像（静默：文件可能已被删除，失败时回退为默认头像）
const getAvatar = async () => {
  if (!userInfo.avatar) return
  try {
    const res = await downloadFileApi(userInfo.avatar, true)
    avatarUrl.value = URL.createObjectURL(res)
  } catch (error) {
    console.warn('头像加载失败，文件可能已被删除:', error);
  }
}

// 加载首页数据
const loadData = async () => {
  loading.value = true;
  try {
    await Promise.all([getDashboardStatsApi(), getRecentLoginsApi()])
      .then(([statsRes, loginsRes]) => {
        stats.value = statsRes;
        recentLogins.value = loginsRes;
      });
    await renderTrendChart();
  } catch (error) {
    console.error('加载首页数据失败:', error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  getAvatar();
  loadData();
  window.addEventListener('resize', handleResize);
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  trendChart?.dispose();
  trendChart = null;
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 120px);
}

/* ============ 欢迎卡片 ============ */
.welcome-card {
  margin-bottom: 16px;
  padding: 24px 32px;
  border-radius: 12px;
  background: linear-gradient(120deg, #4F6EF2 0%, #6A5AE0 55%, #8F5AE0 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 20px rgba(79, 110, 242, 0.25);
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-avatar {
  background: rgba(255, 255, 255, 0.25);
  font-size: 22px;
  flex-shrink: 0;
}

.welcome-text h2 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 13px;
  opacity: 0.85;
}

.welcome-right {
  text-align: center;
  color: #fff;
}

.welcome-date {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-day {
  font-size: 40px;
  font-weight: 700;
  line-height: 1;
}

.date-detail {
  font-size: 12px;
  line-height: 1.8;
  opacity: 0.85;
  text-align: left;
}

/* ============ 统计卡片 ============ */
.stats-row {
  margin-bottom: 16px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
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
  flex-shrink: 0;
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
  min-width: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-unit {
  margin-left: 4px;
  font-size: 12px;
  font-weight: 400;
  color: #909399;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* ============ 内容区域 ============ */
.content-row {
  flex: 1;
  min-height: 0;
}

.content-row + .content-row {
  margin-top: 16px;
}

.fill-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  padding: 8px 0;
}

.card-hint {
  font-size: 12px;
  font-weight: 400;
  color: #909399;
}

.trend-chart {
  width: 100%;
  height: 240px;
}

/* ============ 最近登录 ============ */
.login-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.login-item:last-child {
  border-bottom: none;
}

.login-main {
  min-width: 0;
}

.login-device {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.login-ip {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.login-time {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
  margin-left: 12px;
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

/* ============ 空状态 ============ */
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

/* ============ 详情对话框 ============ */
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

/* ============ 响应式 ============ */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media screen and (max-width: 768px) {
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
    padding: 16px 20px;
  }

  .welcome-date {
    display: none;
  }
}
</style>
