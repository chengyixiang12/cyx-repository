<template>
  <el-dialog :title="isAdd ? '新增任务' : '编辑任务'" v-model="visible" width="50vw" :close-on-click-modal="false"
    :before-close="handleClose" class="custom-dialog">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="right"
        class="scrollable-form">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="formData.jobName" placeholder="请输入任务名称" />
        </el-form-item>

        <el-form-item label="任务组" prop="jobGroup">
          <el-input v-model="formData.jobGroup" placeholder="请输入任务组名称" />
        </el-form-item>

        <el-form-item label="Cron表达式" prop="cron">
          <el-input v-model="formData.cron" placeholder="请输入Cron表达式" />
          <el-collapse v-model="cronPanelVisible" style="margin-top: 10px">
            <el-collapse-item name="1">
              <template #title>
                <span style="color: #409EFF; cursor: pointer;">生成Cron表达式</span>
              </template>
              <!-- 使用 CronElementPlus -->
              <CronElementPlus v-model="formData.cron" :locale="'zh-CN'" color="#409EFF" @error="onCronError" />
            </el-collapse-item>
          </el-collapse>
        </el-form-item>

        <!-- <el-form-item label="Cron描述">
          <span style="font-size: 12px; color: #666;">{{ cronDescription }}</span>
        </el-form-item> -->

        <el-form-item label="任务类型" prop="jobType">
          <el-input v-model="formData.jobType" placeholder="请输入任务类型" />
        </el-form-item>

        <el-form-item label="调度类型" prop="scheduleType">
          <el-radio-group v-model="formData.scheduleType">
            <el-radio label="0">简单调度</el-radio>
            <el-radio label="1">Cron调度</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="任务状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">暂停</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="任务参数" prop="jobParam">
          <el-input v-model="formData.jobParam" type="textarea" :rows="3" placeholder="请输入任务所需参数（JSON格式）" />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>

        <el-form-item label="间隔时间" prop="jobInterval">
          <el-input v-model.number="formData.jobInterval" placeholder="请输入间隔时间（单位：根据间隔类型）" />
        </el-form-item>

        <el-form-item label="间隔类型" prop="intervalType">
          <el-select v-model="formData.intervalType" placeholder="请选择间隔类型" style="width: 100%">
            <el-option label="毫秒" value="0" />
            <el-option label="秒" value="1" />
            <el-option label="分钟" value="2" />
            <el-option label="小时" value="3" />
          </el-select>
        </el-form-item>

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
import { CronElementPlus } from '@vue-js-cron/element-plus'
import '@vue-js-cron/element-plus/dist/element-plus.css'
// import { humanize } from '@vue-js-cron/core'

// 表单引用
const formRef = ref<FormInstance>()

// 控制弹窗显示
const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 折叠面板控制
const cronPanelVisible = ref(['1']) // 默认展开

// 表单数据
const formData = ref({
  jobName: '',
  jobGroup: '',
  cron: '0 0 0/1 * * ?', // 默认值
  jobType: '',
  scheduleType: '1',
  status: '1',
  jobParam: '',
  startTime: null,
  endTime: null,
  jobInterval: null,
  intervalType: '2',
  remark: '',
})

// 表单验证规则
const rules: FormRules = {
  jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  jobGroup: [{ required: true, message: '请输入任务组名称', trigger: 'blur' }],
  cron: [
    { required: true, message: '请输入Cron表达式', trigger: 'blur' },
    { pattern: /^(\S+\s+){5,6}\S+$/, message: 'Cron表达式格式不正确', trigger: 'blur' }
  ],
  jobType: [{ required: true, message: '请输入任务类型', trigger: 'blur' }],
  scheduleType: [{ required: true, message: '请选择调度类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择任务状态', trigger: 'change' }]
}

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
  const valid = await formRef.value?.validate()
  if (valid) {
    emit('submit', formData.value)
    emit('update:visible', false)
  }
}

// 获取任务详情（编辑模式）
const fetchJobDetail = async () => {
  if (!props.isAdd && props.jobId) {
    // 这里调用接口获取任务详情，示例为模拟数据
    // const res = await getJobDetailApi(props.jobId)
    // formData.value = res
  }
}

const onCronError = (err: Error) => {
  console.warn('Cron 表达式错误：', err.message)
}

// const cronDescription = computed(() => {
//   if (!formData.value.cron) return ''
//   try {
//     // humanize 内置支持中文
//     return humanize(formData.value.cron, { locale: 'zh_CN' })
//   } catch (e) {
//     return '无效的Cron表达式'
//   }
// })

onMounted(() => {
  fetchJobDetail()
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

/* 覆盖 cron-light 的按钮样式 */
.vue-js-cron .vjs-button {
  background-color: #409EFF;
  /* Element Plus 主题蓝 */
  color: #fff;
  border-radius: 4px;
  padding: 4px 10px;
  border: none;
  cursor: pointer;
}

.vue-js-cron .vjs-button:hover {
  background-color: #66b1ff;
}

/* 覆盖选择框 */
.vue-js-cron select {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 13px;
}
</style>