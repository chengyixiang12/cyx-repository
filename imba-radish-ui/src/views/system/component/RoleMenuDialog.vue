<template>
  <el-dialog v-model="visible" title="赋予菜单" width="460px" :close-on-click-modal="false" :append-to-body="true">
    <div class="tree-toolbar">
      <span class="tree-tip">勾选父菜单会自动带上其子菜单</span>
      <div class="tree-actions">
        <el-button link type="primary" @click="selectAll">全选</el-button>
        <el-button link type="primary" @click="clearAll">清空</el-button>
        <span class="tree-count">已选 {{ checkedCount }} 项</span>
      </div>
    </div>

    <div class="tree-wrapper">
      <el-tree ref="menuTreeRef" :data="menuTree" node-key="id" show-checkbox default-expand-all highlight-current
        :props="{ children: 'children', label: 'name' }" :default-checked-keys="selectedMenuIds" @check="handleCheck" />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" :disabled="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, watch, nextTick, computed } from 'vue'
import { getAllMenuTreeApi, getAssignedMenuApi } from '@/api/menu'
import type { GetMenuTreeVo } from '@/types/menu'
import { SetMenusRequest } from '@/types/role';

interface FatherParam {
  modelValue: boolean;
  roleId: string;
}

const props = withDefaults(defineProps<FatherParam>(), {
  modelValue: false,
  roleId: ''
})

const emit = defineEmits(['update:modelValue', 'submit'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const menuTree = ref<GetMenuTreeVo[]>([])
const selectedMenuIds = ref<string[]>([])
const menuTreeRef = ref()
const submitLoading = ref(false)
const checkedCount = ref(0)

const loadMenus = async (roleId: string) => {
  const [allMenus, assignedMenus] = await Promise.all([
    getAllMenuTreeApi(),
    getAssignedMenuApi(roleId)
  ])
  menuTree.value = allMenus
  selectedMenuIds.value = assignedMenus.map(item => item.id);

  nextTick(() => {
    menuTreeRef.value.setCheckedKeys(selectedMenuIds.value)
    syncCheckedCount()
  })
}

// 递归收集树中所有节点 id
const collectAllIds = (nodes: GetMenuTreeVo[]): string[] =>
  nodes.flatMap(node => [node.id, ...collectAllIds(node.children || [])])

// 同步已选计数（仅统计完全勾选的节点）
const syncCheckedCount = () => {
  checkedCount.value = (menuTreeRef.value?.getCheckedKeys() as string[])?.length ?? 0
}

// 勾选变化时刷新计数
const handleCheck = () => {
  syncCheckedCount()
}

const selectAll = () => {
  menuTreeRef.value.setCheckedKeys(collectAllIds(menuTree.value))
  syncCheckedCount()
}

const clearAll = () => {
  menuTreeRef.value.setCheckedKeys([])
  syncCheckedCount()
}

watch(
  () => [visible.value, props.roleId],
  async ([vis, id]) => {
    if (vis && typeof id === 'string') {
      await loadMenus(id)
    }
  },
  { immediate: true }
)

const handleSubmit = () => {
  if (submitLoading.value) return
  const checkedKeys = menuTreeRef.value.getCheckedKeys() as string[]
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys() as string[]

  const allSelected = [...new Set([...checkedKeys, ...halfCheckedKeys])]

  const request: SetMenusRequest = {
    roleId: props.roleId,
    menuIds: allSelected
  }
  submitLoading.value = true
  try {
    emit('submit', request)
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.tree-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.tree-tip {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.tree-actions {
  display: flex;
  align-items: center;
}

.tree-count {
  margin-left: 8px;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-regular);
}

.tree-wrapper {
  border: 1px solid var(--radish-border);
  border-radius: var(--radish-radius);
  padding: 8px;
  max-height: 55vh;
  overflow-y: auto;
}
</style>
