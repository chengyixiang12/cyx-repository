<template>
    <el-dialog :title="isAdd ? '新增字典类型' : '编辑字典类型'" v-model="visible" width="700px" :close-on-click-modal="false"
        :before-close="handleClose" class="custom-dialog">
        <div class="dialog-body-wrapper">
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="right"
                class="scrollable-form">
                <el-form-item label="字典名称" prop="dictName">
                    <el-input v-model="formData.dictName" placeholder="请输入字典名称" />
                </el-form-item>

                <el-form-item label="字典类型" prop="dictType" v-if="isAdd">
                    <el-input v-model="formData.dictType" placeholder="请输入字典类型（唯一标识）" />
                </el-form-item>
                <el-form-item label="字典类型" prop="dictType" v-else>
                    <el-input v-model="formData.dictType" disabled />
                </el-form-item>

                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="formData.status">
                        <el-radio :value="1">启用</el-radio>
                        <el-radio :value="0">禁用</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="排序" prop="sortOrder">
                    <el-input v-model="formData.sortOrder" type="text" @input="handleSortInput" placeholder="请输入排序值（数字越小越靠前）" />
                </el-form-item>

                <el-form-item label="备注" prop="remark">
                    <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
            </el-form>
        </div>

        <template #footer>
            <el-button @click="visible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getDictTypeApi } from '@/api/dictType'
import type { SaveDictTypeRequest } from '@/types/dictType'

interface FatherParam {
    visible: boolean
    isAdd: boolean
    dictTypeId?: number | null
}

const props = withDefaults(defineProps<FatherParam>(), {
    visible: false,
    isAdd: false,
    dictTypeId: null
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref<FormInstance>()
const formData = ref<SaveDictTypeRequest>({
    dictName: '',
    dictType: '',
    status: 1,
    sortOrder: null,
    remark: ''
})

const rules: FormRules = {
    dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
    dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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

const loadDictType = async () => {
    if (!props.isAdd && props.dictTypeId) {
        const res = await getDictTypeApi(props.dictTypeId)
        formData.value = res
    }
}

// 排序表单输入校验
const handleSortInput = (value: string) => {
  // 替换掉所有非数字的字符，只保留数字
  const numericValue = value.replace(/\D/g, '')
  // 如果有输入，且转成了数字
  if (numericValue) {
    formData.value.sortOrder = parseInt(numericValue, 10)
  } else {
    formData.value.sortOrder = null
  }
}

onMounted(() => {
    loadDictType()
})
</script>

<style scoped>
.custom-dialog {
    display: flex;
    flex-direction: column;
    max-height: 80vh;
    margin-top: 5vh;
    overflow: hidden;
}

.dialog-body-wrapper {
    flex: 1;
    overflow-y: auto;
    padding-right: 10px;
}

.scrollable-form {
    max-height: 50vh;
    overflow-y: auto;
    padding: 25px;
}

.el-form-item {
    margin-bottom: 20px;
}
</style>