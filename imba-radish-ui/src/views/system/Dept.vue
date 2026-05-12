<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>部门管理</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleAdd" class="add-button">
          新增
        </el-button>
        <el-button type="primary" @click="handleExport" class="export-button">
          导出
        </el-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
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
      </el-form>
    </div>

    <!-- 部门表格 -->
    <div class="table-wrapper">
      <el-table :data="deptList" border size="small" style="width: 100%" v-loading="loading" highlight-current-row
        @selection-change="handleSelectionChange" height="calc(100vh - 350px)">
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
              <el-popconfirm title="确认删除该部门吗？" confirm-button-text="确认" cancel-button-text="取消"
                @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button type="danger" class="action-button delete-button">
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </div>
  </div>

  <!-- 新增部门弹窗 -->
  <dept-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑部门弹窗 -->
  <dept-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :deptId="deptId"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import {
  getDeptsApi,
  deleteDeptApi,
  saveDeptApi,
  updateDeptApi,
  exportDept
} from '@/api/dept'
import type { GetDeptsRequest, GetDeptsVo, SaveDeptRequest, EditDeptRequest } from '@/types/dept'
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
  await saveDeptApi(formData)
  await loadDepts()
}

// 提交编辑部门
const handleEditSubmit = async (formData: EditDeptRequest) => {
  await updateDeptApi({
    id: deptId.value,
    code: formData.code,
    name: formData.name,
    sortOrder: formData.sortOrder || 0,
    parentId: formData.parentId
  })
  await loadDepts()
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

// 删除部门
const handleDelete = async (id: string) => {
  await deleteDeptApi(Number(id))
  await loadDepts()
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
  try {
    const ids = selectedRows.value.length > 0
      ? selectedRows.value.map(row => row.id)
      : []
    const { blob, filename } = await exportDept(ids, '部门数据')
    download(blob, filename)
  } catch (error) {
    console.error('导出部门失败:', error)
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