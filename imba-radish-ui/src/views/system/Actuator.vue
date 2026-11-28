<template>
  <div class="monitor container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>系统监控</span>
        <span class="status-pill" :class="overallLevel">{{ overallText }}</span>
      </div>
      <div class="right-header">
        <el-radio-group v-model="range" size="small" @change="loadTrend">
          <el-radio-button v-for="item in rangeOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
        <el-button type="primary" :loading="loading" @click="refreshAll">
          <el-icon>
            <Refresh />
          </el-icon>
        </el-button>
      </div>
    </div>

    <div class="monitor-body">
      <!-- 应用指标 -->
      <div class="section-title">
        应用指标
        <span class="section-hint">最近 30 秒窗口 · 每 30 秒自动刷新</span>
      </div>
      <div class="metric-grid">
        <div v-for="card in appCards" :key="card.label" class="metric-card">
          <div class="metric-label">{{ card.label }}</div>
          <div class="metric-value" :class="card.level">
            {{ card.value }}<span class="metric-unit">{{ card.unit }}</span>
          </div>
          <div class="metric-sub">{{ card.sub }}</div>
        </div>
      </div>

      <!-- 资源 -->
      <div class="section-title">资源</div>
      <div class="resource-grid">
        <div class="resource-card">
          <div class="resource-head">
            <span>CPU 使用率</span>
            <span class="resource-meta">{{ overview.cpuCount }} 核</span>
          </div>
          <el-progress :percentage="clamp(overview.cpuUsage)" :color="progressColor(overview.cpuUsage)"
            :stroke-width="8" :show-text="false" />
          <div class="resource-foot">
            宿主机 {{ fixed(overview.cpuUsage) }}% · 应用 {{ fixed(overview.processCpuUsage) }}%
          </div>
        </div>

        <div class="resource-card">
          <div class="resource-head">
            <span>堆内存</span>
            <span class="resource-meta">{{ formatBytes(overview.heapMax) }}</span>
          </div>
          <el-progress :percentage="clamp(overview.heapUsage)" :color="progressColor(overview.heapUsage)"
            :stroke-width="8" :show-text="false" />
          <div class="resource-foot">
            已用 {{ formatBytes(overview.heapUsed) }} · {{ fixed(overview.heapUsage) }}%
          </div>
        </div>

        <div class="resource-card">
          <div class="resource-head">
            <span>非堆内存</span>
            <span class="resource-meta">元空间 + 类空间 + 代码缓存</span>
          </div>
          <div class="resource-big">{{ formatBytes(overview.nonheapUsed) }}</div>
          <div class="resource-foot">{{ overview.jvmVersion || '—' }}</div>
        </div>

        <div class="resource-card">
          <div class="resource-head">
            <span>线程</span>
            <span class="resource-meta">阻塞 {{ overview.threadsBlocked ?? 0 }}</span>
          </div>
          <div class="resource-big">{{ overview.threadsLive ?? 0 }}</div>
          <div class="resource-foot">
            {{ overview.threadsBlocked ? '存在阻塞线程，注意排查' : '无阻塞线程' }}
          </div>
        </div>

        <div class="resource-card">
          <div class="resource-head">
            <span>磁盘</span>
            <span class="resource-meta">{{ formatBytes(overview.diskTotal) }}</span>
          </div>
          <el-progress :percentage="clamp(diskUsage)" :color="progressColor(diskUsage)" :stroke-width="8"
            :show-text="false" />
          <div class="resource-foot">
            可用 {{ formatBytes(overview.diskFree) }} · {{ fixed(diskUsage) }}%
          </div>
        </div>

        <div class="resource-card">
          <div class="resource-head">
            <span>运行时长</span>
            <span class="resource-meta">{{ overview.osName || '—' }}</span>
          </div>
          <div class="resource-big">{{ formatUptime(overview.uptime) }}</div>
          <div class="resource-foot">采集于 {{ overview.sampleTime || '—' }}</div>
        </div>
      </div>

      <!-- 趋势 -->
      <div class="chart-grid">
        <div class="panel">
          <div class="panel-head">
            <span>资源趋势</span>
            <span class="panel-hint">CPU 与堆使用率（%）</span>
          </div>
          <div ref="resourceChartRef" class="chart"></div>
          <div v-if="!hasResourceData" class="chart-empty">暂无数据</div>
        </div>

        <div class="panel">
          <div class="panel-head">
            <span>请求趋势</span>
            <span class="panel-hint">QPS（左轴）与 P95 延迟（右轴）</span>
          </div>
          <div ref="requestChartRef" class="chart"></div>
          <div v-if="!hasRequestData" class="chart-empty">暂无数据</div>
        </div>
      </div>

      <!-- 组件 -->
      <div class="component-grid">
        <div class="panel">
          <div class="panel-head">
            <span>组件状态</span>
            <span class="panel-hint">每 60 秒探测</span>
          </div>
          <div class="component-list">
            <div v-for="item in components.components" :key="item.component" class="component-item">
              <span class="lamp" :class="lampClass(item.status)"></span>
              <span class="component-name">{{ item.componentName }}</span>
              <span class="component-status" :class="lampClass(item.status)">
                {{ statusText(item.status) }}
              </span>
              <span class="component-detail">{{ item.detail || '' }}</span>
            </div>
            <div v-if="components.components.length === 0" class="empty-text">暂无组件状态</div>
          </div>
        </div>

        <div class="panel">
          <div class="panel-head">
            <span>状态变更</span>
            <span class="panel-hint">仅记录状态变化，正常时不产生记录</span>
          </div>
          <div class="change-list">
            <div v-for="(item, index) in components.changes" :key="index" class="change-item">
              <span class="lamp" :class="lampClass(item.status)"></span>
              <span class="change-name">{{ item.componentName }}</span>
              <span class="change-desc">{{ item.detail || statusText(item.status) }}</span>
              <span class="change-time">{{ formatDateTime(item.changeTime) }}</span>
            </div>
            <div v-if="components.changes.length === 0" class="empty-text">近期没有状态变化</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { Refresh } from '@element-plus/icons-vue'
