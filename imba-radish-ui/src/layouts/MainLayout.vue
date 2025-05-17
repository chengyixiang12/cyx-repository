<template>
    <div class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="main-header">
            <div class="header-left">
                <!-- 添加菜单收缩按钮 -->
                <el-icon class="collapse-icon" @click="toggleCollapse">
                    <Expand v-if="isCollapsed" />
                    <Fold v-else />
                </el-icon>
                <span class="system-name">萝卜系统</span>
                <img src="../assets/dashboard.png" class="logo" alt="Logo">
            </div>
            <div class="header-right">
                <!-- 消息图标 -->
                <div class="message-icon-wrapper">
                    <el-icon class="message-icon" @click="handleMessageClick">
                        <Bell />
                        <span v-if="unreadCount > 0" class="message-dot"></span>
                    </el-icon>
                </div>
                <!-- 新增带提示的首页图标 -->
                <el-tooltip content="首页" placement="bottom" effect="light">
                    <el-icon class="home-icon" @click="goHome">
                        <HomeFilled />
                    </el-icon>
                </el-tooltip>
                <el-dropdown>
                    <span class="user-info">
                        <el-avatar :size="36" :src="user.avatar" />
                        <span class="user-name">{{ user.name }}</span>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="goProfile">
                                <el-icon>
                                    <User />
                                </el-icon> 个人中心
                            </el-dropdown-item>
                            <el-dropdown-item divided @click="logout">
                                <el-icon>
                                    <SwitchButton />
                                </el-icon> 退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <div class="main-body">
            <!-- 左侧菜单栏 -->
            <el-aside :width="isCollapsed ? '64px' : '220px'" class="main-sidebar">
                <el-menu router :default-active="$route.path" :unique-opened="true" background-color="#767e87"
                    text-color="#e7e8e9" active-text-color="#99c0e7" :collapse="isCollapsed"
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

            <!-- 右侧内容区 -->
            <el-main class="main-content">
                <module-tabs :tabs="cachedTabs" v-model:activePath="activePath" @switch="switchTab" @close="closeTab" />

                <div class="content-wrapper">
                    <router-view v-slot="{ Component }">
                        <transition name="fade" mode="out-in">
                            <div v-if="Component">
                                <component :is="Component" />
                            </div>
                        </transition>
                    </router-view>
                </div>
            </el-main>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
    User,
    SwitchButton,
    Expand,
    Fold,
    HomeFilled,
    Bell
} from '@element-plus/icons-vue';
import { clearCache } from '../utils/clearCache';
import { UserInfoVo } from '@/types/login';
import { logouted } from '@/api/login';
import { ElTooltip } from 'element-plus';
import type { MenuItem } from '@/types/menu';
import { getMessageNumApi } from '@/api/message';
import { getLeftMenusApi } from '@/api/dashboard';
import ModuleTabs from '@/components/layout/ModuleTabs.vue';
import { useRoute } from 'vue-router'
import { CachedTabsType } from '@/types/layout';
import { getWebSocketInstance } from '@/utils/websocket';
import { showMessage } from '@/utils/message';

const router = useRouter();
const user = ref({
    name: '',
    avatar: ''
});
const route = useRoute()
// 折叠菜单
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

// 个人中心
const goProfile = () => {
    router.push('/profile')
}

// 退出登录
const logout = () => {
    const ws = getWebSocketInstance();
    ws.close();
    logouted().then(() => {
        clearCache();
        router.push('/login');
    });
}

// 首页
const goHome = () => {
    router.push('/');
}

// 组件挂载时加载菜单
const loadMenu = async () => {
    menuList.value = await getLeftMenusApi();
}

// 组件挂载时获取用户昵称
const getNicname = async () => {
    const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '');
    user.value.name = userInfo.nickname;
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
    const token = sessionStorage.getItem('Authorization') || ''
    // 连接websocket
    const ws = getWebSocketInstance()
    ws.connect(token)
    ws.onForceLogout = () => {
        clearCache()
        router.push('/login')
        showMessage('您已被强制下线', 'warning')
    }
}

onMounted(() => {
    loadMenu();
    getNicname();
    getMessageNum();
    existDashboard();
    initWebsocket();
})
</script>

<style scoped>
.main-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.main-body {
    flex: 1;
    display: flex;
    min-height: 0;
    margin-top: 5px;
    margin-bottom: 5px;
    margin-left: 5px;
}

.main-header {
    background: #b3b9bf;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.main-sidebar {
    background: #b3b9bf;
    transition: width 0.3s;
    border-radius: 4px;
    margin-right: 10px;
    overflow: hidden;
}

.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 0;
    margin-right: 5px;
    min-height: 0;
}

.header-left {
    display: flex;
    align-items: center;
    padding: 10px 0;
}

.logo {
    height: 40px;
    margin-right: 15px;
}

.system-name {
    font-size: 18px;
    font-weight: bold;
}

.user-info {
    outline: none !important;
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 4px;
    transition: all 0.3s;
}

.user-info:hover {
    background: rgba(255, 255, 255, 0.1);
}

.user-name {
    margin-left: 10px;
}

.el-menu {
    border-right: none;
}

.el-menu-item,
.el-sub-menu {
    margin-bottom: 4px;
}

.el-menu-item:not(:last-child),
.el-sub-menu:not(:last-child) {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.home-icon {
    margin-top: 17px;
    margin-right: 20px;
    font-size: 20px;
    cursor: pointer;
    transition: color 0.3s;
    color: #e6e7e8;
}

.home-icon:hover {
    color: #409EFF;
}

/* 消息图标容器 */
.message-icon-wrapper {
    position: relative;
    display: inline-block;
    margin-right: 20px;
    margin-top: 17px;
    /* 保持与其他图标对齐 */
}

/* 消息图标基础样式 */
.message-icon {
    font-size: 20px;
    color: #e6e7e8;
    cursor: pointer;
    transition: color 0.3s;
    position: relative;
    /* 为红点定位做准备 */
}

.message-icon:hover {
    color: #409eff;
}

/* 小红点样式 */
.message-dot {
    position: absolute;
    top: -3px;
    right: -3px;
    width: 8px;
    height: 8px;
    background-color: #f56c6c;
    border-radius: 50%;
    border: 1px solid #b3b9bf;
    /* 与header背景色一致 */
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
    font-size: 20px;
    cursor: pointer;
    transition: all 0.3s;
}

.collapse-icon:hover {
    color: #99c0e7;
}

.content-wrapper {
    flex: 1;
    overflow: auto;
    padding: 10px;
    background: #f0f2f5;
    border-radius: 4px;
    min-height: 0;
}
</style>