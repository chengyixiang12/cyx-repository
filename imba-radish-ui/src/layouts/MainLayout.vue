<template>
    <el-container class="page-container">
        <!-- 顶部导航栏 -->
        <el-header class="page-header">
            <div class="header-left">
                <el-icon class="collapse-icon" @click="toggleCollapse">
                    <Expand v-if="isCollapsed" />
                    <Fold v-else />
                </el-icon>
                <span class="system-name">萝卜系统</span>
                <img src="../assets/dashboard.png" class="logo" alt="Logo">
            </div>
            <div class="header-right">
                <div class="message-icon-wrapper">
                    <el-icon class="message-icon" @click="handleMessageClick">
                        <Bell />
                        <span v-if="unreadCount > 0" class="message-dot"></span>
                    </el-icon>
                </div>
                <el-tooltip :content="uploadTooltip" placement="bottom" effect="light">
                    <div class="upload-entry" @click="togglePanel">
                        <el-badge v-if="taskCount > 0" :value="taskCount" :max="99" type="primary"
                            class="upload-entry__badge" />
                        <el-icon class="upload-icon">
                            <Upload />
                        </el-icon>
                    </div>
                </el-tooltip>
                <el-tooltip content="首页" placement="bottom" effect="light">
                    <el-icon class="home-icon" @click="goHome">
                        <HomeFilled />
                    </el-icon>
                </el-tooltip>
                <el-dropdown>
                    <span class="user-info">
                        <el-avatar :size="36" :src="avatarUrl" />
                        <span class="user-name">{{ userInfo.nickname }}</span>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="goProfile">
                                <el-icon>
                                    <User />
                                </el-icon> 个人中心
                            </el-dropdown-item>
                            <el-dropdown-item divided @click="handleLogout">
                                <el-icon>
                                    <SwitchButton />
                                </el-icon> 退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <!-- 主体内容区：左侧菜单 + 右侧内容（main + footer） -->
        <el-container class="page-body">
            <!-- 左侧菜单栏 -->
            <el-aside :width="isCollapsed ? '64px' : '200px'" class="main-left">
                <el-menu router :default-active="$route.path" :unique-opened="true" background-color="transparent"
                    text-color="#4E5969" active-text-color="#4F6EF2" :collapse="isCollapsed"
                    :collapse-transition="false">
                    <template v-for="menu in menuList" :key="menu.path">
                        <el-sub-menu v-if="menu.children?.length" :index="menu.path">
                            <template #title>
                                <el-icon v-if="menu.icon">
                                    <component :is="menu.icon" />
                                </el-icon>
                                <span>{{ menu.name }}</span>
                            </template>
                            <el-menu-item v-for="child in menu.children" :key="child.path" :index="`${child.path}`">
                                <el-icon v-if="child.icon">
                                    <component :is="child.icon" />
                                </el-icon>
                                <span>{{ child.name }}</span>
                            </el-menu-item>
                        </el-sub-menu>

                        <el-menu-item v-else :index="menu.path">
                            <el-icon v-if="menu.icon">
                                <component :is="menu.icon" />
                            </el-icon>
                            <span>{{ menu.name }}</span>
                        </el-menu-item>
                    </template>
                </el-menu>
            </el-aside>

            <!-- 右侧内容区：main + footer 垂直排列 -->
            <el-container class="main-right-wrapper">
                <!-- 主内容区 -->
                <el-main class="main-right">
                    <module-tabs :tabs="cachedTabs" v-model:activePath="activePath" @switch="switchTab" @close="closeTab"
                        @close-other="closeOtherTabs" @close-all="closeAllTabs" />
                    <div class="router-view-wrapper">
                        <router-view v-slot="{ Component }">
                            <!-- key 必须是字符串: 用组件对象作 key 会导致 keep-alive 缓存永远不命中 -->
                            <keep-alive :include="cachedComponentNames">
                                <component :is="Component" :key="route.path" />
                            </keep-alive>
                        </router-view>
                    </div>
                </el-main>

                <!-- 底部页脚 -->
                <el-footer class="page-footer">
                    <div class="footer-content">
                        <span>© 2024 萝卜系统 - 后台管理系统</span>
                    </div>
                </el-footer>
            </el-container>
        </el-container>

        <!-- 全局上传任务面板：关闭后仍可从顶栏图标重新打开，切页面不丢任务 -->
        <upload-task-panel />
    </el-container>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
    User,
    SwitchButton,
    Expand,
    Fold,
    HomeFilled,
    Bell,
    Upload
} from '@element-plus/icons-vue'
import { clearCache } from '../utils/clearCache'
import { UserInfoVo } from '@/types/login'
import { logouted } from '@/api/login'
import { ElTooltip, ElMessageBox } from 'element-plus'
import type { MenuItem } from '@/types/menu'
import { getMessageNumApi } from '@/api/message'
import { getLeftMenusApi } from '@/api/dashboard'
import ModuleTabs from './component/ModuleTabs.vue'
import { useRoute } from 'vue-router'
import { CachedTabsType } from '@/types/layout'
import { getWebSocketInstance } from '@/utils/websocket'
import { showMessage } from '@/utils/message'
import { WebsocketMessage } from '@/utils/websocketManager'
import { useAvatar } from '@/composables/useAvatar';
import { useUploadTasks } from '@/composables/useUploadTasks'
import UploadTaskPanel from '@/components/UploadTaskPanel.vue'