import { getMonitorComponentsApi, getMonitorOverviewApi, getMonitorTrendApi } from '@/api/actuator'
import type { ComponentOverviewVO, MetricOverviewVO, TrendSeriesVO } from '@/types/actuator'

const REFRESH_INTERVAL = 30_000

const rangeOptions = [
  { label: '15 分钟', value: '15m' },
  { label: '1 小时', value: '1h' },
  { label: '6 小时', value: '6h' },
  { label: '1 天', value: '1d' },
  { label: '7 天', value: '7d' },
  { label: '30 天', value: '30d' }
]

const range = ref('15m')
const loading = ref(false)

const overview = reactive<MetricOverviewVO>({})
const components = reactive<ComponentOverviewVO>({ components: [], changes: [] })
const seriesList = ref<TrendSeriesVO[]>([])

const resourceChartRef = ref<HTMLElement | null>(null)
const requestChartRef = ref<HTMLElement | null>(null)
let resourceChart: echarts.ECharts | null = null
let requestChart: echarts.ECharts | null = null
let timer: number | null = null

// ==================== 派生状态 ====================

const diskUsage = computed(() => {
  const total = overview.diskTotal ?? 0
  const free = overview.diskFree ?? 0
  if (total <= 0) return 0
  return ((total - free) / total) * 100
})

const overallLevel = computed(() => {
  const items = components.components
  if (items.length === 0) return 'unknown'
  if (items.some(item => item.status === 'DOWN')) return 'down'
  if (items.some(item => item.status === 'UNKNOWN')) return 'unknown'
  return 'up'
})

const overallText = computed(() => {
  const textMap: Record<string, string> = {
    up: '整体正常',
    down: '存在异常组件',
    unknown: '状态待确认'
  }
  return textMap[overallLevel.value] || '状态待确认'
})

const appCards = computed(() => [
  {
    label: '请求 QPS',
    value: fixed(overview.qps),
    unit: '次/秒',
    sub: '最近 30 秒',
    level: ''
  },
  {
    label: 'P95 延迟',
    value: overview.p95Ms ?? 0,
    unit: 'ms',
    sub: (overview.p95Ms ?? 0) > 800 ? '超过 800ms 阈值' : '阈值 800ms',
    level: (overview.p95Ms ?? 0) > 800 ? 'warning' : ''
  },
  {
    label: '5xx 错误率',
    value: fixed(overview.errorRate),
    unit: '%',
    sub: (overview.errorRate ?? 0) > 5 ? '错误率偏高' : '正常',
    level: (overview.errorRate ?? 0) > 5 ? 'danger' : ''
  },
  {
    label: '连接池等待',
    value: overview.poolPending ?? 0,
    unit: '个',
    sub: `活跃 ${overview.poolActive ?? 0} / 上限 ${overview.poolMax ?? 0}`,
    level: (overview.poolPending ?? 0) > 0 ? 'danger' : ''
  },
  {
    label: 'GC 停顿',
    value: overview.gcPauseMs ?? 0,
    unit: 'ms',
    sub: `最近 30 秒 ${overview.gcCount ?? 0} 次`,
    level: (overview.gcPauseMs ?? 0) > 500 ? 'warning' : ''
  }
])

