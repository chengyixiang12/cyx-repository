<template>
  <div class="actuator-container container">
    <!-- 顶部刷新信息 -->
    <div class="refresh-info">
      <el-button type="primary" @click="refreshData" :loading="loading">
        <el-icon><Refresh /></el-icon>
        刷新数据
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
            <el-tag v-if="cpuLoading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="cpu-info">
          <div class="info-item">
            <span class="label">CPU核数：</span>
            <span class="value">{{ cpuCoreCount }}</span>
          </div>
          <div class="info-item">
            <span class="label">CPU使用率：</span>
            <span class="value">{{ (cpuUsage * 100).toFixed(2) }}%</span>
          </div>
          <el-progress 
            :percentage="Math.round((cpuUsage * 100))" 
            :color="getProgressColor(cpuUsage)"
            :stroke-width="15"
          />
        </div>
      </el-card>
      
      <!-- 内存监控 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>内存监控</span>
            <el-tag v-if="memoryLoading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="memory-info">
          <div class="info-item">
            <span class="label">已用内存：</span>
            <span class="value">{{ formatMemory(memoryUsed) }}</span>
          </div>
          <div class="info-item">
            <span class="label">总内存：</span>
            <span class="value">{{ formatMemory(memoryTotal) }}</span>
          </div>
          <div class="info-item">
            <span class="label">内存使用率：</span>
            <span class="value">{{ memoryUsagePercentage.toFixed(2) }}%</span>
          </div>
          <el-progress 
            :percentage="Math.round(memoryUsagePercentage)" 
            :color="getProgressColor(memoryUsagePercentage / 100)"
            :stroke-width="15"
          />
        </div>
      </el-card>
      
      <!-- 磁盘监控 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>磁盘监控</span>
            <el-tag v-if="diskLoading" size="small">加载中...</el-tag>
          </div>
        </template>
        <div class="disk-info">
          <div class="info-item">
            <span class="label">可用磁盘：</span>
            <span class="value">{{ formatDisk(diskFree) }}</span>
          </div>
          <div class="info-item">
            <span class="label">总磁盘：</span>
            <span class="value">{{ formatDisk(diskTotal) }}</span>
          </div>
          <div class="info-item">
            <span class="label">磁盘使用率：</span>
            <span class="value">{{ diskUsagePercentage.toFixed(2) }}%</span>
          </div>
          <el-progress 
            :percentage="Math.round(diskUsagePercentage)" 
            :color="getProgressColor(diskUsagePercentage / 100)"
            :stroke-width="15"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 折线图监控 -->
    <div class="charts-grid">
      <!-- CPU使用率折线图 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>CPU使用率趋势</span>
          </div>
        </template>
        <div ref="cpuChartRef" class="chart-container"></div>
      </el-card>
      
      <!-- 内存使用率折线图 -->
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>内存使用率趋势</span>
          </div>
        </template>
        <div ref="memoryChartRef" class="chart-container"></div>
      </el-card>
    </div>
    
    <!-- 组件状态（移到最下面） -->
    <div class="status-card">
      <el-card shadow="hover" :body-style="{ padding: '20px' }">
        <template #header>
          <div class="card-header">
            <span>服务状态</span>
            <el-tag :type="healthStatus === 'UP' ? 'success' : 'danger'">
              {{ healthStatus === 'UP' ? '正常' : '异常' }}
            </el-tag>
          </div>
        </template>
        <div class="status-info">
          <div class="info-item">
            <span class="label">系统启动时间：</span>
            <span class="value">{{ uptimeFormatted }}</span>
          </div>
          
          <div class="components-status">
            <h3>组件状态</h3>
            <div class="components-grid">
              <div 
                v-for="(component, key) in healthComponents" 
                :key="key" 
                class="component-item"
              >
                <span class="component-name">{{ getComponentName(key) }}</span>
                <el-tag 
                  :type="component.status === 'UP' ? 'success' : 'danger'"
                  size="small"
                >
                  {{ component.status === 'UP' ? '正常' : '异常' }}
                </el-tag>
                <div v-if="component.details" class="component-details">
                  <template v-for="(value, detailKey) in component.details" :key="detailKey">
                    <span class="detail-item" v-if="value !== null && value !== undefined">
                      {{ getDetailName(detailKey) }}: {{ formatDetailValue(detailKey, value) }}
                    </span>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import * as echarts from 'echarts';
