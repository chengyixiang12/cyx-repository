<template>
  <el-dialog :title="isAdd ? '新增菜单' : '编辑菜单'" v-model="visible" width="50vw" :close-on-click-modal="false"
    :before-close="handleClose">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="6vw" label-position="right"
        class="scrollable-form">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入菜单名称" />
        </el-form-item>

        <el-form-item label="路由" prop="path">
          <el-input v-model="formData.path" placeholder="请输入菜单路径" />
        </el-form-item>

        <el-form-item label="组件" prop="component">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type" @change="handleTypeChange">
            <el-radio value="0">目录</el-radio>
            <el-radio value="1">菜单</el-radio>
            <el-radio value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="父级菜单" prop="parentId" v-if="formData.type !== '0'">
          <el-tree-select v-model="formData.parentId" :data="menuParentData" :props="treeProps" check-strictly
            placeholder="请选择父级菜单" style="width: 100%" clearable />
        </el-form-item>

        <el-form-item label="排序" prop="orderNum">
          <el-input v-model="formData.orderNum" type="text" @input="handleSortInput" placeholder="请填写序号" />
        </el-form-item>

        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="输入Element Plus图标名称，如：Edit、Search、User" clearable>
            <template #prefix>
              <el-icon v-if="formData.icon">
                <component :is="getIconComponent(formData.icon)" />
              </el-icon>
            </template>
          </el-input>
          <div class="icon-tips">
            <p><el-link href="https://element-plus.org/en-US/component/icon.html" target="_blank">Element
              Plus图标文档</el-link></p>
          </div>
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

        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" :rows="3" />
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
import { computed, onMounted, ref, watch } from 'vue'
import type { Component } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { GetMenuVo, GetSelectMenuVo } from '@/types/menu'
import { getMenuApi, getSelectMenu } from '@/api/menu'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const iconsMap = new Map()

// 初始化图标映射（支持多种格式的图标名称）
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  // 原始名称：Edit (保持原样)
  iconsMap.set(key, component)

  // 小写格式：edit
  iconsMap.set(key.toLowerCase(), component)

  // 移除Icon后缀：EditIcon → Edit
  const withoutIcon = key.replace(/icon$/i, '')
  if (withoutIcon !== key) {
    iconsMap.set(withoutIcon, component)
    iconsMap.set(withoutIcon.toLowerCase(), component)
  }

  // 支持el-icon-前缀：el-icon-edit
  iconsMap.set(`el-icon-${key.toLowerCase()}`, component)
})

interface FatherParam {
  visible: boolean;
  isAdd: boolean;
  menuId?: number | null;
  type?: string;
}

const props = withDefaults(defineProps<FatherParam>(), {
  isAdd: false,
  menuId: null,
  type: '',
  visible: false
})

const emit = defineEmits(['update:visible', 'submit'])

const formRef = ref<FormInstance>()
const formData = ref<GetMenuVo>({
  id: 0,
  parentId: null,
  name: '',
  path: '',
  component: '',
  icon: '',
  type: '0',  // 默认类型为目录
  orderNum: null,
  status: 1,
  visible: 1,
  remark: '',
})

// 初始验证规则
const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入菜单路径', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
  visible: [{ required: true, message: '请选择显示状态', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择父级菜单', trigger: 'blur' }],
}

const menuParentData = ref<GetSelectMenuVo[]>([])

const treeProps = {
  value: 'id',
  label: 'name',
}

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 获取菜单树数据
const getMenuTree = async (type: string) => {
  if (type !== '0') {  // 只有类型不是“目录”时才请求菜单数据
    const res = await getSelectMenu(type)
    menuParentData.value = res
  }
}

const getIconComponent = (iconName: string): Component | null => {
  if (!iconName) return null

  // 去除前后空格
  const trimmedName = iconName.trim()

  // 直接查找（支持多种格式）
  return iconsMap.get(trimmedName) ||
    iconsMap.get(trimmedName.toLowerCase()) ||
    null
}

// 监听类型变化，处理父级菜单逻辑
const handleTypeChange = () => {
  formData.value.parentId = null

  // 动态修改验证规则
  if (formData.value.type === '0') {
    // 如果是目录类型，不需要校验父级菜单
    delete rules.parentId
    formRef.value?.clearValidate('parentId')
  } else {
    // 如果是菜单或按钮，设置父级菜单为必填项
    rules.parentId = [{ required: true, message: '请选择父级菜单', trigger: 'blur' }]
  }

  // 如果是菜单或按钮，调用菜单树接口，传递相应参数
  getMenuTree(formData.value.type)
}

const submitForm = async () => {
  const valid = await formRef.value?.validate()
  if (valid) {
    emit('submit', formData.value)
    emit('update:visible', false)
  }
}

const getMenu = async () => {
  if (props.menuId) {
    const menuInfo = await getMenuApi(props.menuId)
    formData.value = menuInfo
  }
  await getMenuTree(formData.value.type)
}

// 处理关闭事件
const handleClose = () => {
  emit('update:visible', false)
}

// 排序表单输入校验
const handleSortInput = (value: string) => {
  // 替换掉所有非数字的字符，只保留数字
  const numericValue = value.replace(/\D/g, '')
  // 如果有输入，且转成了数字
  if (numericValue) {
    formData.value.orderNum = parseInt(numericValue, 10)
  } else {
    formData.value.orderNum = null
  }
}

onMounted(() => {
  getMenu()
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

:deep(.el-select-dropdown__item) {
  height: 36px;
  padding: 0 12px;
}

:deep(.el-select-group__title) {
  padding-left: 12px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.icon-tips {
  margin-top: 8px;
  font-size: 10px;
  color: var(--el-text-color-secondary);
}

.icon-tips p {
  margin: 4px 0;
}
</style>
