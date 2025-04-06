import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

// 1. 定义菜单项类型
interface MenuItem {
  path: string
  title: string
  icon?: string
  component?: string
  children?: MenuItem[]
}

// 2. 基础路由
const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'MainLayout',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  }
]

// 3. 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes
})

// 4. 动态路由状态管理
let dynamicRoutesAdded = false
const addedRoutes = new Set<string>()

// 5. 改进的动态路由生成
function generateRoutes(menus: MenuItem[], parentPath: string = ''): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []

  menus.forEach(menu => {
    // 处理有子菜单的情况
    if (menu.children?.length) {
      if (!menu.component) {
        // 纯父级菜单，只处理子菜单
        routes.push(...generateRoutes(menu.children, menu.path))
      } else {
        // 既有子菜单又有组件，创建父路由
        const route = createRouteFromMenu(menu, parentPath)
        route.children = generateRoutes(menu.children, menu.path)
        routes.push(route)
      }
      return
    }

    // 处理具体页面的路由
    if (menu.component) {
      routes.push(createRouteFromMenu(menu, parentPath))
    }
  })

  return routes
}

function createRouteFromMenu(menu: MenuItem, parentPath: string = ''): RouteRecordRaw {
  // 移除开头的 views/ 如果存在
  const cleanComponent = menu.component!.replace(/^views\//, '')
  const componentPath = `../views/${cleanComponent}${cleanComponent.endsWith('.vue') ? '' : '.vue'}`

  return {
    path: menu.path.startsWith('/') ? menu.path : `/${menu.path}`,
    name: menu.path.replace(/\s+/g, '-').toLowerCase(),
    component: () => import(/* @vite-ignore */ componentPath),
    meta: {
      title: menu.title,
      icon: menu.icon,
      parentPath: parentPath,
      requiresAuth: true
    }
  }
}

// 6. 安全添加动态路由
export async function addDynamicRoutes(menuList: MenuItem[]) {
  try {
    // 生成路由配置
    const dynamicRoutes = generateRoutes(menuList)

    // 清除现有的动态路由
    const removePromises = Array.from(addedRoutes).map(path => {
      try {
        router.removeRoute(path)
        return Promise.resolve()
      } catch (e) {
        console.warn(`Failed to remove route: ${path}`, e)
        return Promise.reject(e)
      }
    })

    await Promise.all(removePromises)
    addedRoutes.clear()

    // 添加新路由
    dynamicRoutes.forEach(route => {
      try {
        router.addRoute('MainLayout', route)
        addedRoutes.add(route.name as string)
      } catch (e) {
        console.error(`Failed to add route: ${route.path}`, e)
      }
    })

    // 确保404路由存在
    if (!router.hasRoute('404')) {
      router.addRoute({
        path: '/:pathMatch(.*)*',
        name: '404',
        component: () => import('../views/error/404.vue'),
        meta: { title: '404', requiresAuth: false }
      })
      addedRoutes.add('404')
    }

    dynamicRoutesAdded = true
  } catch (err) {
    console.error('添加动态路由失败:', err)
    throw err
  }
}

// 7. 改进路由守卫
router.beforeEach(async (to, from, next) => {
  const token = sessionStorage.getItem('Authorization')
  
  // 登录页特殊处理
  if (to.path === '/login') {
    token ? next('/') : next()
    return
  }

  // 检查认证
  if (to.meta.requiresAuth && !token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}&error=unauthorized`)
    return
  }

  // 处理动态路由
  if (token && (!dynamicRoutesAdded || to.name === '404')) {
    try {
      const menus: MenuItem[] = JSON.parse(sessionStorage.getItem('menus') || '[]')
      if (menus.length > 0) {
        await addDynamicRoutes(menus)
        // 重定向到原始路径
        next({ path: to.fullPath, replace: true })
        return
      }
    } catch (err) {
      console.error('路由加载失败:', err)
      next('/login?error=route_load_failed')
      return
    }
  }

  // 处理未匹配路由
  if (to.matched.length === 0) {
    next('/404')
  } else {
    next()
  }
})

export default router