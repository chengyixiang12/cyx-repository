<template>
  <!-- 聊天记录首次加载时的占位: 模拟 AI 回答气泡的形态 -->
  <div v-if="visible" class="message-skeleton">
    <div v-for="i in rows" :key="i" class="ms-row">
      <span class="sk-avatar ms-avatar" />
      <div class="ms-bubble">
        <span v-for="l in lineCount(i)" :key="l" class="sk-bar ms-line" :style="{ width: lineWidth(i, l) }" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  loading: boolean
  count?: number
  rows?: number
}>(), {
  count: 0,
  rows: 3
})

const visible = computed(() => props.loading && props.count === 0)

const lineCount = (i: number) => 2 + ((i * 2) % 3)
const lineWidth = (i: number, l: number) => {
  const w = 55 + ((l * 19 + i * 11) % 42)
  // 每组最后一行收短
  return l === lineCount(i) ? `${Math.max(30, w - 28)}%` : `${w}%`
}
</script>

<style scoped>
.message-skeleton {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 4px 0;
}

.ms-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.ms-avatar {
  width: 34px;
  height: 34px;
  margin-top: 2px;
  opacity: 0.85;
}

.ms-bubble {
  max-width: 62%;
  min-width: 260px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px 18px;
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border);
  border-radius: var(--radish-radius-xl);
  border-bottom-left-radius: 4px;
  box-shadow: var(--radish-shadow-card);
}

.ms-line {
  height: 11px;
}
</style>
