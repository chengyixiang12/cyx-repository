# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Module Layout

多模块 Maven 工程，根 `pom.xml` 是**纯 BOM + pluginManagement 父 POM**（不含任何实际依赖）。

| Module | Type | Port | Purpose |
|---|---|---|---|
| `radish-common` | 库 | — | 公共基础件：`R`、异常、常量、枚举、工具、注解。包名 `com.soft.common.*` |
| `radish-nacos` | 库 | — | Nacos 注册/配置中心接入封装 |
| `radish-gateway` | 可执行 | 8080 | API 网关（Gateway Server **WebMVC**） |
| `radish-system` | 可执行 | 8082 | 系统设置服务（阶段一为空骨架） |
| `radish-business` | 可执行 | 8083 | 业务服务（阶段一为空骨架） |
| `radish-monolithic` | 可执行 | 8081 | 原单体，保留可独立运行，当前承载全部业务代码 |

**模块约束（改动时务必遵守）**：

- 库模块（`radish-common` / `radish-nacos`）**绝不**声明 `spring-boot-maven-plugin`，否则 repackage 会因找不到主类而构建失败。
- 各服务启动类置于 `com.soft` 包，但**必须用 `scanBasePackages` 收窄扫描范围**。`radish-common` 中的 `HttpUtil` 是 `@Component` 且构造器注入 `WebClient`，默认全包扫描会让未引入 webflux 的模块（如网关）启动即 `NoClassDefFoundError`。
- `GlobalExceptionHandler` 位于 `com.soft.common.handle`，**不得引用任何 Spring Security 异常类型**；认证/授权异常已拆到 `com.soft.sys.core.handle.SecurityExceptionHandler`。否则无 spring-security 的模块加载它即崩。
- `radish-common` 的框架依赖一律 `<optional>true</optional>`，防止向使用方传递 webflux 等。

## Build & Run Commands

```bash
# 全量构建（tests 默认跳过）
mvn clean package

# 只构建某模块及其依赖
mvn -pl radish-monolithic -am clean package

# 运行单体（dev profile 默认）
mvn -pl radish-monolithic spring-boot:run
# 或直接跑 fat jar
java -jar radish-monolithic/target/radish-monolithic-1.0-SNAPSHOT.jar

# 运行网关
java -jar radish-gateway/target/radish-gateway-1.0-SNAPSHOT.jar

# prod profile
java -jar radish-monolithic/target/radish-monolithic-1.0-SNAPSHOT.jar --spring.profiles.active=prod

# 跑单个测试类
mvn test -Dtest=YourTestClass -DfailIfNoTests=false
```

端口分配：monolithic `8081`、gateway `8080`、system `8082`、business `8083`。

## Prerequisites

- Java 21
- Maven 3.6.3+（Spring Boot 4 的最低要求；多模块构建建议 3.9.x）
- External services declared in `.env` — MySQL (master), PostgreSQL (slave), Redis 15 (dev) / 14 (prod), RabbitMQ, Minio, DeepSeek API.

  ⚠️ `.env` 必须位于**启动进程的工作目录**下：`EnvLoaderHandler` 用相对路径 `new FileInputStream(".env")` 读取，且文件缺失会直接抛异常终止启动。从模块目录启动时，该目录下需有 `.env`（`radish-monolithic/` 下已备一份）。
- 微服务模式还需 Nacos 3.x。阶段一通过 `NACOS_ENABLED=false` 默认关闭，各服务可脱离 Nacos 独立启动。

## Architecture Overview

**Tech Stack**: Spring Boot 4.0.7 / Java 21 / MyBatis Plus 3.5.16 / Spring Security / WebSocket / Redis / RabbitMQ / Minio / Flyway / Flowable 7.2 / Spring AI 2.0.0 (DeepSeek) / Quartz / Spring Modulith 1.4.9

### Package Layout