const router = useRouter()
const { avatarUrl, refreshAvatar } = useAvatar()
// 顶栏图标 / 徽章 / 全局任务面板共用同一份上传队列
const { taskCount, activeTaskCount, togglePanel, resetUploadTasks } = useUploadTasks()
const uploadTooltip = computed(() =>
    activeTaskCount.value > 0 ? `上传任务（${activeTaskCount.value} 个进行中）` : '上传任务'
)
const route = useRoute()
const isCollapsed = ref(false)
// 未读消息数
const unreadCount = ref(5)
// 当前选择的菜单
const activePath = ref<string>(route.path)
// 菜单数据
const menuList = ref<MenuItem[]>([])

const cachedTabs = ref<CachedTabsType[]>([
    {
        path: route.path,
        title: route.meta.title as string,
        isClose: route.meta.isClose as boolean,
        visible: route.meta.visible as number
    }
])

// 用户信息
const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '');

const toggleCollapse = () => {
    isCollapsed.value = !isCollapsed.value
}

const switchTab = (path: string) => {
    router.push(path)
}

// 关闭标签
const closeTab = (path: string) => {
    const index = cachedTabs.value.findIndex(tab => tab.path === path)
    cachedTabs.value.splice(index, 1)

    if (activePath.value === path) {
        const nextTab = cachedTabs.value[index - 1] || cachedTabs.value[0]
        if (nextTab) {
            activePath.value = nextTab.path
            router.push({ path: nextTab.path })
        }
    }
}

// 关闭其他标签
const closeOtherTabs = (keepPath: string) => {
    cachedTabs.value = cachedTabs.value.filter(tab => tab.path === keepPath || !tab.isClose)
    activePath.value = keepPath
}

// 关闭全部标签
const closeAllTabs = () => {
    const homeTab = cachedTabs.value.find(tab => !tab.isClose)
    if (homeTab) {
        cachedTabs.value = [homeTab]
        activePath.value = homeTab.path
        router.push({ path: homeTab.path })
    }
}

// 当路由切换时自动添加 tab
watch(
    () => route.path,
    () => {
        const exists = cachedTabs.value.some(tab => (tab.path === route.path))
        if (route.meta.visible === 1 && !exists && route.meta.title) {
            cachedTabs.value.push({
                path: route.path,
                title: route.meta.title as string,
                isClose: route.meta.isClose as boolean,
                visible: route.meta.visible as number
            })
        }
        activePath.value = route.path
    },
    { immediate: true }
)

