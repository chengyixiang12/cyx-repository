<template>
  <div class="dictType-container container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="dictType-card">
          <template #header>
            <div class="list-header">
              <div class="header-title">
                <el-icon class="title-icon"><Document /></el-icon>
                <span>字典类型管理</span>
              </div>
              <div class="right-header">
                <el-button type="primary" @click="handleAddType" class="add-button">
                  <el-icon><Plus /></el-icon>
                  新增类型
                </el-button>
              </div>
            </div>
          </template>

          <!-- 搜索条件 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="字典名称/类型" clearable class="keyword-input" />
              </el-form-item>
              <el-form-item label="状态:">
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

          <!-- 类型表格 -->
          <div class="list-table">
            <el-table :data="dictTypeList" border size="small" style="width: 100%" v-loading="loading" :row-class-name="tableRowClassName">
              <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                  {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="dictName" label="字典名称" align="center" show-overflow-tooltip>
                <template #default="scope">
                  <el-link @click="goToData(scope.row)" type="primary">{{ scope.row.dictName }}</el-link>
                </template>
              </el-table-column>
              <el-table-column prop="sortOrder" label="排序" align="center" min-width="80" sortable />
              <el-table-column prop="status" label="状态" min-width="80" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
                    @change="changeStatus(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="200" align="center">
                <template #default="scope">
                  <div class="action-buttons-container">
                    <el-button 
                      size="small" 
                      type="primary" 
                      @click="handleEditType(scope.row)"
                      class="action-button edit-button"
                    >
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-popconfirm 
                      title="确认删除该类型吗？" 
                      confirm-button-text="确认" 
                      cancel-button-text="取消"
                      @confirm="deleteType(scope.row.id)"
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

          <!-- 分页 -->
          <div class="list-pagination">
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
              layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
              @size-change="handleSizeChange" size="default" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <dict-type-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible" @submit="handleAddSubmit" />
  <dict-type-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :dictTypeId="dictTypeId" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete, Document, Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getDictTypesApi, deleteDictTypeApi, enableDictTypeApi, forbiddenDictTypeApi, saveDictTypeApi, editDictTypeApi } from '@/api/dictType'
import type { DictTypesVo, GetDictTypesRequest, SaveDictTypeRequest } from '@/types/dictType'
import DictTypeFormDialog from './component/DictTypeFormDialog.vue'

const router = useRouter()
const loading = ref(false)
const addDialogVisible = ref<boolean>(false)
const editDialogVisible = ref<boolean>(false)
const dictTypeId = ref<string>('');

const dictTypeList = ref<DictTypesVo[]>([])
const searchForm = ref<GetDictTypesRequest>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: null
})
const total = ref<number>(0)

// 跳转字典数据模块
const goToData = (row: DictTypesVo) => {
  router.push({ name: 'dictData', query: { parentId: row.id, dictName: row.dictName } })
}

// 打开新增字典类型弹窗
const handleAddType = () => {
  addDialogVisible.value = true;
}
// 打开编辑弹窗
const handleEditType = (row: DictTypesVo) => {
  editDialogVisible.value = true;
  dictTypeId.value = row.id;
}
// 删除
const deleteType = async (id: string) => {
  await deleteDictTypeApi(id);
  await handleSearch();
}
// 状态切换接口
const changeStatus = async (row: DictTypesVo) => {
  if (row.status === 1) {
    await enableDictTypeApi(row.id);
  } else {
    await forbiddenDictTypeApi(row.id);
  }
  await handleSearch();
}
// 新增提交
const handleAddSubmit = async (formData: SaveDictTypeRequest) => {
  await saveDictTypeApi(formData)
  await handleSearch()
}
// 编辑提交
const handleEditSubmit = async (formData: SaveDictTypeRequest) => {
  await editDictTypeApi({
    ...formData,
    id: dictTypeId.value,
  })
  await handleSearch()
}

const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    status: null,
    pageNum: 1,
    pageSize: 10
  }
  handleSearch();
}
const handlePageChange = (val: number) => {
  searchForm.value.pageNum = val;
  handleSearch()
 }
const handleSizeChange = (val: number) => {
  searchForm.value.pageSize = val;
  handleSearch()
}

const tableRowClassName = ({ rowIndex }: { rowIndex: number }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

const handleSearch = async () => {
  try {
    loading.value = true;
    const data = await getDictTypesApi(searchForm.value);
    total.value = data.total;
    dictTypeList.value = data.records;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  handleSearch()
})
</script>
<style scoped>
/* DictType页面特有样式 */

/* 字典类型卡片样式 */
.dictType-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  height: 98%;
}

/* 列表头部样式 */
.list-header {
  height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

/* 搜索区域样式 */
.search-container {
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
  margin-bottom: 16px;
}

/* 关键字输入框样式 */
.keyword-input {
  width: 240px !important;
}

/* 表格样式 */
.list-table {
  height: calc(100vh - 400px);
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

/* 表格行样式 */
.even-row {
  background-color: #ffffff;
}

.odd-row {
  background-color: #f9f9f9;
}

/* 分页样式 */
.list-pagination {
  padding: 0 12px 12px;
}
</style>