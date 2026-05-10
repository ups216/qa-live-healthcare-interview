# Consultation.vue 患者登录功能修改说明

## 📋 修改概述

将Consultation.vue页面的患者身份验证方式从"姓名+生日"改为"用户名+密码"的登录验证方式，与后端患者登录API集成。

---

## ✅ 主要修改内容

### 1. 登录表单改造

**修改前**:
- 输入字段：姓名、生日（日期选择器）
- 验证方式：本地store验证
- 按钮文本："验证身份"

**修改后**:
- 输入字段：用户名、密码（密码输入框）
- 验证方式：调用后端API `/api/v1/patients/login`
- 按钮文本："登录"
- 添加加载状态显示

### 2. 新增功能

#### 2.1 用户名密码登录
```typescript
const loginPatient = async () => {
  // 调用后端API进行登录验证
  const response = await fetch('http://localhost:8080/api/v1/patients/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: authForm.username,
      password: authForm.password,
    }),
  });
  
  // 处理响应，保存Token和用户信息
}
```

#### 2.2 自动登录
页面加载时检查localStorage中是否有保存的Token和用户信息，如果有则自动恢复登录状态。

```typescript
onMounted(() => {
  const savedToken = localStorage.getItem('patient_token');
  const savedUser = localStorage.getItem('patient_user');
  
  if (savedToken && savedUser) {
    // 自动恢复登录状态
    store.state.currentPatient = { ... };
    message.success('已自动登录');
  }
});
```

#### 2.3 退出登录
清除localStorage中的Token和用户信息，清空表单。

```typescript
const logoutPatient = () => {
  localStorage.removeItem('patient_token');
  localStorage.removeItem('patient_user');
  store.logoutPatient();
  authForm.username = '';
  authForm.password = '';
  message.success('已退出登录');
};
```

### 3. 表单验证规则

**用户名验证**:
- 必填
- 长度3-50个字符
- 只能包含字母、数字和下划线

**密码验证**:
- 必填
- 长度8-128个字符

### 4. UI改进

- 添加LockOutlined图标到密码输入框
- 登录按钮添加loading状态
- 更新提示文案

---

## 🧪 测试步骤

### 前置条件

1. **后端服务运行中**
   ```bash
   cd server/qa-service-user
   ./mvnw spring-boot:run
   ```

2. **前端服务运行中**
   ```bash
   cd web/qa-web
   npm run dev
   ```

3. **数据库中有测试账号**
   - 用户名: patient001
   - 密码: Patient123

### 测试场景

#### 场景1: 正常登录

**步骤**:
1. 访问 http://localhost:5173/consultation
2. 在登录表单中输入:
   - 用户名: `patient001`
   - 密码: `Patient123`
3. 点击"登录"按钮

**预期结果**:
- ✅ 显示"登录成功，欢迎回来！"消息
- ✅ 页面切换到患者门户界面
- ✅ 显示患者用户名和问诊功能
- ✅ localStorage中保存了token和user信息

**验证命令**:
```bash
# 查看localStorage
open http://localhost:5173/consultation
# 在浏览器控制台执行:
console.log(localStorage.getItem('patient_token'))
console.log(localStorage.getItem('patient_user'))
```

---

#### 场景2: 错误的用户名或密码

**步骤**:
1. 输入错误的用户名或密码
2. 点击"登录"按钮

**预期结果**:
- ✅ 显示"用户名或密码错误"消息
- ✅ 停留在登录页面
- ✅ 可以重新输入

**测试用例**:
- 用户名: `patient001`, 密码: `WrongPassword`
- 用户名: `wronguser`, 密码: `Patient123`

---

#### 场景3: 表单验证

**步骤**:
1. 不输入任何内容，直接点击登录
2. 输入过短的用户名（如"ab"）
3. 输入过短的密码（如"12345"）
4. 输入包含特殊字符的用户名（如"user@123"）

**预期结果**:
- ✅ 显示相应的验证错误消息
- ✅ 无法提交表单

---

#### 场景4: 自动登录

**步骤**:
1. 先正常登录一次
2. 刷新页面或关闭浏览器后重新打开
3. 访问 http://localhost:5173/consultation

**预期结果**:
- ✅ 自动跳过登录页面
- ✅ 直接进入患者门户
- ✅ 显示"已自动登录"消息

---

#### 场景5: 退出登录

**步骤**:
1. 登录后进入患者门户
2. 点击"切换用户"按钮