/**
 * keep-alive include 名单: 由"当前打开的标签页"推导各页面的组件名。
 * - 组件经 vue-router 懒加载首次解析后, record.components.default 即为 SFC 对象,
 *   其 name 来自 SFC 文件名(script setup 自动推断), 与 keep-alive 的 include 匹配规则一致。
 * - 懒加载函数尚未解析时取不到 name, 自动跳过(该页面也尚未被访问过)。
 * - 标签页关闭 => 名单移除对应组件名 => keep-alive 自动释放缓存, 重开为全新实例。
 */
const cachedComponentNames = computed<string[]>(() => {
    const names = new Set<string>()
    for (const tab of cachedTabs.value) {
        try {
            const matched = router.resolve(tab.path).matched
            const comp = matched[matched.length - 1]?.components?.default as any
            const name = comp && (comp.name || comp.__name)
            if (typeof name === 'string' && name) {
                names.add(name)
            }
        } catch {
            // 路由解析失败时忽略该标签
        }
    }
    return Array.from(names)
})

// 个人中心
const goProfile = () => {
    router.push('/profile')
}

// 退出登录
const logout = () => {
    // 清空上传队列，避免下一个账号看到上一账号的文件名与任务
    resetUploadTasks();
    const ws = getWebSocketInstance();
    ws.close();
    logouted().then(() => {
        clearCache();
        router.push('/login');
        ws.close()
    });
}

// 退出登录确认
const handleLogout = async () => {
    try {
        await ElMessageBox.confirm(
            '确定要退出登录吗？',
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
                buttonSize: 'small'
            }
        )
        logout()
    } catch {
        // 用户取消退出
    }
}

// 首页
const goHome = () => {
    router.push('/');
}

// 组件挂载时加载菜单
const loadMenu = async () => {
    menuList.value = await getLeftMenusApi();
}

const handleMessageClick = async () => {
    // 跳转到消息界面
}

// 获取消息数量
const getMessageNum = async () => {
    unreadCount.value = await getMessageNumApi();
}

// 检测标签页中是否存在首页
const existDashboard = () => {
    if (!cachedTabs.value.some(item => item.path === '/dashboard')) {
        cachedTabs.value.unshift({
            path: '/dashboard',
            title: '首页',
            isClose: false,
            visible: 1
        })
    }
}

// 初始化websocket
const initWebsocket = async () => {
    const token = sessionStorage.getItem('Authorization')
    if (!token) return
    // 连接websocket
    const ws = getWebSocketInstance();
    ws.connect(token);
    ws.onForceLogout = (data: WebsocketMessage) => {
        resetUploadTasks()
        clearCache()
        router.push('/login')
        showMessage(data.msg, 'warning')
    };
    ws.refreshToken = (data: WebsocketMessage) => {
        const token = data.token;
        if (token) {
            sessionStorage.setItem('Authorization', token);
        }
    };
}

onMounted(() => {
    loadMenu();
    getMessageNum();
    existDashboard();
    initWebsocket();
    refreshAvatar();
})
</script>

<style scoped>
.page-container {
    height: 100vh;
    overflow: hidden;
    background: var(--radish-bg-page);
}

/* ---------- 顶栏: 微灰白底 + 清晰分割线 ---------- */
.page-header {
    height: var(--radish-header-height);
    background: var(--radish-bg-bar);
    color: var(--radish-text-primary);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid var(--radish-border);
}

/* ---------- 侧边栏: 微灰白底, 与内容区靠色阶+分割线区分 ---------- */
/* 滚动逻辑在 global.css: .main-left 定位容器 + 顶层菜单绝对定位自行滚动 */
.main-left {
    background: var(--radish-bg-bar);
    border-right: 1px solid var(--radish-border);
}

.main-right-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;
}

/* flex 纵向子项默认 min-height:auto 会被内容撑开, 必须归零才能让内层滚动生效 */
.page-body {
    min-height: 0;
}

.main-right {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 0;
    min-height: 0;
    background: var(--radish-bg-page);
}

