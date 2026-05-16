<template>
  <div class="actuator-container container">
    <!-- 顶部刷新信息 -->
    <div class="refresh-info">
      <div class="time-range-selector">
        <span class="time-label">时间范围：</span>
        <el-select v-model="quickTimeRange" class="quick-range-select" @change="handleQuickTimeRangeChange">
          <el-option label="7分钟" :value="7" />
          <el-option label="30分钟" :value="30" />
          <el-option label="1小时" :value="60" />
          <el-option label="当天" :value="'today'" />
        </el-select>
        <span class="time-separator">-</span>
        <el-date-picker v-model="startTime" type="datetime" placeholder="开始时间" class="time-picker"
          :max="endTime ? dayjs(endTime).subtract(1, 'minute').toDate() : undefined" />
        <span class="time-separator">至</span>
        <el-date-picker v-model="endTime" type="datetime" placeholder="结束时间" class="time-picker"
          :min="startTime ? dayjs(startTime).add(1, 'minute').toDate() : undefined" :max="dayjs().toDate()" />
        <el-button type="primary" size="small" @click="queryData">
          查询
        </el-button>
      </div>
      <el-button type="primary" @click="refreshData" :loading="loading">
        <el-icon>
          <Refresh />
        </el-icon>
      </el-button>
      <span class="last-refresh">最后刷新：{{ lastRefreshTime }}</span>
    </div>

    <!-- 指标监控 -->
    <div class="metrics-grid">
      <!-- CPU监控 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>CPU监控</span>
            <el-tag v-if="loading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="metric-content">
          <el-progress type="circle" :percentage="Math.round(latestActuatorMetric.cpuUsage * 100)"
            :color="getProgressColor(latestActuatorMetric.cpuUsage * 100)" :width="100" />
          <div class="metric-info">
            <div class="info-item">
              <span class="label">CPU核数：</span>
              <span class="value">{{ latestActuatorMetric.cpuCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">CPU使用率：</span>
              <span class="value">{{ (latestActuatorMetric.cpuUsage * 100).toFixed(2) }}%</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 内存监控 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>内存监控</span>
            <el-tag v-if="loading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="metric-content">
          <el-progress type="circle" :percentage="Math.round(memoryUsage)" :color="getProgressColor(memoryUsage)"
            :width="100" />
          <div class="metric-info">
            <div class="info-item">
              <span class="label">已用内存：</span>
              <span class="value">{{ formatMemory(latestActuatorMetric.memoryUsed) }}</span>
            </div>
            <div class="info-item">
              <span class="label">总内存：</span>
              <span class="value">{{ formatMemory(latestActuatorMetric.memoryMax) }}</span>
            </div>
            <div class="info-item">
              <span class="label">内存使用率：</span>
              <span class="value">{{ memoryUsage.toFixed(2) }}%</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 磁盘监控 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>磁盘监控</span>
            <el-tag v-if="loading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="metric-content">
          <el-progress type="circle" :percentage="Math.round(diskUsage)" :color="getProgressColor(diskUsage)"
            :width="100" />
          <div class="metric-info">
            <div class="info-item">
              <span class="label">可用磁盘：</span>
              <span class="value">{{ formatDisk(latestActuatorMetric.diskFree) }}</span>
            </div>
            <div class="info-item">
              <span class="label">总磁盘：</span>
              <span class="value">{{ formatDisk(latestActuatorMetric.diskTotal) }}</span>
            </div>
            <div class="info-item">
              <span class="label">磁盘使用率：</span>
              <span class="value">{{ diskUsage.toFixed(2) }}%</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 系统启动时间 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>系统启动时间</span>
            <el-tag v-if="loading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="uptime-content">
          <div class="uptime-value">{{ uptimeFormatted }}</div>
        </div>
      </el-card>
    </div>

    <!-- 趋势图监控 -->
    <el-card shadow="hover" :body-style="{ padding: '20px' }" class="trend-card">
      <el-tabs v-model="activeTrendTab" type="border-card" @tab-change="handleTrendTabChange">
        <el-tab-pane label="CPU使用率趋势" name="cpu">
          <template #default>
            <div ref="cpuChartRef" class="chart-container"></div>
          </template>
        </el-tab-pane>
        <el-tab-pane label="内存使用率趋势" name="memory">
          <template #default>
            <div ref="memoryChartRef" class="chart-container"></div>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 组件状态 -->
    <div class="status-card">
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        
        <div class="status-info">
          <div class="components-status">
            <h3>组件状态</h3>
            <div class="components-table-wrapper">
              <el-table :data="healthHistory" border size="small" :loading="tableLoading" highlight-current-row>
                <el-table-column prop="createTime" label="时间" min-width="180" align="center">
                  <template #default="scope">
                    {{ formatTableTime(scope.row.createTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="health" label="服务器" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.health === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.health === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="healthDb" label="数据库" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.healthDb === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.healthDb === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="healthDiskSpace" label="磁盘空间" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.healthDiskSpace === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.healthDiskSpace === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="healthPing" label="系统响应" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.healthPing === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.healthPing === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="healthRabbit" label="RabbitMQ" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.healthRabbit === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.healthRabbit === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="healthRedis" label="Redis" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.healthRedis === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.healthRedis === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="healthSsl" label="SSL证书" min-width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.healthSsl === 'UP' ? 'success' : 'danger'" size="small">
                      {{ scope.row.healthSsl === 'UP' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="pagination-wrapper">
              <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
                :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
                @current-change="handlePageChange" @size-change="handleSizeChange" />
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick, reactive } from 'vue';
import * as echarts from 'echarts';
import dayjs from 'dayjs';
import {
  listActuatorPageApi,
  getLatestActuatorMetricApi,
  listCpuTrendApi,
  listMemoryTrendApi
} from '@/api/actuator';
import { Refresh } from '@element-plus/icons-vue';
import type { GetLatestActuatorMetricVO, ListActuatorVO } from '@/types/actuator';
import { showMessage } from '@/utils/message';

// 状态变量
const loading = ref(false);
const lastRefreshTime = ref('');
const latestActuatorMetric = reactive<GetLatestActuatorMetricVO>({
  cpuCount: 0,
  cpuUsage: 0,
  memoryUsed: 0,
  memoryMax: 0,
  uptime: 0,
  diskFree: 0,
  diskTotal: 0
});

const diskUsage = computed<number>(() => {
  if (latestActuatorMetric.diskTotal === latestActuatorMetric.diskFree) return 0;
  return Number((((latestActuatorMetric.diskTotal - latestActuatorMetric.diskFree) / latestActuatorMetric.diskTotal) * 100).toFixed(2));
});

// 图表相关
const cpuChartRef = ref<HTMLElement | null>(null);
const memoryChartRef = ref<HTMLElement | null>(null);
let cpuChart: echarts.ECharts | null = null;
let memoryChart: echarts.ECharts | null = null;
const activeTrendTab = ref('cpu');

// 时间范围选择（仅作用于图表）
const startTime = ref<Date | null>(null);
const endTime = ref<Date | null>(null);
const quickTimeRange = ref<number | string>(7);

// 组件状态历史记录（直接使用 ListActuatorVO）
const healthHistory = ref<ListActuatorVO[]>([]);

// 分页状态
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
});

// 表格加载状态
const tableLoading = ref(false);

// 计算属性
const uptimeFormatted = computed(() => {
  const seconds = latestActuatorMetric.uptime;
  const days = Math.floor(seconds / (24 * 60 * 60));
  const hours = Math.floor((seconds % (24 * 60 * 60)) / (60 * 60));
  const minutes = Math.floor((seconds % (60 * 60)) / 60);
  const secs = Math.floor(seconds % 60);
  return `${days}天 ${hours}小时 ${minutes}分钟 ${secs}秒`;
});

const memoryUsage = computed<number>(() => {
  if (latestActuatorMetric.memoryMax === 0) return 0;
  return Number(((latestActuatorMetric.memoryUsed / latestActuatorMetric.memoryMax) * 100).toFixed(2));
});

// 方法
const getProgressColor = (percentage: number) => {
  if (percentage < 60) return '#67C23A';
  if (percentage < 80) return '#E6A23C';
  return '#F56C6C';
};

const formatMemory = (bytes: number) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

const formatDisk = (bytes: number) => {
  return formatMemory(bytes);
};

// 加载最新指标数据（使用getLatestActuatorMetricApi）
const loadLatestMetrics = async () => {
  try {
    const metrics: GetLatestActuatorMetricVO = await getLatestActuatorMetricApi();
    Object.assign(latestActuatorMetric, metrics);
  } catch (error) {
    console.error('获取最新指标数据失败:', error);
  }
};

/**
 * 加载cpu趋势数据
 */
const loadCpuTrend = async () => {
  if (!startTime.value || !endTime.value) return;
  const cpuTrend = await listCpuTrendApi(formatDateTime(startTime.value), formatDateTime(endTime.value));
  // 更新图表图表数据
  updateCpuChart(cpuTrend.map(item => item.createTime), cpuTrend.map(item => Number((item.usageRate * 100).toFixed(2))));
};

/**
 * 加载内存趋势数据
 */
const loadMemoryTrend = async () => {
  if (!startTime.value || !endTime.value) return;
  const memoryTrend = await listMemoryTrendApi(formatDateTime(startTime.value), formatDateTime(endTime.value));
  // 更新图表图表数据
  updateMemoryChart(memoryTrend.map(item => item.createTime), memoryTrend.map(item => Number((item.usageRate * 100).toFixed(2))));
};

// tab切换时卸载当前图表并加载目标图表
const handleTrendTabChange = async (tabName: string) => {
  // 等待DOM渲染完成
  await nextTick();

  if (tabName === 'memory') {
    // 卸载CPU图表
    if (cpuChart) {
      cpuChart.dispose();
      cpuChart = null;
    }
    // 加载内存图表
    if (!memoryChart && memoryChartRef.value) {
      memoryChart = echarts.init(memoryChartRef.value);
      await loadMemoryTrend();
      // 确保图表正确适应容器尺寸
      setTimeout(() => {
        memoryChart?.resize();
      }, 100);
    }
  } else if (tabName === 'cpu') {
    // 卸载内存图表
    if (memoryChart) {
      memoryChart.dispose();
      memoryChart = null;
    }
    // 加载CPU图表
    if (!cpuChart && cpuChartRef.value) {
      cpuChart = echarts.init(cpuChartRef.value);
      await loadCpuTrend();
      // 确保图表正确适应容器尺寸
      setTimeout(() => {
        cpuChart?.resize();
      }, 100);
    }
  }
};

// 更新CPU图表
const updateCpuChart = async (times: string[], data: number[]) => {
  if (!cpuChart) return;

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      interval: 20,
      axisLabel: {
        formatter: '{value}%',
        margin: 10
      },
      splitNumber: 5,
      axisTick: {
        interval: 20
      },
      splitLine: {
        lineStyle: {
          width: 1
        }
      }
    },
    series: [{
      data: data,
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: 'rgba(64, 158, 255, 0.2)'
      },
      symbol: 'circle',
      symbolSize: 6
    }]
  };

  cpuChart.setOption(option);
};

// 更新内存图表
const updateMemoryChart = async (times: string[], data: number[]) => {
  if (!memoryChart) return;

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      interval: 20,
      axisLabel: {
        formatter: '{value}%',
        margin: 10
      },
      splitNumber: 5,
      axisTick: {
        interval: 20
      },
      splitLine: {
        lineStyle: {
          width: 1
        }
      }
    },
    series: [{
      data: data,
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: 'rgba(103, 194, 58, 0.2)'
      },
      symbol: 'circle',
      symbolSize: 6
    }]
  };

  memoryChart.setOption(option);
};

