<template>
  <el-dialog :title="isAdd ? '新增部门' : '编辑部门'" v-model="visible" width="700px" :close-on-click-modal="false"
    :before-close="handleClose" class="custom-dialog">
    <div class="dialog-body-wrapper">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" label-position="right"
        class="scrollable-form">

        <!-- 父级部门 -->
        <el-form-item label="父级部门" prop="parentId">
          <el-tree-select v-model="formData.parentId" :data="deptTree" check-strictly placeholder="请选择父级部门" clearable
            filterable :filter-node-method="filterNode" :props="treeProps" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="部门名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入部门名称" />
        </el-form-item>

        <el-form-item label="部门编码" prop="code">
          <el-input v-model="formData.code" placeholder="部门编码ROLE_开头" />
        </el-form-item>

        <el-form-item label="排序" prop="osrtOrder">
          <el-input v-model="formData.sortOrder" type="text" @input="handleSortInput" placeholder="请填写序号" />
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
import { getDeptApi, getDeptTree } from '@/api/dept'
import { DeptTreeVo, DeptVo } from '@/types/dept'

interface FatherParam {
  visible: boolean;
  isAdd: boolean;
  roleId?: number | null
}

const props = withDefaults(defineProps<FatherParam>(), {
  visible: false,
  isAdd: false,
  roleId: null,
})
const emit = defineEmits(['update:visible', 'submit'])
const formRef = ref<FormInstance>()
const formData = ref<DeptVo>({
  id: 0,
  name: '',
  sortOrder: null,
  code: '',
  parentId: null,
})
// 部门树数据
const deptTree = ref<any[]>([])
const treeProps = {
  value: 'id',
  label: 'name',
  children: 'children'
}

const rules: FormRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
  parentId: [{ required: false }],
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

// 获取部门信息（编辑模式）
const getDept = async () => {
  if (!props.isAdd && props.roleId) {
    const res = await getDeptApi(props.roleId)
    formData.value = res
  }
}

// 获取父级部门树
const loadDeptTree = async () => {
  try {
    const res = await getDeptTree()
    deptTree.value = res
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

// 部门搜索过滤
const filterNode = (value: string, data: DeptTreeVo) => {
  if (!value) return true
  return data.name.toLowerCase().includes(value.toLowerCase())
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
  getDept()
  loadDeptTree()
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
