# 患者登录功能 API 测试文档

## 文档信息

| 字段 | 内容 |
|------|------|
| 文档标题 | 患者登录功能 API 测试 |
| 版本 | v1.0 |
| 创建日期 | 2026-08-27 |
| 测试环境 | localhost |

---

## 1. 测试概述

### 1.1 测试范围
- 患者注册接口
- 患者登录接口
- 获取患者信息接口
- 医生登录接口（已有）
- 获取医生列表接口（已有）

### 1.2 前置条件
- MySQL 数据库运行正常（端口 3308）
- 后端服务运行正常（端口 8080）
- 数据库已初始化测试数据

### 1.3 测试数据

**患者测试账号：**
| 用户名 | 密码 | 姓名 | 手机号 |
|--------|------|------|--------|
| zhao-ming | 123456 | 赵明 | 138****1234 |
| sun-li | 123456 | 孙丽 | 139****5678 |
| zhou-jie | 123456 | 周杰 | 137****9012 |

**医生测试账号：**
| 用户名 | 密码 | 姓名 | 科室 |
|--------|------|------|------|
| dr-zhang-wei | 123456 | 张伟医生 | 心内科 |
| dr-li-na | 123456 | 李娜医生 | 儿科 |

---

## 2. 测试用例

### 2.1 患者注册接口

#### TC-001: 正常注册
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test-user-001",
    "password": "123456",
    "name": "Test User",
    "phone": "13800000001"
  }'
```

**预期响应：**
- 状态码：200
- 响应体包含：id, username, name, phone

---

#### TC-002: 用户名重复注册
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhao-ming",
    "password": "123456",
    "name": "Duplicate User",
    "phone": "13800000002"
  }'
```

**预期响应：**
- 状态码：400
- 响应体包含错误信息："用户名已存在"

---

#### TC-003: 缺少必填字段
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test-user-002",
    "password": "123456"
  }'
```

**预期响应：**
- 状态码：400 或 500
- 返回错误信息

---

#### TC-004: 用户名格式验证（前端）
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "password": "123456",
    "name": "Short Username",
    "phone": "13800000003"
  }'
```

**预期响应：**
- 后端可能接受（前端有验证，后端可选）

---

### 2.2 患者登录接口

#### TC-005: 正常登录
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhao-ming",
    "password": "123456"
  }'
```

**预期响应：**
- 状态码：200
- 响应体包含：id, username, name, phone, gender, birthday

---

#### TC-006: 错误密码登录
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhao-ming",
    "password": "wrong-password"
  }'
```

**预期响应：**
- 状态码：400
- 响应体包含错误信息："用户名或密码错误"

---

#### TC-007: 不存在的用户登录
**请求：**
```bash
curl -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistent-user",
    "password": "123456"
  }'
```

**预期响应：**
- 状态码：400
- 响应体包含错误信息："用户名或密码错误"

---

### 2.3 获取患者信息接口

#### TC-008: 正常获取患者信息
**请求：**
```bash
curl -X GET http://localhost:8080/api/patients/patient001
```

**预期响应：**
- 状态码：200
- 响应体包含：id, username, name, phone, gender, birthday

---

#### TC-009: 获取不存在的患者
**请求：**
```bash
curl -X GET http://localhost:8080/api/patients/nonexistent-id
```

**预期响应：**
- 状态码：404

---

### 2.4 医生登录接口

#### TC-010: 正常登录医生
**请求：**
```bash
curl -X GET http://localhost:8080/api/doctors/dr-zhang-wei
```

**预期响应：**
- 状态码：200
- 响应体包含医生信息

---

### 2.5 获取医生列表接口

#### TC-011: 获取所有医生
**请求：**
```bash
curl -X GET http://localhost:8080/api/doctors
```

**预期响应：**
- 状态码：200
- 响应体为数组，包含 5 个医生

---

#### TC-012: 获取在线医生
**请求：**
```bash
curl -X GET http://localhost:8080/api/doctors/active
```

**预期响应：**
- 状态码：200
- 响应体为数组，包含 4 个在线医生（isActive=true）

---

## 3. 测试执行流程

### 3.1 执行步骤

1. **检查服务状态**
   ```bash
   # 检查 MySQL
   docker ps | grep qa-mysql
   
   # 检查后端
   curl -s http://localhost:8080/api/doctors | head -50
   ```

2. **执行测试用例**
   - 按顺序执行 TC-001 到 TC-012
   - 记录每个测试的实际响应

3. **生成测试报告**
   - 记录测试结果（通过/失败）
   - 记录失败原因（如有）

### 3.2 测试报告模板

```
## 测试报告

### 测试环境
- 测试时间：YYYY-MM-DD HH:MM:SS
- 测试人员：AI Assistant

### 测试结果汇总
| 编号 | 测试场景 | 状态 | 备注 |
|------|----------|------|------|
| TC-001 | 正常注册 | PASS/FAIL | |
| TC-002 | 用户名重复注册 | PASS/FAIL | |
| ... | ... | ... | ... |

### 测试统计
- 总用例数：12
- 通过：X
- 失败：X
- 通过率：XX%

### 失败用例详情
（如有失败用例，记录详细信息）
```

---

## 4. AI 测试指导

### 4.1 AI 执行测试流程

1. **读取本文档**：理解测试用例和预期结果

2. **执行前置检查**：
   ```bash
   docker ps | grep qa-mysql
   curl -s http://localhost:8080/api/doctors | head -50
   ```

3. **逐个执行测试用例**：
   - 使用 Bash 工具执行 curl 命令
   - 记录响应状态码和响应体
   - 与预期结果对比

4. **生成测试报告**：
   - 创建测试报告文件
   - 记录测试结果和统计信息

### 4.2 测试报告输出位置

测试报告应保存在：`docs/API_TEST_REPORT.md`

---

## 附录

### A. 常用调试命令

```bash
# 查看后端日志
tail -f /path/to/backend.log

# 查看数据库数据
docker exec qa-mysql mysql -u root -proot123456 --default-character-set=utf8mb4 qa_healthcare -e "SELECT * FROM patients;"

# 查看 API 响应头
curl -I http://localhost:8080/api/doctors
```

### B. 错误码说明

| 状态码 | 含义 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
