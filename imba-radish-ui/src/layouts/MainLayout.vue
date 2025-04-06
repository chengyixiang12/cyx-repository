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
                <!-- <img src="@/assets/logo.png" class="logo" alt="Logo"> -->
                <span class="system-name">萝卜系统</span>
            </div>
            <div class="header-right">
                <!-- 新增带提示的首页图标 -->
                <el-tooltip content="首页" placement="bottom" effect="light">
                    <el-icon class="home-icon" @click="goHome">
                        <House />
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
                                <span>{{ menu.title }}</span>
                            </template>
                            <el-menu-item v-for="child in menu.children" :key="child.path" :index="`${child.path}`">
                                <el-icon v-if="child.icon">
                                    <component :is="child.icon" />
                                </el-icon>
                                <span>{{ child.title }}</span>
                            </el-menu-item>
                        </el-sub-menu>

                        <el-menu-item v-else :index="menu.path">
                            <el-icon v-if="menu.icon">
                                <component :is="menu.icon" />
                            </el-icon>
                            <span>{{ menu.title }}</span>
                        </el-menu-item>
                    </template>
                </el-menu>
            </el-aside>

            <!-- 右侧内容区 -->
            <el-main class="main-content">
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
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
    User,
    SwitchButton,
    Expand,
    Fold,
    House
} from '@element-plus/icons-vue';
import { clearCache } from '../utils/clearCache';
import { UserInfoVo } from '@/types/login';
import { logouted } from '@/api/login';
import { ElTooltip } from 'element-plus';
import type { MenuItem } from '@/types/menu';

const router = useRouter();
const user = ref({
    name: '',
    avatar: ''
});
const isCollapsed = ref(false);

const toggleCollapse = () => {
    isCollapsed.value = !isCollapsed.value
}

const goProfile = () => {
    router.push('/profile')
}

const logout = () => {
    logouted().then(() => {
        clearCache();
        router.push('/login');
    });
}

const goHome = () => {
    router.push('/');
}

// 菜单数据
const menuList = ref<MenuItem[]>([])

// 组件挂载时加载菜单
const loadMenu = async () => {
    const menus: MenuItem[] = JSON.parse(sessionStorage.getItem('menus') || '');
    menuList.value = menus;
}

// 组件挂载时获取用户昵称
const getNicname = async () => {
    const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '');
    user.value.name = userInfo.nickname;
}


onMounted(() => {
    loadMenu();
    getNicname();
})
</script>

<style scoped>
.main-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    /* 防止全局溢出 */
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
    /* 防止菜单溢出 */
}

.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 0;
    margin-right: 5px;
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
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 4px;
    border: 1px solid rgb(255, 255, 255);
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
    color: #4879b1;
}

.home-icon:hover {
    color: #409EFF;
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
    display: flex;
    flex-direction: column;
    min-height: 0;
    overflow: auto;
    /* 允许内容滚动 */
    padding: 10px;
    /* 内容内边距 */
    background: #f0f2f5;
    border-radius: 4px;
}
</style>