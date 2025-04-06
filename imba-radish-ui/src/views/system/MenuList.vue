<template>
  <div class="unified-list-container">
    <el-card shadow="hover" class="compact-card">
      <!-- 使用统一的header样式 -->
      <template #header>
        <div class="unified-header compact-header">
          <span class="header-title">菜单列表</span>
          <div class="header-actions">
            <el-button size="small" @click="refreshList">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 表格使用统一样式 -->
      <div class="unified-table">
        <el-table :data="menuList" border style="width: 100%" v-loading="loading"
          :default-sort="{ prop: 'orderNum', order: 'ascending' }" class="compact-table">
          <!-- 表格列保持不变 -->
          <el-table-column label="序号" align="center">
            <template #default="scope">
              {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="name" label="菜单名称" />
          <el-table-column prop="type" label="类型" align="center">
            <template #default="scope">
              <el-tag :type="getTypeTagType(scope.row.type)" size="small">
                {{ getTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="orderNum" label="排序" align="center" sortable />
          <el-table-column prop="status" label="状态" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status ? 'success' : 'danger'" size="small">
                {{ scope.row.status ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="图标" align="center">
            <template #default="scope">
              <el-icon v-if="scope.row.icon" :size="16">
                <component :is="scope.row.icon" />
              </el-icon>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row.id)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页使用统一样式 -->
      <div class="unified-pagination compact-pagination">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          size="small" @size-change="handleSizeChange" @current-change="handlePageChange" />
      </div>
    </el-card>
  </div>

  <menu-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible" @submit="handleAddSubmit" />
  <menu-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :menu-id="menuId" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { GetMenuListRequest, GetMenuListVo, MenuType, GetMenuVo } from '@/types/menu'
import { getMenuList } from '@/api/menu'
import MenuFormDialog from '@/components/system/MenuFormDialog.vue'

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const getMenuListRequest = ref<GetMenuListRequest>({
  menuName: '',
  pageNum: pagination.value.current,
  pageSize: pagination.value.size
})

const loading = ref(false)
const menuList = ref<GetMenuListVo[]>([])
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const menuId = ref<number>(0)

// 新增提交逻辑
const handleAddSubmit = async () => {

}

// 编辑提交逻辑
const handleEditSubmit = async (formData: GetMenuVo) => {
  
}

// 加载菜单数据
const loadMenuList = async () => {
  try {
    loading.value = true
    const data = await getMenuList(getMenuListRequest.value)
    menuList.value = data.result
    pagination.value.total = data.total
  } catch (error) {
    ElMessage.error('加载菜单列表失败')
  } finally {
    loading.value = false
  }
}

// 编辑
const handleEdit = async (id: number) => {
  menuId.value = id;
  editDialogVisible.value = true;
}

// 删除
const handleDelete = async (id: number) => {

}

// 刷新列表
const refreshList = async () => {
  loadMenuList()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  loadMenuList()
}

const getTypeTagType = (type: MenuType) => {
  const types = {
    [MenuType.Directory]: 'primary', // 目录
    [MenuType.Menu]: 'success',     // 菜单
    [MenuType.Button]: 'warning'    // 按钮
  }
  return types[type] || 'info'
}

const getTypeName = (type: MenuType) => {
  const names = {
    [MenuType.Directory]: '目录',
    [MenuType.Menu]: '菜单',
    [MenuType.Button]: '按钮'
  }
  return names[type] || '未知'
}

onMounted(() => {
  loadMenuList()
})
</script>

<style scoped>
.compact-card {
  :deep(.el-card__header) {
    padding: 8px 16px;
    background-color: var(--el-bg-color-page);
  }

  :deep(.el-card__body) {
    padding: 12px;
  }
}

.unified-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 32px;

  .header-title {
    font-size: 14px;
    font-weight: 500;
    color: var(--el-text-color-primary);
  }
}

/* 统一搜索栏样式 */
.unified-search {
  margin-bottom: 12px;

  :deep(.el-input) {
    width: 100%;
    max-width: 400px;
  }
}

/* 统一表格样式 */
.unified-table {
  :deep(.el-table) {
    font-size: 13px;

    th {
      padding: 8px 0;
      background-color: var(--el-bg-color);

      .cell {
        font-weight: 500;
      }
    }

    td {
      padding: 8px 0;
    }
  }
}

/* 统一分页样式 */
.unified-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 紧凑调整 */
.compact-table {
  :deep(.el-table__header th) {
    height: 36px;
  }

  :deep(.el-table__row td) {
    height: 40px;
  }
}

.compact-pagination {
  :deep(.el-pagination) {
    --el-pagination-button-width: 28px;
    --el-pagination-button-height: 28px;
    font-size: 12px;
  }
}
</style>