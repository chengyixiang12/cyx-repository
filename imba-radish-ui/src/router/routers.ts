import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import MainLayout from '../layouts/MainLayout.vue';

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
        component: () => import('/src/views/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('/src/views/login/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: '404',
    component: () => import('/src/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
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
function generateRoutes(menus: MenuItem[], parentPath: string = ''): RouteRecordRaw[] {

  const routes: RouteRecordRaw[] = [];

  menus.forEach(menu => {
    // 如果是有子菜单的父级菜单，直接处理子菜单
    if (menu.children?.length && !menu.component) {
      routes.push(...generateRoutes(menu.children, menu.path));
      return;
    }

    // 处理具体页面的路由
    if (menu.component) {
      // 移除开头的 views/ 如果存在
      const cleanComponent = menu.component.replace(/^views\//, '');
      const componentPath = `/src/views/${cleanComponent}${cleanComponent.endsWith('.vue') ? '' : '.vue'}`;

      const route: RouteRecordRaw = {
        path: menu.path.startsWith('/') ? menu.path : `/${menu.path}`,
        name: menu.path.replace(/\s+/g, '-').toLowerCase(),
        component: () => {
          return import(/* @vite-ignore */ componentPath);
        },
        meta: {
          title: menu.title,
          icon: menu.icon,
          parentPath: parentPath, // 记录父级路径用于面包屑
          requiresAuth: true
        }
      };

      routes.push(route);
    }
  });

  return routes;
}

// 6. 安全添加动态路由
export async function addDynamicRoutes(menuList: MenuItem[]) {
  if (dynamicRoutesAdded) {
    return;
  }

  try {
    // 生成路由配置
    const dynamicRoutes = generateRoutes(menuList);

    // 清除现有的动态路由
    Array.from(addedRoutes).forEach(path => {
      try {
        router.removeRoute(path);
      } catch (e) {
        console.warn(`Failed to remove route: ${path}`, e);
      }
    });
    addedRoutes.clear();

    // 将所有路由直接添加到主布局下
    dynamicRoutes.forEach(route => {
      try {
        router.addRoute('MainLayout', route);
        addedRoutes.add(route.path);
      } catch (e) {
        console.error(`Failed to add route: ${route.path}`, e);
      }
    });

    // 添加404路由
    // router.addRoute({
    //   path: '/:pathMatch(.*)*',
    //   name: '404',
    //   component: () => import('/src/views/error/404.vue'),
    //   meta: { title: '404' }
    // });
    addedRoutes.add('*');

    dynamicRoutesAdded = true;
  } catch (err) {
    console.error('添加动态路由失败:', err);
    throw err;
  }
}

// 7. 改进路由守卫
router.beforeEach(async (to, from, next) => {
  const token = sessionStorage.getItem('Authorization');
  if (to.path === '/login') {
    token ? next('/') : next()
    return
  }

  // 如果路由需要认证但没有 token，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login?error=unauthorized');
    return;
  }

  if (token && !dynamicRoutesAdded) {
    try {
      const menus: MenuItem[] = JSON.parse(sessionStorage.getItem('menus') || '[]');
      await addDynamicRoutes(menus)
      next({ ...to, replace: true })
    } catch (err) {
      console.error('路由加载失败:', err)
      next('/login?error=route_load_failed')
    }
    return
  }

  // 简化路由匹配逻辑
  if (to.matched.length === 0) {
    next('/404')
  } else {
    next()
  }
})

export default router