.router-view-wrapper {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
    overflow-x: hidden;
    min-height: 0;
}

.router-view {
    flex: 1;
    min-height: 0;
    overflow: hidden;
}

/* ---------- 页脚 ---------- */
.page-footer {
    height: var(--radish-footer-height);
    background: var(--radish-bg-bar);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 20px;
    border-top: 1px solid var(--radish-border);
}

.footer-content {
    text-align: center;
    font-size: var(--radish-font-xs);
    color: var(--radish-text-secondary);
}

:deep(.el-tabs__header) {
    margin-bottom: 5px;
    margin-top: 0px;
}

/* ---------- 顶栏元素 ---------- */
.header-left {
    display: flex;
    align-items: center;
    padding: 10px 0;
}

/* 右侧图标组: flex 垂直居中, 替代原来的 margin-top 对齐 hack */
.header-right {
    display: flex;
    align-items: center;
}

.logo {
    height: 32px;
    margin-right: 12px;
}

.system-name {
    font-size: var(--radish-font-lg);
    font-weight: 700;
    color: var(--radish-text-primary);
    letter-spacing: 0.5px;
}

.user-info {
    outline: none !important;
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: var(--radish-radius-round);
    transition: background 0.25s;
}

.user-info:hover {
    background: var(--radish-fill);
}

.user-name {
    margin-left: 10px;
    font-size: var(--radish-font-base);
    color: var(--radish-text-primary);
}

/* ---------- 菜单样式 ---------- */
/* 尺寸/滚动在 global.css (.main-left > .el-menu), 这里只管视觉 */
.el-menu {
    border-right: none;
    width: 100%;
    padding: 8px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
    border-radius: var(--radish-radius-sm);
    margin: 2px 0;
    height: 40px;
    line-height: 40px;
    transition: background 0.2s, color 0.2s;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
    background: var(--radish-border-light);
}

:deep(.el-menu-item.is-active) {
    background: var(--radish-primary-light-9);
    font-weight: 600;
}

/* ---------- 顶栏图标 ---------- */
/* 上传入口: Badge 数字在图标左侧, 点击整块切换任务面板 */
.upload-entry {
    display: inline-flex;
    align-items: center;
    gap: var(--radish-space-2);
    margin-right: 16px;
    cursor: pointer;
    vertical-align: middle;
}

.upload-entry__badge {
    flex-shrink: 0;
}

.upload-icon {
    font-size: var(--radish-font-xl);
    color: var(--radish-text-regular);
    transition: color 0.25s;
}

.upload-entry:hover .upload-icon {
    color: var(--radish-primary);
}

.home-icon {
    margin-right: 16px;
    font-size: var(--radish-font-xl);
    cursor: pointer;
    transition: color 0.25s;
    color: var(--radish-text-regular);
    vertical-align: middle;
}

.home-icon:hover {
    color: var(--radish-primary);
}

/* 消息图标容器 */
.message-icon-wrapper {
    position: relative;
    display: inline-block;
    margin-right: 20px;
    vertical-align: middle;
}

/* 消息图标基础样式 */
.message-icon {
    font-size: var(--radish-font-xl);
    color: var(--radish-text-regular);
    cursor: pointer;
    transition: color 0.25s;
    position: relative;
}

.message-icon:hover {
    color: var(--radish-primary);
}

/* 小红点样式 */
.message-dot {
    position: absolute;
    top: -3px;
    right: -3px;
    width: 8px;
    height: 8px;
    background-color: var(--radish-danger);
    border-radius: 50%;
    border: 2px solid var(--radish-bg-card);
    box-sizing: border-box;
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.2);
    }

    100% {
        transform: scale(1);
    }
}

.collapse-icon {
    margin-right: 15px;
    font-size: var(--radish-font-xl);
    cursor: pointer;
    transition: color 0.25s;
    color: var(--radish-text-regular);
}

.collapse-icon:hover {
    color: var(--radish-primary);
}
</style>