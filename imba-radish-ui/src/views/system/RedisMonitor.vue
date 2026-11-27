<template>
  <div class="redis-monitor container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>Redis 缓存监控</span>
        <span class="readonly-pill">只读模式 · 敏感值脱敏</span>
      </div>
      <div class="right-header">
        <el-button type="primary" :loading="loading" @click="refreshAll">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
    </div>

    <div class="monitor-body">
      <!-- 总览指标 -->
      <div class="metric-grid">
        <div class="metric-card">
          <div class="metric-label">key 总数</div>
          <div class="metric-value">{{ overview.dbSize ?? 0 }}</div>
          <div class="metric-sub">当前数据库</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">内存占用</div>
          <div class="metric-value">{{ overview.usedMemoryHuman || '—' }}</div>
          <div class="metric-sub">上限 {{ overview.maxMemoryHuman || 'unlimited' }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">命中率</div>
          <div class="metric-value" :class="hitRateClass">
            {{ overview.hitRate !== undefined && overview.hitRate !== null ? overview.hitRate + '%' : '—' }}
          </div>
          <div class="metric-sub">命中 {{ overview.keyspaceHits ?? 0 }} / 未命中 {{ overview.keyspaceMisses ?? 0 }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">连接客户端</div>
          <div class="metric-value">{{ overview.connectedClients ?? '—' }}</div>
          <div class="metric-sub">碎片率 {{ overview.memFragmentationRatio ?? '—' }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">Redis 版本</div>
          <div class="metric-value version">{{ overview.redisVersion || '—' }}</div>
          <div class="metric-sub">已运行 {{ overview.uptimeDays ?? '—' }} 天</div>
        </div>
      </div>

      <!-- 前缀分组 -->
      <div class="panel">
        <div class="panel-head">
          <span>缓存分组</span>
          <span class="panel-hint">按业务前缀统计，点击「查看 key」进入对应分组</span>
        </div>
        <el-table :data="groups" v-loading="loading" size="default" @row-click="openGroup">
          <el-table-column prop="prefix" label="前缀" min-width="220">
            <template #default="{ row }">
              <span class="key-text">{{ row.prefix }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="businessName" label="业务含义" min-width="200" />
          <el-table-column prop="keyCount" label="key 数量" width="110" align="right">
            <template #default="{ row }">
              <span class="count-text">{{ row.keyCount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="敏感性" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.sensitive" type="warning" size="small">脱敏</el-tag>
              <el-tag v-else type="success" size="small">可查看</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click.stop="openGroup(row)">查看 key</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- key 列表 -->
      <div class="panel">
        <div class="panel-head">
          <span>key 列表</span>
          <span v-if="keyList.truncated" class="panel-hint warning-text">
            数据量较大，仅展示前 {{ SCAN_LIMIT }} 条匹配结果
          </span>
        </div>
        <div class="filter-bar">
          <el-select v-model="filterPrefix" clearable filterable placeholder="按前缀过滤" style="width: 240px" @change="loadKeys">
            <el-option v-for="group in groups" :key="group.prefix" :label="`${group.businessName}（${group.prefix}*）`"
              :value="group.prefix" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索 key 关键字" clearable style="width: 240px" @keyup.enter="loadKeys"
            @clear="loadKeys">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button @click="loadKeys">查询</el-button>
        </div>
        <el-table :data="keyList.records" v-loading="keysLoading" size="default">
          <el-table-column prop="key" label="key" min-width="320">
            <template #default="{ row }">
              <span class="key-text clickable" @click="openDetail(row)">{{ row.key }}</span>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small" :type="typeTagType(row.type)">{{ row.type.toUpperCase() }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="TTL" width="160">
            <template #default="{ row }">
              <span>{{ formatTtl(row.ttlSeconds) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="敏感性" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.sensitive" type="warning" size="small">脱敏</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pager">
          <el-pagination v-model:current-page="page" :page-size="pageSize" :total="keyList.total"
            layout="total, prev, pager, next, sizes" :page-sizes="[10, 20, 50, 100]" @current-change="loadKeys"
            @size-change="loadKeys" />
        </div>
      </div>
    </div>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" :title="detail?.key || 'key 详情'" size="560px">
      <div v-if="detail" class="detail-body">
        <el-alert v-if="detail.masked" type="warning" :closable="false" show-icon
          :title="'该 key 为敏感数据，值已脱敏'" :description="detail.maskReason" />
        <el-alert v-else-if="detail.parseFailed" type="warning" :closable="false" show-icon title="值解析失败"
          :description="detail.maskReason" />

        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="key">
            <span class="key-text">{{ detail.key }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="类型">
            <el-tag size="small" :type="typeTagType(detail.type)">{{ detail.type.toUpperCase() }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="TTL">{{ formatTtl(detail.ttlSeconds) }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.entryCount > 0" label="元素数量">
            {{ detail.entryCount }}{{ detail.truncated ? `（仅展示前 100 条）` : '' }}
          </el-descriptions-item>
        </el-descriptions>

        <template v-if="!detail.masked && !detail.parseFailed && detail.value !== null && detail.value !== undefined">
          <div class="value-title">值内容</div>
          <pre class="value-block">{{ formatValue(detail) }}</pre>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getRedisGroupsApi, getRedisKeysApi, getRedisOverviewApi, getRedisValueApi } from '@/api/redis'
import type { RedisKeyListVo, RedisKeyValueVo, RedisOverviewVo, RedisPrefixGroupVo } from '@/types/redis'

/** 与后端 SCAN_LIMIT 一致，仅用于截断提示文案 */
const SCAN_LIMIT = 5000

const loading = ref(false)
const keysLoading = ref(false)

const overview = ref<RedisOverviewVo>({})
const groups = ref<RedisPrefixGroupVo[]>([])
const keyList = ref<RedisKeyListVo>({ records: [], total: 0, truncated: false, scanned: 0 })

const filterPrefix = ref('')
const keyword = ref('')
const page = ref(1)
const pageSize = ref(20)

const drawerVisible = ref(false)
const detail = ref<RedisKeyValueVo | null>(null)

// ==================== 派生状态 ====================

const hitRateClass = computed(() => {
  const rate = overview.value.hitRate
  if (rate === undefined || rate === null) return ''
  if (rate < 50) return 'danger'
  if (rate < 80) return 'warning'
  return ''
})

// ==================== 数据加载 ====================

const loadOverview = async () => {
  overview.value = await getRedisOverviewApi()
}

const loadGroups = async () => {
  groups.value = await getRedisGroupsApi()
}

const loadKeys = async () => {
  keysLoading.value = true
  try {
    keyList.value = await getRedisKeysApi({
      prefix: filterPrefix.value || undefined,
      keyword: keyword.value || undefined,
      page: page.value,
      size: pageSize.value
    })
  } finally {
    keysLoading.value = false
  }
}

const refreshAll = async () => {
  loading.value = true
  try {
    await Promise.all([loadOverview(), loadGroups(), loadKeys()])
  } catch (error) {
    console.error('刷新Redis监控数据失败:', error)
  } finally {
    loading.value = false
  }
}

const openGroup = (row: RedisPrefixGroupVo) => {
  if (row.prefix === '（其他）') {
    filterPrefix.value = ''
  } else {
    filterPrefix.value = row.prefix
  }
  page.value = 1
  keyword.value = ''
  loadKeys()
  // 滚动到 key 列表面板
  document.querySelector('.filter-bar')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

const openDetail = async (row: { key: string }) => {
  detail.value = null
  drawerVisible.value = true
  detail.value = await getRedisValueApi(row.key)
}

// ==================== 工具方法 ====================

const formatTtl = (ttl?: number) => {
  if (ttl === -1) return '永不过期'
  if (ttl === -2) return '已过期 / 不存在'
  if (ttl === undefined || ttl === null) return '—'
  if (ttl < 60) return `${ttl} 秒`
  if (ttl < 3600) return `${Math.floor(ttl / 60)} 分 ${ttl % 60} 秒`
  if (ttl < 86400) return `${Math.floor(ttl / 3600)} 时 ${Math.floor((ttl % 3600) / 60)} 分`
  return `${Math.floor(ttl / 86400)} 天 ${Math.floor((ttl % 86400) / 3600)} 时`
}

const typeTagType = (type: string) => {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'info'> = {
    string: 'primary',
    hash: 'success',
    list: 'warning',
    set: 'danger',
    zset: 'info'
  }
  return map[type] || 'info'
}

const formatValue = (item: RedisKeyValueVo) => {
  const value = item.value
  if (value === null || value === undefined) return '（空）'
  if (typeof value === 'string') return value
  try {
    return JSON.stringify(value, null, 2)
  } catch {
    return String(value)
  }
}

// ==================== 生命周期 ====================

onMounted(refreshAll)
</script>

<style scoped>
.readonly-pill {
  padding: 2px 10px;
  border-radius: var(--radish-radius-round);
  font-size: var(--radish-font-xs);
  font-weight: 500;
  background: var(--radish-primary-light-9);
  color: var(--radish-primary);
}

.monitor-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
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

.metric-value.version {
  font-size: var(--radish-font-xl);
}

.metric-value.warning {
  color: var(--radish-warning);
}

.metric-value.danger {
  color: var(--radish-danger);
}

.metric-sub {
  margin-top: 2px;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
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
  margin-bottom: 10px;
  font-size: var(--radish-font-sm);
  font-weight: 600;
  color: var(--radish-text-primary);
}

.panel-hint {
  font-size: var(--radish-font-xs);
  font-weight: 400;
  color: var(--radish-text-secondary);
}

.warning-text {
  color: var(--radish-warning);
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--radish-space-2);
  margin-bottom: var(--radish-space-3);
}

.key-text {
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
  word-break: break-all;
}

.key-text.clickable {
  cursor: pointer;
}

.key-text.clickable:hover {
  color: var(--radish-primary);
}

.count-text {
  font-weight: 600;
  color: var(--radish-primary);
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--radish-space-3);
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: var(--radish-space-4);
}

.value-title {
  font-size: var(--radish-font-sm);
  font-weight: 600;
  color: var(--radish-text-primary);
}

.value-block {
  margin: 0;
  padding: var(--radish-space-3);
  background: var(--radish-fill);
  border: 1px solid var(--radish-border-light);
  border-radius: var(--radish-radius);
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: var(--radish-font-xs);
  line-height: 1.6;
  color: var(--radish-text-regular);
  max-height: 420px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

@media (max-width: 1400px) {
  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
