<template>
    <el-tabs :model-value="activePath" type="card" closable @tab-click="handleTabClick" @tab-remove="handleTabClose"
        class="module-tabs">
        <el-tab-pane v-for="tab in tabs" :key="tab.path" :label="tab.title" :name="tab.path" />
    </el-tabs>
</template>

<script lang="ts" setup>
const props = defineProps<{
    tabs: { path: string; title: string }[];
    activePath: string;
}>();

const emit = defineEmits(["update:activePath", "close"]);

const handleTabClick = (pane: any) => {
    emit("update:activePath", pane.props.name);
};

const handleTabClose = (tabName: string) => {
    emit("close", tabName);
};
</script>
<style scoped>
.module-tabs {
    padding: 0 8px;
    border-bottom: 1px solid #ebeef5;
    background-color: #fff;
    box-shadow: inset 0 -1px 0 #ebeef5;
}

/* 标签整体样式 */
:deep(.el-tabs__item) {
    padding: 4px 10px;
    font-size: 12px;
    height: 28px;
    line-height: 28px;
    color: #666;
    background-color: #f4f4f5;
    border-radius: 4px 4px 0 0;
    transition: all 0.2s ease;
}

/* 激活标签样式 */
:deep(.el-tabs__item.is-active) {
    background-color: #ffffff;
    color: #409eff;
    font-weight: 500;
    border-bottom: 2px solid #409eff;
}

/* 悬浮标签样式 */
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