| Package | Purpose |
|---|---|
| `com.soft.ImbaRadishApplication` | Entry point. Uses `EnvLoaderHandler` initializer to load `.env` → Spring Environment. Annotated `@EnableCaching` + `@EnableScheduling`. |
| `com.soft.sys.controller` | REST controllers (18 classes). Modeled as a typical admin-CRUD system (users, roles, menus, permissions, depts, dicts, logs, files, announcements, dialogs, schedule jobs/records, secret keys), plus `AuthController` (login) and `MessageController` (captchas/messages). |
| `com.soft.sys.service` / `impl` | Business logic via interface + `*ServiceImpl` extending MyBatis Plus `ServiceImpl<M, E>`. Includes `UsersDetailServiceImpl` for Spring Security user details, `SecretKeyService` for RSA key-pair management, and `SysActuatorServiceImpl` implementing LTTB trend downsampling (see below). |
| `com.soft.sys.mapper` | MyBatis Plus mappers (17 interfaces), backed by `.xml` files under `src/main/resources/mapper/sys/`. |
| `com.soft.sys.entity` | DB entities (17 business classes + `BaseEntity`), all extend `BaseEntity` (provides `id`, `createBy/Time`, `updateBy/Time`, `delFlag` with auto-fill). Logic delete via MyBatis Plus `delFlag` column. |
| `com.soft.common.constants` | **已迁至 radish-common**。7 个常量类：`BaseConstant`, `DictConstant`, `RabbitmqConstant`, `RedisConstant`, `RegexConstant`, `TokenConstant`, `WebSocketConstant`. |
| `com.soft.sys.enums` | 部分迁至 `com.soft.common.enums`：`ResultEnum`, `LogModuleEnum`, `LogTypeEnum`。仍留此处：`KeyTypeEnum`, `LogLevelEnum`, `QuartzIntervalEnum`, `WebSocketOrderEnum`. |
| `com.soft.sys.model.dto` | Internal transfer objects (15 classes, including `rabbitmq/` sub-package for `EmailDTO`, `GenerateFileHashDTO`). |
| `com.soft.sys.model.vo` | Response view objects (43 classes) returned to frontend. |
| `com.soft.sys.model.request` | Inbound request bodies (46 classes). |
| `com.soft.common.model.ctf` | **已迁至 radish-common**。`MenuTree` 树接口。 |
| `com.soft.sys.core.conf` | `@Configuration` classes (14): Security, MyBatis Plus, Redis, RabbitMQ, Minio, Knife4j, Quartz, Async, ThreadPool, WebSocket, Jackson, Captcha, WebClient, Transaction. |
| `com.soft.sys.core.filter` | Servlet filters: `AuthorizationVerifyFilter` (JWT token → Redis lookup → SecurityContext), `RateLimitFilter` (Redis sliding window). Filter chain order matters: RateLimit → Auth → business. |
| `com.soft.sys.core.aspect` | AOP: `SysLogAspect` (audit logging via `@SysLog`), `SysLockAspect` (distributed lock via `@SysLock`), `AccessControlAspect`. |
| `com.soft.sys.core.handle` | `AuthenticationHandler`, `CustomAccessDeniedHandler`, `LogoutAfterSuccessHandler`, `ScheduleJobLoadHandler`, `SecurityExceptionHandler`。注：`EnvLoaderHandler` 与 `GlobalExceptionHandler` 已迁至 `com.soft.common.handle`；认证授权异常处理从后者拆出，见 `SecurityExceptionHandler`。 |
| `com.soft.common.annotation` | **已迁至 radish-common**。`AccessControl`, `LogIgnore`, `SysLock`, `SysLog`. |
| `com.soft.sys.core.listener` | Application lifecycle listeners: `ApplicationShutdownListener` (cleanly closes WebSocket sessions on `ContextClosedEvent` at highest precedence). |
| `com.soft.sys.core.permission` | Permission evaluation: `CustomPermissionService` (custom permission logic for Spring Security). |
| `com.soft.sys.websocket` | WebSocket subsystem with registry pattern (see below). Sub-packages: `api/`, `handler/`, `interceptor/`, `receive/`, `registry/`, `send/`, `session/`. |
| `com.soft.sys.rabbitmq` | RabbitMQ producers (`EmailProduce`, `SysLogProduce`) and consumer (`MessageConsume`). |
| `com.soft.sys.quartz` | Quartz jobs (`ClearDeletedFile`, `NmapScanScheduler`, `UpdatePrimaryKey`) and listeners (`LoggingJobListener`, `QuartzAppender`). |
| `com.soft.sys.async` | Async operations: `FileUploadAsync` (background file upload processing), `SendPingAsync` (WebSocket PING via virtual threads). |
| `com.soft.sys.schedule` | Scheduled tasks: `ActuatorMetric` (system health data collection every 30s), `WebSocketHeardHeatTimer` (PING heartbeat every 30s via virtual threads). |
| `com.soft.common.resultapi.R<T>` | **已迁至 radish-common**。Unified REST response: `{timestamp, code, msg, data, extra}`. Factory methods: `R.ok(data)`, `R.fail(msg)`. |
| `com.soft.common.exception` | **已迁至 radish-common**。`GlobalException`. |
| `com.soft.sys.properties` | `@ConfigurationProperties` classes bound to structured YAML config under `radish.*`, `websocket.*` (`WebSocketProperty`), `rate-limit.*`, `minio.*`, `web-client.*`, `spring.security.permit.*`. |
| `com.soft.common.utils` | **部分已迁至 radish-common**：`AESUtil`, `RSAUtil`, `CommonUtil`, `ResponseUtil`, `HttpUtil`。仍留 `com.soft.sys.utils`：`SecurityUtil`（依赖 SecurityContext + UserDTO）、`MinioUtil`（依赖 MinioClient）。 |
| `com.soft.module` | Spring Modulith `@ApplicationModule` extension module. `allowedDependencies` 现为 `sys::utils`, `sys::service`, `sys::entity`, `sys::websocket`（`sys::exception` 与 `sys::resultapi` 已随 radish-common 抽离）。当前只有高德天气第三方 API 封装与 `TestController`，待阶段二迁入 `radish-business`。 |