const resourceSeries = computed(() => seriesList.value.filter(item => ['cpu_usage', 'heap_usage'].includes(item.key)))
const requestSeries = computed(() => seriesList.value.filter(item => ['qps', 'p95_ms'].includes(item.key)))
const hasResourceData = computed(() => resourceSeries.value.some(item => item.points.length > 0))
const hasRequestData = computed(() => requestSeries.value.some(item => item.points.length > 0))

// ==================== 数据加载 ====================

const loadOverview = async () => {
  const data = await getMonitorOverviewApi()
  if (data) {
    Object.assign(overview, data)
  }
}

const loadComponents = async () => {
  const data = await getMonitorComponentsApi()
  if (data) {
    components.components = data.components || []
    components.changes = data.changes || []
  }
}

const loadTrend = async () => {
  const metrics = 'cpu_usage,heap_usage,qps,p95_ms'
  seriesList.value = await getMonitorTrendApi(range.value, metrics)
  await nextTick()
  renderCharts()
}

const refreshAll = async () => {
  loading.value = true
  try {
    await Promise.all([loadOverview(), loadComponents(), loadTrend()])
  } catch (error) {
    console.error('刷新监控数据失败:', error)
  } finally {
    loading.value = false
  }
}

// ==================== 图表 ====================

const renderCharts = () => {
  if (resourceChartRef.value) {
    resourceChart = resourceChart || echarts.init(resourceChartRef.value)
    resourceChart.setOption(buildOption(resourceSeries.value, false), true)
  }
  if (requestChartRef.value) {
    requestChart = requestChart || echarts.init(requestChartRef.value)
    requestChart.setOption(buildOption(requestSeries.value, true), true)
  }
}

const buildOption = (list: TrendSeriesVO[], dualAxis: boolean): echarts.EChartsOption => {
  const axisStyle = {
    axisLine: { lineStyle: { color: '#E5E7EB' } },
    axisTick: { show: false },
    axisLabel: { color: '#8A8F99', fontSize: 11 }
  }

  return {
    grid: { left: 8, right: dualAxis ? 8 : 8, top: 34, bottom: 6, containLabel: true },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'line', lineStyle: { color: '#C9CDD4' } },
      formatter: (params: any) => {
        const rows = Array.isArray(params) ? params : [params]
        if (rows.length === 0) return ''
        const time = dayjs(rows[0].value[0]).format('MM-DD HH:mm:ss')
        return [time, ...rows.map((row: any) => `${row.marker}${row.seriesName}：${row.value[1]}`)].join('<br/>')
      }
    },
    legend: {
      top: 0,
      left: 'center',
      itemWidth: 12,
      itemHeight: 8,
      textStyle: { fontSize: 12, color: '#5F6570' }
    },
    xAxis: { type: 'time', ...axisStyle },
    yAxis: dualAxis
      ? [
        { type: 'value', name: 'QPS', nameTextStyle: { color: '#8A8F99', fontSize: 11 }, splitLine: { lineStyle: { color: '#F1F2F4' } }, ...axisStyle },
        { type: 'value', name: 'ms', nameTextStyle: { color: '#8A8F99', fontSize: 11 }, splitLine: { show: false }, ...axisStyle }
      ]
      : { type: 'value', splitLine: { lineStyle: { color: '#F1F2F4' } }, ...axisStyle },
    series: list.map((item, index) => ({
      name: item.name,
      type: 'line',
      smooth: true,
      showSymbol: false,
      lineStyle: { width: 1.6 },
      areaStyle: index === 0 ? { opacity: 0.08 } : undefined,
      data: item.points.map(point => [point.t, point.v]),
      yAxisIndex: dualAxis && index === 1 ? 1 : 0
    }))
  }
}

// ==================== 工具方法 ====================

const clamp = (value?: number) => Math.min(Math.max(Math.round(value ?? 0), 0), 100)

const fixed = (value?: number, digits = 2) => (value === undefined || value === null ? '0' : Number(value).toFixed(digits))

const progressColor = (value?: number) => {
  const percent = value ?? 0
  if (percent >= 85) return '#F56C6C'
  if (percent >= 70) return '#E6A23C'
  return '#4F6EF2'
}

const formatBytes = (bytes?: number) => {
  if (!bytes || bytes <= 0) return '—'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const index = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1)
  return `${(bytes / Math.pow(1024, index)).toFixed(index === 0 ? 0 : 1)} ${units[index]}`
}

