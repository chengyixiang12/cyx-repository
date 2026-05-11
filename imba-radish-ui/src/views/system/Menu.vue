<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>菜单管理</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleAdd">
          新增
        </el-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键字:">
          <el-input v-model="searchForm.keyword" placeholder="菜单名称/路由" clearable class="keyword-input" />
        </el-form-item>
        <el-form-item label="菜单状态:">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 菜单表格 - 树形数据与懒加载 -->
    <div class="table-wrapper">
      <el-table :data="menuList" border size="small" style="width: 100%" v-loading="loading" height="60vh" lazy
        :load="loadChildMenus" :tree-props="{ children: 'children', hasChildren: 'hasChild' }" row-key="id">
        <el-table-column prop="name" align="center" label="菜单名称" show-overflow-tooltip min-width="180" />
        <el-table-column label="菜单图标" min-width="70" align="center">
          <template #default="scope">
            <el-icon v-if="scope.row.icon">
              <component :is="scope.row.icon" />
            </el-icon>
            <el-icon v-else>
              <QuestionFilled />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="type" align="center" label="类型" :formatter="formatType" show-overflow-tooltip
          width="80" />
        <el-table-column prop="path" align="center" label="路由" show-overflow-tooltip min-width="150" />
        <el-table-column prop="component" align="center" label="组件" show-overflow-tooltip min-width="180" />
        <el-table-column prop="sort" align="center" label="排序" min-width="65" sortable />
        <el-table-column prop="status" label="菜单状态" align="center" min-width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="'1'" :inactive-value="'0'" active-color="#13ce66"
              inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="显示状态" align="center" min-width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.visible" :active-value="'1'" :inactive-value="'0'" active-color="#13ce66"
              inactive-color="#ff4949" @change="handleVisibleChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" align="center">
          <template #default="scope">
            <div class="action-buttons-container">
              <el-button type="primary" @click="handleEdit(scope.row)" class="action-button edit-button">
                编辑
              </el-button>
              <el-popconfirm title="确认删除该菜单吗？" confirm-button-text="确认" cancel-button-text="取消"
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
      <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </div>
  </div>

  <!-- 新增菜单弹窗 -->
  <menu-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑菜单弹窗 -->
  <menu-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :menu-id="menuId"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import {
  addMenuStatusApi,
  updateMenuStatusApi,
  deleteMenuApi,
  pageMenuTreeApi,
  enableMenuApi,
  disableMenuApi,
  menuShowApi,
  menuHideApi
} from '@/api/menu'
import MenuFormDialog from './component/MenuFormDialog.vue'
import type { EditMenuRequest, PageMenuTreeRequest, PageMenuTreeVO, SaveMenuRequest } from '@/types/menu'

const loading = ref(false)
const menuList = ref<PageMenuTreeVO[]>([])
const total = ref(0)

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const menuId = ref<string>('')

// 搜索表单
const searchForm = ref<PageMenuTreeRequest>({
  keyword: null,
  status: null,
  type: null,
  parentId: null,
  pageNum: 1,
  pageSize: 10,
})

const formatType = (row: any, column: any, cellValue: string) => {
  switch (cellValue) {
    case '0':
      return '目录';
    case '1':
      return '菜单';
    case '2':
      return '按钮';
    default:
      return '未知';
  }
};

// 加载菜单列表
const loadMenus = async () => {
  try {
    loading.value = true
    const res = await pageMenuTreeApi({ ...searchForm.value, parentId: null })
    menuList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载菜单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 懒加载子菜单
const loadChildMenus = async (row: PageMenuTreeVO, treeNode: unknown, resolve: (data: PageMenuTreeVO[]) => void) => {
  try {
    const children = await pageMenuTreeApi({ ...searchForm.value, parentId: row.id, pageNum: 1, pageSize: 1000 })
    resolve(children.records)
  } catch (error) {
    console.error('加载子菜单失败:', error)
    resolve([])
  }
}

// 新增菜单
const handleAdd = () => {
  menuId.value = ''
  addDialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row: PageMenuTreeVO) => {
  menuId.value = row.id
  editDialogVisible.value = true
}

// 提交新增菜单
const handleAddSubmit = async (formData: SaveMenuRequest) => {
  await addMenuStatusApi(formData)
  await loadMenus()
}

// 提交编辑菜单
const handleEditSubmit = async (formData: EditMenuRequest) => {
  await updateMenuStatusApi(formData)
  await loadMenus()
}

// 删除菜单
const handleDelete = async (id: string) => {
  await deleteMenuApi(id)
  await loadMenus()
}

// 状态变更
const handleStatusChange = async (row: PageMenuTreeVO) => {
  if (row.status === '1') {
    await enableMenuApi(row.id)
  } else {
    await disableMenuApi(row.id)
  }
  await loadMenus()
}

// 显示状态变更
const handleVisibleChange = async (row: PageMenuTreeVO) => {
  if (row.visible === '1') {
    await menuShowApi(row.id)
  } else {
    await menuHideApi(row.id)
  }
  await loadMenus()
}

// 分页变化
const handlePageChange = (page: number) => {
  searchForm.value.pageNum = page
  loadMenus()
}

// 页面大小变化
const handleSizeChange = (size: number) => {
  searchForm.value.pageSize = size
  searchForm.value.pageNum = 1
  loadMenus()
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadMenus()
}

const resetSearch = () => {
  searchForm.value.status = null
  searchForm.value.type = null
  searchForm.value.keyword = null
  loadMenus()
}

onMounted(() => {
  loadMenus()
})
</script>

<style scoped>

</style>