# Cyx Repository - imba-radish

一个基于 Spring Boot 3 + Vue 3 构建的现代化后台管理系统，提供完整的用户管理、权限控制、系统监控等功能。

## 🌐 项目地址

- GitHub: [https://github.com/chengyixiang12/cyx-repository/tree/for-update](https://github.com/chengyixiang12/cyx-repository/tree/for-update)

## 🛠️ 技术栈

### 后端技术栈
| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Java | 17 | 编程语言 |
| Spring Boot | 3.4.6 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0.31 | 关系型数据库 |
| Redis | - | 缓存数据库 |
| MinIO | 8.5.2 | 对象存储 |
| RabbitMQ | - | 消息队列 |
| Spring AI | 1.1.0 | AI 集成 |
| Flowable | 7.2.0 | 工作流引擎 |
| Quartz | - | 定时任务 |

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

### 数据管理
- **字典管理**：字典类型和字典数据管理
- **文件管理**：文件上传、下载、预览、删除
- **在线磁盘**：个人文件存储空间

### 系统监控
- **操作日志**：记录所有操作行为
- **系统监控**：Actuator 健康检查指标
- **定时任务**：Quartz 任务管理和执行记录

### 消息通信
- **实时聊天**：WebSocket 即时通讯
- **消息推送**：系统通知推送

### AI 功能
- **AI 对话**：集成 DeepSeek 模型的智能问答
- **对话历史**：记录和管理对话记录

## 📁 项目结构

```
cyx-repository/
├── imba-radish/                    # 后端 Spring Boot 项目
│   ├── src/main/java/com/soft/
│   │   ├── base/                   # 基础模块
│   │   │   ├── controller/         # REST API 控制器
│   │   │   ├── service/            # 业务逻辑层
│   │   │   ├── mapper/             # 数据访问层
│   │   │   ├── entity/             # 实体类
│   │   │   ├── core/               # 核心配置
│   │   │   ├── websocket/          # WebSocket 通信
│   │   │   └── utils/              # 工具类
│   │   └── module/                 # 业务模块
│   ├── src/main/resources/         # 配置文件
│   └── pom.xml                     # Maven 依赖管理
├── imba-radish-ui/                 # 前端 Vue 项目
│   ├── src/
│   │   ├── api/                    # API 接口定义
│   │   ├── components/             # 公共组件
│   │   ├── views/                  # 页面视图
│   │   ├── router/                 # 路由配置
│   │   ├── utils/                  # 工具函数
│   │   └── types/                  # TypeScript 类型定义
│   └── package.json                # npm 依赖管理
└── README.md                       # 项目说明文档
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 20+
- MySQL 8.0+
- Redis 6.0+
- MinIO 8.0+（可选）
- RabbitMQ 3.9+（可选）

### 后端启动

1. **进入后端目录**
```bash
cd imba-radish
```

2. **配置数据库**

修改 `src/main/resources/application.yml` 配置数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

3. **运行项目**

使用 Maven 运行：
```bash
mvn spring-boot:run
```

或打包后运行：
```bash
mvn clean package
java -jar target/imba-radish-1.0-SNAPSHOT.jar
```

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
- 前端页面: `http://localhost:5173`
- 后端 API: `http://localhost:8080`
- Swagger 文档: `http://localhost:8080/doc.html`

## 🔧 配置说明

### 后端配置文件
主要配置项位于 `src/main/resources/application.yml`：

| 配置项 | 说明 | 默认值 |
| :--- | :--- | :--- |
| server.port | 服务端口 | 8080 |
| spring.datasource | 数据库连接 | - |
| spring.redis | Redis 连接 | localhost:6379 |
| spring.ai | AI 模型配置 | - |
| minio | 对象存储配置 | - |

### 前端配置文件
前端配置位于 `src/utils/http.ts` 和 `src/common/global-config.ts`。

## 🔐 安全特性

- JWT 令牌认证
- RSA 非对称加密
- 接口访问频率限制
- 细粒度权限控制
- 操作日志记录

## 📝 开发规范

### 后端规范
- 使用 Spring Boot 标准分层架构
- 遵循 RESTful API 设计原则
- 使用 MyBatis Plus 进行数据库操作
- 使用 Lombok 简化代码
- 统一异常处理和响应格式

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

**Powered by Spring Boot 3 & Vue 3** 🚀