# API 测试文档

## 概述

本文档用于指导AI完成医疗健康咨询平台API接口的测试，并生成测试报告。测试涵盖项目中所有已实现的API接口。

## 测试环境

### 环境配置
- **后端服务地址**：`http://localhost:8080`
- **前端服务地址**：`http://localhost:5173`
- **数据库**：H2内存数据库（开发环境）
- **测试工具**：curl、Postman、浏览器开发者工具

### 测试数据准备
测试前需要确保数据库中有以下测试数据：

1. **医生数据**：至少2位活跃医生
2. **患者数据**：至少3位测试患者
3. **问诊数据**：至少5条测试问诊记录

## API接口列表

### 1. 医生相关API

#### 1.1 获取活跃医生列表
```
GET /api/doctors/active
```

**测试目的**：验证前端医生页面能够正常获取活跃医生数据

**测试步骤**：
1. 启动后端服务
2. 使用curl或Postman调用API
3. 验证返回数据格式和内容

**测试用例**：
```bash
# 测试命令
curl -X GET "http://localhost:8080/api/doctors/active" \
  -H "Content-Type: application/json"

# 预期响应
{
  "success": true,
  "message": "成功获取活跃医生列表",
  "data": [
    {
      "id": "doc001",
      "username": "dr-zhang-wei",
      "name": "张伟医生",
      "title": "主任医师",
      "department": "心内科",
      "avatar": "https://example.com/avatar.jpg",
      "experience": "15年临床经验",
      "isActive": true,
      "specialties": ["高血压", "冠心病", "心律失常"]
    }
  ]
}
```

#### 1.2 获取所有医生
```
GET /api/doctors
```

**测试目的**：验证获取所有医生（包括非活跃）功能正常

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/doctors" \
  -H "Content-Type: application/json"
```

#### 1.3 获取医生统计信息
```
GET /api/doctors/statistics
```

**测试目的**：验证首页统计数据显示正确

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/doctors/statistics" \
  -H "Content-Type: application/json"

# 预期响应
{
  "success": true,
  "data": {
    "totalDoctors": 15,
    "activeDoctors": 12
  }
}
```

#### 1.4 根据ID获取医生
```
GET /api/doctors/{id}
```

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/doctors/doc001" \
  -H "Content-Type: application/json"
```

#### 1.5 根据用户名获取医生
```
GET /api/doctors/username/{username}
```

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/doctors/username/dr-zhang-wei" \
  -H "Content-Type: application/json"
```

### 2. 患者相关API

#### 2.1 患者登录
```
POST /api/patients/login
```

**测试目的**：验证患者登录功能正常

**测试步骤**：
1. 准备测试患者账号（username: patient001, password: 123456）
2. 使用正确/错误的凭据测试登录
3. 验证登录状态和返回数据

**测试用例**：
```bash
# 测试1：正确登录
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "password": "123456"
  }'

# 预期成功响应
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "patient": {
      "id": "patient001",
      "username": "patient001",
      "name": "张三",
      "birthday": "1990-01-01",
      "phone": "13800138000",
      "gender": "男"
    }
  }
}

# 测试2：错误密码
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "password": "wrongpassword"
  }'

# 预期失败响应
{
  "success": false,
  "message": "用户名或密码错误",
  "data": null
}

# 测试3：用户不存在
curl -X POST "http://localhost:8080/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistent",
    "password": "123456"
  }'
```

#### 2.2 患者注册
```
POST /api/patients/register
```

**测试用例**：
```bash
curl -X POST "http://localhost:8080/api/patients/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newpatient",
    "password": "newpassword123",
    "name": "李四",
    "birthday": "1992-05-15",
    "phone": "13900139000",
    "gender": "女",
    "email": "lisi@example.com"
  }'
```

#### 2.3 获取所有活跃患者
```
GET /api/patients
```

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/patients" \
  -H "Content-Type: application/json"
```

#### 2.4 根据用户名获取患者
```
GET /api/patients/username/{username}
```

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/patients/username/patient001" \
  -H "Content-Type: application/json"
```

#### 2.5 验证患者身份
```
GET /api/patients/verify?name={name}&birthday={birthday}
```

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/patients/verify?name=张三&birthday=1990-01-01" \
  -H "Content-Type: application/json"
```

#### 2.6 健康检查
```
GET /api/patients/health
```

**测试用例**：
```bash
curl -X GET "http://localhost:8080/api/patients/health" \
  -H "Content-Type: application/json"
```

### 3. 测试相关API

#### 3.1 CORS测试
```
GET /api/test/cors
POST /api/test/cors
OPTIONS /api/test/cors
```

**测试目的**：验证跨域资源共享配置正确

**测试用例**：
```bash
# GET测试
curl -X GET "http://localhost:8080/api/test/cors" \
  -H "Content-Type: application/json"

# POST测试
curl -X POST "http://localhost:8080/api/test/cors" \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}'

# OPTIONS测试（预检请求）
curl -X OPTIONS "http://localhost:8080/api/test/cors" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -H "Origin: http://localhost:5173"
```

## 端到端测试流程

### 测试场景1：完整的前后端交互

**测试目标**：验证从患者登录到问诊的完整流程

**测试步骤**：

1. **启动服务**
```bash
# 启动后端服务
cd server/qa-service-user
./mvnw spring-boot:run

# 启动前端服务
cd web/qa-web
npm run dev
```

2. **验证服务状态**
```bash
# 检查后端健康状态
curl http://localhost:8080/actuator/health

# 检查前端访问
curl http://localhost:5173
```

3. **测试患者登录流程**
   - 访问 `http://localhost:5173/patient/login`
   - 输入测试账号：patient001 / 123456
   - 点击登录按钮
   - 验证跳转到问诊页面
   - 验证右上角显示患者信息

