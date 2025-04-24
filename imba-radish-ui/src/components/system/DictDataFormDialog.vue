<template>
<el-dialog :title="isAdd ? '新增字典数据' : '编辑字典数据'" v-model="visible" width="700px" :close-on-click-modal="false"
        :before-close="handleClose" class="custom-dialog">
        <div class="dialog-body-wrapper">
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="right"
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
                    <el-input v-model="formData.sortOrder" placeholder="请输入排序值（数字越小越靠前）" />
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
import { ref, onMounted } from 'vue'
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

const visible = ref(props.visible)
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

onMounted(() => {
    loadDictData();
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