### WebSocket Architecture (Registry Pattern)

```
WebSocketInterceptor (handshake)
  → WebSocketHandler (routes by WebSocketOrderEnum string)
    → WebSocketConcreteRegistry (@PostConstruct scans all WebSocketConcreteHandler<?> beans)
      → WebSocketConcreteHolder (in-memory map: order string → handler)
        → WebSocketConcreteHandler<T> (one per order type)
```

**Package structure:**

| Sub-package | Content |
|---|---|
| `api/` | `WebSocketConcreteHandler<T>` interface, `WebSocketConcreteHolder` static registry map |
| `handler/` | Concrete handler implementations: `ChatHandler`, `FileTransferHandler`, `FileTransferStartHandler`, `FileTransferContinueHandler`, `FileTransferOverHandler`, `ForceOfflineHandler`, `PingHandler`, `PongHandler`, `PushMessageHandler`, `RefreshTokenHandler` + `WebSocketHandler` (router) + `CustomWebSocketHandlerDecorator` |
| `interceptor/` | `WebSocketInterceptor` (handshake auth/origin validation) |
| `receive/` | Request POJOs: `WebSocketRequest` (interface), `AbstractWebSocketRequest`, `ChatRequest`, `FileTransferContinueRequest`, `FileTransferOverRequest`, `FileTransferStartRequest`, `FilesTransferRequest`, `ForceOfflineRequest`, `HeartbeatRequest`, `PushMessageRequest`, `RefreshTokenRequest` |
| `registry/` | `WebSocketConcreteRegistry` — `@PostConstruct` auto-discovers all `WebSocketConcreteHandler<?>` beans |
| `send/` | Response POJOs: `WebSocketResponse` (interface), `AbstractWebSocketResponse`, `ChatResponse`, `FileTransferContinueResponse`, `FileTransferStartResponse`, `ForceOfflineResponse`, `HeartBeatResponse`, `PingResponse`, `PushMessageResponse`, `RefreshTokenResponse` |
| `session/` | `WebSocketSessionManager` — session cache backed by `TimedCache` (30s TTL, 10s prune interval), keyed by user ID |

