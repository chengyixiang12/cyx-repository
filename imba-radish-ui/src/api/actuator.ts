import { ComponentOverviewVO, MetricOverviewVO, TrendSeriesVO } from '@/types/actuator'
import { get } from '@/utils/http'

/**
 * 实时总览（后端读内存快照，不查库）
 */
export async function getMonitorOverviewApi(): Promise<MetricOverviewVO> {
  const res = await get<MetricOverviewVO>('/actuator/overview', { flag: true })
  return res.data
}

/**
 * 指标趋势
 * @param range 时间范围：15m/1h/6h/1d/7d/30d
 * @param metrics 指标标识，逗号分隔
 */
export async function getMonitorTrendApi(range: string, metrics: string): Promise<TrendSeriesVO[]> {
  const res = await get<TrendSeriesVO[]>('/actuator/trend', { params: { range, metrics }, flag: true })
  return res.data || []
}

/**
 * 组件当前状态与最近的状态变更
 */
export async function getMonitorComponentsApi(): Promise<ComponentOverviewVO> {
  const res = await get<ComponentOverviewVO>('/actuator/components', { flag: true })
  return res.data
}
