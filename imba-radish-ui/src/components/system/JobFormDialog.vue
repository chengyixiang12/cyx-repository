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
                    <el-button @click="showCronDialog = true" style="margin-top: 10px" plain>生成Cron表达式</el-button>
                </el-form-item>
                <el-form-item label="Cron描述">
                    <span style="font-size: 12px; color: #666;">{{ cronDescription }}</span>
                </el-form-item>

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
            <el-dialog v-model="showCronDialog" title="生成 Cron 表达式" width="60%">
                <custom-cron-generator v-model="formData.cron" />
                <template #footer>
                    <el-button @click="showCronDialog = false">关闭</el-button>
                </template>
            </el-dialog>
        </div>

        <template #footer>
            <el-button @click="visible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
        </template>
    </el-dialog>
</template>
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import CustomCronGenerator from '@/components/common/CustomCronGenerator.vue'
import cronstrue from 'cronstrue'

interface JobFormData {
    id?: number | null
    jobName: string
    jobGroup: string
    cron: string
    jobType: string
    scheduleType: string
    status: string
    jobParam: string
    startTime: string | null
    endTime: string | null
    jobInterval: number | null
    intervalType: string
    remark: string
}

interface Props {
    visible: boolean
    isAdd: boolean
    jobId?: number | null
}

const props = withDefaults(defineProps<Props>(), {
    visible: false,
    isAdd: true,
    jobId: null,
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref<FormInstance>()
const formData = ref<JobFormData>({
    jobName: '',
    jobGroup: '',
    cron: '',
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
const showCronDialog = ref(false)

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

const visible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
})

const handleClose = () => {
    emit('update:visible', false)
}

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
        // 调用接口获取任务详情，你自己实现
        // const res = await getJobDetailApi(props.jobId)
        // formData.value = res
    }
}

// 原有逻辑
const cronDescription = computed(() => {
    try {
        return cronstrue.toString(formData.value.cron, { locale: 'zh-CN' })
    } catch {
        return ''
    }
})

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
</style>