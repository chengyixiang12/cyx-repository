<template>
  <div class="container">
    <!-- 头部 -->
    <div class="list-header">
      <div class="header-title">
        <span>定时任务</span>
      </div>
      <div class="right-header">
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键字:">
          <el-input v-model="searchForm.keyword" placeholder="任务名称/任务组" clearable class="keyword-input" />
        </el-form-item>
        <el-form-item label="任务状态:">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="暂停" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 任务表格 -->
    <div class="table-wrapper">
      <el-table :data="jobList" border size="small" style="width: 100%" v-loading="loading" height="calc(100vh - 325px)">
        <el-table-column label="序号" min-width="50" align="center">
          <template #default="scope">
            {{ (searchForm.pageNum - 1) * searchForm.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="jobName" align="center" label="任务名称" show-overflow-tooltip />
        <el-table-column prop="jobGroup" align="center" label="任务组" show-overflow-tooltip />
        <el-table-column prop="jobType" align="center" label="任务类型" show-overflow-tooltip />
        <el-table-column prop="scheduleType" align="center" label="调度类型" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" align="center" min-width="70">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#13ce66"
              inactive-color="#ff4949" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" align="center" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" min-width="240" align="center">
          <template #default="scope">
            <div class="action-buttons-container">
              <el-popconfirm title="确认执行？" confirm-button-text="确认" cancel-button-text="取消"
                @confirm="handleExecute(scope.row.id)">
                <template #reference>
                  <el-button type="primary" :disabled="scope.row.status == 0" class="action-button">
                    执行
                  </el-button>
                </template>
              </el-popconfirm>
              <el-button type="primary" @click="handleEdit(scope.row.id)"
                :disabled="scope.row.status == 1" class="action-button">
                编辑
              </el-button>
              <el-popconfirm title="确认删除？" confirm-button-text="确认" cancel-button-text="取消"
                @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button type="danger" class="action-button">
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
              <el-button type="info" class="action-button" @click="handleViewRecords(scope.row)">
                记录
              </el-button>
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

  <!-- 新增任务弹窗 -->
  <job-form-dialog v-model:visible="addDialogVisible" :is-add="true" v-if="addDialogVisible"
    @submit="handleAddSubmit" />

  <!-- 编辑任务弹窗 -->
  <job-form-dialog v-model:visible="editDialogVisible" :is-add="false" v-if="editDialogVisible" :job-id="jobId"
    @submit="handleEditSubmit" />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import JobFormDialog from './component/JobFormDialog.vue'
import { getQuartzTasksApi, createJobApi, startJobApi, stopJobApi, editJobApi, deleteJobApi, execImmediately } from '@/api/quartz'
import { EditJobRequest, GetQuartzTasksRequest, GetQuartzTasksVo, SaveJobRequest } from '@/types/quartz'
import router from '@/router/routers'

const loading = ref(false)
const jobList = ref<GetQuartzTasksVo[]>([])

const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const jobId = ref<number | null>(null)

const searchForm = ref<GetQuartzTasksRequest>({
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})
const total = ref(0)

// 加载任务列表
const loadJobs = async () => {
  const res = await getQuartzTasksApi(searchForm.value);
  jobList.value = res.records;
  total.value = res.total;
}

// 新增任务
const handleAdd = () => {
  addDialogVisible.value = true
}

// 编辑任务
const handleEdit = (id: number) => {
  jobId.value = id
  editDialogVisible.value = true
}

// 提交新增任务
const handleAddSubmit = async (formData: SaveJobRequest) => {
  await createJobApi(formData);
  await loadJobs();
}

// 提交编辑任务
const handleEditSubmit = async (formData: EditJobRequest) => {
  await editJobApi(formData)
  await loadJobs()
}

// 删除任务
const handleDelete = async (id: string) => {
  await deleteJobApi(id)
  await loadJobs()
}

// 状态变更处理
const handleStatusChange = async (row: GetQuartzTasksVo) => {
  if (row.status === 1) {
    await startJobApi(row.id)
  } else {
    await stopJobApi(row.id)
  }
  await loadJobs()
}

// 查看执行记录
const handleViewRecords = (row: GetQuartzTasksVo) => {
  router.push({ name: 'quartzRecord', query: { id: row.id } })
}

// 分页变化
const handlePageChange = (page: number) => {
  searchForm.value.pageNum = page
  loadJobs()
}

const handleSizeChange = (size: number) => {
  searchForm.value.pageSize = size
  loadJobs()
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  loadJobs()
}

const resetSearch = () => {
  searchForm.value.status = null
  searchForm.value.keyword = ''
  loadJobs()
}

// 立即执行
const handleExecute = async (id: string) => {
  await execImmediately(id)
}

onMounted(() => {
  loadJobs()
})
</script>

<style scoped>

</style>