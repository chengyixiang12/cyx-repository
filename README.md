# cyx-repository 系统 README 文档（更新版）

## 项目简介
cyx-repository 是一套基于 **前后端分离架构** 构建的企业级全栈权限管理系统，集成集成权限控制、实时通信、文件管理、AI 交互等核心能力，支持中小型中小型项目快速落地或二次开发。系统采用成熟技术栈，架构清晰、功能完善，兼顾安全性、可扩展性与用户体验。


## 技术栈更新（2025 版）
### 1. 后端技术栈
| 技术/框架         | 版本       | 作用说明                                                                 |
|--------------------|------------|--------------------------------------------------------------------------|
| Spring Boot        | 3.3.4      | 基础开发框架，简化配置与依赖管理，支持自动装配                           |
| Spring Security    | 6.3.2      | 权限控制核心，实现 JWT 认证、动态权限校验、接口安全防护                   |
| MyBatis Plus       | 3.5.6      | ORM 框架，简化数据库操作，支持分页、逻辑删除、条件构造                   |
| MySQL              | 8.4.1      | 关系型数据库，存储用户、角色、权限等核心业务数据                         |
| Redis              | 7.4.0      | 分布式缓存，用于 Token 存储、用户在线状态、限流计数、本地缓存降级         |
| RabbitMQ           | 3.13.0     | 消息队列，解耦异步任务（如文件传输通知、系统消息推送）                   |
| MinIO              | RELEASE.2025-08-15T10-05-58Z | 对象存储服务，用于文件上传/下载、分片存储，兼容 S3 协议                 |
| Knife4j            | 4.4.0      | 接口文档工具，基于 Swagger 增强，支持在线调试、文档导出                 |
| JWT（jjwt）        | 0.12.6     | 无状态认证，生成/解析 Token，保障接口访问安全                           |
| Lombok             | 1.18.32    | 简化 Java 代码，自动生成 Getter/Setter、构造函数、日志等                 |


### 2. 前端技术栈
| 技术/框架         | 版本       | 作用说明                                                                 |
|--------------------|------------|--------------------------------------------------------------------------|
| Vue                | 3.4.29     | 前端核心框架，采用 Composition API 提升代码复用性与可维护性              |
| TypeScript         | 5.5.4      | 静态类型检查，减少运行时错误，提升代码可读性与协作效率                   |
| Element Plus       | 2.7.0      | UI 组件库，提供表单、表格、弹窗等企业级组件，支持响应式布局               |
| Vite               | 5.4.1      | 构建工具，实现快速热更新、按需编译，提升开发效率                         |
| vue-router         | 4.4.3      | 路由管理，实现页面跳转、权限路由拦截                                     |
| Axios              | 1.7.4      | HTTP 请求库，封装统一请求/响应拦截、错误处理、Token 携带                 |
| vue3-markdown-it   | 1.2.2      | Markdown 渲染组件，支持 AI 对话内容、富文本展示                           |
| crypto-js          | 4.2.0      | 前端加密工具，用于敏感数据（如密码）加密传输                             |
| @vue-js-cron/element-plus | 2.0.0 | 定时任务组件，生成 cron 表达式，支持定时任务配置                         |


## 核心功能
### 1. 权限管理模块
- **三级权限模型**：用户 → 角色 → 权限（支持菜单级、按钮级权限控制）
- **无状态认证**：基于 JWT + Redis 实现登录认证，支持 Token 实时失效（登出、强制下线）
- **动态权限**：角色-权限关联可在线配置，无需重启服务；支持 `@PreAuthorize` 方法级权限注解
- **安全防护**：接口限流、XSS 过滤、密码 BCrypt 加密、未授权访问拦截


### 2. 实时通信模块（WebSocket）
- **AI 流式对话**：支持 AI 回答实时推送（类似 ChatGPT 打字效果），存储对话历史
- **系统消息推送**：定向/群体消息推送（如任务提醒、状态变更通知）
- **用户状态管控**：实时维护用户在线状态，支持管理员强制下线操作
- **文件分片传输**：大文件（>1MB）分片上传/下载，支持断点续传，避免传输失败重传


### 3. 基础业务模块
- **用户管理**：用户增删改查、状态锁定/解锁、在线状态展示
- **角色管理**：角色创建、权限分配、批量操作
- **部门管理**：树形部门结构、部门用户关联、数据权限控制
- **文件管理**：基于 MinIO 的文件上传/下载、预览、删除，支持文件权限校验
- **操作日志**：记录 AI 对话、权限变更、文件操作等关键行为，支持日志查询


### 4. 开发支持能力
- **接口文档**：Knife4j 自动生成接口文档，支持在线调试、参数说明、响应示例
- **容器部署**：提供 Dockerfile，支持后端服务容器化部署，简化环境配置
- **前端工程化**：TS 类型约束、组件封装、统一请求处理，降低二次开发成本


## 环境要求
| 环境依赖           | 最低版本要求 | 说明                                                                 |
|--------------------|--------------|----------------------------------------------------------------------|
| JDK                | 17           | Spring Boot 3.x 最低要求，推荐使用 JDK 17 LTS                         |
| Maven              | 3.9.0        | 后端项目构建工具，用于依赖管理与打包                                 |
| Node.js            | 18.17.0      | 前端项目运行环境，推荐使用 Node.js 18 LTS（配套 npm 9+ 或 yarn 1.22+）|
| MySQL              | 8.0          | 需提前创建数据库（默认库名：`cyx_repository`），导入 `db/` 目录下 SQL 脚本 |
| Redis              | 7.0          | 需启用 Redis 服务，配置密码（若有），确保端口可访问                   |
| MinIO              | 2024.05+     | 需提前创建存储桶（默认桶名：`cyx-file`），配置访问密钥                 |
| RabbitMQ           | 3.10+        | 需启用默认交换机，确保队列可正常创建（用于异步任务）                   |


