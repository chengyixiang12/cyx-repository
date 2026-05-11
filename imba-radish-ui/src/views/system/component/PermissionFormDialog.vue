<template>
<el-dialog :title="isAdd ? '新增权限' : '编辑权限'" v-model="visible" width="50vw" :close-on-click-modal="false"
    :before-close="handleClose" class="custom-dialog">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="6vw" label-position="right"
        class="scrollable-form">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入权限名称" />
        </el-form-item>

        <el-form-item label="权限编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入权限编码" />
        </el-form-item>

        <el-form-item label="权限类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio value="1">菜单</el-radio>
            <el-radio value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="'1'">启用</el-radio>
            <el-radio value="'0'">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入备注" :rows="3" />
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
import { GetPermissionVo } from '@/types/permission';
import { getPermissionApi } from '@/api/permission';


interface FatherParam {
  visible: boolean;
  isAdd: boolean;
  permissionId?: string;
}

const props = withDefaults(defineProps<FatherParam>(), {
  visible: false,
  isAdd: false,
  permissionId: '',
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref<FormInstance>()
const formData = ref<GetPermissionVo>({
    id: '',
    name: '',
    code: '',
    description: '',
    status: '1',
    type: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择角色状态', trigger: 'blur' }]
}

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 关闭弹窗事件
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

// 获取角色信息（编辑模式）
const getRole = async () => {
  if (!props.isAdd) {
    const res = await getPermissionApi(props.permissionId)
    formData.value = res
  }
}

onMounted(() => {
  getRole()
})
</script>