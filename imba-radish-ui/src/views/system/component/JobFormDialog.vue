<template>
  <el-dialog :title="isAdd ? '新增任务' : '编辑任务'" v-model="visible" width="60vw" :close-on-click-modal="false"
    :before-close="handleClose" class="custom-dialog">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="right"
        class="scrollable-form">
        <!-- 任务名称 -->
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="formData.jobName" placeholder="请输入任务名称" />
        </el-form-item>

        <!-- 任务组 -->
        <el-form-item label="任务组" prop="jobGroup">
          <el-input v-model="formData.jobGroup" placeholder="请输入任务组名称" />
        </el-form-item>

        <el-form-item label="Cron表达式" prop="cron" v-if="formData.scheduleType === '1'">
          <!-- 关键：使用div包裹并设置明确高度，确保组件有足够空间渲染 -->
          <div class="cron-wrapper">
            <!-- Cron 生成器组件 -->
            <vue3-cron v-model="formData.cron" :disabled="false" @change="handleCronChange" class="cron-editor" style="width: 100%" />
          </div>
        </el-form-item>

        <!-- 任务类型 -->
        <el-form-item label="任务类型" prop="jobType">
          <el-input v-model="formData.jobType" placeholder="请输入任务类型" />
        </el-form-item>

        <!-- 调度类型 -->
        <el-form-item label="调度类型" prop="scheduleType">
          <el-radio-group v-model="formData.scheduleType" @change="handleScheduleTypeChange">
            <el-radio value="0">简单调度</el-radio>
            <el-radio value="1">Cron调度</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 任务状态 -->
        <el-form-item label="任务状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">暂停</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 任务参数 -->
        <el-form-item label="任务参数" prop="jobParam">
          <el-input v-model="formData.jobParam" type="textarea" :rows="3" placeholder="请输入任务所需参数（JSON格式）" />
        </el-form-item>

        <!-- 开始时间 -->
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>

        <!-- 结束时间 -->
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>

        <!-- 间隔时间 (仅在简单调度时显示) -->
        <el-form-item label="间隔时间" prop="jobInterval" v-if="formData.scheduleType === '0'">
          <el-input v-model.number="formData.jobInterval" placeholder="请输入间隔时间（单位：根据间隔类型）" />
        </el-form-item>

        <!-- 间隔类型 (仅在简单调度时显示) -->
        <el-form-item label="间隔类型" prop="intervalType" v-if="formData.scheduleType === '0'">
          <el-select v-model="formData.intervalType" placeholder="请选择间隔类型" style="width: 100%">
            <el-option label="毫秒" value="0" />
            <el-option label="秒" value="1" />
            <el-option label="分钟" value="2" />
            <el-option label="小时" value="3" />
          </el-select>
        </el-form-item>

        <!-- 备注 -->
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

// 表单引用
const formRef = ref<FormInstance>()

// 控制弹窗显示
const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 表单数据
const formData = ref({
  jobName: '',
  jobGroup: '',
  cron: '0 0 0/1 * * ?', // 默认值
  jobType: '',
  scheduleType: '1', // 默认Cron调度
  status: '1',
  jobParam: '',
  startTime: null,
  endTime: null,
  jobInterval: null,
  intervalType: '2',
  remark: '',
})

// 表单验证规则 - 根据调度类型动态变化
const rules = computed<FormRules>(() => ({
  jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  jobGroup: [{ required: true, message: '请输入任务组名称', trigger: 'blur' }],
  // 仅在Cron调度时验证cron表达式
  cron: formData.value.scheduleType === '1'
    ? [
      { required: true, message: '请配置Cron表达式', trigger: 'blur' },
      { pattern: /^(\S+\s+){4,6}\S+$/, message: 'Cron表达式格式不正确', trigger: 'blur' }
    ]
    : [],
  jobType: [{ required: true, message: '请输入任务类型', trigger: 'blur' }],
  scheduleType: [{ required: true, message: '请选择调度类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择任务状态', trigger: 'change' }],
  // 仅在简单调度时验证间隔时间
  jobInterval: formData.value.scheduleType === '0'
    ? [
      { required: true, message: '请输入间隔时间', trigger: 'blur' },
      { type: 'number', min: 1, message: '间隔时间必须大于0', trigger: 'blur' }
    ]
    : [],
  intervalType: formData.value.scheduleType === '0'
    ? [{ required: true, message: '请选择间隔类型', trigger: 'change' }]
    : []
}))

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isAdd: {
    type: Boolean,
    default: true
  },
  jobId: {
    type: [Number, String, null],
    default: null
  }
})

// Emits
const emit = defineEmits(['update:visible', 'submit'])

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    emit('submit', { ...formData.value })
    emit('update:visible', false)
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 调度类型变更处理
const handleScheduleTypeChange = (value: string) => {
  if (value === '0') {
    formData.value.cron = ''
  } else {
    formData.value.jobInterval = null
    // 切换到Cron调度时，确保组件重新渲染
    formData.value.cron = '0 0 0/1 * * ?'
  }
  formRef.value?.clearValidate()
}

// 处理Cron表达式变化
const handleCronChange = (expression: string) => {
  formData.value.cron = expression
}

// 获取任务详情（编辑模式）
const fetchJobDetail = async () => {
  if (!props.isAdd && props.jobId) {
    // 这里调用接口获取任务详情，示例为模拟数据
    // const res = await getJobDetailApi(props.jobId)
    // formData.value = res
  }
}

onMounted(() => {
  // 初始化时触发一次验证规则计算
  rules.value
})
</script>

<style scoped>
.dialog-body-wrapper {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
  padding-right: 10px;
}

.dialog-body-wrapper::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}

.dialog-body-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.scrollable-form {
  padding: 25px;
}

.el-form-item {
  margin-bottom: 20px;
}

/* 关键：为Cron组件设置足够的高度和样式，确保下拉选择框可见 */
.cron-wrapper {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 6px;
  min-height: 300px; /* 确保有足够高度显示所有选择器 */
  position: relative;
}

/* 确保选择器下拉框不被遮挡 */
.cron-editor {
  z-index: 1000;
  position: relative;
}

:deep(.cron-editor .el-select-dropdown) {
  z-index: 2000 !important;
}
</style>
