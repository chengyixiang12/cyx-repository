<template>
  <div class="dictData-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="list-header">
              <el-button link icon="ArrowLeft" @click="goBack" class="go-back">返回</el-button>
              <span class="header-title">{{ route.query.dictName }}</span>
              <el-button type="primary" @click="handleAddData">新增</el-button>
            </div>
          </template>

          <!-- 搜索 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="关键字:">
                <el-input v-model="searchForm.keyword" placeholder="标签/编码" clearable class="keyword-input" />
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

          <!-- 数据表格 -->
          <div class="list-table">
            <el-table :data="dictDataList" border size="small" style="width: 100%" v-loading="loading">
              <el-table-column label="序号" min-width="50" align="center">
                <template #default="scope">
                  {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="label" align="center" label="标签" />
              <el-table-column prop="code" align="center" label="编码" />
              <el-table-column prop="value" align="center" label="值" />
              <el-table-column prop="isDefault" label="默认" min-width="80" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.isDefault" :active-value="1" :inactive-value="0" @change="setDefault(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" min-width="80" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
                    @change="changeStatus(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column prop="sortOrder" label="排序" min-width="80" align="center" sortable />
              <el-table-column label="操作" min-width="120" align="center">
                <template #default="scope">
                  <el-button size="small" type="primary" :icon="Edit" circle @click="editData(scope.row)" />
                  <el-popconfirm title="确认删除该数据？" @confirm="deleteData(scope.row.id)">
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
              @size-change="handleSizeChange" size="default" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!--新增弹窗-->
  <dict-data-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible" @submit="handleAddSubmit" />
  <!--编辑弹窗-->
  <dict-data-form-dialog v-model:visible="editDialogVisible" :is-add="false" :dict-data-id="dictDataId" v-if="editDialogVisible" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Edit, Delete } from '@element-plus/icons-vue'
import type { DictDatasRequest, DictDatasVo, SaveDictDataRequest } from '@/types/dictData'
import DictDataFormDialog from '@/components/system/DictDataFormDialog.vue'
import { deleteDictDataApi, editDictDataApi, enableDictDataApi, forbiddenDictDataApi, getDictDatasApi, saveDictDataApi, setDefaultRoleApi } from '@/api/dictData'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const total = ref<number>(0)
const dictDataId = ref<number | null>(null)
const addDialogVisible = ref<boolean>(false)
const editDialogVisible = ref<boolean>(false)
const dictType = ref<string>(route.query.dictType as string)

const dictDataList = ref<DictDatasVo[]>([])
const searchForm = ref<DictDatasRequest>({
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10,
  dictType: dictType.value
})

// 返回字典类型模块
const goBack = () => {
  router.push({ name: '/dict' })
}

// 新增
const handleAddData = async () => {
  addDialogVisible.value = true
}

// 编辑
const editData = async (row: DictDatasVo) => {
  editDialogVisible.value = true
  dictDataId.value = row.id
}

// 新增数据提交
const handleAddSubmit = async (formdata: SaveDictDataRequest) => {
  formdata.dictType = dictType.value;
  await saveDictDataApi(formdata)
  await handleSearch()
}

// 编辑数据提交
const handleEditSubmit = async (formdata: SaveDictDataRequest) => {
  formdata.dictType = dictType.value;
  await editDictDataApi({
    ...formdata,
    id: dictDataId.value
  })
}

// 删除
const deleteData = async (id: number) => {
  await deleteDictDataApi(id)
  await handleSearch()
}

// 修改状态
const changeStatus = async (row: DictDatasVo) => {
  if (row.status === 1) {
    await enableDictDataApi(row.id)
  } else {
    await forbiddenDictDataApi(row.id)
  }
  await handleSearch();
}

// 列表条件查询
const handleSearch = async () => {
  const res = await getDictDatasApi(searchForm.value)
  dictDataList.value = res.records
  total.value = res.total
}

// 重置
const resetSearch = () => {
  searchForm.value.keyword = '';
  searchForm.value.status = null;
}

// 设置默认
const setDefault = async (row: DictDatasVo) => {
  await setDefaultRoleApi(row.id, dictType.value);
  await handleSearch();
}

const handlePageChange = (val: number) => { searchForm.value.pageNum = val }
const handleSizeChange = (val: number) => { searchForm.value.pageSize = val }

onMounted(() => {
  handleSearch()
})
</script>
<style scoped>
.dictData-container {
  height: 100%;
  padding: 10px;
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
  height: 53vh;
  overflow: auto;
  padding-top: 12px;
}

.list-table::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}
.list-table::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
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

:deep(.el-card__body) {
    padding: 14px !important;
}

.go-back {
  font-size: small;
}
</style>