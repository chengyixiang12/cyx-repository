<template>
    <div class="page-container">
        <div style="margin-bottom: 5px;">
            <el-row :gutter="20" style="width: 100%;">
                <el-col :span="24">
                    <div class="search-block">
                        <el-form :inline="true">
                            <el-form-item label="关键字">
                                <el-input v-model="searchForm.keyword" placeholder="请填写关键字" />
                            </el-form-item>
                        </el-form>
                    </div>
                </el-col>
            </el-row>
        </div>
        <div class="card-container">
            <el-row :gutter="20" style="width: 100%;">
                <el-col :span="24">
                    <div class="card-list">
                        <el-card shadow="hover" v-for="(item, index) in visibleCards" :key="item.id" class="card-item">
                            <template #header>
                                <div class="card-header">
                                    <span class="title">{{ item.title }}</span>
                                    <el-dropdown>
                                        <span class="el-dropdown-link">
                                            <el-icon>
                                                <MoreFilled />
                                            </el-icon>
                                        </span>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-item @click="renameCard(item)">重命名</el-dropdown-item>
                                                <el-dropdown-item @click="deleteCard(item)">删除</el-dropdown-item>
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                            </template>
                            <div class="card-content">
                                {{ item.content }}
                            </div>
                            <template #footer>
                                <div class="card-footer">
                                    <span>创建时间：{{ item.createTime }}</span>
                                    <span>创建人：{{ item.createBy }}</span>
                                </div>
                            </template>
                        </el-card>
                    </div>
                </el-col>
            </el-row>
        </div>
        <!-- 分页 -->
          <div class="list-pagination">
            <el-pagination :current-page="searchForm.pageNum" :page-size="searchForm.pageSize" :total="total"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange" @size-change="handleSizeChange" size="default" />
          </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { MoreFilled } from '@element-plus/icons-vue';
import { deleteDialogueApi, getDialogueHistoriesApi } from '@/api/dialogueHistory'
import { GetDialogueHistoriesRequest, GetDialogueHistoriesVo } from '@/types/dialogueHistory';

const visibleCards = ref<GetDialogueHistoriesVo[]>([]);
const total = ref<number>(0);

const searchForm = ref<GetDialogueHistoriesRequest>({
    pageNum: 1,
    pageSize: 10,
    keyword: null
});

const loadMore = async () => {
    const res = await getDialogueHistoriesApi(searchForm.value);
    total.value = res.total;
    visibleCards.value.length = 0;
    visibleCards.value.push(...res.records);
}

function renameCard(item: GetDialogueHistoriesVo) {
    ElMessage.info(`重命名卡片：${item.title}`);
}

const deleteCard = async(item: GetDialogueHistoriesVo) => {
    await deleteDialogueApi(item.id);
    await loadMore(); 
}

const handlePageChange = (val: number) => {
  searchForm.value.pageNum = val
  loadMore()
}

const handleSizeChange = (val: number) => {
  searchForm.value.pageSize = val
  loadMore()
}

onMounted(() => {
    loadMore();
});
</script>

<style scoped>
.page-container {
    padding: 10px;
    background-color: #f5f7fa;
}

.card-container {
    max-height: 62vh;
    overflow: auto;
}

.card-container::-webkit-scrollbar {
    height: 6px;
    width: 5px;
}

.card-container::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 3px;
}

.search-block {
    background: #fff;
    padding: 10px;
    border-radius: 4px;
}

.card-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.card-item {
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-header .title {
    flex: 1;
    text-align: center;
    font-weight: bold;
}

.card-content {
    min-height: 60px;
    padding: 10px 0;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #666;
}

.load-more-trigger {
    height: 1px;
}
.list-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>