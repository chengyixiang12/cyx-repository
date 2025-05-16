
# cyx-repository

这是一个基于 Spring Boot 3.3.1 和 Vue3 构建的全栈权限管理系统，采用前后端分离架构，集成了常见企业级开发所需的中间件与模块，功能完善，结构清晰，适合中小型项目快速启动和二次开发。

---

## 🚀 技术栈

### 后端（Java）
- Spring Boot 3.3.1
- Spring Security 权限控制
- Redis 缓存与限流支持
- RabbitMQ 消息队列
- WebSocket 实时通信
- MyBatis Plus + MySQL 持久层支持
- Knife4j 接口文档

### 前端（Vue3）
- Vue 3 + Composition API + TypeScript
- Element Plus UI 组件库
- Vite 构建工具
- Axios 通信 + 自动引入

---

## 🧩 项目结构概览

```
cyx-repository/
├── imba-radish/         # 后端服务
│   ├── conf/            # 全局配置类（安全、缓存、XSS、WebSocket 等）
│   ├── controller/      # 控制器层，处理接口请求
│   ├── entity/          # 数据库实体类
│   ├── service/impl/    # 业务实现类
│   ├── filter/handle/   # 自定义过滤器与异常处理
│   ├── annotation/aspect/async/  # AOP注解、日志、锁、异步封装
│   ├── websocket/       # WebSocket 模块（包括会话管理、消息路由、参数封装等）
│   ├── properties/      # 自定义配置绑定类
│   ├── resultapi/       # 通用响应包装
│   └── ...              # 其他通用模块（枚举、常量、工具类等）
│
├── imba-radish-ui/      # 前端项目
│   ├── src/
│   │   ├── api/         # 后端接口封装
│   │   ├── components/  # 公共组件
│   │   ├── views/       # 页面视图
│   │   ├── store/       # 状态管理（可升级为 Pinia）
│   │   └── utils/       # 工具函数
│   ├── vite.config.ts   # Vite 构建配置
│   └── tsconfig.json    # TypeScript 配置
```

---

## 🧠 核心模块亮点

### ✅ 权限系统（Spring Security）
- JWT 无状态认证机制
- 白名单与动态权限配置支持
- 限流拦截器 + 登录/登出/异常处理自定义
- 方法级权限注解（@PreAuthorize）支持

### ✅ WebSocket 模块
- 多类型消息处理（心跳、强制下线、推送、文件分片传输）
- 会话集中管理，支持点对点/群体消息推送
- 支持连接装饰器、握手拦截、自动重连机制

### ✅ 异步与分布式锁
- 使用 `@Async` 实现文件传输等异步任务
- 自定义注解 + 切面实现分布式锁功能，防重复提交

### ✅ 高可扩展性配置
- 支持 Docker 一键部署（Dockerfile、日志分离）
- 配置分离 `.env` 文件，环境敏感配置集中管理
- Redis + RabbitMQ 已接入，方便集成缓存与消息业务

---

## 📦 启动指南

### 后端服务启动

```bash
cd imba-radish
mvn clean package -DskipTests
java -jar target/*.jar
```

或使用 Spring Boot 插件运行：

```bash
mvn spring-boot:run
```

### 前端服务启动

```bash
cd imba-radish-ui
npm install
npm run dev
```

前端默认端口为 `5173`，后端为 `8080`。

---

## 📄 开源协议

本项目基于 MIT 协议开源，欢迎使用和二次开发。

---
