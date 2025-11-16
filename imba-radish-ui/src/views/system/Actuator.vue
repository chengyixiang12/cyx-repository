<template>
  <div class="actuator-monitor">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>系统监控</span>
          <el-button type="primary" @click="refreshData">刷新</el-button>
        </div>
      </template>
      
      <!-- 健康状态 -->
      <el-row :gutter="20" class="status-row">
        <el-col :span="6">
          <el-card class="status-card" :class="healthStatusClass">
            <div class="status-content">
              <i :class="healthIcon"></i>
              <div>
                <p>健康状态</p>
                <h3>{{ healthStatus }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="status-card info">
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
          <el-card class="status-card warning">
            <div class="status-content">
              <i class="el-icon-cpu"></i>
              <div>
                <p>CPU使用率</p>
                <h3>{{ cpuUsage }}%</h3>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="status-card danger">
            <div class="status-content">
              <i class="el-icon-box"></i>
              <div>
                <p>内存使用</p>
                <h3>{{ memoryUsage }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 监控指标 -->
      <el-tabs v-model="activeTab" class="monitor-tabs">
        <el-tab-pane label="健康检查" name="health">
          <el-table :data="healthDetails" style="width: 100%" border>
            <el-table-column prop="name" label="组件名称" width="200"></el-table-column>
            <el-table-column prop="status" label="状态" width="150">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'UP' ? 'success' : 'danger'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="details" label="详情"></el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="内存信息" name="memory">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="总内存">{{ heapMemory.total }}</el-descriptions-item>
            <el-descriptions-item label="已用内存">{{ heapMemory.used }}</el-descriptions-item>
            <el-descriptions-item label="空闲内存">{{ heapMemory.free }}</el-descriptions-item>
            <el-descriptions-item label="使用率">
              <el-progress :percentage="heapMemory.percent" :status="heapMemory.percent > 80 ? 'exception' : ''" />
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="线程信息" name="threads">
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
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 响应式数据定义
const activeTab = ref('health')
const healthStatus = ref('UNKNOWN')
const uptime = ref('0天0小时0分钟')
const cpuUsage = ref(0)
const memoryUsage = ref('0 MB / 0 MB')

// 健康详情数据
const healthDetails = ref<any>([])

// 内存信息
const heapMemory = ref({
  total: '0 MB',
  used: '0 MB',
  free: '0 MB',
  percent: 0
})

// 线程信息
const threadInfo = ref({
  peakThreadCount: 0,
  threadCount: 0,
  daemonThreadCount: 0
})

// 环境信息
const envInfo = ref({
  java: {
    version: '',
    vmName: ''
  },
  os: {
    name: ''
  }
})

// 计算属性
const healthStatusClass = computed(() => {
  switch(healthStatus.value) {
    case 'UP': return 'success'
    case 'DOWN': return 'danger'
    default: return 'warning'
  }
})

const healthIcon = computed(() => {
  switch(healthStatus.value) {
    case 'UP': return 'el-icon-success'
    case 'DOWN': return 'el-icon-error'
    default: return 'el-icon-warning'
  }
})

// 格式化字节大小
const formatBytes = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 获取运行时间
const getUptime = (startTime: number): string => {
  const now = new Date().getTime()
  const diff = now - startTime
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  return `${days}天${hours}小时${minutes}分钟`
}

// 刷新数据
const refreshData = () => {
  fetchHealthInfo()
  fetchMetricsInfo()
  fetchEnvInfo()
  fetchThreadInfo()
  ElMessage.success('数据刷新成功')
}

// 获取健康信息
const fetchHealthInfo = async () => {
  try {
    const response = await axios.get('/actuator/health')
    const data = response.data
    
    healthStatus.value = data.status
    
    // 处理健康详情
    const details: any[] = []
    if (data.components) {
      Object.keys(data.components).forEach(key => {
        const component = data.components[key]
        details.push({
          name: key,
          status: component.status,
          details: JSON.stringify(component.details || {})
        })
      })
    }
    healthDetails.value = details
  } catch (error) {
    console.error('获取健康信息失败:', error)
    ElMessage.error('获取健康信息失败')
  }
}

// 获取指标信息
const fetchMetricsInfo = async () => {
  try {
    // 获取JVM内存信息
    const memoryResponse = await axios.get('/actuator/metrics/jvm.memory.used')
    const maxMemoryResponse = await axios.get('/actuator/metrics/jvm.memory.max')
    
    const usedValue = memoryResponse.data.measurements[0]?.value || 0
    const maxValue = maxMemoryResponse.data.measurements[0]?.value || 1
    
    heapMemory.value = {
      total: formatBytes(maxValue),
      used: formatBytes(usedValue),
      free: formatBytes(maxValue - usedValue),
      percent: Math.round((usedValue / maxValue) * 100)
    }
    
    memoryUsage.value = `${formatBytes(usedValue)} / ${formatBytes(maxValue)}`
    
    // 获取CPU信息
    const cpuResponse = await axios.get('/actuator/metrics/system.cpu.usage')
    cpuUsage.value = Math.round((cpuResponse.data.measurements[0]?.value || 0) * 100)
    
    // 获取启动时间
    const processResponse = await axios.get('/actuator/metrics/process.start.time')
    const startTime = processResponse.data.measurements[0]?.value 
    if (startTime) {
      uptime.value = getUptime(startTime * 1000)
    }
  } catch (error) {
    console.error('获取指标信息失败:', error)
    ElMessage.error('获取指标信息失败')
  }
}

// 获取环境信息
const fetchEnvInfo = async () => {
  try {
    const response = await axios.get('/actuator/env')
    const data = response.data
    
    envInfo.value = {
      java: {
        version: data.propertySources?.systemProperties?.properties?.['java.version'] || '',
        vmName: data.propertySources?.systemProperties?.properties?.['java.vm.name'] || ''
      },
      os: {
        name: data.propertySources?.systemProperties?.properties?.['os.name'] || ''
      }
    }
  } catch (error) {
    console.error('获取环境信息失败:', error)
  }
}

// 获取线程信息
const fetchThreadInfo = async () => {
  try {
    const response = await axios.get('/actuator/threaddump')
    const data = response.data
    
    threadInfo.value = {
      peakThreadCount: data.threads?.length || 0,
      threadCount: data.threads?.filter((t: any) => t.threadState === 'RUNNABLE').length || 0,
      daemonThreadCount: data.threads?.filter((t: any) => t.daemon).length || 0
    }
  } catch (error) {
    console.error('获取线程信息失败:', error)
  }
}

// 组件挂载时获取数据
onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.actuator-monitor {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.monitor-tabs {
  margin-top: 20px;
}
</style>