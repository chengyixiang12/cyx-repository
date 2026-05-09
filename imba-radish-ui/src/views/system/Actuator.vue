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
        <el-date-picker 
          v-model="startTime" 
          type="datetime" 
          placeholder="开始时间" 
          class="time-picker"
          :max="endTime ? dayjs(endTime).subtract(1, 'minute').toDate() : undefined"
        />
        <span class="time-separator">至</span>
        <el-date-picker 
          v-model="endTime" 
          type="datetime" 
          placeholder="结束时间" 
          class="time-picker"
          :min="startTime ? dayjs(startTime).add(1, 'minute').toDate() : undefined"
          :max="dayjs().toDate()"
        />
        <el-button type="primary" size="small" @click="refreshChartData">
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
          <el-progress :percentage="Math.round((cpuUsage * 100))" :color="getProgressColor(cpuUsage)"
            :stroke-width="15" />
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
          <el-progress :percentage="Math.round(memoryUsagePercentage)"
            :color="getProgressColor(memoryUsagePercentage / 100)" :stroke-width="15" />
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
          <el-progress :percentage="Math.round(diskUsagePercentage)"
            :color="getProgressColor(diskUsagePercentage / 100)" :stroke-width="15" />
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

    <!-- 组件状态 -->
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
            <div class="components-table-wrapper">
              <el-table :data="healthHistory" border size="small" :loading="tableLoading" highlight-current-row>
                <el-table-column prop="createTime" label="时间" min-width="180" align="center">
                  <template #default="scope">
                    {{ formatTableTime(scope.row.createTime) }}
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
              <el-pagination
                :current-page="pagination.current"
                :page-size="pagination.size"
                :total="pagination.total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="handlePageChange"
                @size-change="handleSizeChange"
              />
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
import dayjs from 'dayjs';
import {
  listActuatorApi,
  listActuatorPageApi
} from '@/api/actuator';
import { Refresh } from '@element-plus/icons-vue';
import type { ListActuatorVO } from '@/types/actuator';
import { showMessage } from '@/utils/message';

// 状态变量
const loading = ref(false);
const healthStatus = ref('UP');
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

// 图表相关
const cpuChartRef = ref<HTMLElement | null>(null);
const memoryChartRef = ref<HTMLElement | null>(null);
let cpuChart: echarts.ECharts | null = null;
let memoryChart: echarts.ECharts | null = null;

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
  const seconds = uptime.value;
  const days = Math.floor(seconds / (24 * 60 * 60));
  const hours = Math.floor((seconds % (24 * 60 * 60)) / (60 * 60));
  const minutes = Math.floor((seconds % (60 * 60)) / 60);
  const secs = Math.floor(seconds % 60);
  return `${days}天 ${hours}小时 ${minutes}分钟 ${secs}秒`;
});

const memoryUsagePercentage = computed<number>(() => {
  if (memoryTotal.value === 0) return 0;
  return Number(((memoryUsed.value / memoryTotal.value) * 100).toFixed(2));
});

