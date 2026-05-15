<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <el-button type="primary" @click="goBack" class="go-back">
        返回
      </el-button>
      <div class="header-title">
        <span>{{ route.query.dictName }}</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleAddData" class="add-button">
          新增
        </el-button>
      </div>
    </div>

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
    <div class="table-wrapper">
      <el-table :data="dictDataList" border size="small" style="width: 100%" v-loading="loading"
        :row-class-name="tableRowClassName" height="calc(100vh - 350px)">
        <el-table-column label="序号" min-width="50" align="center">
          <template #default="scope">
            {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="label" align="center" label="标签" />
        <el-table-column prop="value" align="center" label="值" />
        <el-table-column prop="isDefault" label="默认" min-width="80" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.isDefault" :active-value="1" :inactive-value="0"
              @change="setDefault(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
              @change="changeStatus(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" min-width="80" align="center" sortable />
        <el-table-column label="操作" min-width="180" align="center">
          <template #default="scope">
            <div class="action-buttons-container">
              <el-button type="primary" @click="editData(scope.row)" class="action-button edit-button">
                编辑
              </el-button>
              <el-popconfirm title="确认删除该数据吗？" confirm-button-text="确认" cancel-button-text="取消"
                @confirm="deleteData(scope.row.id)">
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
        layout="total, sizes, prev, pager, next, jumper" @current-change="handlePageChange"
        @size-change="handleSizeChange" />
    </div>
  </div>

  <!--新增弹窗-->
  <dict-data-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />
  <!--编辑弹窗-->
  <dict-data-form-dialog v-model:visible="editDialogVisible" :is-add="false" :dict-data-id="dictDataId"
    v-if="editDialogVisible" @submit="handleEditSubmit" />
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import type { DictDatasRequest, DictDatasVo, SaveDictDataRequest } from '@/types/dictData'
import DictDataFormDialog from './component/DictDataFormDialog.vue'
import { deleteDictDataApi, editDictDataApi, enableDictDataApi, forbiddenDictDataApi, getDictDatasApi, saveDictDataApi, setDefaultRoleApi } from '@/api/dictData'
import { showMessage } from '@/utils/message'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const total = ref<number>(0)
const dictDataId = ref<string>('')
const addDialogVisible = ref<boolean>(false)
const editDialogVisible = ref<boolean>(false)
const parentId = ref<string>(route.query.parentId as string)

const dictDataList = ref<DictDatasVo[]>([])
const searchForm = ref<DictDatasRequest>({
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10,
  parentId: parentId.value
})

// 返回字典类型模块
const goBack = () => {
  router.push({ name: 'dict' })
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
  formdata.parentId = parentId.value;
  await saveDictDataApi(formdata);
  await handleSearch();
}

// 编辑数据提交
const handleEditSubmit = async (formdata: SaveDictDataRequest) => {
  formdata.parentId = parentId.value;
  await editDictDataApi({
    ...formdata,
    id: dictDataId.value
  });
  await handleSearch();
}

// 删除
const deleteData = async (id: string) => {
  await deleteDictDataApi(id);
  await handleSearch();
}

// 修改状态
const changeStatus = async (row: DictDatasVo) => {
  if (row.status === 1) {
    await enableDictDataApi(row.id);
  } else {
    await forbiddenDictDataApi(row.id);
  }
  await handleSearch();
}

// 列表条件查询
const handleSearch = async () => {
  const res = await getDictDatasApi(searchForm.value);
  dictDataList.value = res.records;
  total.value = res.total;
}

// 重置
const resetSearch = () => {
  searchForm.value.keyword = '';
  searchForm.value.status = null;
}

// 设置默认
const setDefault = async (row: DictDatasVo) => {
  if (row.isDefault === 1) {
    await setDefaultRoleApi(row.id, parentId.value);
  } else {
    showMessage('非法操作', 'warning');
    row.isDefault = 1;
  }
  await handleSearch();
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

onMounted(() => {
  handleSearch();
})
</script>
<style scoped>

</style>