## 快速启动
### 1. 后端启动（imba-radish 模块）
#### 步骤 1：环境配置
修改 `src/main/resources/application.yml` 中核心依赖配置（根据实际环境调整）：
```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cyx_repository?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 替换为实际数据库密码
  # Redis 配置
  redis:
    host: localhost
    port: 6379
    password:  # 若 Redis 无密码则留空
  # RabbitMQ 配置
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

# MinIO 配置
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin  # 替换为实际 Access Key
  secret-key: minioadmin  # 替换为实际 Secret Key
  bucket-name: cyx-file

# JWT 配置
jwt:
  secret: cyx-repository-2025-secret-key  # 生产环境需修改为自定义密钥
  expiration: 86400000  # Token 有效期（毫秒，默认 24 小时）
```

#### 步骤 2：初始化数据库
1. 登录 MySQL，创建数据库：`CREATE DATABASE IF NOT EXISTS cyx_repository DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. 执行 `imba-radish/db/` 目录下所有 SQL 脚本（按 `sys_user.sql`、`sys_role.sql`、`sys_permission.sql` 顺序执行）

#### 步骤 3：启动服务
```bash
# 进入后端目录
cd imba-radish

# 方式 1：Maven 直接启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 方式 2：打包后启动
mvn clean package -DskipTests  # 跳过测试打包
java -jar target/imba-radish-1.0.0.jar --spring.profiles.active=dev
```

#### 验证后端启动
访问接口文档地址：`http://localhost:8080/doc.html`，若能正常打开 Knife4j 文档页面，说明后端启动成功。


### 2. 前端启动（imba-radish-ui 模块）
#### 步骤 1：安装依赖
```bash
# 进入前端目录
cd imba-radish-ui

# 安装依赖（推荐使用 npm）
npm install

# 若依赖安装缓慢，可切换淘宝镜像
npm config set registry https://registry.npmmirror.com/
npm install
```

#### 步骤 2：配置环境
修改 `.env.development` 中后端接口地址（确保与后端服务地址一致）：
```env
# 开发环境后端 API 地址
VITE_APP_BASE_API = 'http://localhost:8080'

# WebSocket 地址
VITE_APP_WS_API = 'ws://localhost:8080/ws'
```

#### 步骤 3：启动前端
```bash
# 开发环境启动（热更新）
npm run dev
```

#### 验证前端启动
启动成功后，终端会输出访问地址（默认：`http://127.0.0.1:5173`），打开浏览器访问：
- 默认管理员账号：`admin`
- 默认密码：`123456`（首次登录建议修改密码）


## 部署说明
### 1. 后端部署（Docker 方式）
#### 步骤 1：构建 Docker 镜像
```bash
# 进入后端目录
cd imba-radish

# 打包 Jar 包
mvn clean package -DskipTests

# 构建 Docker 镜像
docker build -t cyx-repository-backend:1.0.0 .
```

#### 步骤 2：启动 Docker 容器
```bash
docker run -d \
  --name cyx-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://192.168.1.100:3306/cyx_repository?useSSL=false" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="prod-password" \
  -e SPRING_REDIS_HOST="192.168.1.100" \
  cyx-repository-backend:1.0.0
```
（注：`prod` 环境需提前配置 `application-prod.yml`，或通过 `-e` 参数指定生产环境变量）

### 2. 前端部署（Nginx 方式）
#### 步骤 1：构建生产包
```bash
# 进入前端目录
cd imba-radish-ui

# 构建生产环境包（输出到 dist 目录）
npm run build
```

#### 步骤 2：配置 Nginx
创建 Nginx 配置文件（`cyx-repository-ui.conf`）：
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 替换为实际域名

    # 前端静态资源目录
    root /path/to/imba-radish-ui/dist;
    index index.html;

    # 解决 Vue Router history 模式刷新 404 问题
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 反向代理后端 API
    location /api {
        proxy_pass http://192.168.1.100:8080;  # 替换为后端服务地址
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 反向代理 WebSocket
    location /ws {
        proxy_pass http://192.168.1.100:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```

#### 步骤 3：启动 Nginx
```bash
# 复制配置文件到 Nginx 配置目录
cp cyx-repository-ui.conf /etc/nginx/conf.d/

# 检查配置并重启 Nginx
nginx -t
nginx -s reload
```


## 版本更新记录
| 日期       | 版本   | 更新内容                                                                 |
|------------|--------|--------------------------------------------------------------------------|
| 2025-09-08 | 1.0.0  | 初始版本发布，集成权限管理、WebSocket 实时通信、MinIO 文件存储、AI 对话功能 |
| 2025-09-10 | 1.0.1  | 技术栈版本升级（Spring Boot 3.3.1 → 3.3.4，Vue 3.4.20 → 3.4.29），修复文件传输断点续传 bug |


## 开源协议
本项目基于 **MIT 协议** 开源，允许自由使用、修改、分发，如需商用请保留原项目版权声明。


## 联系方式
若遇到问题或需二次开发支持，可通过以下方式交流：
- GitHub 仓库：https://github.com/chengyixiang12/cyx-repository.git
- 问题反馈：请在 GitHub Issues 提交详细问题描述（附报错日志、环境信息）