**预期结果**:
- ✅ 显示"已退出登录"消息
- ✅ 返回登录页面
- ✅ localStorage中的token和user被清除
- ✅ 表单被清空

**验证命令**:
```javascript
// 在浏览器控制台执行
console.log(localStorage.getItem('patient_token'))  // 应该为null
console.log(localStorage.getItem('patient_user'))   // 应该为null
```

---

#### 场景6: 网络错误处理

**步骤**:
1. 停止后端服务
2. 尝试登录

**预期结果**:
- ✅ 显示"网络错误，请稍后重试"消息
- ✅ 不会崩溃或卡住

---

## 🔍 API调用详情

### 登录请求

**URL**: `POST http://localhost:8080/api/v1/patients/login`

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

**成功响应** (HTTP 200):
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

**失败响应** (HTTP 401):
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

## 📊 代码变更统计

| 项目 | 数量 |
|-----|------|
| 新增行数 | ~110行 |
| 删除行数 | ~40行 |
| 净增加 | ~70行 |
| 修改的函数 | 3个 |
| 新增的图标 | 1个 (LockOutlined) |

---

## ⚠️ 注意事项

### 1. CORS配置

确保后端已配置CORS允许前端访问：

```java
// SecurityConfig.java
http.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(true);
    return config;
}));
```

### 2. Token有效期

- JWT Token有效期为2小时
- 过期后需要重新登录
- 当前版本未实现Token刷新机制

### 3. 安全性

**当前实现**:
- ✅ 密码通过HTTPS传输（生产环境必须启用HTTPS）
- ✅ 密码使用BCrypt加密存储
- ✅ Token保存在localStorage

**建议改进**:
- ⚠️ 生产环境应使用HttpOnly Cookie存储Token
- ⚠️ 实现Token刷新机制
- ⚠️ 添加CSRF保护

### 4. 兼容性

- 保留了原有的store结构
- `currentPatient`仍然使用旧的Patient接口
- 生日和性别字段设为空字符串以保持兼容

---

## 🐛 已知问题

### 问题1: Store数据结构不完全匹配

**描述**: 
后端返回的用户信息只有id、username、email、phone，但store的Patient接口还需要birthday和gender字段。

**当前方案**: 
将birthday和gender设为空字符串。

**改进建议**: 
更新Patient接口，移除不需要的字段，或从后端获取完整用户信息。

---

### 问题2: 未实现Token过期处理

**描述**: 
Token过期后，用户操作可能会失败，但没有自动跳转到登录页面。

**改进建议**: 
在API调用时检查401错误，自动清除Token并跳转登录页。

---

### 问题3: 缺少加载状态反馈

**描述**: 
除了登录按钮的loading状态，页面没有其他加载指示。

**改进建议**: 
添加全局loading状态或骨架屏。

---

## 📝 后续优化建议

### 短期（1周内）

1. **添加Token过期检测**
   ```typescript
   // 在API拦截器中添加
   if (error.response?.status === 401) {
     localStorage.removeItem('patient_token');
     localStorage.removeItem('patient_user');
     window.location.href = '/consultation';
   }
   ```

2. **完善错误处理**
   - 区分不同类型的错误（网络错误、服务器错误、认证错误）
   - 提供更友好的错误提示

3. **添加记住我功能**
   - 可选的"7天内自动登录"
   - 使用Refresh Token机制

### 中期（1个月内）

1. **实现密码重置功能**
   - 邮箱验证码重置
   - 手机验证码重置

2. **添加双因素认证**
   - 短信验证码
   - 邮箱验证码

3. **优化用户体验**
   - 添加登录动画
   - 记住上次登录的用户名
   - 密码强度提示

### 长期（3个月内）

1. **社交登录集成**
   - 微信登录
   - 支付宝登录

2. **生物识别登录**
   - 指纹识别
   - 面部识别

3. **安全审计**
   - 记录所有登录尝试
   - 异常登录检测
   - IP白名单

---

## 📞 技术支持

如有问题，请检查：

1. **浏览器控制台** - 查看JavaScript错误
2. **网络面板** - 查看API请求和响应
3. **后端日志** - 查看服务器端错误
4. **数据库** - 确认测试账号存在

**常用调试命令**:

```bash
# 查看后端日志
tail -f /tmp/backend.log

# 查看前端日志
tail -f /tmp/frontend.log

# 测试API
curl -X POST http://localhost:8080/api/v1/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"Patient123"}'
```

---

**文档版本**: V1.0  
**更新日期**: 2026-05-10  
**维护人员**: 开发团队
