# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
npm run dev        # 启动开发服务器 (Vite, 端口 8090)
npm run build      # 生产构建
npm run preview    # 预览生产构建
npm run typecheck  # 运行 vue-tsc 类型检查 (无此命令，需自行添加: "typecheck": "vue-tsc --noEmit")
```

## 技术栈

- **框架**: Vue 3 (Composition API + `<script setup lang="ts">`)
- **UI**: Element Plus (中文语言包, 小尺寸组件)
- **构建**: Vite 5 + TypeScript 5.6
- **路由**: Vue Router 4 (动态路由)
- **HTTP**: Axios (封装于 `src/utils/http.ts`)
- **WebSocket**: 自定义 `WebsocketManager` (自动重连, 消息队列, PING/PONG 心跳)
- **样式**: SCSS + Element Plus + CSS变量

## 项目架构

### 目录结构

```
src/
├── api/          # API 请求层，按业务模块拆分 (login, user, menu, chat 等)
├── types/        # TypeScript 类型定义 (api.d.ts 通用类型, 其余按模块)
├── utils/        # 工具函数
│   ├── http.ts       # Axios 封装 (请求/响应拦截器, JWT, 错误码处理, blob下载)
│   ├── websocket.ts  # WebSocket 单例工厂
│   ├── websocketManager.ts  # WebSocket 管理器 (重连, 消息队列, 状态管理)
│   ├── rsa.ts         # RSA 加密 (node-forge)
│   ├── message.ts     # ElMessage 封装
│   ├── notify.ts      # ElNotification 封装
│   ├── download.ts    # 文件下载工具
│   ├── filemd5.ts     # 文件 MD5 计算 (SparkMD5)
│   ├── clearCache.ts  # 清除 sessionStorage
│   ├── uuid.ts        # UUID 生成
│   └── delay.ts       # 延时工具
├── views/         # 页面组件
│   ├── login/    # 登录页 (密码登录/邮箱验证码登录)
│   ├── register/ # 注册 (多步骤表单: StepPassword/StepVerify/StepUserInfo/StepSuccess)
│   ├── chat/     # AI 对话页面 (WebSocket 流式输出)
│   ├── system/   # 系统管理模块 (User, Role, Menu, Dept, Permission, Dict, Quartz, File, Log, Actuator)
│   └── error/    # 404 页面
├── layouts/      # 布局组件
│   ├── MainLayout.vue     # 主布局 (侧边栏 + 顶栏 + 多标签页)
│   └── component/ModuleTabs.vue  # 多标签页组件
├── router/       # 路由配置
│   └── routers.ts   # 静态基础路由 + 动态路由生成 (从后端菜单API获取)
├── components/   # 共享组件 (如 FileUpload.vue)
├── assets/       # 静态资源 (图片, 全局 CSS)
├── common/       # 全局配置
└── i18n/         # 国际化
```

### 关键设计

#### 1. 动态路由

路由系统通过后端驱动：用户在 `MainLayout` 组件挂载后，调用 `/menu/getMenuRoute` 获取菜单树，由 `routers.ts` 中的 `generateRoutes()` 函数递归转换为 Vue 路由配置。视图组件通过 `import.meta.glob('../views/**/*.vue')` 动态导入，按 `src/views/` 下的相对路径映射。

```typescript
// 组件路径映射规则:
// src/views/system/User.vue → system/User
// 菜单的 component 字段需匹配上述路径
```

#### 2. API 请求层

`src/utils/http.ts` 封装了 Axios，统一处理：
- **认证**: 从 `sessionStorage` 取 `Authorization` 字段，以 `Bearer` 格式注入请求头
- **响应解析**: 统一检查 `ApiResponse.code`，10000 为成功
- **错误处理**: 认证错误码 (10003/10005/10006) 自动跳转登录页
- **请求配置**: `flag` 控制是否注入认证头，`silent` 控制是否显示错误弹窗
- **Blob 下载**: 支持 `getBlob` / `postBlob` 获取二进制流

API 方法统一返回 `ApiResponse<T>` 结构体:
```typescript
interface ApiResponse<T> {
  timestamp: number;
  code: number;      // 10000 表示成功
  msg: string;
  data: T;
}
```

#### 3. WebSocket 通信

`WebsocketManager` 类管理单个 WebSocket 连接，用于 AI 对话和系统消息推送。

- 连接地址: `/ws?Authorization=<token>` (Vite 代理到 `ws://127.0.0.1:8081`)
- 自动重连: 首次立即重连，后续间隔 30s，最多 5 次
- 消息队列: 断线时消息暂存，恢复后发送 (上限 100 条)
- 消息协议: `{ order: string, msg: string, status: boolean }`
  - `PONG` → 心跳响应，含 refreshFlag 时触发 token 刷新
  - `PING` → 服务器心跳，回复 PONG
  - `FORCE_OFFLINE` → 强制下线
  - `AI` → AI 对话流式响应
  - `REFRESH_TOKEN` → Token 刷新通知
- 注入回调: `onMessage`, `onForceLogout`, `aiAnwser`, `refreshToken`, `onStatusChange`

#### 4. 登录流程

1. 输入账号密码 / 邮箱验证码
2. 获取 RSA 公钥 (`/auth/getPublicKey`) 加密密码
3. 调用 `/auth/login` 获取 token
4. 保存 token 和 userInfo 到 `sessionStorage`
5. 路由守卫自动加载动态菜单路由
6. 建立 WebSocket 连接

#### 5. 后端约定

后端是 Java Spring Boot 应用，API 前缀经过 Vite 代理 (`/api → http://127.0.0.1:8081`, 移除 `/api` 前缀)。
- **认证方式**: JWT (Bearer Token)
- **认证错误码**: 10003 (未登录), 10005 (Token过期), 10006 (无权限)
- **数据存储**: sessionStorage (非 localStorage)
- **指纹识别**: 使用 `@fingerprintjs/fingerprintjs`

#### 6. UI 布局

- 顶栏: 系统名 + 消息图标 + 首页按钮 + 用户头像/下拉菜单
- 侧边栏: 可折叠菜单 (ElMenu)
- 主体: 多标签页 (ModuleTabs) + 路由视图
- 全局使用 Element Plus 小尺寸 (`<el-config-provider :size="'small'">`)
