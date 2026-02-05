# 医生列表 API 测试说明

本文档说明如何执行针对 **医生列表页所使用 API**（`GET /api/doctors`）的测试，与 README「测试要求」对应。

## 环境准备

1. **启动 MySQL 与 phpMyAdmin**（必须先于后端）
   ```bash
   # 在项目根目录执行
   docker-compose up -d
   ```
   等待 MySQL 健康检查通过（约 10 秒）。phpMyAdmin 访问：http://localhost:8082

2. **启动 qa-service-user**
   ```bash
   cd server/qa-service-user
   ./mvnw spring-boot:run
   ```
   服务就绪后监听 http://localhost:8080

## 执行测试脚本

在项目根目录执行：

```bash
bash scripts/test-doctor-api.sh
```

或（无 bash 时）：

```bash
sh scripts/test-doctor-api.sh
```

**可选**：脚本内默认 `BASE_URL=http://localhost:8080`，可覆盖：
```bash
BASE_URL=http://127.0.0.1:8080 bash scripts/test-doctor-api.sh
```

## 预期结果

- HTTP 状态码：**200**
- 响应 body：JSON 数组，每项包含 `id`、`username`、`name`、`title`、`department`、`avatar`、`experience`、`specialties`、`isActive`，**不包含** `password`
- 若已执行过 `data.sql`，数组长度至少为 5（与原有 doctor-user-list.json 等价）

## 无 jq 时的验收

若环境未安装 `jq`，脚本仍会校验：
1. HTTP 状态码为 200  
2. 响应 body 非空  

可再人工打开响应确认为 JSON 数组且结构正确。安装 jq 后重新执行可做完整校验（可选）：
- macOS: `brew install jq`
- Linux: `apt install jq` / `yum install jq`

## 手动 curl 示例

```bash
# 仅查看状态码
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors

# 查看完整响应（格式化需 jq）
curl -s http://localhost:8080/api/doctors | jq .
```

更多 API 说明与测试方式见 [docs/api.md](docs/api.md)。
