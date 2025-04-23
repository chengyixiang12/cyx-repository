<template>
  <div class="dictType-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <span class="header-title">字典类型管理</span>
              <el-button type="primary" @click="handleAddType">新增</el-button>
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
            <el-table :data="dictTypeList" border size="small" style="width: 100%" v-loading="loading">
              <el-table-column label="序号" width="80" align="center">
                <template #default="scope">
                  {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="dictName" label="字典名称" show-overflow-tooltip>
                <template #default="scope">
                  <el-link @click="goToData(scope.row)" type="primary">{{ scope.row.dictName }}</el-link>
                </template>
              </el-table-column>
              <el-table-column prop="dictType" label="字典类型" show-overflow-tooltip />
              <el-table-column prop="sortOrder" label="排序" sortable />
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
                    @change="changeStatus(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-button size="small" type="primary" :icon="Edit" circle @click="handleEditType(scope.row)" />
                  <el-popconfirm title="确认删除？" @confirm="deleteType(scope.row.id)">
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
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
              layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
              @size-change="handleSizeChange" size="small" />
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
import { Edit, Delete } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getDictTypesApi, deleteDictTypeApi, enableDictTypeApi, forbiddenDictTypeApi, saveDictTypeApi, editDictTypeApi } from '@/api/dictType'
import type { DictTypesVo, DictTypeVo, GetDictTypesRequest, SaveDictTypeRequest } from '@/types/dictType'
import DictTypeFormDialog from '@/components/system/DictTypeFormDialog.vue'

const router = useRouter()
const loading = ref(false)
const addDialogVisible = ref<boolean>(false)
const editDialogVisible = ref<boolean>(false)
const dictTypeId = ref<number | null>(null);

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
  router.push({ name: '/dictdata', query: { dictType: row.dictType, dictName: row.dictName } })
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
const deleteType = async (id: number) => {
  await deleteDictTypeApi(id);
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
    id: dictTypeId.value,
    dictName: formData.dictName,
    dictType: formData.dictType,
    sortOrder: formData.sortOrder,
    status: formData.status,
    remark: formData.remark
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
const handlePageChange = (val: number) => { searchForm.value.pageNum = val }
const handleSizeChange = (val: number) => { searchForm.value.pageNum = val }

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
.dictType-container {
  height: 100%;
  padding: 12px;
  background-color: #f5f7fa;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 36px;
  padding: 0 12px;
}

.header-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.header-button {
  padding: 4px 12px;
  font-size: 13px;
  height: 28px;
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

.list-table {
  width: 100%;
  overflow-x: auto;
  padding-top: 12px;
}

.list-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
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
</style>