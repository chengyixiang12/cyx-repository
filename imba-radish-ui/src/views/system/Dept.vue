<template>
  <ListPage title="部门管理" :loading="loading" :count="deptList.length" :columns="8" :model="searchForm">
    <template #actions>
      <el-button type="primary" @click="handleAdd" class="add-button">
        新增
      </el-button>
      <el-button type="primary" @click="handleExport" class="export-button">
        导出
      </el-button>
    </template>

    <template #search>
      <el-form-item label="关键字:">
        <el-input v-model="searchForm.keyword" placeholder="部门名称/部门编号" clearable class="keyword-input" />
      </el-form-item>
      <el-form-item label="父级:">
        <el-input v-model="searchForm.parent" placeholder="部门名称/部门编号" clearable class="keyword-input" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="primary" @click="resetSearch">重置</el-button>
      </el-form-item>
    </template>

    <el-table :data="deptList" border size="small" style="width: 100%" v-loading="loading && deptList.length > 0" highlight-current-row
        @selection-change="handleSelectionChange" height="100%">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" min-width="60" align="center" type="index" />
        <el-table-column prop="name" label="部门名称" show-overflow-tooltip min-width="150" align="center" />
        <el-table-column prop="code" label="部门编码" show-overflow-tooltip min-width="150" align="center" />
        <el-table-column prop="parentName" label="父级名称" show-overflow-tooltip min-width="150" align="center" />
        <el-table-column prop="parentCode" label="父级编码" show-overflow-tooltip min-width="150" align="center" />
        <el-table-column prop="level" label="部门层级" show-overflow-tooltip min-width="150" align="center" />
        <el-table-column label="操作" min-width="180" align="center">
          <template #default="scope">
            <div class="action-buttons-container">
              <el-button type="primary" @click="handleEdit(scope.row)" class="action-button edit-button">
                编辑
              </el-button>
              <el-button type="danger" class="action-button delete-button" @click="handleDeleteClick(scope.row)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

    <template #footer>
      <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </template>
  </ListPage>

  <!-- 新增部门弹窗 -->
  <dept-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑部门弹窗 -->
  <dept-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :deptId="deptId"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import ListPage from '@/components/layout/ListPage.vue'
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDeptsApi,
  getDeptTreeApi,
  deleteDeptApi,
  saveDeptApi,
  updateDeptApi,
  exportDept
} from '@/api/dept'
import type { DeptTreeVo, GetDeptsRequest, GetDeptsVo, SaveDeptRequest, EditDeptRequest } from '@/types/dept'
import DeptFormDialog from './component/DeptFormDialog.vue'
import { download } from '@/utils/download'

const loading = ref(false)
const deptList = ref<GetDeptsVo[]>([])
const selectedRows = ref<GetDeptsVo[]>([])

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const deptId = ref<string>('')

// 搜索表单
const searchForm = ref<GetDeptsRequest>({
  keyword: '',
  parent: null,
  pageNum: 1,
  pageSize: 10
})

// 分页
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 新增部门
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑部门
const handleEdit = (row: GetDeptsVo) => {
  deptId.value = row.id
  editDialogVisible.value = true
}

// 提交新增部门
const handleAddSubmit = async (formData: SaveDeptRequest) => {
  try {
    await saveDeptApi(formData)
    deptTreeCache = null
    addDialogVisible.value = false
    await loadDepts()
  } catch (e) {
    // 失败时不关闭弹窗
  }
}

// 提交编辑部门
const handleEditSubmit = async (formData: EditDeptRequest) => {
  try {
    await updateDeptApi({
      id: deptId.value,
      code: formData.code,
      name: formData.name,
      sortOrder: formData.sortOrder || 0,
      parentId: formData.parentId
    })
    deptTreeCache = null
    editDialogVisible.value = false
    await loadDepts()
  } catch (e) {
    // 失败时不关闭弹窗
  }
}

// 加载部门数据
const loadDepts = async () => {
  try {
    loading.value = true
    const params: GetDeptsRequest = {
      keyword: searchForm.value.keyword,
      parent: searchForm.value.parent,
      pageNum: pagination.value.current,
      pageSize: pagination.value.size
    }
    const res = await getDeptsApi(params)
    deptList.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    console.error('加载部门列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 部门树缓存：列表是分页扁平数据，拿不到层级关系，只在删除前用它统计下级部门数量
let deptTreeCache: DeptTreeVo[] | null = null

const loadDeptTree = async (): Promise<DeptTreeVo[]> => {
  if (!deptTreeCache) {
    deptTreeCache = (await getDeptTreeApi(null)) || []
  }
  return deptTreeCache
}

// 统计该部门下所有层级（含子、孙）的部门数量
const countDescendants = (nodes: DeptTreeVo[], id: string): number => {
  const findNode = (list: DeptTreeVo[]): DeptTreeVo | null => {
    for (const node of list) {
      if (node.id === id) return node
      const hit = findNode(node.children || [])
      if (hit) return hit
    }
    return null
  }
  const countAll = (node: DeptTreeVo): number =>
    (node.children || []).reduce((sum, child) => sum + 1 + countAll(child), 0)

  const target = findNode(nodes)
  return target ? countAll(target) : 0
}

// 删除前提示：说明会级联删除下级部门，以及部门内有用户时会被拒绝
const handleDeleteClick = async (row: GetDeptsVo) => {
  let descendantCount = 0
  try {
    descendantCount = countDescendants(await loadDeptTree(), row.id)
  } catch (error) {
    // 树加载失败不影响删除，只是不显示下级部门数量
    console.error('加载部门树失败:', error)
  }

  const message = descendantCount > 0
    ? `确定删除部门「${row.name}」吗？其下 ${descendantCount} 个下级部门将一并删除，不可恢复；若其中仍有用户，将拒绝删除。`
    : `确定删除部门「${row.name}」吗？若该部门下仍有用户，将拒绝删除。`

  try {
    await ElMessageBox.confirm(message, '删除部门', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return // 用户取消
  }

  await handleDelete(row.id)
}

// 删除部门
const handleDelete = async (id: string) => {
  try {
    await deleteDeptApi(id)
    deptTreeCache = null // 层级变了，下次删除重新拉树
    ElMessage.success('删除成功')
    await loadDepts()
  } catch (error) {
    // 失败提示（如“部门下还有用户”）已由请求拦截器统一弹出，这里只保证列表不刷新
  }
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadDepts()
}

// 页面大小变化
const handleSizeChange = (size: number) => {
  pagination.value.size = size
  loadDepts()
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  loadDepts()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value.keyword = ''
  searchForm.value.parent = null
  loadDepts()
}

// 导出部门
const handleExport = async () => {
  const ids = selectedRows.value.map(row => row.id)
  try {
    if (ids.length === 0) {
      await ElMessageBox.confirm('未勾选任何部门，将导出全部部门。是否继续？', '导出部门', {
        confirmButtonText: '导出全部',
        cancelButtonText: '取消',
        type: 'info'
      })
    }
    const { blob, filename } = await exportDept(ids, '部门数据')
    download(blob, filename)
    ElMessage.success(ids.length > 0 ? '导出成功' : '已导出全部部门')
  } catch (error) {
    // confirm 取消会走进这里，只有非取消的才记录
    if (error !== 'cancel' && (error as any)?.message !== 'cancel') {
      console.error('导出部门失败:', error)
    }
  }
}

// 选择变化
const handleSelectionChange = (rows: GetDeptsVo[]) => {
  selectedRows.value = rows
}

// 初始化加载
onMounted(() => {
  loadDepts()
})
</script>

<style scoped>

</style>