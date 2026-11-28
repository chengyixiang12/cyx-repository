<template>
  <!-- 卡片内列表(公告/待办/日志流)的骨架: 标题行 + 时间行 -->
  <div v-if="visible" class="list-skeleton">
    <div v-for="i in lines" :key="i" class="ls-item">
      <span class="sk-bar ls-title" :style="{ width: titleWidth(i) }" />
      <span class="sk-bar ls-meta" :style="{ width: metaWidth(i) }" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  loading: boolean
  count?: number
  lines?: number
}>(), {
  count: 0,
  lines: 5
})

const visible = computed(() => props.loading && props.count === 0)

const titleWidth = (i: number) => `${52 + ((i * 23) % 38)}%`
const metaWidth = (i: number) => `${18 + ((i * 11) % 16)}%`
</script>

<style scoped>
.list-skeleton {
  display: flex;
  flex-direction: column;
}

.ls-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 11px 0;
  border-bottom: 1px solid var(--radish-border-light);
}

.ls-item:last-child {
  border-bottom: none;
}

.ls-title {
  height: 12px;
}

.ls-meta {
  height: 10px;
  flex-shrink: 0;
}
</style>