const diskUsagePercentage = computed<number>(() => {
  if (diskTotal.value === 0) return 0;
  const used = diskTotal.value - diskFree.value;
  return Number(((used / diskTotal.value) * 100).toFixed(2));
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

// 初始化图表
const initCharts = () => {
  if (cpuChartRef.value) {
    cpuChart = echarts.init(cpuChartRef.value);
    updateCpuChart([], []);
  }
  if (memoryChartRef.value) {
    memoryChart = echarts.init(memoryChartRef.value);
    updateMemoryChart([], []);
  }
};

// 更新CPU图表
const updateCpuChart = (times: string[], data: number[]) => {
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
const updateMemoryChart = (times: string[], data: number[]) => {
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
        rotate: 45,
        interval: 2
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      interval: 20,
      axisLabel: {
        formatter: '{value}%'
      },
      splitNumber: 5,
      axisTick: {
        interval: 20
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

// 异步加载所有监控数据（使用listActuatorApi）
const loadChartData = async () => {
  try {
    let queryStartTime: string;
    let queryEndTime: string;

    if (startTime.value == null || endTime.value == null) {
      const now = new Date();
      const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);

      endTime.value = now;
      startTime.value = oneHourAgo;
    }

    // 使用 Date 对象格式化
    queryStartTime = formatDateTime(startTime.value);
    queryEndTime = formatDateTime(endTime.value);

    const actuatorList = await listActuatorApi(queryStartTime, queryEndTime);

    if (actuatorList.length > 0) {
      // 获取第一个元素（最新数据，因为返回的数据是按 createTime 倒序的）
      const latestData = actuatorList[0];

      // 更新CPU监控数据（保持为小数形式，用于进度条显示）
      if (latestData.cpuUsage !== undefined) {
        cpuUsage.value = latestData.cpuUsage;
      }
      if (latestData.cpuCount !== undefined) {
        cpuCoreCount.value = latestData.cpuCount;
      }

      // 更新内存监控数据
      if (latestData.memeryUsed !== undefined) {
        memoryUsed.value = latestData.memeryUsed;
      }
      if (latestData.memeryMax !== undefined) {
        memoryTotal.value = latestData.memeryMax;
      }

      // 更新磁盘监控数据
      if (latestData.diskFree !== undefined) {
        diskFree.value = latestData.diskFree;
      }
      if (latestData.diskTotal !== undefined) {
        diskTotal.value = latestData.diskTotal;
      }

      // 更新服务状态数据
      if (latestData.health !== undefined) {
        healthStatus.value = latestData.health;
      }
      if (latestData.uptime !== undefined) {
        uptime.value = latestData.uptime;
      }

      // 从服务端获取数据，构建图表数据和组件状态历史（不使用本地存储）
      const times: string[] = [];
      const cpuData: number[] = [];
      const memoryData: number[] = [];

      // 图表数据需要时间正序（从左到右递增），所以反转数组
      const reversedList = [...actuatorList].reverse();
      
      // 数据采样函数：保留首尾，中间等间隔采样（最多显示15个点）
      const sampleData = (data: ListActuatorVO[], maxPoints: number = 15): ListActuatorVO[] => {
        if (data.length <= maxPoints) {
          return data;
        }
        
        const result: ListActuatorVO[] = [];
        const step = Math.floor((data.length - 2) / (maxPoints - 2));
        
        // 保留第一个（开始时间）
        result.push(data[0]);
        
        // 中间采样
        for (let i = step; i < data.length - 1; i += step) {
          result.push(data[i]);
        }
        
        // 保留最后一个（结束时间）
        result.push(data[data.length - 1]);
        
        return result;
      };
      
      // 对数据进行采样
      const sampledList = sampleData(reversedList, 15);
      
      sampledList.forEach((item: ListActuatorVO) => {
        if (item.createTime) {
          times.push(formatTime(item.createTime));
          // CPU使用率保留两位小数
          cpuData.push(Number(((item.cpuUsage || 0) * 100).toFixed(2)));
          if (item.memeryUsed !== undefined && memoryTotal.value > 0) {
            // 内存使用率保留两位小数
            memoryData.push(Number(((item.memeryUsed / memoryTotal.value) * 100).toFixed(2)));
          } else {
            memoryData.push(0);
          }
        }
      });

      // 保持最多显示20个数据点（取最新的20个）
      const maxPoints = 15;
      if (times.length > maxPoints) {
        const startIndex = times.length - maxPoints;
        times.splice(0, startIndex);
        cpuData.splice(0, startIndex);
        memoryData.splice(0, startIndex);
      }

      // 更新图表（直接传入服务端获取的数据）
      updateCpuChart(times, cpuData);
      updateMemoryChart(times, memoryData);
    }
  } catch (error) {
    console.error('获取监控数据失败:', error);
  }
};

// 格式化时间（使用 dayjs）
const formatTime = (timeStr: string): string => {
  try {
    return dayjs(timeStr).format('HH:mm:ss');
  } catch {
    return timeStr;
  }
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
  
  // 自动刷新图表
  refreshChartData();
};

// 仅刷新图表数据（使用用户选择的时间范围）
const refreshChartData = async () => {
  if (!startTime.value || !endTime.value) {
    showMessage('请选择开始时间和结束时间', 'warning');
    return;
  }
  
  // 检查时间范围是否超过一天
  const diffDays = dayjs(endTime.value).diff(dayjs(startTime.value), 'day');
  if (diffDays > 1 || (diffDays === 1 && dayjs(endTime.value).hour() > 0)) {
    showMessage('时间范围不能超过一天', 'warning');
    return;
  }
  
  await loadChartData();
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

    // 加载图表数据
    await loadChartData();

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

// 窗口大小变化时调整图表
const handleResize = () => {
  cpuChart?.resize();
  memoryChart?.resize();
};


// 设置默认时间范围为最近7分钟
const setDefaultTimeRange = () => {
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

onMounted(() => {
  // 设置默认时间范围
  setDefaultTimeRange();
  // 先初始化图表，确保图表实例存在
  setTimeout(() => {
    initCharts();
    // 图表初始化完成后再加载数据
    refreshData();
    // 加载表格数据
    loadTableData();
  }, 100);
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
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