# API 测试说明

本文档说明如何执行医生列表 API（GET /api/doctors）的测试，以及预期结果。测试用例脚本位于 `scripts/` 目录。

---

## 1. 环境准备

1. **MySQL 与 phpMyAdmin**  
   在项目根目录执行：
   ```bash
   docker-compose up -d
   ```
   等待 MySQL 健康检查通过（约 10～20 秒）。

2. **qa-service-user 后端**  
   在 `server/qa-service-user` 目录执行：
   ```bash
   ./mvnw spring-boot:run
   ```
   等待应用启动完成（看到 "Started QaServiceUserApplication" 等日志）。

3. **可选依赖**  
   - **jq**：若已安装，脚本会额外校验响应为 JSON 数组且不包含 `password` 字段。  
   - 无 jq 时，脚本仅校验 HTTP 状态码为 200 且响应体为以 `[` 开头的合法 JSON。

---

## 2. 执行测试脚本

在项目根目录执行：

```bash
bash scripts/test-doctors-api.sh
```

或：

```bash
chmod +x scripts/test-doctors-api.sh
./scripts/test-doctors-api.sh
```

---

## 3. 预期结果

- **HTTP 状态码**：200  
- **响应体**：JSON 数组，每项为医生对象，包含 `id`、`username`、`name`、`title`、`department`、`avatar`、`experience`、`specialties`、`isActive`，且**不包含** `password`。  
- 脚本输出应包含「全部检查通过」。  
- **验收时**：若前端医生页无数据而脚本通过，请确认数据库中**仅有一张**医生表（小写 `doctor`）且至少有 5 条记录；若存在两个 doctor 表，见 [docs/技术实现文档.md](docs/技术实现文档.md) 二、数据库设计排查。

---

## 4. 无 jq 时的验收方式

若环境未安装 jq：

- 脚本会校验：(1) HTTP 状态码为 200；(2) 响应体非空且以 `[` 开头（视为 JSON 数组）。  
- 是否包含 `password` 可人工查看响应内容，或安装 jq 后重新执行脚本。

---

## 5. 手动 curl 示例

```bash
# 仅检查状态码
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors
# 预期输出：200

# 查看完整响应（可选 jq 格式化）
curl -s http://localhost:8080/api/doctors | jq .
# 或无 jq：
curl -s http://localhost:8080/api/doctors
```

---

## 6. 其他 API 测试

qa-service-user 的其余接口（如 GET/POST/OPTIONS `/api/test/cors`）的说明与测试方式见 [docs/api.md](docs/api.md)。上述脚本仅覆盖 GET /api/doctors；其他接口可按 docs/api.md 中的示例使用 curl 自行验证。
