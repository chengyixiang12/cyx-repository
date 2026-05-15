<template>
  <el-dialog v-model="visible" title="赋予菜单" width="50vw" :close-on-click-modal="false" :append-to-body="true">
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

const loadMenus = async (roleId: string) => {
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
    if (vis && typeof id === 'string') {
      await loadMenus(id)
    }
  },
  { immediate: true }
)

const handleSubmit = () => {
  const checkedKeys = menuTreeRef.value.getCheckedKeys() as string[]
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys() as string[]

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

</style>