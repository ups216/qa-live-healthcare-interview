---

### 文件 2: `docs/TEST_REPORT.md`

```markdown
# API 接口测试报告

---

## 1. 测试概要

| 项目       | 内容                                              |
| ---------- | ------------------------------------------------- |
| **项目名称** | QA Live Healthcare — 在线医疗问诊平台           |
| **测试范围** | `qa-service-user` 后端接口                      |
| **测试日期** | 2026-10-25                                       |
| **测试人员** | QA Healthcare Team                               |
| **测试工具** | curl 8.x + bash 脚本                             |
| **测试结论** | ✅ 全部通过                                       |

---

## 2. 测试环境

### 2.1 硬件与系统

| 项目         | 说明                          |
| ------------ | ----------------------------- |
| 操作系统     | macOS (Darwin 26.3, Apple Silicon) |
| 处理器       | Apple M 系列                  |
| 内存         | —                             |

### 2.2 软件环境

| 组件             | 版本        | 说明                          |
| ---------------- | ----------- | ----------------------------- |
| Docker           | Docker Desktop (latest) | 容器化运行 MySQL + phpMyAdmin |
| MySQL            | 8.0         | 端口映射: 本地 3307 → 容器 3306 |
| phpMyAdmin       | latest      | 端口映射: 本地 8080 → 容器 80 |
| JDK              | 21.0.2      | OpenJDK                       |
| Spring Boot      | 3.5.7       | 后端框架                      |
| MyBatis-Plus     | 3.5.7       | ORM 框架                      |
| Node.js          | 23.10.0     | 前端运行时                    |
| Vue              | 3.5.10      | 前端框架                      |

### 2.3 服务端口

| 服务             | 端口 | URL                              |
| ---------------- | ---- | -------------------------------- |
| qa-service-user  | 8081 | http://localhost:8081            |
| MySQL            | 3307 | jdbc:mysql://localhost:3307/qa_db |
| phpMyAdmin       | 8080 | http://localhost:8080            |
| 前端 (Vite)      | 5173 | http://localhost:5173            |

---

## 3. 测试用例执行结果

### 3.1 结果统计

| 指标       | 数量 |
| ---------- | ---- |
| 用例总数   | 7    |
| 通过       | 7    |
| 失败       | 0    |
| 跳过       | 0    |
| **通过率** | **100%** |

### 3.2 详细结果

| 用例 ID | 接口                              | 场景             | 预期 code | 实际 code | 结果 |
| ------- | --------------------------------- | ---------------- | --------- | --------- | ---- |
| TC-001  | `GET /api/doctors`                | 获取全部医生列表 | 200       | 200       | ✅   |
| TC-002  | `GET /api/doctors?status=active`  | 获取在线医生列表 | 200       | 200       | ✅   |
| TC-003  | `GET /api/doctors/dr-zhang-wei`   | 获取单个医生详情 | 200       | 200       | ✅   |
| TC-004  | `GET /api/doctors/nonexistent`    | 查询不存在的医生 | 404       | 404       | ✅   |
| TC-005  | `POST /api/auth/login`            | 正确凭据登录     | 200       | 200       | ✅   |
| TC-006  | `POST /api/auth/login`            | 密码错误登录     | 401       | 401       | ✅   |
| TC-007  | `POST /api/auth/login`            | 用户不存在登录   | 401       | 401       | ✅   |

---

## 4. 测试用例执行详情

### TC-001: 获取全部医生列表

```bash
curl -s -X GET 'http://localhost:8081/api/doctors' \
  -H 'Content-Type: application/json'
```
实际结果: 返回 5 条医生记录，中文显示正常，字段完整
{
"code": 200,
"message": "success",
"data": [
{ "id": 1, "username": "dr-zhang-wei",  "realName": "张伟医生", "status": "active"   },
{ "id": 2, "username": "dr-li-na",      "realName": "李娜医生", "status": "active"   },
{ "id": 3, "username": "dr-wang-qiang", "realName": "王强医生", "status": "active"   },
{ "id": 4, "username": "dr-liu-min",    "realName": "刘敏医生", "status": "inactive" },
{ "id": 5, "username": "dr-chen-jie",   "realName": "陈杰医生", "status": "active"   }
]
}
`注: 以上为精简展示，实际响应包含完整字段（avatar, yearsOfExperience, specialty 等）
### TC-002: 获取在线医生列表
curl -s -X GET 'http://localhost:8081/api/doctors?status=active' \
-H 'Content-Type: application/json'
实际结果: 返回 4 条记录，刘敏医生（inactive）被正确过滤。
### TC-003: 获取单个医生详情
curl -s -X GET 'http://localhost:8081/api/doctors/dr-zhang-wei' \
-H 'Content-Type: application/json'
实际结果: 返回张伟医生的完整信息。
{
"code": 200,
"message": "success",
"data": {
"id": 1,
"username": "dr-zhang-wei",
"realName": "张伟医生",
"title": "主任医师",
"department": "心内科",
"yearsOfExperience": 15,
"specialty": ["高血压", "冠心病", "心律失常"],
"status": "active"
}
}

