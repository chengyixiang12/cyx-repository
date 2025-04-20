<template>
  <div class="menu-container">
    <el-row :gutter="20">
      <!-- 右侧菜单表格 -->
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <span class="header-title">菜单管理</span>
              <div>
                <el-button type="primary" @click="handleAdd" class="header-button">新增菜单</el-button>
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

          <!-- 菜单表格 -->
          <div class="list-table">
            <el-table :data="menuList" border size="small" style="width: 100%" v-loading="loading">
              <el-table-column label="序号" width="80" align="center">
                <template #default="scope">
                  {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="name" label="菜单名称" show-overflow-tooltip />
              <el-table-column label="菜单图标" width="70" align="center">
                <template #default="scope">
                  <el-icon v-if="scope.row.icon">
                    <component :is="getIconComponent(scope.row.icon)" />
                  </el-icon>
                  <el-icon v-else>
                    <QuestionFilled />
                  </el-icon>
                </template>
              </el-table-column>
              <el-table-column prop="parentName" label="父级菜单" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" :formatter="formatType" show-overflow-tooltip />
              <el-table-column prop="path" label="路由" show-overflow-tooltip />
              <el-table-column prop="component" label="组件" show-overflow-tooltip />
              <el-table-column prop="orderNum" label="排序" width="50" />
              <el-table-column prop="status" label="菜单状态" align="center" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#13ce66"
                    inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="handleEdit(scope.row)" :icon="Edit" circle />
                  <el-popconfirm title="确认删除吗？" confirm-button-text="确认" cancel-button-text="取消"
                    @confirm="handleDelete(scope.row.id)">
                    <template #reference>
                      <el-button size="small" type="danger" :icon="Delete" circle />
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 分页 -->
          <div class="list-pagination">
            <el-pagination :current-page="pagination.current" :page-size="pagination.size" :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" @size-change="handleSizeChange" size="small" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 新增菜单弹窗 -->
  <menu-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑菜单弹窗 -->
  <menu-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :menuId="menuId" :type="type"
    @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import {
  getMenuList,
  addMenuStatusApi,
  updateMenuStatusApi,
  deleteMenuApi,
  enableMenuApi,
  disableMenuApi
} from '@/api/menu'
import MenuFormDialog from '@/components/system/MenuFormDialog.vue'
import type { EditMenuRequest, GetMenuListVo, SaveMenuRequest } from '@/types/menu'

const loading = ref(false)
const menuList = ref<GetMenuListVo[]>([])

// 弹窗相关状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const menuId = ref<number>(0)
const type = ref<string>('')

// 搜索表单
const searchForm = ref({
  keyword: '',
  status: undefined as number | undefined,
  type: '' as string | undefined
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
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

// 初始化图标映射
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  // 将图标名称转换为统一格式（如全部小写）
  iconsMap.set(key.toLowerCase().replace(/icon$/, ''), component)
})

const getIconComponent = (iconName: string) => {
  if (!iconName) return null
  
  // 统一处理图标名称格式
  const normalizedName = iconName.toLowerCase().replace(/^el-icon-/, '')
  
  // 从映射中查找图标
  return iconsMap.get(normalizedName) || null
}

// 加载菜单列表
const loadMenus = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      keyword: searchForm.value.keyword,
      status: searchForm.value.status,
      type: searchForm.value.type
    }
    const res = await getMenuList(params)
    menuList.value = res.records || []
    pagination.value.total = res.total || 0
  } catch (error) {
    console.error('加载菜单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增菜单
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row: GetMenuListVo) => {
  type.value = row.type;
  menuId.value = row.id
  editDialogVisible.value = true
}

// 提交新增菜单
const handleAddSubmit = async (formData: SaveMenuRequest) => {
  await addMenuStatusApi(formData)
  loadMenus()
}

// 提交编辑菜单
const handleEditSubmit = async (formData: EditMenuRequest) => {
  await updateMenuStatusApi(formData)
  loadMenus()
}

// 菜单状态变更
const handleStatusChange = async (row: GetMenuListVo) => {
  if (row.status === 1) {
    await enableMenuApi(row.id);
  } else {
    await disableMenuApi(row.id);
  }
  await loadMenus();
}

// 删除菜单
const handleDelete = async (id: number) => {
  await deleteMenuApi(id)
  loadMenus()
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadMenus()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  loadMenus()
}

onMounted(() => {
  loadMenus()
})

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  loadMenus()
}

const resetSearch = () => {
  searchForm.value.status = undefined
  searchForm.value.type = ''
  searchForm.value.keyword = ''
  loadMenus()
}
</script>

<style scoped>
.menu-container {
  height: 100%;
  padding: 12px;
  background-color: #f5f7fa;
}

.list-table {
  width: 100%;
  overflow-x: auto;
  padding-top: 12px;
}

.header-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 36px;
  padding: 0 12px;
}

.el-card {
  height: 100%;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

:deep(.el-card__header) {
  padding: 8px 12px !important;
  min-height: 36px !important;
  border-bottom: 1px solid #ebeef5;
}

.search-container {
  padding: 12px;
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.keyword-input {
  width: 200px !important;
}

:deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 16px;
}

:deep(.el-form-item__label) {
  padding-right: 8px;
  color: #606266;
}

.list-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.header-button {
  padding: 4px 12px;
  font-size: 13px;
  height: 28px;
}
</style>
