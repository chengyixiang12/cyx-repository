<template>
  <div class="actuator-monitor">
    <el-card class="box-card">
      <template #header>
        <div class="list-header">
          <div class="right-header">
            <el-button type="primary" @click="refreshData">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 健康状态 -->
      <el-row :gutter="20" class="status-row">

        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-content">
              <i class="el-icon-monitor"></i>
              <div>
                <p>运行时间</p>
                <h3>{{ uptime }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-content">
              <div class="gauge-container">
                <el-tooltip :content="`CPU使用率: ${cpuUsage}% (${cpuCoreCount}核心)`" placement="top">
                  <el-progress type="dashboard" :percentage="cpuUsage" :color="cpuUsageColor" :width="80"
                    :stroke-width="6">
                    <template #default="{ percentage }">
                      <span class="gauge-text">{{ percentage }}%</span>
                      <div class="gauge-label">CPU使用率</div>
                      <div class="gauge-sublabel">{{ cpuCoreCount }}核心</div>
                    </template>
                  </el-progress>
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-content">
              <div class="gauge-container">
                <el-tooltip
                  :content="`内存使用: ${(heapMemory.used / (1024 * 1024)).toFixed(2)} MB / ${(heapMemory.total / (1024 * 1024)).toFixed(2)} MB`"
                  placement="top">
                  <el-progress type="dashboard" :percentage="heapMemory.percent" :color="memoryUsageColor" :width="80"
                    :stroke-width="6">
                    <template #default="{ percentage }">
                      <span class="gauge-text">{{ percentage }}%</span>
                      <div class="gauge-label">内存使用率</div>
                    </template>
                  </el-progress>
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-content">
              <div class="gauge-container">
                <el-tooltip
                  :content="`磁盘使用: ${(diskUsage.used / (1024 * 1024 * 1024)).toFixed(2)} GB / ${(diskUsage.total / (1024 * 1024 * 1024)).toFixed(2)} GB`"
                  placement="top">
                  <el-progress type="dashboard" :percentage="diskUsage.percent" :color="diskUsageColor" :width="80"
                    :stroke-width="6">
                    <template #default="{ percentage }">
                      <span class="gauge-text">{{ percentage }}%</span>
                      <div class="gauge-label">磁盘使用率</div>
                    </template>
                  </el-progress>
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 监控指标 -->
      <el-tabs v-model="activeTab" class="monitor-tabs">
        <el-tab-pane label="健康检查" name="health">
          <div class="tab-content-wrapper">
            <el-table :data="healthTableData" style="width: 100%" border>
              <el-table-column prop="name" label="组件名称" width="200" align="center"/>
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 'UP' ? 'success' : 'danger'">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="details" label="详情" align="center" show-overflow-tooltip/>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- <el-tab-pane label="线程信息" name="threads">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="线程总数">{{ threadInfo.peakThreadCount }}</el-descriptions-item>
            <el-descriptions-item label="活跃线程数">{{ threadInfo.threadCount }}</el-descriptions-item>
            <el-descriptions-item label="守护线程数">{{ threadInfo.daemonThreadCount }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="环境信息" name="env">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="Java版本">{{ envInfo.java.version }}</el-descriptions-item>
            <el-descriptions-item label="操作系统">{{ envInfo.os.name }}</el-descriptions-item>
            <el-descriptions-item label="JVM名称">{{ envInfo.java.vmName }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane> -->
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  getCpuUsageApi,
  getDiskTotalApi,
  getDiskFreeApi,
  getMemoryTotalApi,
  getMemoryUsageApi,
  getUptimeApi,
  getCpuCoreCountApi,
  getHealthApi
} from '@/api/actuator'
import { showMessage } from '@/utils/message'
import { Health } from '@/types/actuator';

// 运行时间
const uptime = ref('');
// tabs初始值
const activeTab = ref('health');
// cpu使用率
const cpuUsage = ref(0);
// cpu核心数
const cpuCoreCount = ref(0);
// 健康详情数据
const healthDetails = ref<Health>();
// 内存信息
const heapMemory = ref({
  percent: 0,
  used: 0,
  total: 0
});
// 磁盘信息
const diskUsage = ref({
  percent: 0,
  used: 0,
  total: 0
});

// CPU使用率颜色
const cpuUsageColor = computed(() => {
  if (cpuUsage.value < 60) {
    return '#5cb87a'
  } else if (cpuUsage.value < 80) {
    return '#e6a23c'
  } else {
    return '#f56c6c'
  }
})

// 内存使用率颜色
const memoryUsageColor = computed(() => {
  if (heapMemory.value.percent < 60) {
    return '#5cb87a'
  } else if (heapMemory.value.percent < 80) {
    return '#e6a23c'
  } else {
    return '#f56c6c'
  }
})

// 磁盘使用率颜色
const diskUsageColor = computed(() => {
  if (diskUsage.value.percent < 60) {
    return '#5cb87a'
  } else if (diskUsage.value.percent < 80) {
    return '#e6a23c'
  } else {
    return '#f56c6c'
  }
});

// 健康表格数据（用于展示）
const healthTableData = computed(() => {
  if (!healthDetails.value?.components) {
    return [];
  }

  return Object.keys(healthDetails.value.components).map(key => {
    const component = healthDetails.value!.components![key];
    return {
      name: key,
      status: component.status,
      details: null // 将details对象转换为字符串 JSON.stringify(component.details || {}, null, 2)
    };
  });
});

// 获取运行时间
const getUptime = (seconds: number): string => {
  const days = Math.floor(seconds / (24 * 3600));
  const hours = Math.floor((seconds % (24 * 3600)) / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = Math.floor(seconds % 60);

  const parts: string[] = [];

  if (days > 0) parts.push(`${days}天`);
  if (hours > 0) parts.push(`${hours}小时`);
  if (minutes > 0) parts.push(`${minutes}分钟`);
  if (secs > 0 || parts.length === 0) parts.push(`${secs}秒`);

  return parts.join(' ');
}

// 刷新数据
const refreshData = () => {
  fetchMetricsInfo()
  showMessage('数据刷新成功', 'success')
}

// 获取内存使用率
const getMemoryUseRate = async () => {
  const memoryUsageData = await getMemoryUsageApi()
  const memoryMaxData = await getMemoryTotalApi()

  const usedValue = memoryUsageData.measurements[0]?.value || 0
  const maxValue = memoryMaxData.measurements[0]?.value || 1

  heapMemory.value = {
    percent: Math.round((usedValue / maxValue) * 100),
    used: usedValue,
    total: maxValue
  }
}

// 获取CPU信息
const getCpuUseRate = async () => {
  const cpuData = await getCpuUsageApi();
  cpuUsage.value = Math.round((cpuData.measurements[0]?.value || 0) * 100);
  const cpuCoreCountData = await getCpuCoreCountApi();
  cpuCoreCount.value = cpuCoreCountData.measurements[0]?.value || 1
}

//获取运行时间
const getUptimeInfo = async () => {
  const uptimeData = await getUptimeApi()
  uptime.value = getUptime(uptimeData.measurements[0]?.value || 0)
}

// 获取磁盘使用率
const getDiskUseRate = async () => {
  const diskFreeData = await getDiskFreeApi();
  const diskTotalData = await getDiskTotalApi();

  const free = diskFreeData.measurements[0].value;
  const total = diskTotalData.measurements[0].value;

  diskUsage.value = {
    percent: Math.round(((total - free) / total) * 100),
    used: total - free,
    total: total
  }
}

const getHealthDetails = async () => {
  healthDetails.value = await getHealthApi();
}

// 获取指标信息
const fetchMetricsInfo = async () => {
  getCpuUseRate();
  getMemoryUseRate();
  getUptimeInfo();
  getDiskUseRate();
  getHealthDetails();
}

// 组件挂载时获取数据
onMounted(() => {
  fetchMetricsInfo()
})
</script>

<style scoped>
.actuator-monitor {
  padding: 10px;
  height: 100%;
  background-color: #f5f7fa;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 16px;
  padding: 0 12px;
}

.right-header {
  margin-left: auto;
}

.status-row {
  margin-bottom: 20px;
}

.status-card {
  height: 120px;
}

.status-card.success {
  border-left: 5px solid #67C23A;
}

.status-card.danger {
  border-left: 5px solid #F56C6C;
}

.status-card.warning {
  border-left: 5px solid #E6A23C;
}

.status-card.info {
  border-left: 5px solid #409EFF;
}

.status-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.status-content i {
  font-size: 36px;
  margin-right: 15px;
  color: #666;
}

.status-content div p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.status-content div h3 {
  margin: 5px 0 0 0;
  color: #333;
}

.gauge-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.gauge-text {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.gauge-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.gauge-sublabel {
  font-size: 10px;
  color: #999;
  margin-top: 2px;
}

.monitor-tabs {
  margin-top: 5px;
}

.tab-content-wrapper {
  max-height: 41vh;
  overflow-y: auto;
}
</style>