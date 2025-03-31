import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import MainLayout from '../layouts/MainLayout.vue';

// 1. 使用Vite的glob导入所有页面组件
// const pageComponents = import.meta.glob('/src/views/**/*.vue')

// 2. 基础路由
const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录', noAuth: true }
  }
]

// 3. 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

// 4. 动态路由状态管理
let dynamicRoutesAdded = false
const addedRoutes = new Set<string>()

// 5. 改进的动态路由生成
function generateRoutes(menus: MenuItem[]): RouteRecordRaw[] {
  return menus.map(menu => {
    // 转换组件路径格式 (system/User -> /src/views/system/User.vue)
    let componentPath;
    console.log(menu.component);
    if (menu.component != null) {
      componentPath = `/src/views/${menu.component}.vue`;
    } else {
      componentPath = '/src/views/error/404.vue';
    }
    const route: RouteRecordRaw = {
      path: menu.path.startsWith('/') ? menu.path : `/${menu.path}`, // 确保父路径有 `/`
      name: menu.title.replace(/\s+/g, '-') || menu.path.replace(/\//g, '-'),
      component: () => import(/* @vite-ignore */componentPath),
      meta: {
        title: menu.title,
        icon: menu.icon,
        requiresAuth: !menu.noAuth
      },
      children: menu.children?.length ? generateRoutes(menu.children) : undefined
    }

    return route
  })
}

// 6. 安全添加动态路由
export async function addDynamicRoutes(menuList: MenuItem[]) {
  if (dynamicRoutesAdded) return

  console.log('a', menuList)
  try {
    const dynamicRoutes = generateRoutes(menuList)
    dynamicRoutes.forEach(route => {
      if (!addedRoutes.has(route.path)) {
        router.addRoute(route)
        addedRoutes.add(route.path)
      }
    })

    // 确保404是最后添加的
    if (!addedRoutes.has('*')) {
      router.addRoute({
        path: '/:pathMatch(.*)*',
        component: () => import('@/views/error/404.vue')
      })
      addedRoutes.add('*')
    }

    // console.log(router.getRoutes());

    dynamicRoutesAdded = true
  } catch (err) {
    console.error('添加动态路由失败:', err)
    throw err
  }
}

// 7. 增强的路由守卫
router.beforeEach(async (to, from, next) => {
  const token = sessionStorage.getItem('Authorization')

  // 登录页特殊处理
  if (to.path === '/login') {
    token ? next('/') : next()
    return
  }

  // 认证检查
  if (to.meta.requiresAuth !== false && !token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }

  // 动态路由加载
  if (token && !dynamicRoutesAdded) {
    try {
      const menus: MenuItem[] = JSON.parse(sessionStorage.getItem('menus') || '');
      await addDynamicRoutes(menus)
      next({ ...to, replace: true })
    } catch (err) {
      console.error('路由加载失败:', err)
      next('/login?error=route_load_failed')
    }
    return
  }

  // 检查路由是否存在
  if (to.matched.length === 0 && !addedRoutes.has('*')) {
    next('/404')
  } else {
    next()
  }
})

export default router