# 患者登录 API 文档

## 概述

本文档描述了患者登录功能的API接口，包括用户认证和用户信息获取。

**基础URL**: `http://localhost:8080/api/v1`

---

## API 接口列表

### 1. 患者登录

**接口地址**: `POST /patients/login`

**描述**: 患者用户使用用户名和密码进行登录，成功后返回JWT Token和用户信息。

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "patient001",
  "password": "Patient123"
}
```

**请求参数说明**:

| 参数名 | 类型 | 必填 | 说明 | 校验规则 |
|-------|------|------|------|---------|
| username | String | 是 | 用户名 | 长度3-50，只能包含字母、数字、下划线 |
| password | String | 是 | 密码 | 长度8-128，必须包含字母和数字 |

**成功响应 (200)**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "username": "patient001",
      "email": "patient001@example.com",
      "phone": "13800138001"
    }
  }
}
```

**错误响应**:

| HTTP状态码 | 响应示例 | 说明 |
|-----------|---------|------|
| 401 | `{"code": 401, "message": "用户名或密码错误", "data": null}` | 用户名或密码错误 |
| 423 | `{"code": 423, "message": "账户已被锁定，请稍后重试", "data": null}` | 账户已被锁定（连续5次失败） |
| 403 | `{"code": 403, "message": "账户未激活", "data": null}` | 账户未激活 |
| 400 | `{"code": 400, "message": "参数校验失败", "data": null}` | 请求参数格式错误 |

**业务逻辑**:
1. 验证请求参数格式
2. 查询数据库中是否存在该用户名
3. 检查账户是否被锁定
4. 验证密码是否正确（BCrypt）
5. 如果验证失败，增加失败计数并可能锁定账户（连续5次失败锁定30分钟）
6. 如果验证成功，重置失败计数并生成JWT Token
7. 返回Token和用户信息

**测试示例**:
```bash
curl -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"Patient123"}'
```

---

### 2. 获取当前用户信息

**接口地址**: `GET /patients/me`

**描述**: 获取当前登录患者的信息。

**请求头**:
```
Authorization: Bearer <token>
```

**成功响应 (200)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "patient001",
    "email": "patient001@example.com",
    "phone": "13800138001"
  }
}
```

**错误响应**:

| HTTP状态码 | 响应示例 | 说明 |
|-----------|---------|------|
| 401 | `{"code": 401, "message": "无效的Token", "data": null}` | Token无效或过期 |

**测试示例**:
```bash
curl -X GET http://localhost:8080/api/v1/patients/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..."
```

---

## 测试账号

系统预置了以下测试账号供开发测试使用：

| 用户名 | 密码 | 邮箱 | 手机号 |
|-------|------|------|-------|
| patient001 | Patient123 | patient001@example.com | 13800138001 |
| patient002 | Patient123 | patient002@example.com | 13800138002 |
| patient003 | Patient123 | patient003@example.com | 13800138003 |

---

## JWT Token 说明

**Token有效期**: 2小时（7200秒）

**Token结构**:
- Header: 算法和令牌类型
- Payload: 包含userId、username、sub、iat、exp等字段
- Signature: 使用HS384算法签名

**Token刷新**: 当前版本不支持自动刷新，Token过期后需要重新登录。

---

## 安全说明

### 密码加密
- 使用BCrypt算法加密存储密码
- BCrypt强度因子: 12

### 账户锁定策略
- 连续5次登录失败后，账户将被锁定30分钟
- 锁定期间无法登录，即使密码正确
- 锁定时间过后自动解锁

### 速率限制
- 建议实施IP级别的速率限制（当前版本未实现）
- 推荐配置: 5次/分钟/IP

---

## 前端集成示例

### Vue 3 + TypeScript

```typescript
import { patientApi } from '@/api/patient';

// 登录
async function handleLogin() {
  try {
    const response = await patientApi.login({
      username: 'patient001',
      password: 'Patient123'
    });
    
    if (response.code === 200) {
      // 保存Token
      localStorage.setItem('token', response.data.token);
      // 跳转到主页
      router.push('/');
    }
  } catch (error) {
    console.error('登录失败:', error);
  }
}

// 获取用户信息
async function getUserInfo() {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  try {
    const response = await patientApi.getCurrentUser(token);
    console.log('用户信息:', response.data);
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
}
```

---

## 常见问题

### Q1: 登录后Token如何保存？
A: 建议将Token保存在localStorage或sessionStorage中，并在后续请求的Authorization header中携带。

### Q2: Token过期如何处理？
A: 当API返回401错误时，清除本地Token并跳转到登录页面。

### Q3: 密码忘记了怎么办？
A: 当前版本不支持密码重置功能，需要联系管理员重置密码。

### Q4: 如何防止暴力破解？
A: 系统已实现账户锁定机制，连续5次失败后锁定30分钟。建议额外配置IP速率限制。

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|-----|------|---------|
| V1.0 | 2026-05-10 | 初始版本，实现基本登录和用户信息查询功能 |

---

**文档维护**: 开发团队  
**最后更新**: 2026-05-10