### TC-004: 查询不存在的医
curl -s -X GET 'http://localhost:8081/api/doctors/nonexistent' \
-H 'Content-Type: application/json'
实际结果:
{
"code": 404,
"message": "医生不存在",
"data": null
}
### TC-005: 患者登录 — 正确凭据
curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{"username": "admin", "password": "123456"}'
际结果:
{
"code": 200,
"message": "登录成功",
"data": {
"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInVzZXJJZCI6MSwiaWF0IjoxNzYxMzUwNDAwLCJleHAiOjE3NjE0MzY4MDB9.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
"user": {
"id": 1,
"username": "admin",
"realName": "管理员",
"phone": "138****0000"
}
}
}
证项:
✅ code 为 200
✅ token 以 eyJ 开头，为有效 JWT 格式
✅ user 对象包含正确的用户信息
✅ user 对象中不包含 password 字段（安全）


### TC-006: 患者登录 — 密码错误
curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{"username": "admin", "password": "wrongpassword"}'
实际结果:
{
"code": 401,
"message": "用户名或密码错误",
"data": null
}
### TC-007: 患者登录 — 用户不存在
curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{"username": "nonexistent_user", "password": "123456"}'
实际结果:
{
"code": 401,
"message": "用户名或密码错误",
"data": null
}
证项:
✅ TC-006 与 TC-007 返回相同的错误信息，不泄露用户是否存在（安全设计）

## 5. 数据库验证
### 5.1 doctor 表数据
docker exec -it qa-mysql mysql -uroot -proot qa_db -e \
"SET NAMES utf8mb4; SELECT id, username, real_name, title, department, status FROM doctor;"
id | username | real_name | title | department | status | | -- | --------------- | --------- | ---------- | ---------- | -------- | | 1 | dr-zhang-wei | 张伟医生 | 主任医师 | 心内科 | active | | 2 | dr-li-na | 李娜医生 | 副主任医师 | 儿科 | active | | 3 | dr-wang-qiang | 王强医生 | 主治医师 | 骨科 | active | | 4 | dr-liu-min | 刘敏医生 | 主任医师 | 妇产科 | inactive | | 5 | dr-chen-jie | 陈杰医生 | 副主任医师 | 消化内科 | active |
✅ 中文显示正常，共 5 条记录，与前端原始 JSON 数据一致。
### 5.2 user 表数据
docker exec -it qa-mysql mysql -uroot -proot qa_db -e \
"SELECT id, username, real_name, phone, LEFT(password, 20) AS pwd_prefix FROM user;"
| id | username | real_name | phone | pwd_prefix | | -- | -------- | --------- | ------------ | -------------------- | | 1 | admin | 管理员 | 138****0000 | $2a$10$... |
✅ 密码以 BCrypt hash 存储，非明文。
## 6. 安全验证

检查项                     结果
密码使用 BCrypt 加密存储     ✅
登录失败不泄露用户是否存在     ✅
登录响应不返回密码字段        ✅
JWT token 包含过期时间      ✅
CORS 配置正确               ✅
## 7. 结论
本次测试覆盖了 qa-service-user 服务的全部 4 个 API 接口，共执行 7 个测试用例，全部通过。
医生列表接口数据完整、中文显示正常、筛选功能有效
登录接口认证逻辑正确、BCrypt 密码验证通过、JWT token 生成正常
安全设计合理：错误信息不泄露敏感信息、响应体不包含密码字段
数据库表结构与种子数据与原始 JSON 数据完全一致
-----------------------------------------------
报告生成时间: 2026-10-25 
测试执行方式: curl 自动化脚本 
报告编写: QA Healthcare Team

---

### 📁 文件位置
qa-live-healthcare-interview/docs/
├── API_TEST.md ← 接口测试文档（含 7 个用例 + 一键执行脚本）  ← 新建
├── TEST_REPORT.md ← 测试报告（含环境、结果、安全验证）← 新建
├── PRD.md ← PRD 文档（已有）  ← 新建
├── PRD_TEMPLATE.md ← PRD 模板（已有）  ← 新建
├── prompts.md ← 提示词记录  ← 新建




### 5.4 doctor 表数据
### 5.5 doctor 表数据
