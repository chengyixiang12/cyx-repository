<template>
  <!-- 首次加载(无数据)时覆盖在表格上方, 保持表格轮廓, 避免整块空白转圈 -->
  <div v-if="visible" class="table-skeleton" :class="{ 'is-border': border }">
    <div v-if="showHeader" class="ts-row ts-head">
      <div v-for="c in columns" :key="`h-${c}`" class="ts-cell">
        <span class="sk-bar" :style="{ width: headerWidth(c), height: '9px' }" />
      </div>
    </div>

    <div v-for="r in rows" :key="`r-${r}`" class="ts-row">
      <div v-for="c in columns" :key="`c-${r}-${c}`" class="ts-cell">
        <span v-if="avatar && c === 1" class="sk-avatar" />
        <span class="sk-bar" :style="{ width: cellWidth(r, c) }" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  /** 是否正在加载 */
  loading: boolean
  /** 当前已有数据条数: 已有数据时不再显示骨架(翻页/刷新交给 v-loading 遮罩, 避免页面跳动) */
  count?: number
  /** 骨架行数 */
  rows?: number
  /** 列数, 与真实表格保持一致 */
  columns?: number
  /** 是否显示表头 */
  showHeader?: boolean
  /** 是否显示单元格竖线(对应 el-table 的 border) */
  border?: boolean
  /** 第一列是否为头像/圆形 */
  avatar?: boolean
}>(), {
  count: 0,
  rows: 9,
  columns: 6,
  showHeader: true,
  border: true,
  avatar: false
})

const visible = computed(() => props.loading && props.count === 0)

// 宽度按行列做确定性伪随机, 让骨架看起来像真实数据而不是整齐的方块
const headerWidth = (c: number) => `${48 + ((c * 17) % 26)}%`
const cellWidth = (r: number, c: number) => `${32 + ((r * 13 + c * 29) % 52)}%`
</script>

<style scoped>
.table-skeleton {
  position: absolute;
  inset: 0;
  z-index: 5;
  background: var(--radish-bg-card);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ts-row {
  display: flex;
  align-items: center;
  height: 40px;
  flex-shrink: 0;
  border-bottom: 1px solid var(--radish-border-light);
}

.ts-head {
  background: var(--radish-fill);
  border-bottom: 1px solid var(--radish-border);
}

.ts-cell {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 100%;
  padding: 0 12px;
}

.is-border .ts-cell + .ts-cell {
  border-left: 1px solid var(--radish-border-light);
}

.is-border .ts-head .ts-cell + .ts-cell {
  border-left-color: var(--radish-border);
}

/* 表头下第一行稍浅, 更接近真实表格的行分隔 */
.ts-row:nth-child(2) {
  border-bottom-color: var(--radish-border-light);
}
</style>
