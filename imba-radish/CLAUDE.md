# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build (tests are skipped by default in surefire config)
mvn clean package

# Run with dev profile (default)
mvn spring-boot:run

# Run with prod profile
java -jar target/imba-radish-1.0-SNAPSHOT.jar --spring.profiles.active=prod

# Run a single test class
mvn test -Dtest=YourTestClass -DfailIfNoTests=false

# Run tests (skip disabled, if you re-enable them)
mvn test
```

The application binds to port `8081` by default.

## Prerequisites

- Java 21
- External services declared in `.env` — MySQL (master), PostgreSQL (slave), Redis 15 (dev) / 14 (prod), RabbitMQ, Minio, DeepSeek API. `.env` is loaded at startup by `EnvLoaderHandler` before Spring context creation.

## Architecture Overview

**Tech Stack**: Spring Boot 3.5.14 / Java 21 / MyBatis Plus 3.5.9 / Spring Security / WebSocket / Redis / RabbitMQ / Minio / Flyway / Flowable 7.2 / Spring AI (DeepSeek) / Quartz / Spring Modulith 1.4.9

### Package Layout

| Package | Purpose |
|---|---|
| `com.soft.ImbaRadishApplication` | Entry point. Uses `EnvLoaderHandler` initializer to load `.env` → Spring Environment. Annotated `@EnableCaching` + `@EnableScheduling`. |
| `com.soft.sys.controller` | REST controllers (18 classes). One per domain entity. Modeled as a typical admin-CRUD system (users, roles, menus, permissions, depts, dicts, logs, files, announcements, dialogs, scheduler). |
| `com.soft.sys.service` / `impl` | Business logic via interface + `*ServiceImpl` extending MyBatis Plus `ServiceImpl<M, E>`. Includes `UsersDetailServiceImpl` for Spring Security user details. `SysActuatorServiceImpl` implements LTTB trend downsampling (see below). |
| `com.soft.sys.mapper` | MyBatis Plus mappers (18 interfaces), backed by `.xml` files under `src/main/resources/mapper/sys/`. |
| `com.soft.sys.entity` | DB entities (18 classes), all extend `BaseEntity` (provides `id`, `createBy/Time`, `updateBy/Time`, `delFlag` with auto-fill). Logic delete via MyBatis Plus `delFlag` column. |
| `com.soft.sys.constants` | Centralized constants: `BaseConstant`, `DictConstant`, `RabbitmqConstant`, `RedisConstant`, `RegexConstant`, `TokenConstant`, `WebSocketConstant`. |
| `com.soft.sys.enums` | Enumerations: `LogLevelEnum`, `LogModuleEnum`, `LogTypeEnum`, `QuartzIntervalEnum`, `ResultEnum`, `SecretKeyEnum`, `WebSocketOrderEnum`. |
| `com.soft.sys.model.dto` | Internal transfer objects (13 classes, including `rabbitmq/` sub-package for `EmailDto`, `GenerateFileHashDto`). |
| `com.soft.sys.model.vo` | Response view objects (40 classes) returned to frontend. |
| `com.soft.sys.model.request` | Inbound request bodies (33 classes). |
| `com.soft.sys.model.ctf` | Custom data structures (e.g., `MenuTree` tree interface). |
| `com.soft.sys.core.conf` | `@Configuration` classes (14): Security, MyBatis Plus, Redis, RabbitMQ, Minio, Knife4j, Quartz, Async, ThreadPool, WebSocket, Jackson, Captcha, WebClient, Transaction. |
| `com.soft.sys.core.filter` | Servlet filters: `AuthorizationVerifyFilter` (JWT token → Redis lookup → SecurityContext), `RateLimitFilter` (Redis sliding window). Filter chain order matters: RateLimit → Auth → business. |
| `com.soft.sys.core.aspect` | AOP: `SysLogAspect` (audit logging via `@SysLog`), `SysLockAspect` (distributed lock via `@SysLock`), `AccessControlAspect`. |
| `com.soft.sys.core.handle` | Exception/authentication/access-denied/logout handlers; `EnvLoaderHandler`; `ScheduleJobLoadHandler`. |
| `com.soft.sys.core.listener` | Application lifecycle listeners: `ApplicationShutdownListener` (cleanly closes WebSocket sessions on `ContextClosedEvent` at highest precedence). |
| `com.soft.sys.core.permission` | Permission evaluation: `CustomPermissionService` (custom permission logic for Spring Security). |
| `com.soft.sys.websocket` | WebSocket subsystem with registry pattern (see below). Sub-packages: `api/`, `handler/`, `interceptor/`, `receive/`, `registry/`, `send/`, `session/`. |
| `com.soft.sys.rabbitmq` | RabbitMQ producers (`EmailProduce`, `SysLogProduce`) and consumer (`MessageConsume`). |
| `com.soft.sys.quartz` | Quartz jobs (`ClearDeletedFile`, `NmapScanScheduler`, `UpdatePrimaryKey`) and listeners (`LoggingJobListener`, `QuartzAppender`). |
| `com.soft.sys.async` | Async operations: `FileUploadAsync` (background file upload processing), `SendPingAsync` (WebSocket PING via virtual threads). |
| `com.soft.sys.schedule` | Scheduled tasks: `ActuatorMetric` (system health data collection every 30s), `WebSocketHeardHeatTimer` (PING heartbeat every 30s via virtual threads). |
| `com.soft.sys.resultapi.R<T>` | Unified REST response: `{timestamp, code, msg, data, extra}`. Factory methods: `R.ok(data)`, `R.fail(msg)`. |
| `com.soft.sys.properties` | `@ConfigurationProperties` classes bound to structured YAML config under `radish.*`, `websocket.*` (`WebSocketProperty`), `rate-limit.*`, `minio.*`, `web-client.*`, `spring.security.permit.*`. |
| `com.soft.sys.utils` | AES/RSA encryption, Minio file ops, HTTP/Security/Response helpers. |
| `com.soft.module` | Spring Modulith `@ApplicationModule` extension module. Declares module dependencies on `sys::exception`, `sys::utils`, `sys::service`, `sys::entity`, `sys::websocket`. Contains `WebController` skeleton. Uses its own mapper logging category. |

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
| `handler/` | Concrete handler implementations: `ChatHandler`, `FileTransferHandler`, `FileTransferStartHandler`, `FileTransferContinueHandler`, `FileTransferOverHandler`, `ForceOfflineHandler`, `HeartbeatHandler`, `PushMessageHandler`, `RefreshTokenHandler` + `WebSocketHandler` (router) + `CustomWebSocketHandlerDecorator` |
| `interceptor/` | `WebSocketInterceptor` (handshake auth/origin validation) |
| `receive/` | Receive parameter POJOs: `AbstractRecParam`, `ChatRecParam`, `FileTransferContinueRecParam`, `FileTransferOverRecParam`, `FileTransferStartRecParam`, `FilesTransferRecParam`, `ForceOfflineRecParam`, `HeartbeatRecParam`, `PushMessageRecParam`, `RefreshTokenRecParm` |
| `registry/` | `WebSocketConcreteRegistry` — `@PostConstruct` auto-discovers all `WebSocketConcreteHandler<?>` beans |
| `send/` | Send parameter POJOs: `AbstractSendParams`, `ChatSendParams`, `FileTransferContinueSendParams`, `FileTransferStartSendParams`, `ForceOfflineSendParams`, `HeartBeatSendParams`, `PushMessageSendParams`, `RefreshTokenSendParam` |
| `session/` | `WebSocketSessionManager` — session cache backed by `TimedCache` (30s TTL, 10s prune interval), keyed by user ID |

- **`WebSocketConcreteHandler<T>`** — generic interface with `handle(session, message)` + `getOrder()` → `WebSocketOrderEnum`.
- **`WebSocketConcreteRegistry`** — on `@PostConstruct`, scans all `WebSocketConcreteHandler<?>` beans via Spring application context and registers them into `WebSocketConcreteHolder` keyed by order string.
- **WebSocket allowed origins** — configured via `WebSocketProperty` (`websocket.allowed-origins` in YAML), no longer hardcoded.
- To add a new WebSocket command: implement `WebSocketConcreteHandler<T>`, add an entry to `WebSocketOrderEnum`, and the registry auto-discovers it.

### Security Flow

- **Auth**: Stateless JWT via `AuthorizationVerifyFilter`. Token stored in Redis as `authorization:username:<token>` with configurable TTL.
- **Login**: `AuthService.authenticate()` — supports password (RSA decrypted) and email-captcha modes. Generates UUID token.
- **Password**: RSA-encrypted in transit → server decrypts with private key → BCrypt hashes for storage.
- **Concurrent-session control**: Same-user new login triggers `ForceOfflineHandler` via WebSocket to the old session. On application shutdown, `ApplicationShutdownListener` gracefully closes all WebSocket sessions at highest priority.
- **White-listed URLs**: Configured via `spring.security.permit.url` in YAML (e.g., `/auth/**`, `/ws/**`, `/secretKey/getPublicKey`).

### Database

- **MySQL** (master, primary) + **PostgreSQL** (slave) via `dynamic-datasource` MyBatis Plus plugin.
- **Flyway** migrations at `src/main/resources/db/migration/` (`V1__init_database.sql` through `V12__mod_202606091630.sql`). Latest migrations: V10 adds `memory_metaspace_max` to `sys_actuator`, V11 drops `memory_g1_old_max`, V12 adds `dict_type` column to `sys_dict_type`.
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
