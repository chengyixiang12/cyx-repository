<template>
  <el-dialog :title="isAdd ? '新增任务' : '编辑任务'" v-model="visible" width="50vw" :close-on-click-modal="false"
    :before-close="handleClose">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="6vw" label-position="right"
        class="scrollable-form">
        <!-- 任务名称 -->
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="formData.jobName" placeholder="请输入任务名称" />
        </el-form-item>

        <!-- 任务组 -->
        <el-form-item label="任务组" prop="jobGroup">
          <el-input v-model="formData.jobGroup" placeholder="请输入任务组名称" />
        </el-form-item>

        <!-- 任务类型 -->
        <el-form-item label="任务类型" prop="jobType">
          <el-input v-model="formData.jobType" placeholder="请输入任务类型" />
        </el-form-item>

        <el-form-item label="作业执行类" prop="jobClass">
          <el-input v-model="formData.jobClass" placeholder="请输入作业执行类" />
        </el-form-item>

        <!-- 任务状态 -->
        <el-form-item label="任务状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">暂停</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 调度类型 -->
        <el-form-item label="调度类型" prop="scheduleType">
          <el-radio-group v-model="formData.scheduleType" @change="handleScheduleTypeChange">
            <el-radio value="0">简单调度</el-radio>
            <el-radio value="1">Cron调度</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="Cron表达式" prop="cron" v-if="formData.scheduleType === '1'">
          <div class="cron-wrapper">
            <el-button type="primary" @click="showCronDrawer = true" class="cron-generator-btn">
              <el-icon>
                <Calendar />
              </el-icon>
              生成 Cron 表达式
            </el-button>
            <div v-if="formData.cron" class="cron-preview">
              当前表达式: {{ formData.cron }}
            </div>
          </div>
        </el-form-item>

        <!-- 开始时间 -->
        <el-form-item label="开始时间" prop="startTime" v-if="formData.scheduleType === '0'">
          <el-date-picker v-model="formData.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>

        <!-- 结束时间 -->
        <el-form-item label="结束时间" prop="endTime" v-if="formData.scheduleType === '0'">
          <el-date-picker v-model="formData.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择结束时间" style="width: 100%" />
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

        <!-- 任务参数 -->
        <el-form-item label="任务参数" prop="jobParam">
          <div class="json-input-wrapper">
            <el-input v-model="formData.jobParam" type="textarea" :rows="3" placeholder="请输入任务所需参数（JSON格式）" />
            <el-button type="primary" size="small" @click="formatJson" class="format-btn">
              格式化
            </el-button>
          </div>
        </el-form-item>

        <!-- 备注 -->
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-drawer v-model="showCronDrawer" title="Cron 表达式生成器" direction="rtl" size="50%">
    <CronGenerate @confirm="handleCronConfirm" @cancel="showCronDrawer = false" />
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import CronGenerate from './CronGenerate.vue'
import { Calendar } from '@element-plus/icons-vue'
import { getJobApi } from '@/api/quartz'
import { GetJobVo } from '@/types/quartz'

// 表单引用
const formRef = ref<FormInstance>()

const showCronDrawer = ref(false)

// 控制弹窗显示
const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 表单数据
const formData = ref<GetJobVo>({
  id: '',
  jobName: '',
  jobGroup: 'default',
  cron: '0 0 0 * * ?',
  jobClass: '',
  jobType: '',
  scheduleType: '1',
  status: 1,
  jobParam: '',
  startTime: null,
  endTime: null,
  jobInterval: null,
  intervalType: '2',
  remark: '',
})

// 表单验证规则 - 根据调度类型动态变化
const rules = {
  jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  jobGroup: [{ required: true, message: '请输入任务组名称', trigger: 'blur' }],
  jobClass: [{ required: true, message: '请输入作业执行类', trigger: 'blur' }],
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
}

interface FatherParam {
  visible: boolean
  isAdd: boolean
  jobId?: number | null
}

const props = withDefaults(defineProps<FatherParam>(), {
  visible: false,
  isAdd: false,
  jobId: null,
})

const emit = defineEmits(['update:visible', 'submit'])

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate()

  if (valid) {
    emit('submit', formData.value)
    emit('update:visible', false)
  }
}

// 格式化JSON
const formatJson = () => {
  try {
    if (formData.value.jobParam) {
      const parsed = JSON.parse(formData.value.jobParam)
      formData.value.jobParam = JSON.stringify(parsed, null, 2)
    }
  } catch (error) {
    // 提示用户JSON格式错误
    ElMessage.error('JSON格式错误，请检查输入')
  }
}

// 调度类型变更处理
const handleScheduleTypeChange = (value: string) => {
  if (value === '0') {
    formData.value.cron = '';
  } else {
    if (!formData.value.cron) {
      formData.value.cron = '0 0 0 * * ?';
    }
    formData.value.jobInterval = null;
  }
  formRef.value?.clearValidate()
}

const handleCronConfirm = (cronExpression: string) => {
  formData.value.cron = cronExpression;
  showCronDrawer.value = false;
}

// 获取任务详情（编辑模式）
const fetchJobDetail = async () => {
  if (!props.isAdd && props.jobId) {
    const res = await getJobApi(props.jobId);
    formData.value = res;
  }
}

onMounted(() => {
  fetchJobDetail()
})
</script>

<style scoped>
.cron-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.cron-generator-btn {
  flex-shrink: 0;
}

.cron-preview {
  font-size: 13px;
  color: #909399;
}

.json-input-wrapper {
  position: relative;
  width: 100%;
}

.format-btn {
  position: absolute;
  right: 8px;
  bottom: 8px;
}
</style>
