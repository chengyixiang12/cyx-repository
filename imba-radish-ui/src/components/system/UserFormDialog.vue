<template>
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" :close-on-click-modal="false"
        @closed="handleClose">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" label-position="right">
            <el-form-item label="用户名" prop="username" v-if="isAdd">
                <el-input v-model="formData.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="用户名" v-else>
                <el-input v-model="formData.username" disabled />
            </el-form-item>

            <el-form-item label="密码" prop="password" v-if="isAdd">
                <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
                <el-input v-model="formData.nickname" placeholder="请输入昵称" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
                <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item label="手机号">
                <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>

            <el-form-item label="部门">
                <el-popover placement="bottom-start" :width="300" trigger="click" popper-class="dept-tree-popper">
                    <template #reference>
                        <el-input v-model="formData.deptName" placeholder="请选择部门" readonly />
                    </template>
                    <el-tree :data="deptTree" :props="treeProps" node-key="id" highlight-current
                        style="max-height: 300px; overflow-y: auto;" @node-click="handleDeptSelect" />
                </el-popover>
            </el-form-item>
        </el-form>

        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, defineProps, defineEmits, watch, onMounted, PropType, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DeptTreeVo } from '@/types/dept'
import { getUser } from '@/api/user'

const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    isAdd: {
        type: Boolean,
        default: true
    },
    showDialog: {
        type: Boolean,
        default: false
    },
    deptTree: {
        type: Array as PropType<DeptTreeVo[]>,
        required: true,
        default: () => []
    },
    userId: {
        type: Number,
        default: () => (null)
    }
})

const emit = defineEmits(['update:visible', 'submit'])

const dialogVisible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
})

const formRules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ]
}

const dialogTitle = computed(() => props.isAdd ? '新增用户' : '编辑用户')

// 头像URL（用于显示）
const avatarUrl = ref('')

// 添加 treeProps 定义
const treeProps = {
    label: 'name',
    children: 'children'
}

const formRef = ref()

const formData = ref({
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    deptId: 0,
    deptName: '', // 添加部门名称字段
    avatar: 0
})

// 在 script 部分添加
const deptTreeVisible = ref(false)
const deptTree = ref<DeptTreeVo[]>([])
const flattenedDeptTree = ref<Array<{ id: number, name: string }>>([])

// 添加部门选择处理方法
const handleDeptSelect = (node: DeptTreeVo) => {
    formData.value.deptId = node.id
    formData.value.deptName = node.name
    deptTreeVisible.value = false
}

// 加载部门树并展平
const loadDeptTree = async () => {
    try {
        deptTree.value = props.deptTree || []
        flattenedDeptTree.value = flattenDeptTree(props.deptTree || [])
    } catch (error) {
        console.error('加载部门树失败:', error)
    }
}

// 递归展平部门树
const flattenDeptTree = (tree: DeptTreeVo[]): Array<{ id: number, name: string }> => {
    return tree.reduce((acc, node) => {
        acc.push({ id: node.id, name: node.name })
        if (node.children && node.children.length > 0) {
            acc.push(...flattenDeptTree(node.children))
        }
        return acc
    }, [] as Array<{ id: number, name: string }>)
}

// 关闭弹窗
const handleClose = () => {
    formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
    try {
        await formRef.value.validate()
        emit('submit', formData.value)
        dialogVisible.value = false

    } catch (error) {
        ElMessage.error('请完善表单信息')
    }
}

// 获取用户
const getUserById = async (param: number) => {
    const userInfo = await getUser(param)
    formData.value = {
        username: userInfo.username,
        password: '',
        nickname: userInfo.nickname,
        email: userInfo.email,
        phone: userInfo.phone || '',
        deptId: userInfo.getUserDeptDto.value || 0,
        deptName: userInfo.getUserDeptDto.label,
        avatar: userInfo.avatar || 0
    }
}

// 根据部门ID查找部门信息
const findDeptById = (tree: DeptTreeVo[], id: number): DeptTreeVo | null => {
    for (const node of tree) {
        if (node.id === id) {
            return node
        }
        if (node.children && node.children.length > 0) {
            const found = findDeptById(node.children, id)
            if (found) return found
        }
    }
    return null
}

onMounted(() => {
    loadDeptTree()
    if (props.userId) {
        getUserById(props.userId)
    }
})

// 监听部门是否改变
watch(() => props.userId, async (val) => {
    if (props.visible) {
        const dept = findDeptById(deptTree.value, formData.value.deptId)
        formData.value.deptName = dept ? dept.name : ''
    }
}, { immediate: true })
</script>

<style scoped>
.dept-tree-popper {
    max-height: 300px;
    overflow-y: auto;
}
</style>