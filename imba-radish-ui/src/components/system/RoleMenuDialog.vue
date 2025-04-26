<template>
  <el-dialog v-model="visible" title="赋予菜单" width="650px" :close-on-click-modal="false" :append-to-body="true"
    class="custom-dialog">
    <div class="tree-wrapper">
      <el-tree ref="menuTreeRef" :data="menuTree" node-key="id" show-checkbox default-expand-all highlight-current
        :props="{ children: 'children', label: 'name' }" :default-checked-keys="selectedMenuIds" />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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
  roleId: number | null;
}

const props = withDefaults(defineProps<FatherParam>(), {
  modelValue: false,
  roleId: null
})

const emit = defineEmits(['update:modelValue', 'submit'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const menuTree = ref<GetMenuTreeVo[]>([])
const selectedMenuIds = ref<number[]>([])
const menuTreeRef = ref()

const loadMenus = async (roleId: number) => {
  const [allMenus, assignedMenus] = await Promise.all([
    getAllMenuTreeApi(),
    getAssignedMenuApi(roleId)
  ])
  menuTree.value = allMenus
  selectedMenuIds.value = assignedMenus.map(item => item.id);

  nextTick(() => {
    menuTreeRef.value.setCheckedKeys(selectedMenuIds.value)
  })
}

watch(
  () => [visible.value, props.roleId],
  async ([vis, id]) => {
    if (vis && typeof id === 'number') {
      await loadMenus(id)
    }
  },
  { immediate: true }
)

const handleSubmit = () => {
  const checkedKeys = menuTreeRef.value.getCheckedKeys() as number[]
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys() as number[]

  const allSelected = [...new Set([...checkedKeys, ...halfCheckedKeys])]

  const request: SetMenusRequest = {
    roleId: props.roleId,
    menuIds: allSelected
  }
  emit('submit', request)
  visible.value = false
}
</script>

<style scoped>
.custom-dialog {
  max-height: 90vh;
}

.tree-wrapper {
  padding: 16px 24px;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.dialog-footer {
  text-align: right;
  padding: 10px 20px;
}
</style>