const formatUptime = (seconds?: number) => {
  const total = seconds ?? 0
  const days = Math.floor(total / 86400)
  const hours = Math.floor((total % 86400) / 3600)
  const minutes = Math.floor((total % 3600) / 60)
  if (days > 0) return `${days} 天 ${hours} 小时`
  if (hours > 0) return `${hours} 小时 ${minutes} 分`
  return `${minutes} 分`
}

const formatDateTime = (value?: string) => (value ? dayjs(value).format('MM-DD HH:mm:ss') : '—')

const lampClass = (status: string) => {
  if (status === 'UP') return 'up'
  if (status === 'DOWN') return 'down'
  if (status === 'DISABLED') return 'disabled'
  return 'unknown'
}

const statusText = (status: string) => {
  const textMap: Record<string, string> = {
    UP: '正常',
    DOWN: '异常',
    DISABLED: '未启用',
    UNKNOWN: '未知'
  }
  return textMap[status] || status
}

// ==================== 生命周期 ====================

const handleResize = () => {
  resourceChart?.resize()
  requestChart?.resize()
}

onMounted(async () => {
  await refreshAll()
  window.addEventListener('resize', handleResize)
  timer = window.setInterval(() => {
    loadOverview().catch(() => undefined)
    loadComponents().catch(() => undefined)
  }, REFRESH_INTERVAL)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (timer !== null) {
    window.clearInterval(timer)
  }
  resourceChart?.dispose()
  requestChart?.dispose()
  resourceChart = null
  requestChart = null
})
</script>

<style scoped>
.monitor-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.status-pill {
  padding: 2px 10px;
  border-radius: var(--radish-radius-round);
  font-size: var(--radish-font-xs);
  font-weight: 500;
}

.status-pill.up {
  background: var(--radish-status-up-bg);
  color: var(--radish-status-up-text);
}

.status-pill.down {
  background: var(--radish-danger-light);
  color: var(--radish-status-down-text);
}

.status-pill.unknown {
  background: var(--radish-status-unknown-bg);
  color: var(--radish-status-unknown-text);
}

.section-title {
  display: flex;
  align-items: baseline;
  gap: 8px;
  font-size: var(--radish-font-sm);
  font-weight: 600;
  color: var(--radish-text-primary);
}

.section-hint,
.panel-hint {
  font-size: var(--radish-font-xs);
  font-weight: 400;
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  padding: 14px 16px;
  box-shadow: var(--radish-shadow-card);
}

.metric-label {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.metric-value {
  margin-top: 2px;
  font-size: var(--radish-font-2xl);
  font-weight: 600;
  color: var(--radish-text-primary);
  line-height: 1.3;
}

.metric-value.warning {
  color: var(--radish-warning);
}

.metric-value.danger {
  color: var(--radish-danger);
}

.metric-unit {
  margin-left: 2px;
  font-size: var(--radish-font-xs);
  font-weight: 400;
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

.metric-sub {
  margin-top: 2px;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.resource-card {
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  padding: 14px 16px;
  box-shadow: var(--radish-shadow-card);
}

.resource-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
  margin-bottom: 10px;
}

.resource-meta {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

.resource-big {
  font-size: var(--radish-font-2xl);
  font-weight: 600;
  color: var(--radish-text-primary);
}

.resource-foot {
  margin-top: 8px;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.chart-grid,
.component-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.panel {
  position: relative;
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  padding: 14px 16px;
  box-shadow: var(--radish-shadow-card);
}

.panel-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: var(--radish-font-sm);
  font-weight: 600;
  color: var(--radish-text-primary);
}

.chart {
  width: 100%;
  height: 220px;
}

.chart-empty {
  position: absolute;
  inset: 80px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
  pointer-events: none;
}

.component-list,
.change-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 4px;
}

.component-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
}

.lamp {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
  background: var(--radish-text-placeholder);
}

.lamp.up {
  background: var(--radish-success);
}

.lamp.down {
  background: var(--radish-danger);
}

.lamp.disabled {
  background: var(--radish-text-placeholder);
}

.lamp.unknown {
  background: var(--radish-warning);
}

.component-name {
  width: 90px;
}

.component-status {
  font-size: var(--radish-font-xs);
  color: var(--radish-success);
}

.component-status.down {
  color: var(--radish-danger);
}

.component-status.disabled {
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

.component-status.unknown {
  color: var(--radish-warning);
}

.component-detail {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.change-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
}

.change-name {
  width: 90px;
}

.change-desc {
  flex: 1;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.change-time {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

.empty-text {
  padding: 12px 0;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-tertiary, var(--radish-text-secondary));
}

@media (max-width: 1400px) {
  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .resource-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
