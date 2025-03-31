<template>
    <div class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="main-header">
            <div class="header-left">
                <!-- <img src="@/assets/logo.png" class="logo" alt="Logo"> -->
                <span class="system-name">萝卜系统</span>
            </div>
            <div class="header-right">
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
            <el-aside width="220px" class="main-sidebar">
                <el-menu router :default-active="$route.path" :unique-opened="true" background-color="#304156"
                    text-color="#bfcbd9" active-text-color="#409EFF">
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
                <router-view />
            </el-main>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
    User,
    SwitchButton
} from '@element-plus/icons-vue';
import { clearCache } from '../utils/clearCache';
import { UserInfoVo } from '@/types/login';
import { logouted } from '@/api/login'

const router = useRouter()
const user = ref({
    name: '',
    avatar: ''
})

const goProfile = () => {
    router.push('/profile')
}

const logout = () => {
    logouted().then(() => {
        clearCache();
        router.push('/login');
    });
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
}

.main-header {
    background: #304156;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
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
    border: 1px solid rgba(255, 255, 255, 0.1);
    transition: all 0.3s;
}

.user-info:hover {
    background: rgba(255, 255, 255, 0.1);
}

.user-name {
    margin-left: 10px;
}

.main-body {
    display: flex;
    flex: 1;
    overflow: hidden;
    margin: 10px;
}

.main-sidebar {
    background: #304156;
    transition: width 0.3s;
    border-radius: 4px;
    margin-right: 10px;
}

.el-menu {
    border-right: none;
}

.el-menu-item, .el-sub-menu {
    margin-bottom: 4px;
}

.el-menu-item:not(:last-child), 
.el-sub-menu:not(:last-child) {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.main-content {
    padding: 20px;
    margin: 10px;
    background: #f0f2f5;
    overflow: auto;
    border-radius: 4px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}
</style>