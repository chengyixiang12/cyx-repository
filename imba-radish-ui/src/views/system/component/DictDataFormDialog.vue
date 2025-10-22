<template>
<el-dialog :title="isAdd ? '新增字典数据' : '编辑字典数据'" v-model="visible" width="50vw" :close-on-click-modal="false"
        :before-close="handleClose">
        <div class="dialog-body-wrapper">
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="6vw" label-position="right"
                class="scrollable-form">
                <el-form-item label="编码" prop="code">
                    <el-input v-model="formData.code" placeholder="请输入编码" />
                </el-form-item>

                <el-form-item label="标签" prop="label">
                    <el-input v-model="formData.label" placeholder="请输入标签" />
                </el-form-item>

                <el-form-item label="键值" prop="value">
                    <el-input v-model="formData.value" placeholder="请输入键值" />
                </el-form-item>

                <el-form-item label="是否默认" prop="isDefault">
                    <el-radio-group v-model="formData.isDefault">
                        <el-radio :value="1">是</el-radio>
                        <el-radio :value="0">否</el-radio>
                    </el-radio-group>
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
import { SaveDictDataRequest } from '@/types/dictData'
import { getDictDataApi } from '@/api/dictData'

interface FatherParam {
    visible: boolean
    isAdd: boolean
    dictDataId?: number | null
}

const props = withDefaults(defineProps<FatherParam>(), {
    visible: false,
    isAdd: false,
    dictDataId: null
})

const emit = defineEmits(['update:visible', 'submit'])
const formRef = ref<FormInstance>()
const formData = ref<SaveDictDataRequest>({
    sortOrder: null,
    code: '',
    label: '',
    value: '',
    dictType: '',
    cssClass: '',
    listClass: '',
    isDefault: 0,
    status: 1,
    remark: ''
})

const rules: FormRules = {
    code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
    label: [{ required: true, message: '请输入标签', trigger: 'blur' }],
    value: [{ required: true, message: '请选择键值', trigger: 'blur' }]
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

const loadDictData = async () => {
    if (!props.isAdd && props.dictDataId) {
        const res = await getDictDataApi(props.dictDataId)
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
    loadDictData();
})
</script>
<style scoped>
.dialog-body-wrapper {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
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