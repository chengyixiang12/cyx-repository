<template>
  <div class="menu-list-container">
    <el-card shadow="hover" class="custom-card">
      <!-- 头部区域 -->
      <template #header>
        <div class="card-header">
          <div class="header-content">
            <span class="header-title">菜单管理</span>
          </div>
          <div class="header-actions">
            <el-button type="primary" size="small" plain @click="addDialogVisible = true">
              <el-icon><Plus /></el-icon>新增菜单
            </el-button>
            <el-button size="small" @click="handleRefresh">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>

      <div class="query-wrapper">
  <el-card shadow="never" class="query-card">
    <el-form :inline="true" :model="queryForm" class="query-form">
      <el-form-item label="菜单名称" class="query-form-item">
        <el-input 
          v-model="queryForm.name" 
          placeholder="请输入菜单名称" 
          clearable 
          size="small"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="菜单状态" class="query-form-item">
        <el-select 
          v-model="queryForm.status" 
          placeholder="请选择状态" 
          clearable 
          size="small" 
          style="width: 120px"
        >
          <el-option label="全部" value="" />
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item class="query-form-item">
        <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
        <el-button size="small" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</div>

      <!-- 表格区域 -->
      <div class="table-wrapper">
        <el-table 
          :data="menuList" 
          border 
          style="width: 100%" 
          v-loading="loading" 
          size="small"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }" 
          stripe 
          highlight-current-row
        >
          <el-table-column label="序号" width="80" align="center">
            <template #default="scope">
              <span class="text-muted">
                {{ (pagination.current - 1) * pagination.size + scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="菜单名称" min-width="150">
            <template #default="scope">
              <div class="menu-name-cell">
                <el-icon v-if="scope.row.icon" class="menu-icon">
                  <component :is="scope.row.icon" />
                </el-icon>
                <span>{{ scope.row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getTypeTagType(scope.row.type)" size="small" effect="light">
                {{ getTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="orderNum" label="排序" width="80" align="center" sortable>
            <template #default="scope">
              <el-tag size="small" effect="plain">{{ scope.row.orderNum }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="scope">
              <el-switch 
                :model-value="scope.row.status === 1" 
                active-text="启用" 
                inactive-text="禁用" 
                inline-prompt
                size="small"
                @change="(val: number) => handleStatusChange(scope.row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row.id)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-popconfirm 
                title="确定要删除此菜单吗？" 
                confirm-button-text="确认" 
                cancel-button-text="取消"
                @confirm="handleDelete(scope.row.id)"
              >
                <template #reference>
                  <el-button size="small" type="danger">
                    <el-icon><Delete /></el-icon>删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-wrapper">
        <el-pagination 
          v-model:current-page="pagination.current" 
          v-model:page-size="pagination.size"
          :total="pagination.total" 
          :page-sizes="[10, 20, 50, 100]" 
          layout="total, sizes, prev, pager, next, jumper"
          small 
          background 
          @size-change="handleSizeChange" 
          @current-change="handlePageChange" 
        />
      </div>
    </el-card>

    <!-- 对话框组件 -->
    <menu-form-dialog 
      v-model:visible="addDialogVisible" 
      :is-add="true" 
      @submit="handleAddSubmit" 
    />
    <menu-form-dialog 
      v-model:visible="editDialogVisible" 
      :is-add="false" 
      :menu-id="menuId"
      @submit="handleEditSubmit" 
    />
  </div>
</template>

<script lang="ts" setup>
import { Menu, Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { GetMenuListRequest, GetMenuListVo, MenuType } from '@/types/menu'
import { getMenuList, deleteMenuApi, updateMenuStatusApi } from '@/api/menu'
import MenuFormDialog from '@/components/system/MenuFormDialog.vue'

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 查询表单
const queryForm = reactive({
  name: '',
  status: ''
})

// 列表数据
const menuList = ref<GetMenuListVo[]>([])
const loading = ref(false)
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const menuId = ref<number>(0)

// 获取菜单列表
const fetchMenuList = async () => {
  loading.value = true
  try {
    const params: GetMenuListRequest = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      menuName: queryForm.name,
      status: queryForm.status ? Number(queryForm.status) : undefined
    }
    
    const { code, data } = await getMenuList(params)
    if (code === 2001) {
      menuList.value = data?.result || []
      pagination.total = data?.total || 0
    }
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  pagination.current = 1
  fetchMenuList()
}

// 重置查询
const handleReset = () => {
  queryForm.name = ''
  queryForm.status = ''
  handleQuery()
}

// 刷新
const handleRefresh = () => {
  fetchMenuList()
}

// 状态变更
const handleStatusChange = async (row: GetMenuListVo, newStatus: number) => {
  try {
    loading.value = true
    const status = newStatus ? 1 : 0
    await updateMenuStatusApi(row.id, status)
    
    // 更新本地数据
    const index = menuList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      menuList.value[index].status = status
    }
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
  } finally {
    loading.value = false
  }
}

// 编辑
const handleEdit = (id: number) => {
  menuId.value = id
  editDialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await deleteMenuApi(id)
    ElMessage.success('删除成功')
    // 如果当前页只有一条数据且不是第一页，则返回上一页
    if (menuList.value.length === 1 && pagination.current > 1) {
      pagination.current--
    }
    fetchMenuList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchMenuList()
}

const handlePageChange = (page: number) => {
  pagination.current = page
  fetchMenuList()
}

// 新增提交
const handleAddSubmit = async () => {
  addDialogVisible.value = false
  await fetchMenuList()
}

// 编辑提交
const handleEditSubmit = async () => {
  editDialogVisible.value = false
  await fetchMenuList()
}

// 类型标签样式
const getTypeTagType = (type: MenuType) => {
  const typeMap = {
    [MenuType.Directory]: 'primary',
    [MenuType.Menu]: 'success',
    [MenuType.Button]: 'warning'
  }
  return typeMap[type] || 'info'
}

// 类型名称
const getTypeName = (type: MenuType) => {
  const nameMap = {
    [MenuType.Directory]: '目录',
    [MenuType.Menu]: '菜单',
    [MenuType.Button]: '按钮'
  }
  return nameMap[type] || '未知'
}

// 初始化加载
onMounted(() => {
  fetchMenuList()
})
</script>

<style scoped lang="scss">
.menu-list-container {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;

  .custom-card {
    border-radius: 8px;
    border: 1px solid var(--el-border-color-light);
    flex: 1;
    display: flex;
    flex-direction: column;

    :deep(.el-card__header) {
      padding: 12px 20px;
      border-bottom: 1px solid var(--el-border-color-light);
      background-color: #f8fafc;
    }

    :deep(.el-card__body) {
      padding: 0;
      flex: 1;
      display: flex;
      flex-direction: column;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-content {
      display: flex;
      align-items: center;

      .header-icon {
        margin-right: 8px;
        color: var(--el-color-primary);
        font-size: 18px;
      }

      .header-title {
        font-size: 16px;
        font-weight: 500;
        color: var(--el-text-color-primary);
      }
    }
  }

  .query-wrapper {
    margin-top: 16px;
  }

  .query-card {
    margin: 0 16px 16px;
    border: none;
    background-color: var(--el-bg-color-page);

    :deep(.el-card__body) {
      padding: 16px 20px;
    }
  }

  .query-form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
  }

  .query-form-item {
    margin-bottom: 0;
    margin-right: 16px;
  }

  .table-wrapper {
    flex: 1;
    padding: 0 16px;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    .el-table {
      flex: 1;
    }
  }

  .menu-name-cell {
    display: flex;
    align-items: center;

    .menu-icon {
      margin-right: 8px;
      color: var(--el-color-primary);
    }
  }

  .pagination-wrapper {
    padding: 16px;
    border-top: 1px solid var(--el-border-color-light);
  }

  .text-muted {
    color: var(--el-text-color-secondary);
  }
}
</style>
