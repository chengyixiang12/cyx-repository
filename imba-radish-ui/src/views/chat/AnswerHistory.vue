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
                    <div ref="loadMoreTrigger" class="load-more-trigger"></div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { MoreFilled } from '@element-plus/icons-vue';
import { getDialogueHistoriesApi } from '@/api/dialogueHistory'
import { GetDialogueHistoriesRequest, GetDialogueHistoriesVo } from '@/types/dialogueHistory';

const visibleCards = ref<GetDialogueHistoriesVo[]>([]);
const loadMoreTrigger = ref<HTMLElement | null>(null);

const loadBatchSize = 10;
let loadedCount = 0;
const searchForm = ref<GetDialogueHistoriesRequest>({
    pageNum: 0,
    pageSize: 10,
    keyword: null
});

const loadMore = async () =>  {
    const res = await getDialogueHistoriesApi(searchForm.value);
    const nextBatch = res.records.slice(loadedCount, loadedCount + loadBatchSize);
    visibleCards.value.push(...nextBatch);
    loadedCount += nextBatch.length;
    searchForm.value.pageNum++;
}

function renameCard(item: GetDialogueHistoriesVo) {
    ElMessage.info(`重命名卡片：${item.title}`);
}

function deleteCard(item: GetDialogueHistoriesVo) {
    visibleCards.value = visibleCards.value.filter(c => c.id !== item.id);
    ElMessage.success(`删除成功：${item.title}`);
}

// 懒加载
const lazyLoading = () => {
    const observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting) {
            loadMore();
        }
    });
    if (loadMoreTrigger.value) {
        observer.observe(loadMoreTrigger.value);
    }
}

onMounted(() => {
    lazyLoading();
});
</script>

<style scoped>
.page-container {
    padding: 10px;
    max-height: 83vh;
    overflow: auto;
    background-color: #f5f7fa;
}

.page-container::-webkit-scrollbar {
    height: 6px;
    width: 5px;
}

.page-container::-webkit-scrollbar-thumb {
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
</style>