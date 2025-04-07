<template>
  <el-dialog :title="isAdd ? '新增菜单' : '编辑菜单'" v-model="visible" width="600px" :close-on-click-modal="false">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="right">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入菜单名称" />
      </el-form-item>
      <el-form-item label="路径" prop="path">
        <el-input v-model="formData.path" placeholder="请输入菜单路径" />
      </el-form-item>
      <el-form-item label="组件路径" prop="component">
        <el-input v-model="formData.component" placeholder="请输入组件路径" />
      </el-form-item>
      <el-form-item label="排序" prop="orderNum">
        <el-input v-model="formData.orderNum" />
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <el-select v-model="formData.icon" placeholder="请选择图标" clearable>
          <el-option v-for="item in iconOptions" :key="item" :label="item" :value="item">
            <span style="margin-right: 10px">
              <el-icon>
                <component :is="item" />
              </el-icon>
            </span>
            <span>{{ item }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="显示状态" prop="visible">
        <el-radio-group v-model="formData.visible">
          <el-radio :value="1">显示</el-radio>
          <el-radio :value="0">隐藏</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="类型" prop="visible">
        <el-radio-group v-model="formData.type">
          <el-radio value="0">目录</el-radio>
          <el-radio value="1">菜单</el-radio>
          <el-radio value="2">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="orderNum">
        <el-input v-model="formData.remark" style="width: 240px" :rows="2" type="textarea" placeholder="Please input" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { GetMenuVo } from '@/types/menu'
import { getMenuApi, getMenuTreeApi } from '@/api/menu'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  isAdd: {
    type: Boolean,
    default: () => (false),
    required: true
  },
  menuId: {
    type: Number,
    default: () => (null)
  }
})

const emit = defineEmits(['update:visible', 'submit'])

const visible = ref(props.visible)
const formRef = ref<FormInstance>()
const formData = ref<GetMenuVo>({
  id: 0,
  parentId: 0,
  name: '',
  path: '',
  component: '',
  icon: '',
  type: '',
  orderNum: 0,
  status: 0,
  visible: 0,
  remark: ''
})

const iconOptions = [
  'Setting',
  'User',
  'Menu',
  'Avatar',
  'Grid',
  'Document',
  'DataBoard',
  'PieChart'
]

const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入菜单路径', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
  visible: [{ required: true, message: '请选择显示状态', trigger: 'blur' }],
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate()
  if (valid) {
    emit('submit', formData.value)
    emit('update:visible', false)
  }
}

// 获取菜单详情
const getMenu = async () => {
  const menuInfo = await getMenuApi(props.menuId);
  formData.value = menuInfo;
}

// 监听visible变化并通知父组件
watch(visible, (val) => {
  emit('update:visible', val)
})

onMounted(() => {
  if (props.menuId) {
    getMenu()
  }
  // getMenuTree()
})
</script>