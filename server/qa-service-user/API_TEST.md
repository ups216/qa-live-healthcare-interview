# API 测试说明 — qa-service-user / Doctors API

## 前置条件

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| curl | ≥ 7.68 | HTTP 请求工具 |
| python3 | ≥ 3.8 | 用于 JSON 格式化与断言 |
| Docker & Docker Compose | ≥ 20 / v2 | 启动 MySQL 数据库 |
| JDK | 17+ | 运行 Spring Boot 后端 |
| Maven | 3.8+ | 构建后端项目 |

---

## 一、启动环境

### 1. 启动数据库（MySQL + phpMyAdmin）

在项目根目录执行：

```bash
docker compose up -d
```

等待 MySQL 健康检查通过（约 20–30 秒），可通过以下命令确认：

```bash
docker compose ps
# qa_mysql 状态应显示 healthy
```

数据库初始化 SQL（`sql/init.sql`）会在首次启动时自动执行，建库建表并插入 5 条医生数据。

> phpMyAdmin 管理界面访问地址：<http://localhost:8081>  
> 账号 `root` / 密码 `rootpassword`

---

### 2. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

服务默认监听 `http://localhost:8080`，Context Path 为 `/api`。

启动成功后可看到日志：

```
Started QaMedicalApplication in X.XXX seconds
```

---

## 二、执行测试脚本

### 快速运行（使用默认地址）

```bash
chmod +x test_doctors_api.sh
./test_doctors_api.sh
```

### 指定自定义 Base URL

```bash
BASE_URL=http://192.168.1.100:8080/api ./test_doctors_api.sh
```

---

## 三、测试用例说明

| 用例 ID | 接口 | 场景描述 | 期望 HTTP 状态码 |
|---------|------|----------|-----------------|
| TC-01 | `GET /doctors` | 查询全部医生列表，应返回 5 条记录 | 200 |
| TC-01-A | — | 断言响应体 `code` 字段为 200 | — |
| TC-01-B | — | 断言 `data` 数组长度为 5 | — |
| TC-02 | `GET /doctors?isActive=true` | 过滤在线医生，应返回 4 条 | 200 |
| TC-02-A | — | 断言响应体 `code` 字段为 200 | — |
| TC-02-B | — | 断言 `data` 数组长度为 4 | — |
| TC-02-C | — | 断言第一条记录 `isActive` 为 `true` | — |
| TC-03 | `GET /doctors?isActive=false` | 过滤离线医生，应返回 1 条 | 200 |
| TC-03-A | — | 断言响应体 `code` 字段为 200 | — |
| TC-03-B | — | 断言 `data` 数组长度为 1 | — |
| TC-03-C | — | 断言离线医生 `username` 为 `dr-liu-min` | — |
| TC-04 | `GET /doctors/doc001` | 按 ID 查询医生，验证字段内容 | 200 |
| TC-04-A | — | 断言 `data.id` 为 `doc001` | — |
| TC-04-B | — | 断言 `data.name` 为 `张伟医生` | — |
| TC-04-C | — | 断言 `data.department` 为 `心内科` | — |
| TC-04-D | — | 断言响应中不含 `password` 字段 | — |
| TC-05 | `GET /doctors/doc999` | 查询不存在的 ID，期望 404 | 404 |
| TC-06 | `GET /doctors/u/dr-li-na` | 按 username 查询，验证字段内容 | 200 |
| TC-06-A | — | 断言 `data.username` 正确 | — |
| TC-06-B | — | 断言 `data.department` 为 `儿科` | — |
| TC-06-C | — | 断言 `data.specialties` 为数组类型 | — |
| TC-07 | `GET /doctors/u/dr-unknown` | 查询不存在的 username，期望 404 | 404 |
| TC-08 | `GET /doctors` | 验证 `Content-Type: application/json` 响应头 | — |
| TC-09 | `GET /doctors` | 验证 CORS 响应头存在 | — |
| TC-10 | `GET /doctors?isActive=invalid` | 非法参数类型，期望 400 | 400 |

---

## 四、解读测试输出

脚本执行后每个用例输出如下格式：

```
──────────────────────────────────────────────────────
[TC-04] GET /doctors/doc001 — 按 ID 查询医生详情
  Expected HTTP: 200
  Actual   HTTP: 200
  Response Body:
    {
        "code": 200,
        "message": "success",
        "data": { ... }
    }
  ✓ PASS
  ✓ ASSERT PASS [TC-04-A] data.id 正确: ['data']['id'] == 'doc001'
```

脚本末尾打印汇总：

```
╔══════════════════════════════════════════════╗
║               测试结果汇总                    ║
╠══════════════════════════════════════════════╣
║  Total : 20
║  Pass  : 20
║  Fail  : 0
╚══════════════════════════════════════════════╝
```

脚本退出码：全部通过返回 `0`，有失败返回 `1`，可直接用于 CI/CD 流水线判断。

---

## 五、常见问题排查

| 现象 | 可能原因 | 解决方法 |
|------|----------|---------|
| `Connection refused` | 后端未启动 | 确认 `mvn spring-boot:run` 已运行 |
| TC-01-B 断言失败（数量不对） | 数据库初始化未执行 | 删除 volume 后重新启动：`docker compose down -v && docker compose up -d` |
| TC-09 CORS 失败 | 生产配置关闭了跨域 | 检查 `@CrossOrigin` 注解或 CORS Filter 配置 |
| TC-10 期望 400 但收到 200 | Spring 默认会忽略无法转换的参数 | 在 Controller 添加 `@Validated` 及参数校验注解 |
| python3 命令不存在 | 环境缺少 Python | `brew install python3` 或 `apt install python3` |
