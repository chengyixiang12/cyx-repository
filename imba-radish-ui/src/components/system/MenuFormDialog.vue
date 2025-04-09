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
      <el-form-item label="父级菜单" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          :data="processedMenuTree"
          :props="{
            value: 'id',
            label: 'title',  // 使用您的title字段
            children: 'children'
          }"
          check-strictly
          :render-after-expand="false"
          placeholder="请选择父级菜单"
          style="width: 100%"
          clearable
        />
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
import { computed, onMounted, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { GetMenuVo, MenuItem } from '@/types/menu'
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
const menuTreeData = ref<MenuItem[]>([])
const treeProps = {
  value: 'id',    // 选中的值对应节点的 id
  label: 'title',  // 显示的名称对应节点的 name
  children: 'children' // 子节点字段
}

const formData = ref<GetMenuVo>({
  id: 0,
  parentId: 0,
  name: '',
  path: '',
  component: null as string | null,
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
  title: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入菜单路径', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
  visible: [{ required: true, message: '请选择显示状态', trigger: 'blur' }],
}

// 处理菜单树数据：将parentId: null转换为0（根节点）
const processedMenuTree = computed(() => {
  // 深度处理原始数据
  const processData = (data: MenuItem[]) => {
    return data.map(item => {
      if (item.children) {
        item.children = processData(item.children)
      }
      return item
    })
  }
})

// 获取菜单树数据
const getMenuTree = async () => {
  const res = await getMenuTreeApi()
  menuTreeData.value = res
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
  const menuInfo = await getMenuApi(props.menuId)
  formData.value = menuInfo
}

// 监听visible变化并通知父组件
watch(visible, (val) => {
  emit('update:visible', val)
})

onMounted(() => {
  getMenuTree() // 初始化时加载菜单树
  if (props.menuId) {
    getMenu()
  }
})
</script>