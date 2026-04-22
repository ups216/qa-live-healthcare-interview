# QA Live Healthcare API 接口测试文档

---

## 1. 概述

本文档描述 QA Live Healthcare 系统的接口测试流程，包括获取医生列表和患者登录两个核心接口的测试规范、测试用例及测试报告模板。

**测试环境：**
- 后端服务地址：`http://localhost:8080`
- 数据库：MySQL (localhost:3306)

---

## 2. 测试前提条件

### 2.1 服务启动

测试前请确保以下服务已启动：

```bash
# 1. 启动 MySQL 数据库 (docker-compose)
cd docs
docker-compose up -d

# 2. 启动后端服务 (qa-service-user)
cd server/qa-service-user
mvn spring-boot:run
# 或使用 IDE 运行
```

### 2.2 验证服务状态

```bash
# 验证后端服务
curl http://localhost:8080/api/doctors
```

---

## 3. 测试接口清单

| 序号 | 接口名称 | 请求方式 | 路径 | 说明 |
|------|---------|---------|------|------|
| 1 | 获取医生列表 | GET | `/api/doctors` | 获取所有在职医生信息 |
| 2 | 患者登录 | POST | `/api/patients/login` | 用户名密码验证登录 |

---

## 4. 接口详细说明

### 4.1 获取医生列表

**请求：**

```bash
curl -X GET "http://localhost:8080/api/doctors" \
  -H "Content-Type: application/json"
```

**成功响应 (200)：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "username": "doctor001",
      "name": "李医生",
      "department": "内科",
      "title": "主任医师",
      "specialties": ["高血压", "糖尿病"],
      "avatar": "https://...",
      "isActive": true
    }
  ]
}
```

### 4.2 患者登录

**请求：**

```bash
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "password": "123456"
  }'
```

**成功响应 (200)：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "patient": {
      "id": "patient001",
      "username": "patient001",
      "name": "赵明",
      "phone": "138****1234",
      "gender": "男"
    }
  }
}
```

**失败响应 (401)：**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

## 5. 测试用例

### 5.1 获取医生列表测试用例

| 用例编号 | 用例描述 | 前置条件 | 测试步骤 | 预期结果 |
|---------|---------|---------|---------|---------|
| TC-DL-01 | 获取所有医生列表 | 数据库有医生数据 | GET /api/doctors | 返回200，data包含医生数组 |

### 5.2 患者登录测试用例

| 用例编号 | 用例描述 | 前置条件 | 测试步骤 | 预期结果 |
|---------|---------|---------|---------|---------|
| TC-PL-01 | 正确用户名密码登录 | 用户名patient001，密码123456 | 输入正确凭证点击登录 | 返回200，包含token和patient信息 |
| TC-PL-02 | 错误密码登录 | 用户名patient001，密码错误 | 输入错误密码 | 返回401，message为"用户名或密码错误" |
| TC-PL-03 | 不存在用户登录 | 用户名不存在 | 输入不存在用户名 | 返回401，message为"用户名或密码错误" |
| TC-PL-04 | 空用户名登录 | 用户名为空 | 不输入用户名直接登录 | 返回400或拒绝请求 |
| TC-PL-05 | 空密码登录 | 密码为空 | 不输入密码直接登录 | 返回400或拒绝请求 |

### 5.3 测试账号

| 用户名 | 密码 | 姓名 |
|--------|------|------|
| patient001 | 123456 | 赵明 |
| patient002 | 123456 | 孙丽 |
| patient003 | 123456 | 周杰 |
| patient004 | 123456 | 吴芳 |
| patient005 | 123456 | 郑浩 |

---

## 6. 测试执行

### 6.1 自动测试脚本

已提供自动化测试脚本，可快速执行所有测试用例：

**Windows 用户：**
```bash
docs\test-api.bat
```

**Linux/Mac 用户：**
```bash
bash docs/test-api.sh
```

### 6.2 手动测试命令

**获取医生列表：**
```bash
curl -X GET "http://localhost:8080/api/doctors"
```

**登录测试 (正确凭证)：**
```bash
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"123456"}'
```

**登录测试 (错误密码)：**
```bash
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"wrongpassword"}'
```

**登录测试 (不存在用户)：**
```bash
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"nonexist","password":"123456"}'
```

---

## 7. 测试报告模板

### 7.1 报告格式

```markdown
# API 接口测试报告

**测试时间：** YYYY-MM-DD HH:MM:SS
**测试人员：** [姓名]
**测试环境：** localhost:8080

---

## 测试概要

| 项目 | 数量 |
|------|------|
| 总用例数 | X |
| 通过数 | X |
| 失败数 | X |
| 通过率 | X% |

---

## 详细结果

### 1. 获取医生列表接口

| 用例编号 | 用例描述 | 状态 | 实际结果 | 备注 |
|---------|---------|------|---------|------|
| TC-DL-01 | 获取所有医生列表 | ✅ 通过 / ❌ 失败 | [实际响应] | - |

### 2. 患者登录接口

| 用例编号 | 用例描述 | 状态 | 实际结果 | 备注 |
|---------|---------|------|---------|------|
| TC-PL-01 | 正确用户名密码登录 | ✅ 通过 / ❌ 失败 | [实际响应] | - |
| TC-PL-02 | 错误密码登录 | ✅ 通过 / ❌ 失败 | [实际响应] | - |
| TC-PL-03 | 不存在用户登录 | ✅ 通过 / ❌ 失败 | [实际响应] | - |
| TC-PL-04 | 空用户名登录 | ✅ 通过 / ❌ 失败 | [实际响应] | - |
| TC-PL-05 | 空密码登录 | ✅ 通过 / ❌ 失败 | [实际响应] | - |

---

## 缺陷记录

| 缺陷编号 | 严重程度 | 描述 | 复现步骤 | 预期结果 | 实际结果 |
|---------|---------|------|---------|---------|---------|
| - | - | - | - | - | - |

---

## 结论

[测试结论说明]

---

**报告生成时间：** YYYY-MM-DD HH:MM:SS
```

---

## 8. AI 测试执行指南

AI 助手可按以下步骤完成测试并生成报告：

1. **确认环境**：检查 `localhost:8080` 后端服务是否可用
2. **执行测试**：按顺序执行上述测试用例的 curl 命令
3. **记录结果**：记录每个测试用例的实际响应和HTTP状态码
4. **对比预期**：将实际结果与预期结果进行对比
5. **生成报告**：根据模板生成测试报告

---

**文档版本：** V1.0.0
**最后更新：** 2026-04-22
