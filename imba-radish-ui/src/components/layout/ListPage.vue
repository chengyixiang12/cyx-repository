<!--
  列表页统一骨架: 头部 + 搜索区 + 表格区 + 分页
  ------------------------------------------------------------------
  用法:
    <ListPage title="角色管理" :loading="loading" :count="list.length" :columns="8" :model="searchForm">
      <template #actions>
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </template>
      <template #search>
        <el-form-item label="关键字:"> ... </el-form-item>
      </template>

      <el-table :data="list" border size="small" height="100%"> ... </el-table>

      <template #footer>
        <el-pagination ... />
      </template>
    </ListPage>

  约定: 表格统一写 height="100%", 由本组件用 flex 撑满剩余空间。
        不要在页面里再写 calc(100vh - 325px) 这类魔法数字。
-->
<template>
  <div class="list-page">
    <!-- 左侧面板(可选): 如组织架构树 -->
    <div v-if="hasAside" class="list-page__aside">
      <slot name="aside" />
    </div>

    <div class="list-page__main">
      <!-- 头部 -->
      <header class="list-page__header">
        <div v-if="hasPrefix" class="list-page__prefix">
          <slot name="prefix" />
        </div>
        <div class="list-page__title">
          <slot name="title">{{ title }}</slot>
        </div>
        <div class="list-page__actions">
          <slot name="actions" />
        </div>
      </header>

      <!-- 搜索区 -->
      <div v-if="hasSearch" class="list-page__search">
        <el-form :inline="true" :model="model" class="list-page__search-form" @submit.prevent>
          <slot name="search" />
        </el-form>
      </div>

      <!-- 表格区: flex 撑满, 作为骨架屏的定位参照 -->
      <div class="list-page__body">
        <TableSkeleton :loading="loading" :count="count" :columns="columns" />
        <slot />
      </div>

      <!-- 分页 -->
      <div v-if="hasFooter" class="list-page__footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, useSlots } from 'vue'
import TableSkeleton from '@/components/skeleton/TableSkeleton.vue'

withDefaults(defineProps<{
  /** 页面标题, 也可用 #title 插槽覆盖(如需要加图标) */
  title?: string
  /** 加载中: 仅在无数据时显示骨架屏 */
  loading?: boolean
  /** 当前数据条数 */
  count?: number
  /** 骨架屏列数, 与实际表格列数保持一致 */
  columns?: number
  /** 搜索表单绑定的对象 */
  model?: Record<string, unknown>
}>(), {
  title: '',
  loading: false,
  count: 0,
  columns: 6,
  model: () => ({})
})

const slots = useSlots()
const hasAside = computed(() => !!slots.aside)
const hasPrefix = computed(() => !!slots.prefix)
const hasSearch = computed(() => !!slots.search)
const hasFooter = computed(() => !!slots.footer)
</script>

<style scoped>
.list-page {
  height: 100%;
  min-height: 0;
  display: flex;
  gap: var(--radish-space-3);
  padding: 0 5px;
  overflow: hidden;
}

.list-page__aside {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.list-page__main {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ---------- 头部 ---------- */
.list-page__header {
  display: flex;
  align-items: center;
  gap: var(--radish-space-2);
  padding: var(--radish-space-3) var(--radish-space-4);
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  box-shadow: var(--radish-shadow-card);
  flex-shrink: 0;
}

.list-page__prefix {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.list-page__title {
  display: flex;
  align-items: center;
  gap: var(--radish-space-2);
  font-size: var(--radish-font-md);
  font-weight: var(--radish-weight-semibold);
  color: var(--radish-text-primary);
  margin-right: auto;
  min-width: 0;
}

.list-page__actions {
  display: flex;
  align-items: center;
  gap: var(--radish-space-2);
  flex-shrink: 0;
}

/* ---------- 搜索区 ---------- */
.list-page__search {
  padding: var(--radish-space-3) var(--radish-space-4);
  margin: 10px 0;
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  box-shadow: var(--radish-shadow-card);
  flex-shrink: 0;
}

.list-page__search-form {
  display: flex;
  flex-wrap: wrap;
  gap: var(--radish-space-3);
  align-items: center;
}

.list-page__search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* ---------- 表格区 ---------- */
.list-page__body {
  flex: 1;
  min-height: 0;
  background: var(--radish-bg-card);
  border-radius: var(--radish-radius-lg);
  box-shadow: var(--radish-shadow-card);
  overflow: hidden;
  position: relative;
}

/* 表格撑满容器: 页面里写 height="100%" 即可, 无需 calc */
.list-page__body :deep(.el-table) {
  height: 100%;
}

/* ---------- 分页 ---------- */
.list-page__footer {
  display: flex;
  justify-content: flex-end;
  padding: var(--radish-space-3) var(--radish-space-4);
  flex-shrink: 0;
}
</style>
