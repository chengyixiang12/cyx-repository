<template>
    <!-- <el-card class="module-tabs-card" shadow="never" :body-style="{ padding: '0px' }">
        
    </el-card> -->
    <el-tabs :model-value="activePath" type="card" @tab-click="handleTabClick" @tab-remove="handleTabClose"
            class="module-tabs">
            <el-tab-pane v-for="tab in tabs" :key="tab.path" :label="tab.title" :name="tab.path"
                :closable="tab.isClose" />
        </el-tabs>
</template>

<script lang="ts" setup>
import { CachedTabsType } from '@/types/layout'

interface FatherParam {
    tabs: CachedTabsType[]
    activePath: string
}

const props = withDefaults(defineProps<FatherParam>(), {
    tabs: () => [],
    activePath: ''
})

const emit = defineEmits(['update:activePath', 'close', 'switch'])

const handleTabClick = (pane: any) => {
    const routePath: string = pane.props.name
    emit('update:activePath', routePath)
    emit('switch', routePath)
}

const handleTabClose = (tabName: string) => {
    emit('close', tabName)
}
</script>

<style scoped>
.module-tabs-card {
    margin: 0 10px 10px 10px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    overflow: hidden;
}

/* ElTabs 本身背景透明去阴影 */
.module-tabs {
    padding: 0;
    background-color: transparent;
    box-shadow: none;
}

/* Tabs整体高度调整 */
:deep(.el-tabs__nav) {
    height: 34px;
    min-height: 34px;
}

/* 每个 tab 的样式 */
:deep(.el-tabs__item) {
    padding: 4px 10px;
    font-size: 13px;
    height: 34px;
    line-height: 34px;
    color: #666;
    background-color: #f4f4f5;
    border-radius: 4px 4px 0 0;
    transition: all 0.2s ease;
}

/* 激活 tab */
:deep(.el-tabs__item.is-active) {
    background-color: #fff;
    color: #409eff;
    font-weight: 500;
    border-bottom: 2px solid #409eff;
}

/* 悬浮 tab */
:deep(.el-tabs__item:hover) {
    background-color: #eaf4ff;
    color: #409eff;
}

/* 关闭按钮样式 */
:deep(.el-icon-close) {
    font-size: 12px;
    color: #bbb;
    margin-left: 4px;
    transition: color 0.2s;
}

:deep(.el-icon-close:hover) {
    color: #f56c6c;
}
</style>