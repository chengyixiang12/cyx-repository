<template>
  <div class="menu-container container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="menu-card">
          <template #header>
            <div class="list-header">
              <div class="header-title">
                <el-icon class="title-icon"><Menu /></el-icon>
                <span>菜单管理</span>
              </div>
              <div class="right-header">
                <el-button type="primary" @click="handleAdd" class="add-button">
                  <el-icon><Plus /></el-icon>
                  新增菜单
                </el-button>
              </div>
            </div>
          </template>

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
              <el-form-item label="菜单类型:">
                <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 100px">
                  <el-option label="目录" :value="0" />
                  <el-option label="菜单" :value="1" />
                  <el-option label="按钮" :value="2" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 菜单表格 - 树形数据与懒加载 -->
          <div class="list-table">
            <el-table 
              :data="menuList" 
              border 
              size="small" 
              style="width: 100%" 
              v-loading="loading" 
              :row-class-name="tableRowClassName"
              lazy
              :load="loadChildMenus"
              :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
              row-key="id"
            >
              <el-table-column prop="name" align="center" label="菜单名称" show-overflow-tooltip min-width="180" />
              <el-table-column label="菜单图标" min-width="70" align="center">
                <template #default="scope">
                  <el-icon v-if="scope.row.icon">
                    <component :is="getIconComponent(scope.row.icon)" />
                  </el-icon>
                  <el-icon v-else>
                    <QuestionFilled />
                  </el-icon>
                </template>
              </el-table-column>
              <el-table-column prop="type" align="center" label="类型" :formatter="formatType" show-overflow-tooltip width="80" />
              <el-table-column prop="path" align="center" label="路由" show-overflow-tooltip min-width="150" />
              <el-table-column prop="component" align="center" label="组件" show-overflow-tooltip min-width="180" />
              <el-table-column prop="orderNum" align="center" label="排序" min-width="65" sortable />
              <el-table-column prop="status" label="菜单状态" align="center" min-width="80">
                <template #default="scope">
                  <el-switch 
                    v-model="scope.row.status" 
                    :active-value="1" 
                    :inactive-value="0" 
                    active-color="#13ce66"
                    inactive-color="#ff4949" 
                    @change="handleStatusChange(scope.row)" 
                  />
                </template>
              </el-table-column>
              <el-table-column prop="visible" label="显示状态" align="center" min-width="80">
                <template #default="scope">
                  <el-switch 
                    v-model="scope.row.visible" 
                    :active-value="1" 
                    :inactive-value="0" 
                    active-color="#13ce66"
                    inactive-color="#ff4949" 
                    @change="handleVisibleChange(scope.row)" 
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="200" align="center">
                <template #default="scope">
                  <div class="action-buttons-container">
                    <el-button 
                      size="small" 
                      type="primary" 
                      @click="handleAddChild(scope.row)"
                      class="action-button add-child-button"
                    >
                      <el-icon><Plus /></el-icon>
                      新增
                    </el-button>
                    <el-button 
                      size="small" 
                      type="primary" 
                      @click="handleEdit(scope.row)"
                      class="action-button edit-button"
                    >
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-popconfirm 
                      title="确认删除该菜单吗？" 
                      confirm-button-text="确认" 
                      cancel-button-text="取消"
                      @confirm="handleDelete(scope.row.id)"
                    >
                      <template #reference>
                        <el-button 
                          size="small" 
                          type="danger" 
                          class="action-button delete-button"
                        >
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 新增菜单弹窗 -->
  <menu-form-dialog 
    v-model:visible="addDialogVisible" 
    :is-add="true" 
    v-if="addDialogVisible"
    @submit="handleAddSubmit" 
  />

  <!-- 编辑菜单弹窗 -->
  <menu-form-dialog 
    v-model:visible="editDialogVisible" 
    :is-add="false" 
    v-if="editDialogVisible" 
    :menu-id="menuId"
    @submit="handleEditSubmit" 
  />

  <!-- 新增子菜单弹窗 -->
  <menu-form-dialog 
    v-model:visible="addChildDialogVisible" 
    :is-add="true" 
    v-if="addChildDialogVisible" 
    :parent-id="parentMenuId"
    @submit="handleAddChildSubmit" 
  />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Menu, Plus } from '@element-plus/icons-vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import {
  getMenuList,
  addMenuStatusApi,
  updateMenuStatusApi,
  deleteMenuApi,
  enableMenuApi,
  disableMenuApi,
  menuShowApi,
  menuHideApi,
  getMenuTreeApi
} from '@/api/menu'
import MenuFormDialog from './component/MenuFormDialog.vue'
import type { EditMenuRequest, GetMenuListRequest, GetMenuListVo, SaveMenuRequest } from '@/types/menu'

