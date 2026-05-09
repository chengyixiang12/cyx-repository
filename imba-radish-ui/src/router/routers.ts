// route.js
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import type { MenuItem } from '../types/menu'
import { getMenuRouteApi } from '@/api/menu'
import { showMessage } from '@/utils/message'

// -------------------- 1. 组件动态导入映射表 --------------------
// 所有 views 下的 vue 文件，按相对路径生成 key，避免字符串拼接路径
const viewModules = import.meta.glob('../views/**/*.vue')
const componentMap: Record<string, () => Promise<unknown>> = {}
for (const path in viewModules) {
  // 例：'../views/system/user/UserList.vue' -> 'system/user/UserList'
  const key = path
    .replace('../views/', '')
    .replace('.vue', '')
    .replace(/\/+/g, '/')
  componentMap[key] = viewModules[path]
}

// -------------------- 2. 默认元信息 --------------------
const defaultMeta = {
  requiresAuth: true,
  isClose: true,
  visible: 1
}

// -------------------- 3. 静态基础路由 --------------------
const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'MainLayout',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',            // 相对路径
        name: 'dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { ...defaultMeta, title: '首页', isClose: false }
      },
      {
        path: 'profile',              // 相对路径
        name: 'Profile',
        component: () => import('../views/system/PersonalCenter.vue'),
        meta: { ...defaultMeta, title: '个人中心' }
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login/Login.vue'),
    meta: { title: '登录', requiresAuth: false, isClose: false, visible: 1 }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/register/Register.vue'),
    meta: { title: '注册', requiresAuth: false, isClose: false, visible: 1 }
  }
]

// -------------------- 4. 创建路由实例并立即添加全局 404 --------------------
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes
})

// 全局 404 放在最后，且不作为需要动态移除的路由
router.addRoute({
  path: '/:pathMatch(.*)*',
  name: '404',
  component: () => import('../views/error/404.vue'),
  meta: { title: '404', requiresAuth: false }
})

// -------------------- 5. 动态路由状态管理 --------------------
let dynamicRoutesAdded = false
const addedRouteNames = new Set<string>()   // 记录动态添加的路由名称
let menuLoadingPromise: Promise<void> | null = null

// -------------------- 6. 根据菜单生成路由配置 --------------------
function generateRoutes(menus: MenuItem[], parentPath = ''): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []

  menus.forEach(menu => {
    // 有子菜单的情况
    if (menu.children?.length) {
      if (!menu.component) {
        // 纯目录，递归处理子菜单
        routes.push(...generateRoutes(menu.children, menu.path))
      } else {
        // 既有组件又有子菜单，创建父路由并嵌套子路由
        const route = createRouteFromMenu(menu, parentPath)
        route.children = generateRoutes(menu.children, menu.path)
        routes.push(route)
      }
      return
    }

    // 叶子菜单必须有 component
    if (menu.component) {
      routes.push(createRouteFromMenu(menu, parentPath))
    }
  })

  return routes
}

function createRouteFromMenu(menu: MenuItem, parentPath = ''): RouteRecordRaw {
  if (!menu.component) {
    console.error(`菜单 "${menu.name}" 缺少 component 配置`)
    return {
      path: menu.path.startsWith('/') ? menu.path.slice(1) : menu.path,
      component: () => import('../views/error/404.vue'),
      meta: { ...defaultMeta, title: menu.name }
    }
  }

  // 从映射表中获取组件导入函数
  const componentImport = componentMap[menu.component.replace(/\\/g, '/')]  // 兼容 Windows 路径
  if (!componentImport) {
    console.error(`组件映射未找到: ${menu.component}`)
    return {
      path: menu.path.startsWith('/') ? menu.path.slice(1) : menu.path,
      component: () => import('../views/error/404.vue'),
      meta: { ...defaultMeta, title: menu.name }
    }
  }

  // 确保 path 为相对片段（去除前后斜杠），这样才能正确拼接在父级路由下
  let cleanPath = menu.path.replace(/^\/|\/$/g, '')
  // 可以保留一个前导/用于显示，但实际注册路由时使用相对路径更安全
  // 这里根据业务习惯：如果是根级菜单可能期望绝对路径，但作为 MainLayout 的子路由必须相对。
  // 我们统一处理为相对路径，由 addDynamicRoutes 添加到 MainLayout 时自动拼接。
  const routePath = cleanPath

  return {
    path: routePath,
    name: menu.path.replace(/\//g, '') || menu.name,  // 保证 name 唯一
    component: componentImport,
    meta: {
      ...defaultMeta,
      title: menu.name,
      icon: menu.icon,
      parentPath,
      visible: menu.visible
    }
  }
}

// -------------------- 7. 安全添加/刷新动态路由 --------------------
export async function addDynamicRoutes(menuList: MenuItem[]) {
  // 先移除之前动态添加的所有路由（404 不在 addedRouteNames 中，不会被移除）
  const removeNames = Array.from(addedRouteNames)
  // 使用 allSettled 保证即使某个移除失败也不影响其他操作
  await Promise.allSettled(
    removeNames.map(name => {
      try {
        router.removeRoute(name)
      } catch (e) {
        console.warn(`移除路由 ${name} 失败：`, e)
      }
    })
  )
  addedRouteNames.clear()

  const dynamicRoutes = generateRoutes(menuList)

  // 逐条添加到 MainLayout 下
  for (const route of dynamicRoutes) {
    try {
      router.addRoute('MainLayout', route)
      if (route.name) {
        addedRouteNames.add(route.name as string)
      }
    } catch (e) {
      console.error(`添加路由失败: ${route.path}`, e)
    }
  }
}

// -------------------- 8. 全局导航守卫 --------------------
router.beforeEach(async (to, _from, next) => {
  // 统一的 token 获取函数（可替换为 store）
  const getToken = () => sessionStorage.getItem('Authorization')
  const token = getToken()

  // 白名单路径直接放行
  const whiteList = ['/login', '/register', '/404']
  if (whiteList.includes(to.path)) {
    if (to.path === '/login' && token) {
      next('/')
      return
    }
    next()
    return
  }

  // 非白名单需认证
  if (to.meta.requiresAuth !== false && !token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}&error=unauthorized`)
    return
  }

  // 已登录且未加载动态路由 → 开始加载
  if (token && !dynamicRoutesAdded) {
    // 防止并发重复请求
    if (!menuLoadingPromise) {
      menuLoadingPromise = (async () => {
        try {
          const menus = await getMenuRouteApi()
          if (menus && menus.length > 0) {
            await addDynamicRoutes(menus)
          }
          dynamicRoutesAdded = true
        } catch (err) {
          console.error('菜单加载失败：', err)
          // 加载失败，强制去登录页
          next('/login?error=route_load_failed')
          return
        } finally {
          menuLoadingPromise = null
        }
      })()
    }
    // 等待加载完毕
    await menuLoadingPromise

    // 如果上面 catch 中已经调用了 next，这里需要避免重复 next
    if (!dynamicRoutesAdded) {
      return
    }

    // 重新尝试当前路径（此时路由表已完整）
    next({ ...to, replace: true })
    return
  }

  // 路由匹配失败（包括不存在）→ 展示 404 提示或自动跳转
  // 注意：这个检查必须在动态路由加载完成后进行，否则刷新时会错误地进入404
  if (to.matched.length === 0) {
    console.warn('路由匹配失败，跳转 404')
    showMessage('未找到该页面', 'warning')
    next({ name: '404', replace: true })
    return
  }

  // 其他情况放行
  next()
})

export default router