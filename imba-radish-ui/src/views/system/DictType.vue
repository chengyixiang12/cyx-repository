<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>字典类型管理</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleAddType" class="add-button">
          新增类型
        </el-button>
      </div>
    </div>

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
    <div class="table-wrapper">
      <el-table :data="dictTypeList" border size="small" style="width: 100%" v-loading="loading" :row-class-name="tableRowClassName" height="60vh">
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
                type="primary" 
                @click="handleEditType(scope.row)"
                class="action-button edit-button"
              >
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
                    type="danger" 
                    class="action-button delete-button"
                  >
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
        layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
        @size-change="handleSizeChange" />
    </div>
  </div>

  <dict-type-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible" @submit="handleAddSubmit" />
  <dict-type-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :dictTypeId="dictTypeId" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
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

</style>