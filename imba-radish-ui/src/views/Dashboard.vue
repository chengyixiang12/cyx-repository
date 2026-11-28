<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片：渐变背景 + 头像 + 时段问候 -->
    <div class="welcome-card">
      <div class="welcome-left">
        <el-avatar :size="56" :src="avatarUrl" class="welcome-avatar">
          {{ nickname.charAt(0) }}
        </el-avatar>
        <div class="welcome-text">
          <h2>{{ greeting }}，{{ nickname }}！</h2>
          <p>今天是 {{ currentDate }}，祝您工作顺利</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="welcome-date">
          <div class="date-day">{{ nowDay }}</div>
          <div class="date-detail">{{ nowMonth }}<br />{{ nowWeek }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷入口：取自当前用户的菜单，天然按权限过滤 -->
    <el-card shadow="hover" class="section-card">
      <template #header>
        <div class="card-header">
          <span>快捷入口</span>
          <span class="card-hint">仅展示当前账号有权访问的功能</span>
        </div>
      </template>
      <div class="quick-grid">
        <button v-for="item in quickEntries" :key="item.path" type="button" class="quick-item"
          @click="goTo(item.path)">
          <span class="quick-icon">
            <el-icon :size="20">
              <component :is="item.icon" />
            </el-icon>
          </span>
          <span class="quick-name">{{ item.name }}</span>
        </button>
      </div>
    </el-card>

    <!-- 我的待办：仅当当前账号有工作流菜单权限时展示 -->
    <el-card v-if="hasWorkflowMenu" shadow="hover" class="section-card">
      <template #header>
        <div class="card-header">
          <span>我的待办<span v-if="todoTotal > 0" class="todo-count">（{{ todoTotal }}）</span></span>
          <el-button v-if="todoPath" type="primary" size="small" plain @click="goTo(todoPath)">
            查看全部
          </el-button>
        </div>
      </template>
      <ListSkeleton :loading="todoLoading" :count="todoList.length" />
      <div v-if="!todoLoading && todoList.length === 0" class="empty-state">
        <el-icon size="48" class="empty-icon">
          <Tickets />
        </el-icon>
        <p>暂无待办任务</p>
      </div>
      <div v-else-if="!todoLoading" class="content-list">
        <div v-for="item in todoList" :key="item.id" class="content-item" @click="openTodo">
          <div class="content-title">{{ item.title || item.processName }}</div>
          <div class="todo-meta">
            <span>{{ item.processName }}</span>
            <span class="todo-meta-dot">·</span>
            <span>发起人：{{ item.startUserNickname || '-' }}</span>
            <span class="todo-meta-dot">·</span>
            <span>{{ formatTime(item.arriveTime) }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 最近对话 + 最近登录（均为当前登录人自己的数据） -->
    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="fill-card">
          <template #header>
            <div class="card-header">
              <span>最近对话</span>
              <el-button v-if="historyPath" type="primary" size="small" plain @click="goHistory">
                查看全部
              </el-button>
            </div>
          </template>
          <ListSkeleton :loading="loading" :count="recentDialogues.length" />
          <div v-if="!loading && recentDialogues.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <ChatDotSquare />
            </el-icon>
            <p>暂无对话记录</p>
            <el-button v-if="chatPath" type="primary" size="small" plain @click="startNewChat">
              开始新对话
            </el-button>
          </div>
          <div v-else-if="!loading" class="content-list">
            <div v-for="item in recentDialogues" :key="item.id" class="content-item"
              @click="openDialogue(item.id)">
              <div class="content-title">{{ item.title || '未命名对话' }}</div>
              <div class="content-time">{{ formatTime(item.createTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="fill-card">
          <template #header>
            <div class="card-header">
              <span>最近登录</span>
              <span class="card-hint">仅本人可见</span>
            </div>
          </template>
          <ListSkeleton :loading="loading" :count="recentLogins.length" />
          <div v-if="!loading && recentLogins.length === 0" class="empty-state">
            <el-icon size="48" class="empty-icon">
              <Clock />
            </el-icon>
            <p>暂无登录记录</p>
          </div>
          <div v-else-if="!loading" class="content-list">
            <div v-for="(item, index) in recentLogins" :key="index" class="login-item">
              <div class="login-main">
                <div class="login-device">{{ item.osBrowserInfo || '未知设备' }}</div>
                <div class="login-ip">{{ item.ipAddress || '未知 IP' }}</div>
              </div>
              <div class="login-time">{{ formatTime(item.loginTime) }}</div>
            </div>
          </div>
          <div v-if="!loading && recentLogins.length > 0" class="security-tip">
            <el-icon class="security-icon">
              <Lock />
            </el-icon>
            <span class="security-text">若以上登录不是您本人操作，请尽快修改密码</span>
            <el-button type="primary" size="small" plain @click="goTo('/profile')">去修改</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { ChatDotSquare, Clock, Lock, Tickets } from '@element-plus/icons-vue'
import type { UserInfoVo } from '@/types/login'
import type { MenuItem } from '@/types/menu'
import type { RecentDialogue, RecentLogin } from '@/types/dashboard'
import type { WfTasksVo } from '@/types/workflow'
import ListSkeleton from '@/components/skeleton/ListSkeleton.vue'
import { getLeftMenusApi, getRecentLoginsApi, getRecentDialoguesApi } from '@/api/dashboard'
import { getWorkflowTodosApi } from '@/api/workflow'
import { showMessage } from '@/utils/message'
import { useAvatar } from '@/composables/useAvatar'

interface QuickEntry {
  name: string
  path: string
  icon: string
}

const router = useRouter()

// 用户信息
const userInfo: UserInfoVo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
const nickname = ref(userInfo.nickname ?? '用户')
const { avatarUrl, refreshAvatar } = useAvatar()

const loading = ref(false)

// ==================== 欢迎卡片 ====================

const weekTextMap = ['日', '一', '二', '三', '四', '五', '六']

const greeting = computed(() => {
  const hour = dayjs().hour()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日'))
const nowDay = computed(() => dayjs().format('DD'))
const nowMonth = computed(() => `${dayjs().month() + 1} 月`)
const nowWeek = computed(() => `星期${weekTextMap[dayjs().day()]}`)

// ==================== 快捷入口 ====================

const menuList = ref<MenuItem[]>([])

// 菜单里可能配置了前端尚未实现的页面，快捷入口只保留真实存在的组件
const viewModules = import.meta.glob('../views/**/*.vue')
// Vite 会把 glob 的 key 规范化为相对本文件的路径（如 ./system/User.vue），
// 若按 '../views/' + component 硬拼会失配、导致所有入口被过滤光。
// 这里按路径后缀匹配，兼容 ./xxx.vue 与 ../views/xxx.vue 两种形态。
const hasView = (component?: string) => {
  if (!component) return false
  const normalized = component.replace(/\\/g, '/')
  // 兼容 ./xxx.vue、../views/xxx.vue、/src/views/xxx.vue 等 glob key 形态
  return Object.keys(viewModules).some(key => key.includes(`${normalized}.vue`))
}

// 菜单是后端按角色返回的，只从这里取入口，避免首页绕过菜单权限
// 后端 getLeftMenus 的 SQL 已带 `sm.visible = 1` 条件，返回的都是可见菜单，无需前端再过滤。
// 菜单里可能配置了前端尚未实现的页面，用组件是否存在做一道兜底。
const collectLeafMenus = (menus: MenuItem[]): MenuItem[] =>
  menus.flatMap(menu => {
    const children = menu.children?.length ? collectLeafMenus(menu.children) : []
    return menu.path && hasView(menu.component) ? [menu, ...children] : children
  })

// 当前账号可用的全部叶子菜单（AI 入口、待办卡片等权限判断用，不受快捷入口标记影响）
const allMenuEntries = computed<QuickEntry[]>(() =>
  collectLeafMenus(menuList.value)
    .filter(menu => menu.path.startsWith('/'))
    .map(menu => ({ name: menu.name, path: menu.path, icon: menu.icon || 'Menu' }))
)

// 快捷入口：仅展示被标记为 quickEntryFlag=1 的菜单（由菜单管理页配置）
const menuEntries = computed<QuickEntry[]>(() =>
  collectLeafMenus(menuList.value)
    .filter(menu => menu.path.startsWith('/') && Number(menu.quickEntryFlag) === 1)
    .map(menu => ({ name: menu.name, path: menu.path, icon: menu.icon || 'Menu' }))
)

// 首页置顶入口：个人中心恒可见；待办/已办在快捷入口里改用友好名称
const pinnedEntries: Record<string, QuickEntry> = {
  '/todo': { name: '我的待办', path: '/todo', icon: 'Tickets' },
  '/done': { name: '我的已办', path: '/done', icon: 'CircleCheck' }
}

const quickEntries = computed<QuickEntry[]>(() => {
  // 个人中心是静态路由，所有登录用户都可访问
  const fixed: QuickEntry[] = [{ name: '个人中心', path: '/profile', icon: 'User' }]

  // 快捷入口菜单：命中置顶映射的改用友好名称并前置，其余按原顺序补齐
  const pinned: QuickEntry[] = []
  const rest: QuickEntry[] = []
  for (const item of menuEntries.value) {
    const friendly = pinnedEntries[item.path]
    if (friendly) pinned.push(friendly)
    else rest.push(item)
  }
  return [...fixed, ...pinned, ...rest].slice(0, 8)
})

// AI 相关入口是否存在，取决于当前账号的菜单权限（与快捷入口标记无关）
const chatPath = computed(() => allMenuEntries.value.some(item => item.path === '/dialog') ? '/dialog' : null)
const historyPath = computed(() => allMenuEntries.value.some(item => item.path === '/answerHistory') ? '/answerHistory' : null)
// 工作流待办入口（用于「查看全部」跳转与模块权限判断）
const todoPath = computed(() => allMenuEntries.value.some(item => item.path === '/todo') ? '/todo' : null)
const hasWorkflowMenu = computed(() => allMenuEntries.value.some(item => item.path === '/todo' || item.path === '/done'))

// ==================== 我的待办 ====================

const todoList = ref<WfTasksVo[]>([])
const todoTotal = ref(0)
const todoLoading = ref(false)

// 首页只展示最近 5 条，完整列表在待办页
const loadTodos = async () => {
  if (!hasWorkflowMenu.value) return
  try {
    todoLoading.value = true
    const res = await getWorkflowTodosApi({ keyword: '', pageNum: 1, pageSize: 5 })
    todoList.value = res.records || []
    todoTotal.value = res.total || 0
  } catch (error) {
    console.error('加载我的待办失败:', error)
  } finally {
    todoLoading.value = false
  }
}

// 点击待办条目进入待办页（待办页暂无按 id 定位，先统一跳转列表）
const openTodo = () => {
  if (todoPath.value) router.push(todoPath.value)
}

// ==================== 最近对话 / 最近登录 ====================

const recentDialogues = ref<RecentDialogue[]>([])
const recentLogins = ref<RecentLogin[]>([])

const formatTime = (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm')

const goTo = (path: string) => {
  router.push(path)
}

const goHistory = () => {
  if (historyPath.value) router.push(historyPath.value)
}

const startNewChat = () => {
  if (chatPath.value) router.push(chatPath.value)
}

const openDialogue = (id: number) => {
  if (!chatPath.value) {
    showMessage('当前账号暂无 AI 对话权限', 'warning')
    return
  }
  router.push({ path: chatPath.value, query: { dialogueId: String(id) } })
}

// 待办与菜单数据一起加载：菜单决定权限与「查看全部」入口是否可见
const loadData = async () => {
  loading.value = true
  const [loginsRes, dialoguesRes, menusRes] = await Promise.allSettled([
    getRecentLoginsApi(),
    getRecentDialoguesApi(),
    getLeftMenusApi()
  ])

  if (loginsRes.status === 'fulfilled') {
    recentLogins.value = loginsRes.value
  } else {
    console.error('加载最近登录失败:', loginsRes.reason)
  }
  if (dialoguesRes.status === 'fulfilled') {
    recentDialogues.value = dialoguesRes.value
  } else {
    console.error('加载最近对话失败:', dialoguesRes.reason)
  }
  if (menusRes.status === 'fulfilled') {
    menuList.value = menusRes.value || []
  } else {
    console.error('加载快捷入口失败:', menusRes.reason)
  }
  loading.value = false

  // 菜单就绪后 hasWorkflowMenu 才准确，此时再决定是否拉取待办
  loadTodos()
}

onMounted(() => {
  refreshAvatar()
  loadData()
})
</script>

<style scoped>
.dashboard-container {
  padding: var(--radish-space-4);
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 120px);
}

/* ============ 欢迎卡片 ============ */
.welcome-card {
  margin-bottom: var(--radish-space-4);
  padding: var(--radish-space-6) var(--radish-space-8);
  border-radius: var(--radish-radius-xl);
  background: var(--radish-grad-brand);
  color: var(--radish-bg-card);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--radish-shadow-pop);
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: var(--radish-space-4);
}

.welcome-avatar {
  background: var(--radish-primary-dark-2);
  font-size: var(--radish-font-2xl);
  flex-shrink: 0;
}

.welcome-text h2 {
  margin: 0 0 var(--radish-space-2);
  font-size: var(--radish-font-xl);
  font-weight: var(--radish-weight-semibold);
}

.welcome-text p {
  margin: 0;
  font-size: var(--radish-font-sm);
  opacity: 0.85;
}

.welcome-right {
  text-align: center;
  color: var(--radish-bg-card);
}

.welcome-date {
  display: flex;
  align-items: center;
  gap: var(--radish-space-3);
}

.date-day {
  /* 首页欢迎卡的日期大字, 属于展示型尺寸, 不入字号阶梯 */
  /* stylelint-disable-next-line declaration-property-value-disallowed-list */
  font-size: 40px;
  font-weight: 700;
  line-height: 1;
}

.date-detail {
  font-size: var(--radish-font-xs);
  line-height: 1.8;
  opacity: 0.85;
  text-align: left;
}

/* ============ 快捷入口 ============ */
.section-card {
  margin-bottom: var(--radish-space-4);
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: var(--radish-space-3);
  padding: var(--radish-space-2);
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--radish-space-2);
  padding: var(--radish-space-3) var(--radish-space-2);
  border: 1px solid var(--radish-border-light);
  border-radius: var(--radish-radius);
  background: var(--radish-bg-card);
  color: var(--radish-text-regular);
  font-family: inherit;
  cursor: pointer;
  transition: border-color var(--radish-transition), background var(--radish-transition);
}

.quick-item:hover {
  border-color: var(--radish-primary-light-7);
  background: var(--radish-primary-light-9);
}

.quick-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radish-radius-round);
  background: var(--radish-primary-light-9);
  color: var(--radish-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.quick-name {
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.todo-count {
  color: var(--radish-primary);
  font-weight: var(--radish-weight-medium);
}

.todo-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--radish-space-1);
  margin-top: var(--radish-space-1);
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.todo-meta-dot {
  color: var(--radish-text-placeholder);
}

/* ============ 内容区域 ============ */
.content-row {
  flex: 1;
  min-height: 0;
}

.fill-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: var(--radish-weight-medium);
  padding: var(--radish-space-2) 0;
}

.card-hint {
  font-size: var(--radish-font-xs);
  font-weight: var(--radish-weight-normal);
  color: var(--radish-text-secondary);
}

/* ============ 最近登录 ============ */
.login-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--radish-space-3) 0;
  border-bottom: 1px solid var(--radish-border-light);
}

.login-item:last-child {
  border-bottom: none;
}

.login-main {
  min-width: 0;
}

.login-device {
  font-size: var(--radish-font-sm);
  color: var(--radish-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.login-ip {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
  margin-top: var(--radish-space-1);
}

.login-time {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
  flex-shrink: 0;
  margin-left: var(--radish-space-3);
}

.security-tip {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--radish-space-2);
  margin-top: var(--radish-space-3);
  padding: var(--radish-space-3);
  border-radius: var(--radish-radius);
  background: var(--radish-fill);
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.security-icon {
  color: var(--radish-warning);
  flex-shrink: 0;
}

.security-text {
  flex: 1;
  min-width: 0;
}

.content-list {
  padding: var(--radish-space-2);
  max-height: 300px;
  overflow-y: auto;
}

.content-item {
  padding: var(--radish-space-3) 0;
  border-bottom: 1px solid var(--radish-border-light);
  cursor: pointer;
  transition: background var(--radish-transition), padding var(--radish-transition);
}

.content-item:hover {
  background-color: var(--radish-fill);
  padding-left: var(--radish-space-2);
}

.content-title {
  font-size: var(--radish-font-base);
  margin-bottom: var(--radish-space-1);
  color: var(--radish-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-time {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

/* ============ 空状态 ============ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--radish-space-2);
  padding: var(--radish-space-8) 0;
  color: var(--radish-text-secondary);
}

.empty-state p {
  margin: 0;
}

.empty-icon {
  color: var(--radish-text-placeholder);
}

/* ============ 响应式 ============ */
@media screen and (max-width: 992px) {
  .fill-card {
    margin-bottom: var(--radish-space-4);
  }
}

@media screen and (max-width: 768px) {
  .welcome-card {
    padding: var(--radish-space-4) var(--radish-space-5);
  }

  .welcome-date {
    display: none;
  }

  .quick-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