// 快捷时间范围选择处理
const handleQuickTimeRangeChange = (value: number | string) => {
  const now = new Date();
  let start: Date;

  if (value === 'today') {
    // 当天：从今天0点到现在
    start = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  } else {
    // 分钟数：从当前时间往前推
    start = new Date(now.getTime() - Number(value) * 60 * 1000);
  }

  startTime.value = start;
  endTime.value = now;
};

// 仅刷新图表数据（使用用户选择的时间范围）
const queryData = async () => {
  if (!startTime.value || !endTime.value) {
    showMessage('请选择开始时间和结束时间', 'warning');
    return;
  }

  loadLatestMetrics();

  // 强制重新加载当前激活的图表数据
  if (activeTrendTab.value === 'cpu') {
    await loadCpuTrend();
  } else {
    await loadMemoryTrend();
  }

  // 同时刷新表格数据
  pagination.value.current = 1;
  loadTableData();
};

const refreshData = async () => {
  loading.value = true;
  try {
    // 重置时间为最近7分钟
    const now = new Date();
    const sevenMinutesAgo = new Date(now.getTime() - 7 * 60 * 1000);
    startTime.value = sevenMinutesAgo;
    endTime.value = now;
    quickTimeRange.value = 7;

    // 加载最新指标数据（CPU、内存、磁盘、启动时间）
    loadLatestMetrics();
    // 强制重新加载当前激活的图表数据
    if (activeTrendTab.value === 'cpu') {
      await loadCpuTrend();
    } else {
      await loadMemoryTrend();
    }

    // 更新最后刷新时间
    lastRefreshTime.value = new Date().toLocaleString();
    // 同时刷新表格数据
    pagination.value.current = 1;
    loadTableData();
  } catch (error) {
    console.error('获取监控数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 设置默认时间范围为最近7分钟
const setDefaultTimeRange = async () => {
  const now = new Date();
  const sevenMinAgo = new Date(now.getTime() - 7 * 60 * 1000);

  endTime.value = now;
  startTime.value = sevenMinAgo;
};

// 格式化日期时间为 ISO 8601 格式（使用 dayjs）
const formatDateTime = (date: Date): string => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
};

// 格式化表格时间
const formatTableTime = (timeStr?: string): string => {
  if (!timeStr) return '-';
  try {
    return dayjs(timeStr).format('YYYY-MM-DD HH:mm:ss');
  } catch {
    return timeStr;
  }
};

// 加载分页表格数据
const loadTableData = async () => {
  tableLoading.value = true;
  try {
    const params: {
      pageNum: number;
      pageSize: number;
      startTime?: string;
      endTime?: string;
    } = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size
    };

    if (startTime.value) {
      params.startTime = formatDateTime(startTime.value);
    }
    if (endTime.value) {
      params.endTime = formatDateTime(endTime.value);
    }

    const result = await listActuatorPageApi(params);
    healthHistory.value = result.records || [];
    pagination.value.total = result.total || 0;
  } catch (error) {
    console.error('获取组件状态分页数据失败:', error);
  } finally {
    tableLoading.value = false;
  }
};

// 分页事件处理
const handlePageChange = (page: number) => {
  pagination.value.current = page;
  loadTableData();
};

const handleSizeChange = (size: number) => {
  pagination.value.size = size;
  pagination.value.current = 1;
  loadTableData();
};

onMounted(async () => {
  await setDefaultTimeRange();
  handleTrendTabChange(activeTrendTab.value);
  refreshData();
  loadTableData();
});

onUnmounted(() => {
  // 清除图表实例
  cpuChart?.dispose();
  memoryChart?.dispose();
});
</script>

<style scoped>
.actuator-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  background-color: #fff;
}