import {
  getCpuUsageApi,
  getCpuCoreCountApi,
  getMemoryUsageApi,
  getMemoryTotalApi,
  getHealthApi,
  getUptimeApi,
  getDiskFreeApi,
  getDiskTotalApi
} from '@/api/actuator';
import { Refresh } from '@element-plus/icons-vue';

// 类型定义
interface HealthComponent {
  status: string;
  details?: Record<string, any>;
}

// 状态变量
const loading = ref(false);
const healthStatus = ref('UP');
const healthComponents = ref<Record<string, HealthComponent>>({});
const uptime = ref(0);
const cpuCoreCount = ref(0);
const cpuUsage = ref(0);
const memoryUsed = ref(0);
const memoryTotal = ref(0);
const diskFree = ref(0);
const diskTotal = ref(0);
const lastRefreshTime = ref('');

// 模块加载状态
const cpuLoading = ref(false);
const memoryLoading = ref(false);
const diskLoading = ref(false);
const healthLoading = ref(false);

// 图表相关
const cpuChartRef = ref<HTMLElement | null>(null);
const memoryChartRef = ref<HTMLElement | null>(null);
let cpuChart: echarts.ECharts | null = null;
let memoryChart: echarts.ECharts | null = null;

// 历史数据
const cpuUsageHistory = ref<number[]>([]);
const memoryUsageHistory = ref<number[]>([]);
const timeHistory = ref<string[]>([]);

// 计算属性
const uptimeFormatted = computed(() => {
  const seconds = uptime.value;
  const days = Math.floor(seconds / (24 * 60 * 60));
  const hours = Math.floor((seconds % (24 * 60 * 60)) / (60 * 60));
  const minutes = Math.floor((seconds % (60 * 60)) / 60);
  const secs = Math.floor(seconds % 60);
  return `${days}天 ${hours}小时 ${minutes}分钟 ${secs}秒`;
});

const memoryUsagePercentage = computed<number>(() => {
  if (memoryTotal.value === 0) return 0;
  return (memoryUsed.value / memoryTotal.value) * 100;
});

const diskUsagePercentage = computed<number>(() => {
  if (diskTotal.value === 0) return 0;
  const used = diskTotal.value - diskFree.value;
  return (used / diskTotal.value) * 100;
});

// 方法
const getProgressColor = (percentage: number) => {
  if (percentage < 0.6) return '#67C23A';
  if (percentage < 0.8) return '#E6A23C';
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

// 获取组件名称
const getComponentName = (key: string): string => {
  const componentNames: Record<string, string> = {
    db: '数据库',
    diskSpace: '磁盘空间',
    mail: '邮件服务',
    ping: '系统响应',
    rabbit: 'RabbitMQ',
    redis: 'Redis',
    ssl: 'SSL证书'
  };
  return componentNames[key] || key;
};

// 获取详情名称
const getDetailName = (key: string): string => {
  const detailNames: Record<string, string> = {
    database: '数据库类型',
    validationQuery: '验证查询',
    result: '结果',
    total: '总空间',
    free: '可用空间',
    threshold: '阈值',
    path: '路径',
    exists: '是否存在',
    location: '服务器地址',
    version: '版本'
  };
  return detailNames[key] || key;
};

// 格式化详情值
const formatDetailValue = (key: string, value: any): string => {
  if (key === 'total' || key === 'free' || key === 'threshold') {
    return formatMemory(value);
  }
  if (typeof value === 'boolean') {
    return value ? '是' : '否';
  }
  return String(value);
};

// 初始化图表
const initCharts = () => {
  if (cpuChartRef.value) {
    cpuChart = echarts.init(cpuChartRef.value);
    updateCpuChart();
  }
  if (memoryChartRef.value) {
    memoryChart = echarts.init(memoryChartRef.value);
    updateMemoryChart();
  }
};

// 更新CPU图表
const updateCpuChart = () => {
  if (!cpuChart) return;
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
    },
    xAxis: {
      type: 'category',
      data: timeHistory.value,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      data: cpuUsageHistory.value,
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      },
      symbol: 'circle',
      symbolSize: 6
    }]
  };
  
  cpuChart.setOption(option);
};

// 更新内存图表
const updateMemoryChart = () => {
  if (!memoryChart) return;
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
    },
    xAxis: {
      type: 'category',
      data: timeHistory.value,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      data: memoryUsageHistory.value,
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
        ])
      },
      symbol: 'circle',
      symbolSize: 6
    }]
  };
  
  memoryChart.setOption(option);
};