4. **测试医生数据加载**
   - 访问 `http://localhost:5173/doctors`
   - 验证医生列表正常显示
   - 验证医生卡片信息完整

5. **测试API调用**
   - 打开浏览器开发者工具（F12）
   - 切换到Network标签页
   - 刷新页面，观察API调用
   - 验证所有API调用成功（状态码200）

### 测试场景2：API降级处理

**测试目标**：验证后端API不可用时前端降级机制

**测试步骤**：
1. 停止后端服务
2. 访问首页 `http://localhost:5173`
3. 验证页面能够正常加载
4. 验证控制台显示降级提示
5. 验证页面使用本地数据

## 自动化测试脚本

### 使用curl进行批量测试

```bash
#!/bin/bash
# api-test-suite.sh

BASE_URL="http://localhost:8080"

echo "=== 开始API测试 ==="

# 1. 测试医生API
echo "测试医生相关API..."
curl -s -X GET "$BASE_URL/api/doctors/active" | jq '.success'

# 2. 测试患者登录
echo "测试患者登录API..."
curl -s -X POST "$BASE_URL/api/patients/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"patient001","password":"123456"}' | jq '.success'

# 3. 测试CORS
echo "测试CORS配置..."
curl -s -X GET "$BASE_URL/api/test/cors" | jq '.message'

echo "=== API测试完成 ==="
```

### 使用Postman集合

1. 导入Postman集合文件（如果存在）
2. 运行集合中的所有测试
3. 生成测试报告

## 测试报告模板

### 测试结果汇总

| 测试类别 | 测试用例数 | 通过数 | 失败数 | 通过率 |
|----------|------------|--------|--------|--------|
| 医生API | 5 | 5 | 0 | 100% |
| 患者API | 6 | 6 | 0 | 100% |
| 测试API | 3 | 3 | 0 | 100% |
| 端到端测试 | 2 | 2 | 0 | 100% |
| **总计** | **16** | **16** | **0** | **100%** |

### 详细测试结果

#### 1. 医生API测试结果
- ✅ `GET /api/doctors/active` - 通过
- ✅ `GET /api/doctors` - 通过
- ✅ `GET /api/doctors/statistics` - 通过
- ✅ `GET /api/doctors/{id}` - 通过
- ✅ `GET /api/doctors/username/{username}` - 通过

#### 2. 患者API测试结果
- ✅ `POST /api/patients/login` - 通过
- ✅ `POST /api/patients/register` - 通过
- ✅ `GET /api/patients` - 通过
- ✅ `GET /api/patients/username/{username}` - 通过
- ✅ `GET /api/patients/verify` - 通过
- ✅ `GET /api/patients/health` - 通过

#### 3. 测试API结果
- ✅ `GET /api/test/cors` - 通过
- ✅ `POST /api/test/cors` - 通过
- ✅ `OPTIONS /api/test/cors` - 通过

#### 4. 端到端测试结果
- ✅ 患者登录流程 - 通过
- ✅ API降级处理 - 通过

### 性能测试结果

| API端点 | 平均响应时间 | 95%响应时间 | 请求成功率 |
|---------|--------------|--------------|------------|
| /api/doctors/active | 120ms | 180ms | 100% |
| /api/patients/login | 150ms | 220ms | 100% |
| /api/test/cors | 80ms | 120ms | 100% |

### 问题与建议

#### 发现的问题
1. 无重大功能问题
2. API响应时间符合预期
3. 降级机制工作正常

#### 改进建议
1. 增加API限流机制
2. 添加更详细的错误日志
3. 优化数据库查询性能

## 测试执行指导

### 指导AI执行测试

**指令示例**：
```
请按照API_TEST.md文档中的指导，执行以下测试：

1. 启动项目的前端和后端服务
2. 使用curl命令测试所有API端点
3. 验证每个API的响应格式和状态码
4. 执行端到端测试流程
5. 生成详细的测试报告

请关注以下关键点：
- 患者登录功能是否正确工作
- API降级机制是否有效
- 跨域请求是否正常
- 数据格式是否符合预期
```

### 测试注意事项

1. **环境准备**
   - 确保Java 17已安装
   - 确保Node.js 16+已安装
   - 确保端口8080和5173未被占用

2. **测试顺序**
   - 先测试后端API
   - 再测试前端页面
   - 最后测试端到端流程

3. **问题排查**
   - 检查服务日志
   - 验证数据库连接
   - 检查网络配置
   - 查看浏览器控制台错误

4. **测试数据**
   - 使用文档中提供的测试账号
   - 测试前重置测试数据
   - 记录测试过程中的异常

## 附录

### 测试数据
```json
{
  "test_doctors": [
    {
      "username": "dr-zhang-wei",
      "name": "张伟医生",
      "title": "主任医师",
      "department": "心内科",
      "password": "123456"
    },
    {
      "username": "dr-li-na", 
      "name": "李娜医生",
      "title": "副主任医师",
      "department": "儿科",
      "password": "123456"
    }
  ],
  "test_patients": [
    {
      "username": "patient001",
      "password": "123456",
      "name": "张三",
      "birthday": "1990-01-01",
      "phone": "13800138000",
      "gender": "男"
    },
    {
      "username": "patient002",
      "password": "123456",
      "name": "李四",
      "birthday": "1992-05-15",
      "phone": "13900139000", 
      "gender": "女"
    }
  ]
}
```

### 常用测试命令

```bash
# 检查服务状态
curl -I http://localhost:8080/actuator/health
curl -I http://localhost:5173

# 查看后端日志
cd server/qa-service-user
tail -f target/logs/application.log

# 查看前端日志
# 在浏览器中按F12打开开发者工具
```

---

**文档版本**：v1.0  
**最后更新**：2026-04-14  
**测试负责人**：AI Assistant