- **`WebSocketConcreteHandler<T>`** — generic interface with `handle(session, message)` + `getOrder()` → `WebSocketOrderEnum`.
- **`WebSocketConcreteRegistry`** — on `@PostConstruct`, scans all `WebSocketConcreteHandler<?>` beans via Spring application context and registers them into `WebSocketConcreteHolder` keyed by order string.
- **WebSocket allowed origins** — configured via `WebSocketProperty` (`websocket.allowed-origins` in YAML), no longer hardcoded.
- To add a new WebSocket command: implement `WebSocketConcreteHandler<T>`, add an entry to `WebSocketOrderEnum`, and the registry auto-discovers it.

### Secret Key Management

- `sys_secret_key` 表存储 RSA 密钥对,由 `SecretKeyService` / `SecretKeyServiceImpl` 提供 `getPublicKey` / `getPrivateKey` / `generateKey` / `getSecretKeyList`。
- `KeyTypeEnum` 区分密钥用途(当前仅 `USER_PASSWORD_KEY`);登录/用户模块按类型取出私钥解密前端 RSA 加密的密码。
- 列表接口(`SysSecretKeyVO`)对公钥/私钥做脱敏显示。

### Security Flow

- **Auth**: Stateless JWT via `AuthorizationVerifyFilter`. Token stored in Redis as `authorization:username:<token>` with configurable TTL.
- **Login**: `AuthService.authenticate()` — supports password (RSA decrypted) and email-captcha modes. Generates UUID token.
- **Password**: RSA-encrypted in transit → server decrypts with the private key loaded from `sys_secret_key` via `SecretKeyService.getPrivateKey(KeyTypeEnum.USER_PASSWORD_KEY)` → BCrypt hashes for storage.
- **Concurrent-session control**: Same-user new login triggers `ForceOfflineHandler` via WebSocket to the old session. On application shutdown, `ApplicationShutdownListener` gracefully closes all WebSocket sessions at highest priority.
- **White-listed URLs**: Configured via `spring.security.permit.url` in YAML (e.g., `/auth/**`, `/ws/**`, `/secretKey/getPublicKey`).

### Database

- **MySQL** (master, primary) + **PostgreSQL** (slave) via `dynamic-datasource` MyBatis Plus plugin.
- **Flyway** migrations at `src/main/resources/db/migration/`. 当前为单个基线迁移 `V1__init_database.sql`(含全部表结构);后续 schema 变更以新版本号追加。
- MyBatis Plus with logic delete (`delFlag` = 0/1), auto-fill timestamps, and `@TableId(assign_id)`.
- Schema changes use Flyway for version-controlled migrations; no JPA/Hibernate DDL auto-generation.

### Actuator Trend Downsampling

CPU/内存趋势数据（30秒采集一次）通过 **LTTB (Largest-Triangle-Three-Buckets)** 算法降采样后返回前端。

- **动态点数** — 根据时间范围对数增长：1小时→20点，7天→~200点（`SysActuatorServiceImpl.calculateMaxPoints`）
- **下限 20 / 上限 200** — 兼顾短时间精度和长时间真实度
- **降采样在 Service 层完成** — Controller 直接调用 `sysActuatorService.listXxxTrend(startTime, endTime)`，无需关心底层实现
- 算法逻辑位于 `SysActuatorServiceImpl` 的私有方法 `downsample()` + `triangleArea()`

### Configuration Patterns

- `radish.*` — captcha, graphics, token, lock, log settings in YAML.
- `websocket.*` — allowed origins via `WebSocketProperty`.
- `.env` file at project root — sensitive values injected into Spring Environment via `EnvLoaderHandler`.
- Dev vs. Prod profiles differ in: database (prod has no slave), Redis DB index (15 vs 14), Knife4j/Swagger (disabled in prod), temp paths, file locations, WebSocket allowed origins, logging levels.

## Docker Deployment

```bash
docker build -t imba-radish .
# Requires config/application.yml and config/application-prod.yml at build context
# Dockerfile uses eclipse-temurin:21-jre-jammy, G1GC heap 256m, exposes 8081
```

There is also a `script/start.sh` that manages graceful shutdown and restart.
