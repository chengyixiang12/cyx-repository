export interface MetricOverviewVO {
  cpuCount?: number
  heapMax?: number
  diskTotal?: number
  jvmVersion?: string
  osName?: string
  cpuUsage?: number
  processCpuUsage?: number
  heapUsed?: number
  heapUsage?: number
  nonheapUsed?: number
  threadsLive?: number
  threadsBlocked?: number
  gcCount?: number
  gcPauseMs?: number
  qps?: number
  p95Ms?: number
  errorRate?: number
  poolActive?: number
  poolPending?: number
  poolMax?: number
  diskFree?: number
  uptime?: number
  sampleTime?: string
}

export interface TrendPointVO {
  t: number
  v: number
}

export interface TrendSeriesVO {
  key: string
  name: string
  unit: string
  points: TrendPointVO[]
}

export type ComponentState = 'UP' | 'DOWN' | 'DISABLED' | 'UNKNOWN'

export interface ComponentStatusVO {
  component: string
  componentName: string
  status: ComponentState | string
  detail?: string
  sinceTime?: string
  lastCheckTime?: string
}

export interface HealthChangeVO {
  componentName: string
  status: string
  detail?: string
  changeTime: string
}

export interface ComponentOverviewVO {
  components: ComponentStatusVO[]
  changes: HealthChangeVO[]
}