h2 {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.status-card {
  margin: 0 10px 10px 10px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 10px;
  margin: 0 10px;
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.metric-info {
  flex: 1;
}

.info-item {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
}

.uptime-content {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
}

.uptime-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.trend-card {
  margin: 10px;
  flex-shrink: 0;
}

.trend-card :deep(.el-tabs__content) {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  margin-top: 15px;
}

.label {
  color: #606266;
}

.value {
  font-weight: bold;
  color: #303133;
}

.chart-container {
  height: 40vh;
  width: 100%;
}

.refresh-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  margin-bottom: 20px;
  gap: 20px;
}

.time-range-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-label {
  color: #606266;
  font-size: 14px;
}

.quick-range-select {
  width: 100px;
}

.time-picker {
  width: 180px;
}

.time-separator {
  color: #909399;
  font-size: 14px;
}

.last-refresh {
  color: #909399;
  font-size: 14px;
}

.el-progress {
  margin-top: 15px;
}

.components-status {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.components-status h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.components-table-wrapper {
  overflow-x: auto;
}

.components-table-wrapper :deep(.el-table) {
  font-size: 14px;
}

.components-table-wrapper :deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #606266;
}

.pagination-wrapper {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.component-details {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.detail-item {
  display: block;
  margin-bottom: 5px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .components-grid {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 250px;
  }
}
</style>