// 添加历史数据
const addHistoryData = () => {
  const now = new Date();
  const timeStr = now.getHours().toString().padStart(2, '0') + ':' + 
                 now.getMinutes().toString().padStart(2, '0') + ':' + 
                 now.getSeconds().toString().padStart(2, '0');
  
  // 添加数据
  cpuUsageHistory.value.push((cpuUsage.value * 100).toFixed(2) as unknown as number);
  memoryUsageHistory.value.push(memoryUsagePercentage.value);
  timeHistory.value.push(timeStr);
  
  // 保持最多显示20个数据点
  if (cpuUsageHistory.value.length > 20) {
    cpuUsageHistory.value.shift();
    memoryUsageHistory.value.shift();
    timeHistory.value.shift();
  }
  
  // 更新图表
  updateCpuChart();
  updateMemoryChart();
};

// 异步加载CPU数据
const loadCpuData = async () => {
  cpuLoading.value = true;
  try {
    const [cpuUsageRes, cpuCoreCountRes] = await Promise.all([
      getCpuUsageApi(),
      getCpuCoreCountApi()
    ]);
    cpuUsage.value = cpuUsageRes.measurements[0]?.value || 0;
    cpuCoreCount.value = cpuCoreCountRes.measurements[0]?.value || 0;
  } catch (error) {
    console.error('获取CPU数据失败:', error);
  } finally {
    cpuLoading.value = false;
  }
};

// 异步加载内存数据
const loadMemoryData = async () => {
  memoryLoading.value = true;
  try {
    const [memoryUsageRes, memoryTotalRes] = await Promise.all([
      getMemoryUsageApi(),
      getMemoryTotalApi()
    ]);
    memoryUsed.value = memoryUsageRes.measurements[0]?.value || 0;
    memoryTotal.value = memoryTotalRes.measurements[0]?.value || 0;
  } catch (error) {
    console.error('获取内存数据失败:', error);
  } finally {
    memoryLoading.value = false;
  }
};

// 异步加载磁盘数据
const loadDiskData = async () => {
  diskLoading.value = true;
  try {
    const [diskFreeRes, diskTotalRes] = await Promise.all([
      getDiskFreeApi(),
      getDiskTotalApi()
    ]);
    diskFree.value = diskFreeRes.measurements[0]?.value || 0;
    diskTotal.value = diskTotalRes.measurements[0]?.value || 0;
  } catch (error) {
    console.error('获取磁盘数据失败:', error);
  } finally {
    diskLoading.value = false;
  }
};

// 异步加载健康状态数据
const loadHealthData = async () => {
  healthLoading.value = true;
  try {
    const [healthRes, uptimeRes] = await Promise.all([
      getHealthApi(),
      getUptimeApi()
    ]);
    healthStatus.value = healthRes.status;
    healthComponents.value = healthRes.components || {};
    uptime.value = uptimeRes.measurements[0]?.value || 0;
  } catch (error) {
    console.error('获取健康状态失败:', error);
  } finally {
    healthLoading.value = false;
  }
};

const refreshData = async () => {
  loading.value = true;
  try {
    // 模块异步加载
    await Promise.all([
      loadCpuData(),
      loadMemoryData(),
      loadDiskData(),
      loadHealthData()
    ]);

    // 添加历史数据
    addHistoryData();

    // 更新最后刷新时间
    lastRefreshTime.value = new Date().toLocaleString();
  } catch (error) {
    console.error('获取监控数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 窗口大小变化时调整图表
const handleResize = () => {
  cpuChart?.resize();
  memoryChart?.resize();
};

// 定时刷新
let refreshInterval: number | undefined;

onMounted(() => {
  // 初始加载数据
  refreshData();
  // 初始化图表
  setTimeout(initCharts, 100);
  // 设置定时刷新，每30秒刷新一次
  refreshInterval = window.setInterval(refreshData, 30000);
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  // 清除定时任务
  if (refreshInterval) {
    clearInterval(refreshInterval);
  }
  // 清除图表实例
  cpuChart?.dispose();
  memoryChart?.dispose();
  // 移除事件监听
  window.removeEventListener('resize', handleResize);
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
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 10px;
  margin: 0 10px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 10px;
  margin: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info,
.cpu-info,
.memory-info,
.disk-info {
  margin-top: 15px;
}

.info-item {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
}

.label {
  color: #606266;
}

.value {
  font-weight: bold;
  color: #303133;
}

.chart-container {
  height: 200px;
  width: 100%;
}

.refresh-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  margin-bottom: 20px;
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

.components-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.component-item {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #409EFF;
}

.component-name {
  font-weight: bold;
  margin-right: 10px;
  color: #303133;
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