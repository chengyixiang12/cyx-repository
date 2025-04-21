<template>
    <el-dialog v-model="visible" title="赋予权限" width="650px" :close-on-click-modal="false" :append-to-body="true"
        class="custom-dialog">
        <!-- 内容区域内边距容器 -->
        <div class="transfer-wrapper">
            <el-transfer v-model="selectedPermissions" :data="permissionOptions" filterable
                filter-placeholder="请输入关键字搜索" :titles="['未赋予权限', '已赋予权限']" :props="{ key: 'id', label: 'name' }"
                style="height: 360px; width: 100%;" />
        </div>

        <!-- 底部按钮 -->
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="visible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </div>
        </template>
    </el-dialog>
</template>


<script lang="ts" setup>
import { ref, watch } from 'vue'
import { getAllPermissionsApi, getAssignPerApi } from '@/api/permission'
import { GetAllPermissionVo, SetPermissionsRequest } from '@/types/permissionts';

const props = defineProps<{
    modelValue: boolean
    roleId: number
}>()

const emit = defineEmits(['update:modelValue', 'submit'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (val) => visible.value = val)
watch(() => visible.value, val => emit('update:modelValue', val))

const permissionOptions = ref<GetAllPermissionVo[]>([])
const selectedPermissions = ref<number[]>([])

const loadPermissions = async (id: number) => {
    // 获取全部权限数据
    const allPermissions = await getAllPermissionsApi()
    permissionOptions.value = allPermissions

    // 获取该角色已分配权限
    const assigned = await getAssignPerApi(id)
    selectedPermissions.value = assigned.map(item => item.id)
}


const handleSubmit = async () => {
    const request: SetPermissionsRequest = { roleId: props.roleId, permissionIds: selectedPermissions.value }
    
    emit('submit', request)
    visible.value = false
}

watch(
  () => [visible.value, props.roleId],
  async ([vis, id]) => {
    if (vis && typeof id === 'number') {
      await loadPermissions(id)
    }
  },
  { immediate: true }
)

</script>

<style scoped>
.custom-dialog {
    max-height: 90vh;
}

.transfer-wrapper {
    padding: 20px 24px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.dialog-footer {
    text-align: right;
    padding: 10px 20px;
}
</style>