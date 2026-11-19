# Cyx Repository - imba-radish

基于 **Spring Boot 4 + Java 21（虚拟线程）+ Vue 3** 的全栈后台管理系统。后端采用多模块 Maven 架构，集成权限控制、WebSocket 实时通信、AI 流式对话（含 Function Calling）、系统监控（DB 指标采样 + LTTB 自适应降采样）、断点续传、事件驱动、消息队列等企业级功能。

## 🌐 项目地址

- GitHub: [https://github.com/chengyixiang12/cyx-repository/tree/for-update](https://github.com/chengyixiang12/cyx-repository/tree/for-update)

## 🛠️ 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Java | 21 | 编程语言（虚拟线程） |
| Spring Boot | 4.0.7 | 应用框架（Jackson 3） |
| Maven 多模块 | - | `radish-common`（公共库）+ `radish-monolithic`（可执行单体） |
| Spring Security | 7.x | 安全框架（JWT 无状态认证） |
| MyBatis Plus | 3.5.16 | ORM 框架 |
| dynamic-datasource | 4.3.1 | MySQL 主库 / PostgreSQL 从库动态数据源 |
| MySQL Driver | 9.3.0 | 主库驱动 |
| PostgreSQL Driver | 42.7.10 | 从库驱动 |
| Redis | 7.x | 缓存、Token 存储、速率限制、限流滑动窗口 |
| RabbitMQ | 3.13+ | 消息队列（并配合 Spring Event 事件驱动） |
| MinIO | 8.6.0 | 对象存储（含头像桶） |
| Flyway | 11.x | 数据库版本迁移 |
| Spring AI | 2.0.0 | AI 集成（DeepSeek，WebFlux 流式） |
| Flowable | 7.2.0 | 工作流引擎（已排除冲突依赖） |
| Quartz | - | 定时任务（单机模式） |
| Spring Modulith | 1.4.9 | 模块化扩展（高德天气第三方 API 封装） |
| Knife4j | 4.6.0 | 接口文档（仅开发环境） |
| EasyExcel / POI | 4.0.3 / 5.4.1 | Excel 导入导出 |
| Hutool / Fastjson2 / OkHttp | 5.8.43 / 2.0.53 / 4.12.0 | 通用工具 / JSON / HTTP |

### 前端技术栈

| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Vue | 3.x | 前端框架（Composition API + `<script setup lang="ts">`） |
| TypeScript | 5.6.x | 类型安全 |
| Vite | 5.4.x | 构建工具（开发端口 5147） |
| Element Plus | 2.9.x | UI 组件库（中文语言包、小尺寸） |
| Vue Router | 4.4.x | 动态路由（后端菜单驱动） |
| ECharts | 6.0.x | 图表库（趋势图 LTTB 降采样数据） |
| Axios | 1.7.x | HTTP 客户端（统一封装） |
| markdown-it / vue3-markdown-it | 14.x / 1.x | AI 回答 Markdown 渲染 |
| fingerprintjs / spark-md5 / streamsaver | - | 设备指纹 / 文件 MD5 / 流式下载 |
| ESLint / Stylelint / Prettier | 10 / 18 / 3 | 代码规范与格式化 |

## ✨ 功能特性

### 系统管理
- **用户管理**：用户列表、新增、编辑、删除、角色分配、个人中心
- **角色管理**：角色列表、权限配置、菜单权限
- **权限管理**：权限定义、资源管理
- **部门管理**：部门树结构管理
- **菜单管理**：动态菜单配置（后端驱动前端路由）
- **密钥管理**：RSA 密钥对管理（登录密码解密，公私钥脱敏展示）
- **公告管理**：系统公告维护
- **注册 / 忘记密码**：多步骤注册、邮箱验证码找回密码

### 数据管理
- **字典管理**：字典类型、字典数据分开维护
- **文件管理**：文件上传、下载、预览、删除（MinIO）
- **断点续传**：基于 `ChunkStorage` 的用户级分片目录（`.part` 原子落盘、进度校验、合并），支持大文件上传与秒传
- **在线磁盘**：个人文件存储空间

### 首页工作台
- **个人数据概览**：仅返回当前登录人数据（管理员也不跨用户）
- **最近登录记录**：当前用户最近 5 次登录
- **最近 AI 对话**：当前用户最近 5 条对话，可快速回溯

### 系统监控
- **实时指标**：CPU、内存（堆/元空间）、磁盘、运行时间
- **指标持久化**：`MetricCollector` 每 30 秒采集资源与应用指标（QPS、错误率、GC 停顿等）落库，`MetricRollupJob` 做聚合归档
- **组件健康**：数据库、Redis、磁盘空间、SSL 证书等状态探测并持久化（`HealthProbe` + 状态表 + 变更日志表）
- **趋势图表**：LTTB 自适应降采样（时间范围越大点数越多，1 小时→20 点 ~ 7 天→200 点）
- **操作日志**：AOP 自动记录所有操作行为（支持日志详情）
- **定时任务**：Quartz 任务管理和执行记录（任务 / 记录分开维护）

### 消息通信
- **实时聊天**：WebSocket 即时通讯（注册模式命令分发）
- **消息推送**：系统通知推送、强制下线、Token 刷新通知
- **文件传输**：WebSocket 在线文件传输（开始 / 传输 / 续传 / 结束）
- **心跳保活**：虚拟线程异步 PING（每 30 秒），断线自动重连 + 消息队列

### AI 功能
- **AI 对话**：DeepSeek 模型，基于 WebSocket 的**流式输出**（60ms 按 token 攒批推送，减少帧频率）
- **上下文记忆**：按会话保留最近 N 条上下文（默认 7 轮，可配置 `spring.ai.max-context-num`）
- **Function Calling**：接入高德天气第三方 API，`ChatHandler` 通过 `.tools()` 注册工具，模型可自动调用
- **对话管理**：会话与消息明细分表存储，支持回答历史回溯与 Markdown 渲染
- **可靠性**：流式响应失败重试（10 次），异常仅下发错误提示、不落库

### 安全特性
- JWT 无状态令牌认证（Redis 存储，可配置过期时间）
- RSA 非对称加密传输（密钥对由 `sys_secret_key` 表管理）
- 登录密码 RSA 解密 + BCrypt 存储
- 邮箱验证码登录、验证码过期控制
- 接口访问频率限制（Redis 滑动窗口）
- 并发登录控制（WebSocket 强制旧会话下线，应用关闭时优雅关闭所有会话）
- 细粒度权限控制（`CustomPermissionService` + `@AccessControl`）
- 分布式锁（`@SysLock`）、AOP 操作审计日志（`@SysLog`）
- 前端设备指纹（FingerprintJS）+ 浏览器本地会话（sessionStorage）

## 📁 项目结构

```
cyx-repository/
├── imba-radish/                          # 后端：多模块 Maven 工程
│   ├── pom.xml                           # 父 POM（纯 BOM + pluginManagement）
│   ├── radish-common/                    # 公共库模块（不打包可执行）
│   │   └── src/main/java/com/soft/common/
│   │       ├── annotation/               # @SysLog / @SysLock / @AccessControl / @LogIgnore
│   │       ├── constants/                # 常量（Base/Redis/Token/WebSocket/Dict...）
│   │       ├── enums/                    # ResultEnum / LogModuleEnum / LogTypeEnum / DeviceTypeEnum
│   │       ├── exception/                # GlobalException
│   │       ├── handle/                   # EnvLoaderHandler / GlobalExceptionHandler
│   │       ├── model/ctf/                # MenuTree
│   │       ├── resultapi/                # 统一响应 R<T>
│   │       └── utils/                    # AES / RSA / Http / Common / Response 工具
│   ├── radish-monolithic/                # 可执行单体模块（当前承载全部业务，端口 8081）
│   │   ├── src/main/java/com/soft/
│   │   │   ├── ImbaRadishApplication.java
│   │   │   ├── sys/
│   │   │   │   ├── controller/           # REST 控制器（20 个，含 Dashboard / 公告 / 对话）
│   │   │   │   ├── service/impl/         # 业务逻辑层 + Security 用户详情 + 仪表盘服务
│   │   │   │   ├── mapper/               # MyBatis Plus 映射器（20+）
│   │   │   │   ├── entity/               # 实体类（含监控采样 / 组件健康 / 对话等）
│   │   │   │   ├── event/                # 事件驱动（EmailPublisher / SysLogPublisher / MessageEventListener）
│   │   │   │   ├── monitor/              # 监控（MetricCollector / HealthProbe / Rollup / Aggregate）
│   │   │   │   ├── websocket/            # WebSocket 子系统（注册模式，含 service/session/decorator）
│   │   │   │   ├── core/                 # conf / filter / aspect / handle / listener / permission
│   │   │   │   ├── rabbitmq/ quartz/ async/ schedule/ file/   # 消息 / 定时 / 异步 / 调度 / 分片存储
│   │   │   │   ├── properties/           # 配置属性绑定（radish.* / websocket.* / rate-limit.*）
│   │   │   │   └── utils/                # SecurityUtil / MinioUtil
│   │   │   └── module/                   # Spring Modulith 模块（高德天气 ThirdInterface + TestController）
│   │   └── src/main/resources/
│   │       ├── mapper/sys/               # MyBatis XML 映射（20 个）
│   │       ├── db/migration/             # Flyway 迁移（V1__init_database.sql）
│   │       └── application*.yml          # 配置文件
│   ├── CLAUDE.md                         # 后端 AI 辅助开发指南
│   ├── Dockerfile                        # 容器化构建
│   ├── script/start.sh                   # 优雅启停脚本
│   ├── .env                              # 敏感配置（已 gitignore）
│   └── target/                           # 构建产物（jar 位于各模块下）
│
├── imba-radish-ui/                       # 前端 Vue 项目
│   ├── src/
│   │   ├── api/                          # API 接口定义（按业务模块，含 dashboard / dialogueHistory）
│   │   ├── views/
│   │   │   ├── Dashboard.vue             # 首页工作台
│   │   │   ├── chat/                     # AI 对话 + 回答历史
│   │   │   ├── login/ register/          # 登录 / 忘记密码 / 注册
│   │   │   ├── system/                   # 用户/角色/菜单/部门/权限/字典/文件/日志/监控/任务/密钥/个人中心
│   │   │   └── error/                    # 404
│   │   ├── layouts/ components/          # 布局 / 公共组件
│   │   ├── router/                       # 静态路由 + 动态路由生成
│   │   ├── utils/                        # http / websocket(Manager) / rsa / filemd5 / download ...
│   │   ├── workers/ composables/         # Web Worker / 组合式函数
│   │   ├── assets/styles/theme.css       # 主题令牌（统一样式变量）
│   │   └── types/                        # TypeScript 类型定义
│   ├── vite.config.ts                    # 代理配置（/api、/ws、/download）
│   ├── CLAUDE.md                         # 前端 AI 辅助开发指南
│   └── package.json                      # npm 依赖与脚本
│
├── LICENSE                               # MIT 许可证
└── README.md                             # 项目说明文档
```

> **架构演进**：后端已从单体演进为多模块结构。当前 `radish-common` 提供公共基础件、`radish-monolithic` 承载全部业务并可独立运行；`CLAUDE.md` 中规划的 `radish-gateway` / `radish-system` / `radish-business` / `radish-nacos` 微服务模块将在后续阶段从单体拆分。

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.6.3+（Spring Boot 4 最低要求；多模块构建建议 3.9.x）
- Node.js 20+
- MySQL 8.0+
- PostgreSQL 16+（可选，从库）
- Redis 7.x
- RabbitMQ 3.13+（可选）
- MinIO 8.0+（可选）
- Nacos 3.x（可选，微服务阶段；默认 `NACOS_ENABLED=false`）

### 后端启动

1. **配置环境变量**

   `.env` 必须位于**启动进程的工作目录**下：`EnvLoaderHandler` 通过相对路径 `new FileInputStream(".env")` 读取，文件缺失会直接终止启动。从 `radish-monolithic/` 目录启动时，该目录下需自备一份（已提供）。

   在 `imba-radish/`（或 `imba-radish/radish-monolithic/`）下维护 `.env`（已 gitignore）：

   ```env
   # 数据源（主库 MySQL / 从库 PostgreSQL）
   DATASOURCE_URL_MYSQL_MASTER=jdbc:mysql://localhost:3306/radish?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
   DATASOURCE_USERNAME_MYSQL_MASTER=root
   DATASOURCE_PASSWORD_MYSQL_MASTER=your_password
   DATASOURCE_URL_MYSQL_SLAVE=jdbc:postgresql://localhost:5432/radish
   DATASOURCE_USERNAME_MYSQL_SLAVE=postgres
   DATASOURCE_PASSWORD_MYSQL_SLAVE=your_password

   # Redis
   REDIS_HOST=localhost
   REDIS_PORT=6379
   REDIS_PASSWORD=

   # DeepSeek AI
   DEEPSEEK_API_KEY=your_key
   DEEPSEEK_BASE_URL=https://api.deepseek.com
   DEEPSEEK_MODEL=deepseek-chat

   # 邮件（验证码 / 通知）
   EMAIL_USERNAME=your@qq.com
   EMAIL_PASSWORD=your_smtp_token

   # MinIO
   MINIO_URL=http://localhost:9000
   MINIO_ACCESS_KEY=your_key
   MINIO_SECRET_KEY=your_secret
   MINIO_DEFAULT_BUCKET=radish
   MINIO_AVATAR_BUCKET=radish-avatar

   # 高德地图（天气 Function Calling）
   GAO_DE_API_KEY=your_key
   ```

2. **构建与运行**（在 `imba-radish/` 根目录执行）

   ```bash
   # 全量构建（测试默认跳过）
   mvn clean package

   # 只构建单体模块及其依赖
   mvn -pl radish-monolithic -am clean package

   # 开发模式启动（dev profile 默认）
   mvn -pl radish-monolithic spring-boot:run

   # 生产模式运行 fat jar
   java -jar radish-monolithic/target/radish-monolithic-1.0-SNAPSHOT.jar --spring.profiles.active=prod
   ```

   后端默认绑定端口 **8081**。

### 前端启动

```bash
cd imba-radish-ui
npm install
npm run dev        # 开发服务器，端口 5147
npm run build      # 生产构建
npm run preview    # 预览生产构建
npm run typecheck  # vue-tsc 类型检查
npm run lint       # Stylelint + ESLint 检查
npm run format     # Prettier 格式化
```

### 访问地址
- 前端页面: `http://localhost:5147`
- 后端 API: `http://localhost:8081`
- Knife4j 接口文档: `http://localhost:8081/doc.html`（仅开发环境启用）

前端开发代理（见 `vite.config.ts`）：`/api` → `http://127.0.0.1:8081`（移除前缀）、`/ws` → `ws://127.0.0.1:8081`、`/download` → MinIO 地址。

## 🔧 配置说明

### 配置分层

| 配置来源 | 说明 | 优先级 |
| :--- | :--- | :--- |
| `.env` | 敏感配置（数据库密码、API Key 等）| 最高（由 `EnvLoaderHandler` 注入 Environment） |
| `application.yml` | 应用框架配置（端口、AI、MyBatis、MinIO、高德等）| 中 |
| `application-dev.yml` | 开发环境覆盖 | 开发时使用 |
| `application-prod.yml` | 生产环境覆盖 | 生产时使用 |

### 关键配置项

| 前缀 | 说明 |
| :--- | :--- |
| `spring.ai.*` | DeepSeek 模型、api-key、上下文轮数（`max-context-num`） |
| `spring.threads.virtual.*` | 虚拟线程开关 |
| `radish.*` | 验证码、Token、分布式锁、日志设置 |
| `websocket.allowed-origins` | WebSocket 允许的来源域名（不再硬编码） |
| `rate-limit.*` | 接口速率限制（Redis 滑动窗口） |
| `minio.*` | 对象存储连接（含默认桶、头像桶） |
| `geo.gao-de.api-key` | 高德天气 API Key |
| `web-client.*` | WebClient 连接池与超时 |
| `spring.security.permit.*` | 白名单 URL |

### 环境差异

| 配置项 | 开发环境 | 生产环境 |
| :--- | :--- | :--- |
| 数据库从库 | 配置存在 | 不启用 |
| Redis DB | 15 | 14 |
| Knife4j/Swagger | 启用 | 禁用 |
| WebSocket 来源 | 宽松 | 限制 |
| 日志级别 | DEBUG | INFO |

## 🐳 Docker 部署

```bash
cd imba-radish
mvn clean package                                  # 先在项目根目录构建 fat JAR
docker build -t imba-radish .
```

Dockerfile 基于 `eclipse-temurin:21-jre-jammy`，安装验证码字体渲染依赖，将 `application.yml` / `application-prod.yml` 外化到 `/app/config/`，复制 `radish-monolithic/target/radish-monolithic-1.0-SNAPSHOT.jar`，G1GC 堆内存 256m，暴露 8081 端口，默认以 `prod` profile 启动。另有 `script/start.sh` 提供优雅启停管理。

## 🔌 WebSocket 架构

采用**注册模式**实现命令分发，新增命令无需修改路由代码：

```
WebSocketInterceptor (握手鉴权 / 来源校验)
  → WebSocketHandler (按 WebSocketOrderEnum 路由)
    → WebSocketConcreteRegistry (@PostConstruct 自动扫描)
      → WebSocketConcreteHolder (命令 → 处理器映射)
        → WebSocketConcreteHandler<T> (具体业务处理)
```

**指令集**（`WebSocketOrderEnum`）：`FORCE_OFFLINE`、`PUSH_MESSAGE`、`PING`、`PONG`、`FILE_TRANSFER_START`、`FILE_TRANSFER`、`FILE_TRANSFER_CONTINUE`、`FILE_TRANSFER_OVER`、`FILES_TRANSFER`、`AI`、`REFRESH_TOKEN`。

**扩展方式**：实现 `WebSocketConcreteHandler<T>` → 注册 `WebSocketOrderEnum` → 自动发现。

### 会话与心跳
- `WebSocketSessionManager` 基于 `TimedCache`（30s TTL、10s 清理间隔）按用户 ID 缓存会话
- `WebSocketHeardHeatTimer` 每 30 秒调度一次，通过 `@Async` + 虚拟线程并发发送 PING
- 前端 `WebsocketManager` 支持自动重连（首次立即、后续 30s，最多 5 次）与断线消息队列（上限 100 条）
- 应用关闭时 `ApplicationShutdownListener` 以最高优先级优雅关闭所有会话

## 🤖 AI 对话链路

```
前端 Chat.vue (WebSocket 发送 question + dialogueId)
  → ChatHandler
      ├─ 问题入库 (SysDialogueDetails, tag=USER)
      ├─ buildMessages(): 按 dialogueId 取最近 N 条上下文（UserMessage / AssistantMessage）
      ├─ ChatClient.prompt().tools(ThirdInterface).stream()
      │     └─ doOnNext: 攒批 60ms → 分片下发 (order=AI)
      ├─ doOnComplete: 冲刷尾部 + 完整回答入库 (tag=AI)
      └─ doOnError: 下发错误提示，不落库
  → 前端 markdown-it 渲染回答，AnswerHistory 回溯历史
```

- **工具调用**：`ThirdInterface`（高德天气）通过 `.tools()` 注册，模型按需调用
- **上下文窗口**：`spring.ai.max-context-num` 控制保留的历史轮数
- **节流下发**：按 token 攒批（60ms）推送，完整内容仍落库，兼顾实时性与帧开销

## 📊 监控与趋势降采样

监控分为两层：

1. **指标采集与持久化**：`MetricCollector` 每 30 秒采样资源（CPU / 内存 / 磁盘）与应用指标（QPS、错误率、GC 停顿）写入 `SysMetricSample`，`MetricRollupJob` 按时间窗口聚合到 `SysMetricRollup`，`MetricSnapshotHolder` 提供最新快照，`MonitorAggregateService` 对外提供聚合查询。
2. **趋势降采样**：CPU / 内存趋势数据经 **LTTB (Largest-Triangle-Three-Buckets)** 算法降采样后返回前端。

| 时间范围 | 降采样点数 | 压缩比 |
| :--- | :--- | :--- |
| 15 分钟 | 20 | 3:1 |
| 1 小时 | 20 | 6:1 |
| 6 小时 | ~83 | ~9:1 |
| 1 天 | ~131 | ~22:1 |
| 7 天 | ~200 | ~101:1 |

- 采用**对数函数**动态计算点数（`SysActuatorServiceImpl.calculateMaxPoints`），下限 20 / 上限 200
- 降采样在 Service 层透明完成，Controller 直接调用 `listXxxTrend(startTime, endTime)`
- 组件健康由 `HealthProbe` 探测 db / redis / diskSpace / ssl，状态与变更日志分别持久化，前端以表格展示 UP / DOWN / DISABLED

## 📝 开发规范

### 后端规范
- 遵循 Spring Boot 标准分层与多模块约束：
  - 库模块（`radish-common`）**绝不**声明 `spring-boot-maven-plugin`
  - 各服务启动类置于 `com.soft` 包，但用 `scanBasePackages` 收窄扫描范围（避免 `HttpUtil` 的 `WebClient` 依赖污染无 webflux 模块）
  - `GlobalExceptionHandler`（`com.soft.common.handle`）**不得**引用 Spring Security 异常类型；认证/授权异常拆分到 `com.soft.sys.core.handle.SecurityExceptionHandler`
  - `radish-common` 的框架依赖一律 `<optional>true</optional>`
- 使用 MyBatis Plus 进行数据库操作，Flyway 管理迁移（schema 变更以新版本号追加）
- 使用 Lombok 简化代码，统一异常处理和响应格式 `R<T>`（`{timestamp, code, msg, data, extra}`）

### 前端规范
- 使用 Vue 3 Composition API + TypeScript + Element Plus
- 样式统一走 `src/assets/styles/theme.css` 主题令牌，避免硬编码颜色值
- 统一 API 调用方式（`src/utils/http.ts`），组件化开发
- 提交前运行 `npm run typecheck` 与 `npm run lint`

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 提交规范
- **Commit 信息**：使用英文动词开头，如 `feat: add new feature`
- **PR 标题**：清晰描述修改内容
- **代码风格**：遵循项目现有风格

### 开发流程
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/foo`)
3. 提交修改 (`git commit -am 'Add some foo'`)
4. 推送到分支 (`git push origin feature/foo`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 📞 联系方式

如有问题或建议，欢迎通过以下方式联系：

- GitHub Issues: [提交 Issue](https://github.com/chengyixiang12/cyx-repository/issues)

---

**Powered by Spring Boot 4 + Java 21 Virtual Threads + Vue 3** 🚀
