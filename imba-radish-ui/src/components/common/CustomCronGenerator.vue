<template>
  <div class="cron-generator">
    <el-form label-width="100px">
      <el-form-item label="秒">
        <el-input v-model="cron.second" placeholder="例如：0/5" />
      </el-form-item>
      <el-form-item label="分">
        <el-input v-model="cron.minute" placeholder="例如：0/10" />
      </el-form-item>
      <el-form-item label="小时">
        <el-input v-model="cron.hour" placeholder="例如：8" />
      </el-form-item>
      <el-form-item label="日">
        <el-input v-model="cron.dayOfMonth" placeholder="例如：*" />
      </el-form-item>
      <el-form-item label="月">
        <el-input v-model="cron.month" placeholder="例如：1-12" />
      </el-form-item>
      <el-form-item label="周几">
        <el-input v-model="cron.dayOfWeek" placeholder="例如：1-5" />
      </el-form-item>
      <el-form-item label="年（可选）">
        <el-input v-model="cron.year" placeholder="例如：2025" />
      </el-form-item>
    </el-form>
    <p style="color: green;">生成的 Cron：{{ cronExpression }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const cron = ref({
  second: '0',
  minute: '0',
  hour: '0',
  dayOfMonth: '*',
  month: '*',
  dayOfWeek: '?',
  year: ''
})

const cronExpression = computed(() => {
  const parts = [
    cron.value.second,
    cron.value.minute,
    cron.value.hour,
    cron.value.dayOfMonth,
    cron.value.month,
    cron.value.dayOfWeek
  ]
  if (cron.value.year) parts.push(cron.value.year)
  return parts.join(' ')
})

// 初始化
cron.value = parseCron(props.modelValue)

function parseCron(str: string) {
  const parts = str.trim().split(/\s+/)
  return {
    second: parts[0] || '0',
    minute: parts[1] || '0',
    hour: parts[2] || '0',
    dayOfMonth: parts[3] || '*',
    month: parts[4] || '*',
    dayOfWeek: parts[5] || '?',
    year: parts[6] || ''
  }
}
</script>