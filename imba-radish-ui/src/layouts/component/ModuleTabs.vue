<template>
    <div class="module-tabs-container">
        <div class="tabs-left">
            <el-tabs :model-value="activePath" type="card" @tab-click="handleTabClick" @tab-remove="handleTabClose"
                class="module-tabs">
                <el-tab-pane v-for="tab in tabs" :key="tab.path" :label="tab.title" :name="tab.path"
                    :closable="tab.isClose" />
            </el-tabs>
        </div>
        <div class="tabs-right">
            <el-dropdown @command="handleDropdownCommand">
                <el-button link class="tabs-more-btn">
                    <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="closeCurrent" :disabled="!canCloseCurrent">
                            关闭当前
                        </el-dropdown-item>
                        <el-dropdown-item command="closeOther" :disabled="tabs.length <= 1">
                            关闭其他
                        </el-dropdown-item>
                        <el-dropdown-item command="closeAll" :disabled="tabs.length <= 1">
                            关闭全部
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { MoreFilled } from '@element-plus/icons-vue'
import { CachedTabsType } from '@/types/layout'

interface FatherParam {
    tabs: CachedTabsType[]
    activePath: string
}

const props = withDefaults(defineProps<FatherParam>(), {
    tabs: () => [],
    activePath: ''
})

const emit = defineEmits(['update:activePath', 'close', 'switch', 'closeOther', 'closeAll'])

const handleTabClick = (pane: any) => {
    const routePath: string = pane.props.name
    emit('update:activePath', routePath)
    emit('switch', routePath)
}

const handleTabClose = (tabName: string) => {
    emit('close', tabName)
}

const canCloseCurrent = computed(() => {
    const currentTab = props.tabs.find(tab => tab.path === props.activePath)
    return currentTab?.isClose !== false
})

const handleDropdownCommand = (command: string) => {
    switch (command) {
        case 'closeCurrent':
            if (canCloseCurrent.value) {
                emit('close', props.activePath)
            }
            break
        case 'closeOther':
            emit('closeOther', props.activePath)
            break
        case 'closeAll':
            emit('closeAll')
            break
    }
}
</script>

<style scoped>
.module-tabs-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 5px 5px 0;
    background-color: #fff;
    /* border-bottom: 1px solid #ebeef5; */
}

.tabs-left {
    flex: 1;
    overflow: hidden;
}

.tabs-right {
    display: flex;
    align-items: center;
    padding-left: 10px;
    border-left: 1px solid #ebeef5;
}

.tabs-more-btn {
    padding: 4px 8px;
    font-size: 14px;
    color: #606266;
    transition: color 0.2s;
}

.tabs-more-btn:hover {
    color: #409eff;
}

/* ElTabs 本身背景透明去阴影 */
.module-tabs {
    padding: 0;
    background-color: transparent;
    box-shadow: none;
}

/* Tabs整体高度调整 */
:deep(.el-tabs__nav) {
    height: 32px;
    min-height: 32px;
}

/* 每个 tab 的样式 */
:deep(.el-tabs__item) {
    padding: 4px 8px;
    font-size: 12px;
    height: 32px;
    line-height: 32px;
    color: #666;
    background-color: #f4f4f5;
    transition: all 0.2s ease;
    margin-right: 2px;
    min-width: 60px;
    max-width: 120px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
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