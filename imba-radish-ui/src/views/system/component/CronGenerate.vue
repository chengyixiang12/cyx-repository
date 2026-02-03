<template>
  <div class="cron-generator-container">
    <!-- 时间单位选择 -->
    <div class="time-unit-selector">
      <el-radio-group v-model="activeTimeUnit" size="small" @change="handleTimeUnitChange">
        <el-radio-button label="second">秒</el-radio-button>
        <el-radio-button label="minute">分钟</el-radio-button>
        <el-radio-button label="hour">小时</el-radio-button>
        <el-radio-button label="day">日</el-radio-button>
        <el-radio-button label="month">月</el-radio-button>
        <el-radio-button label="week">周</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 配置选项区域 -->
    <div class="config-options">
      <!-- 通配符选项 -->
      <el-radio v-model="configType" label="wildcard" class="config-option">
        {{ getUnitLabel() }}允许的通配符[*, - /]
      </el-radio>

      <!-- 范围选项 -->
      <el-radio v-model="configType" label="range" class="config-option">
        周期从
        <el-input-number v-model="rangeStart" :min="minValue" :max="maxValue" size="mini"
          class="input-number"></el-input-number>
        到
        <el-input-number v-model="rangeEnd" :min="rangeStart" :max="maxValue" size="mini"
          class="input-number"></el-input-number>
        {{ getUnitLabel() }}
      </el-radio>

      <!-- 间隔选项 -->
      <el-radio v-model="configType" label="interval" class="config-option">
        周期从
        <el-input-number v-model="intervalStart" :min="minValue" :max="maxValue" size="mini"
          class="input-number"></el-input-number>
        {{ getUnitLabel() }}开始，每
        <el-input-number v-model="intervalStep" :min="1" :max="maxValue - intervalStart" size="mini"
          class="input-number"></el-input-number>
        {{ getUnitLabel() }}执行一次
      </el-radio>

      <!-- 指定选项 -->
      <el-radio v-model="configType" label="specific" class="config-option">
        指定
        <div class="specific-values">
          <el-checkbox v-for="num in allValues" :key="num" v-model="checkedValuesProxy" :label="num"
            :disabled="!isValidValue(num)" class="value-checkbox" size="small">
            {{ formatNumber(num) }}
          </el-checkbox>
        </div>
      </el-radio>
    </div>

    <!-- 表达式展示区域 -->
    <div class="expression-area">

      <!-- 最近运行时间 -->
      <div class="next-runs">
        <h3 class="section-title">最近5次运行时间</h3>
        <ul class="run-time-list">
          <li v-for="(time, index) in nextRunTimes" :key="index">
            <el-icon size="14" class="time-icon">
              <Clock />
            </el-icon>
            {{ time }}
          </li>
        </ul>
      </div>
    </div>

    <!-- 在 </div> 结束标签之前添加 -->
    <div class="drawer-footer">
      <el-button @click="cancelCron">取消</el-button>
      <el-button type="primary" @click="confirmCron">确定</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { Clock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { parseCronApi } from '@/api/quartz';

// 时间单位配置
const timeUnits = {
  second: { min: 0, max: 59, label: '秒' },
  minute: { min: 0, max: 59, label: '分钟' },
  hour: { min: 0, max: 23, label: '小时' },
  day: { min: 1, max: 31, label: '日' },
  month: { min: 1, max: 12, label: '月' },
  week: { min: 1, max: 7, label: '星期' }
};

// 状态变量
const activeTimeUnit = ref<'second' | 'minute' | 'hour' | 'day' | 'month' | 'week'>('minute');
const configType = ref<'wildcard' | 'range' | 'interval' | 'specific'>('specific');
const rangeStart = ref(0);
const rangeEnd = ref(59);
const intervalStart = ref(0);
const intervalStep = ref(10);
const checkedValues = ref<number[]>([0]);
const cronExpression = ref<string[]>(['0', '0', '0', '*', '*', '?']);
const nextRunTimes = ref<string[]>([]);

// 计算属性
const currentUnitConfig = computed(() => timeUnits[activeTimeUnit.value]);
const minValue = computed(() => currentUnitConfig.value.min);
const maxValue = computed(() => currentUnitConfig.value.max);
const allValues = computed(() => {
  const values: number[] = [];
  for (let i = minValue.value; i <= maxValue.value; i++) {
    values.push(i);
  }
  return values;
});

const fullCronExpression = computed({
  get: () => cronExpression.value.join(' '),
  set: (val) => {
    const parts = val.split(' ');
    if (parts.length === 6) {
      cronExpression.value = parts;
    }
  }
});

const emit = defineEmits<{
  (e: 'confirm', cronExpression: string): void
  (e: 'cancel'): void
}>()

// 添加确认和取消方法
const confirmCron = () => {
  emit('confirm', fullCronExpression.value)
}

const cancelCron = () => {
  emit('cancel')
}

const getUnitLabel = () => currentUnitConfig.value.label;

const formatNumber = (num: number) => {
  return num.toString();
};

const isValidValue = (value: number) => {
  return value >= minValue.value && value <= maxValue.value;
};

// 添加一个新的计算属性来决定默认配置类型
const getDefaultConfigType = (unit: string) => {
  if (['second', 'minute', 'hour'].includes(unit)) {
    return 'specific';
  }
  return 'wildcard';
};

const handleTimeUnitChange = () => {
  const index = getCronIndexByUnit(activeTimeUnit.value);
  const currentValue = cronExpression.value[index];

  if (currentValue === '*' || currentValue === '?') {
    configType.value = getDefaultConfigType(activeTimeUnit.value);
  } else if (currentValue.includes('/')) {
    const [start, step] = currentValue.split('/').map(Number);
    intervalStart.value = start;
    intervalStep.value = step;
    configType.value = 'interval';
  } else if (currentValue.includes('-')) {
    const [start, end] = currentValue.split('-').map(Number);
    rangeStart.value = start;
    rangeEnd.value = end;
    configType.value = 'range';
  } else if (currentValue.includes(',')) {
    checkedValues.value = currentValue.split(',').map(Number);
    configType.value = 'specific';
  } else {
    const numValue = Number(currentValue);
    if (!isNaN(numValue)) {
      checkedValues.value = [numValue];
      configType.value = 'specific';
    } else {
      configType.value = 'wildcard';
    }
  }
  
  // 设置默认勾选值
  if (['second', 'minute', 'hour'].includes(activeTimeUnit.value) && 
      configType.value === 'specific' && 
      checkedValues.value.length === 0) {
    checkedValues.value = [0];
  }
};

const getCronIndexByUnit = (unit: string) => {
  const unitMap: Record<string, number> = {
    second: 0,
    minute: 1,
    hour: 2,
    day: 3,
    month: 4,
    week: 5
  };
  return unitMap[unit];
};

const checkedValuesProxy = computed({
  get: () => checkedValues.value,
  set: (val: number[]) => {
    if (val.length === 0) {
      ElMessage.warning('至少需要选择一个值');
      return;
    }
    checkedValues.value = val;
  }
});

const generateExpressionPart = () => {
  const index = getCronIndexByUnit(activeTimeUnit.value);
  let part = '*';

  switch (configType.value) {
    case 'wildcard':
      part = '*';
      break;
    case 'range':
      part = `${rangeStart.value}-${rangeEnd.value}`;
      break;
    case 'interval':
      part = `${intervalStart.value}/${intervalStep.value}`;
      break;
    case 'specific':
      part = checkedValues.value.sort((a, b) => a - b).join(',');
      break;
  }

  if (activeTimeUnit.value === 'week' && part === '*') {
    part = '?';
  }

  cronExpression.value[index] = part;
  calculateNextRunTimes();
};

const calculateNextRunTimes = async () => {
  try {
    let cronExpress = fullCronExpression.value.trim();


    console.log('cronExpress', cronExpress);

    if (!cronExpress || cronExpress === '') return;

    // 解析 Quartz Cron 表达式
    const cronFormate = await parseCronApi(cronExpress);


    const times: string[] = [];
    const seenTimes = new Set<string>();

    // 获取未来 5 次不重复的执行时间
    for (let i = 0; i < 5; i++) {
      try {
        const next = cronFormate[i];

        if (!seenTimes.has(next)) {
          seenTimes.add(next);
          times.push(next);
        }
      } catch {
        break;
      }
    }

    nextRunTimes.value = times.length > 0 ? times : ['暂无匹配的执行时间'];
  } catch (error) {
    console.error('Cron解析错误:', error);
    nextRunTimes.value = ['表达式格式错误（需6字段：秒 分 时 日 月 周）'];
  }
};

watch([configType, rangeStart, rangeEnd, intervalStart, intervalStep, checkedValues], generateExpressionPart);

onMounted(() => {
  configType.value = getDefaultConfigType(activeTimeUnit.value);
  calculateNextRunTimes();
});
</script>

<style scoped>
.cron-generator-container {
  height: 100%;
  box-sizing: border-box;
  padding: 10px;
}

.time-unit-selector {
  margin-bottom: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: thin;
}

.time-unit-selector :deep(.el-radio-button__inner) {
  padding: 8px 16px;
  font-size: 14px;
}

.config-options {
  background-color: #f5f7fa;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 15px;
  height: auto;
  max-height: 30vh;
  overflow: auto;
}

.config-option {
  display: block;
  margin-bottom: 8px;
  padding: 6px 0;
  line-height: 1.4;
  font-size: 13px;
}

.input-number {
  width: 100px;
  margin: 0 6px;
}

::v-deep .el-input-number {
  width: 100px;
  height: 26px;
}

.specific-values {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  row-gap: 6px;
}

.expression-area {
  margin-top: 16px;
}

.section-title {
  font-size: 14px;
  color: #333;
  margin: 0 0 12px 0;
  font-weight: 500;
}

.cron-result {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  width: 200px;
}

.cron-result .el-input {
  flex: 1;
}

.parse-btn {
  white-space: nowrap;
}

.next-runs {
  margin-top: 16px;
}

.run-time-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.run-time-list li {
  padding: 6px 0;
  font-size: 13px;
  color: #444;
  display: flex;
  align-items: center;
}

.time-icon {
  margin-right: 6px;
  color: #4ecdc4;
}

.drawer-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  text-align: right;
  border-top: 1px solid #e8eaec;
  background-color: #fff;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .config-options {
    padding: 10px;
    max-height: 22vh;
  }

  .config-option {
    margin-bottom: 6px;
    padding: 5px 0;
    font-size: 12px;
  }

  .input-number {
    width: 60px;
  }

  ::v-deep .el-input-number {
    width: 60px;
    height: 24px;
  }

  .value-checkbox {
    width: 36px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .config-options {
    padding: 8px;
    max-height: 20vh;
  }

  .config-option {
    font-size: 12px;
  }

  .input-number {
    width: 55px;
  }

  ::v-deep .el-input-number {
    width: 55px;
    height: 22px;
  }

  ::v-deep .el-input-number__input {
    font-size: 11px;
  }
}
</style>