const loading = ref(false)
const menuList = ref<GetMenuListVo[]>([])

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const addChildDialogVisible = ref(false)
const menuId = ref<string>('')
const parentMenuId = ref<string>('')
const total = ref(0)

// 搜索表单
const searchForm = ref<GetMenuListRequest>({
  keyword: '',
  status: null,
  type: '',
  visible: null,
  pageNum: 1,
  pageSize: 1000,
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

const iconsMap = new Map()

Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  iconsMap.set(key.toLowerCase().replace(/icon$/, ''), component)
})

const getIconComponent = (iconName: string) => {
  if (!iconName) return null
  const normalizedName = iconName.toLowerCase().replace(/^el-icon-/, '')
  return iconsMap.get(normalizedName) || null
}

// 加载菜单列表
const loadMenus = async () => {
  try {
    loading.value = true
    const res = await getMenuList(searchForm.value)
    // 直接使用返回的记录，不需要构建树形结构
    menuList.value = (res.records || []).map(menu => ({
      ...menu,
      hasChildren: menu.hasChild || false
    }))
    total.value = res.total || 0
  } catch (error) {
    console.error('加载菜单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 懒加载子菜单
const loadChildMenus = async (row: GetMenuListVo, treeNode: unknown, resolve: (data: GetMenuListVo[]) => void) => {
  try {
    const res = await getMenuTreeApi(row.id)
    const children = res || []
    // 标记是否有子节点
    resolve(children.map(child => ({
      ...child,
      hasChildren: child.hasChild || false
    })))
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

// 新增子菜单
const handleAddChild = (row: GetMenuListVo) => {
  parentMenuId.value = row.id
  addChildDialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row: GetMenuListVo) => {
  menuId.value = row.id
  editDialogVisible.value = true
}

// 提交新增菜单
const handleAddSubmit = async (formData: SaveMenuRequest) => {
  await addMenuStatusApi(formData)
  await loadMenus()
}

// 提交新增子菜单
const handleAddChildSubmit = async (formData: SaveMenuRequest) => {
  await addMenuStatusApi(formData)
  await loadMenus()
}

// 提交编辑菜单
const handleEditSubmit = async (formData: EditMenuRequest) => {
  await updateMenuStatusApi(formData)
  await loadMenus()
}

// 菜单状态变更
const handleStatusChange = async (row: GetMenuListVo) => {
  if (row.status === 1) {
    await enableMenuApi(row.id)
  } else {
    await disableMenuApi(row.id)
  }
  await loadMenus()
}

// 菜单显示状态变更
const handleVisibleChange = async (row: GetMenuListVo) => {
  if (row.visible === 1) {
    await menuShowApi(row.id)
  } else {
    await menuHideApi(row.id)
  }
  await loadMenus()
}

// 删除菜单
const handleDelete = async (id: string) => {
  await deleteMenuApi(id)
  await loadMenus()
}

const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadMenus()
}

const resetSearch = () => {
  searchForm.value.status = null
  searchForm.value.type = ''
  searchForm.value.keyword = ''
  searchForm.value.visible = null
  loadMenus()
}

onMounted(() => {
  loadMenus()
})
</script>

<style scoped>
.menu-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  height: 98%;
}

.list-header {
  height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.title-icon {
  font-size: 20px;
}

.right-header {
  float: right;
}

.add-button {
  margin-right: 0;
}

.search-container {
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 16px;
}

.keyword-input {
  width: 240px !important;
}

.list-table {
  height: calc(100vh - 350px);
  overflow-y: auto;
  margin-bottom: 16px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #303133;
}

.even-row {
  background-color: #ffffff;
}

.odd-row {
  background-color: #f9f9f9;
}

.action-buttons-container {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 4px;
}

.add-child-button {
  background-color: #67C23A;
  border-color: #67C23A;
}

.add-child-button:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}
</style>