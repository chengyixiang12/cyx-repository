# Cyx Repository - imba-radish

基于 **Spring Boot 4 + Java 21 (虚拟线程) + Vue 3** 的全栈后台管理系统，集成权限控制、WebSocket 实时通信、系统监控（LTTB 自适应降采样）、消息队列、AI 对话等企业级功能。

## 🌐 项目地址

- GitHub: [https://github.com/chengyixiang12/cyx-repository/tree/for-update](https://github.com/chengyixiang12/cyx-repository/tree/for-update)

## 🛠️ 技术栈

### 后端技术栈
| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Java | 21 | 编程语言（支持虚拟线程） |
| Spring Boot | 4.0.7 | 应用框架 |
| Spring Modulith | 1.4.9 | 模块化架构 |
| Spring Security | 7.x | 安全框架（JWT 无状态认证） |
| MyBatis Plus | 3.5.16 | ORM 框架 |
| MySQL | 8.0+ | 主库 |
| PostgreSQL | 16+ | 从库（动态数据源） |
| Redis | 7.x | 缓存、Token 存储、速率限制 |
| RabbitMQ | 3.13+ | 消息队列 |
| MinIO | 8.5.2+ | 对象存储 |
| Flyway | 11.x | 数据库版本迁移 |
| Spring AI | 2.0.0+ | AI 集成（DeepSeek） |
| Flowable | 7.2.0 | 工作流引擎 |
| Quartz | - | 定时任务（单机模式） |
| ECharts | - | 趋势图表（LTTB 降采样） |

### 前端技术栈
| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Vue | 3.x | 前端框架 |
| TypeScript | 5.6.x | 类型安全 |
| Vite | 5.4.x | 构建工具 |
| Element Plus | 2.9.x | UI 组件库 |
| Vue Router | 4.4.x | 路由管理 |
| ECharts | 6.0.x | 图表库 |
| Axios | 1.7.x | HTTP 客户端 |

## ✨ 功能特性

### 系统管理
- **用户管理**：用户列表、新增、编辑、删除、角色分配
- **角色管理**：角色列表、权限配置、菜单权限
- **权限管理**：权限定义、资源管理
- **部门管理**：部门树结构管理
- **菜单管理**：动态菜单配置
- **密钥管理**：RSA 密钥对管理（登录密码解密，脱敏展示）

### 数据管理
- **字典管理**：字典类型和字典数据管理
- **文件管理**：文件上传、下载、预览、删除（MinIO）
- **在线磁盘**：个人文件存储空间

### 系统监控
- **实时指标**：CPU、内存（堆/元空间）、磁盘、运行时间
- **趋势图表**：LTTB 自适应降采样（时间范围越大点数越多，1 小时→20 点 ~ 7 天→200 点）
- **组件健康**：数据库、Redis、RabbitMQ、SSL、磁盘空间等状态监控
- **操作日志**：AOP 自动记录所有操作行为
- **定时任务**：Quartz 任务管理和执行记录

### 消息通信
- **实时聊天**：WebSocket 即时通讯
- **消息推送**：系统通知推送
- **文件传输**：WebSocket 在线文件传输
- **心跳保活**：虚拟线程异步 PING（每 30 秒）

### AI 功能
- **AI 对话**：集成 DeepSeek 模型的智能问答（Spring AI）
- **对话历史**：记录和管理对话记录

### 安全特性
- JWT 无状态令牌认证（Redis 存储）
- RSA 非对称加密传输（密钥对由 `sys_secret_key` 表管理）
- 接口访问频率限制（Redis 滑动窗口）
- 并发登录控制（WebSocket 强制下线）
- 细粒度权限控制
- AOP 操作审计日志

## 📁 项目结构

```
cyx-repository/
├── imba-radish/                          # 后端 Spring Boot 项目
│   ├── src/main/java/com/soft/
│   │   ├── sys/
│   │   │   ├── controller/               # REST API 控制器（18 个）
│   │   │   ├── service/impl/             # 业务逻辑层
│   │   │   ├── mapper/                   # MyBatis Plus 映射器（17 个）
│   │   │   ├── entity/                   # 实体类（继承 BaseEntity）
│   │   │   ├── constants/                # 常量定义
│   │   │   ├── enums/                    # 枚举
│   │   │   ├── model/                    # dto / vo / request 数据模型
│   │   │   ├── core/
│   │   │   │   ├── conf/                 # 配置类（Security/Redis/RabbitMQ 等）
│   │   │   │   ├── filter/               # 过滤器（速率限制 → JWT 鉴权）
│   │   │   │   ├── aspect/               # AOP（审计日志、分布式锁）
│   │   │   │   └── handle/               # 异常/认证处理器
│   │   │   ├── websocket/                # WebSocket 子系统（注册模式）
│   │   │   ├── rabbitmq/                 # RabbitMQ 生产者/消费者
│   │   │   ├── quartz/                   # Quartz 定时任务
│   │   │   ├── async/                    # 异步操作（虚拟线程）
│   │   │   ├── schedule/                 # 定时调度任务
│   │   │   ├── resultapi/                # 统一响应 R<T>
│   │   │   ├── properties/               # 配置属性绑定
│   │   │   └── utils/                    # 工具类（AES/RSA、MinIO 等）
│   │   └── module/                       # Spring Modulith 扩展模块（当前为空）
│   ├── src/main/resources/
│   │   ├── mapper/sys/                   # MyBatis XML 映射
│   │   ├── db/migration/                 # Flyway 迁移脚本
│   │   └── application*.yml              # 配置文件
│   ├── CLAUDE.md                         # 后端 AI 辅助开发指南
│   ├── Dockerfile                        # 容器化构建
│   ├── script/start.sh                   # 优雅启停脚本
│   ├── .env                              # 敏感配置（已 gitignore）
│   └── pom.xml                           # Maven 依赖管理
│
├── imba-radish-ui/                       # 前端 Vue 项目
│   ├── src/
│   │   ├── api/                          # API 接口定义
│   │   ├── components/                   # 公共组件
│   │   ├── views/                        # 页面视图
│   │   ├── router/                       # 路由配置
│   │   ├── utils/                        # 工具函数
│   │   └── types/                        # TypeScript 类型定义
│   ├── CLAUDE.md                         # 前端 AI 辅助开发指南
│   └── package.json                      # npm 依赖管理
│
├── LICENSE                               # MIT 许可证
└── README.md                             # 项目说明文档
```

## 🚀 快速开始

### 环境要求
- JDK 21+
- Node.js 20+
- MySQL 8.0+
- PostgreSQL 16+（可选，从库）
- Redis 7.x
- MinIO 8.0+（可选）
- RabbitMQ 3.13+（可选）

### 后端启动

1. **进入后端目录**
```bash
cd imba-radish
```

2. **配置环境变量**

在 `imba-radish` 目录下维护 `.env` 文件（已 gitignore），配置数据库、Redis、RabbitMQ 等外部服务连接信息：
```env
DATASOURCE_MASTER_URL=jdbc:mysql://localhost:3306/radish?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
DATASOURCE_MASTER_USERNAME=root
DATASOURCE_MASTER_PASSWORD=your_password
REDIS_HOST=localhost
REDIS_PORT=6379
```

3. **运行项目**
```bash
# 编译（默认跳过测试）
mvn clean package

# 开发模式启动
mvn spring-boot:run

# 生产模式启动
java -jar target/imba-radish-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

后端默认绑定端口 **8081**。

### 前端启动

1. **进入前端目录**
```bash
cd imba-radish-ui
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

4. **构建生产版本**
```bash
npm run build
```

### 访问地址
- 前端页面: `http://localhost:8090`
- 后端 API: `http://localhost:8081`
- Knife4j 接口文档: `http://localhost:8081/doc.html`（仅开发环境）

## 🔧 配置说明

### 配置分层

| 配置来源 | 说明 | 优先级 |
| :--- | :--- | :--- |
| `.env` | 敏感配置（数据库密码、API Key 等）| 最高 |
| `application.yml` | 应用框架配置 | 中 |
| `application-dev.yml` | 开发环境覆盖 | 开发时使用 |
| `application-prod.yml` | 生产环境覆盖 | 生产时使用 |

### 关键配置项

| 前缀 | 说明 |
| :--- | :--- |
| `radish.*` | 验证码、Token、分布式锁、日志设置 |
| `websocket.*` | WebSocket 允许的来源域名 |
| `rate-limit.*` | 接口速率限制（Redis 滑动窗口） |
| `minio.*` | 对象存储连接 |
| `spring.security.permit.*` | 白名单 URL |

### 环境差异

| 配置项 | 开发环境 | 生产环境 |
| :--- | :--- | :--- |
| 数据库从库 | 无 | 无 |
| Redis DB | 15 | 14 |
| Knife4j/Swagger | 启用 | 禁用 |
| WebSocket 来源 | 宽松 | 限制 |
| 日志级别 | DEBUG | INFO |

## 🐳 Docker 部署

```bash
cd imba-radish
mvn clean package                 # 先构建 fat JAR
docker build -t imba-radish .     # Dockerfile 自动复制 src/main/resources 下的配置
```

镜像基于 `eclipse-temurin:21-jre-jammy`，G1GC 堆内存 256m，暴露 8081 端口。另有 `script/start.sh` 提供优雅启停管理。

## 🔌 WebSocket 架构

采用**注册模式**实现命令分发，新增命令无需修改路由代码：

```
WebSocketInterceptor (握手鉴权)
  → WebSocketHandler (路由分发)
    → WebSocketConcreteRegistry (@PostConstruct 自动扫描)
      → WebSocketConcreteHolder (命令→处理器映射)
        → WebSocketConcreteHandler<T> (具体业务处理)
```

**扩展方式**：实现 `WebSocketConcreteHandler<T>` → 注册 `WebSocketOrderEnum` → 自动发现。

### 心跳机制
- `WebSocketHeardHeatTimer` 每 30 秒调度一次
- 通过 `@Async` + 虚拟线程并发发送 PING
- `AsyncConfig` 配置 `newVirtualThreadPerTaskExecutor()` 作为异步执行器

## 📊 监控趋势降采样

CPU/内存趋势数据每 30 秒采集一次，通过 **LTTB (Largest-Triangle-Three-Buckets)** 算法降采样后返回前端。

| 时间范围 | 降采样点数 | 压缩比 |
| :--- | :--- | :--- |
| 15 分钟 | 20 | 3:1 |
| 1 小时 | 20 | 6:1 |
| 6 小时 | ~83 | ~9:1 |
| 1 天 | ~131 | ~22:1 |
| 7 天 | ~200 | ~101:1 |

- 采用**对数函数**动态计算点数：短时间保留细节，长时间保证真实度
- Controller 直接调用 `sysActuatorService.listXxxTrend(startTime, endTime)`，降采样在 Service 层透明完成

## 📝 开发规范

### 后端规范
- 使用 Spring Boot 标准分层架构
- 遵循 RESTful API 设计原则
- 使用 MyBatis Plus 进行数据库操作，Flyway 管理迁移
- 使用 Lombok 简化代码
- 统一异常处理和响应格式 `R<T>`
- 数据库版本变更必须通过 Flyway 迁移脚本

### 前端规范
- 使用 Vue 3 Composition API
- 使用 TypeScript 进行类型检查
- 使用 Element Plus 组件库
- 统一 API 调用方式
- 组件化开发

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
