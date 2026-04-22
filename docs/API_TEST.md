# API测试文档

## 测试环境

- 操作系统：Windows
- 测试工具：curl
- 后端服务：
  - qa-service-question：运行在 http://localhost:8081
  - qa-service-user：运行在 http://localhost:8080

## 测试流程

### 1. 启动后端服务

确保qa-service-question和qa-service-user服务已经启动并运行。

### 2. 测试qa-service-question服务的API

#### 2.1 患者登录接口

**接口地址**：`http://localhost:8081/api/patients/login`
**请求方法**：POST
**请求参数**：
- name: 患者姓名
- password: 患者密码

**测试命令**：

```bash
# 测试登录成功
curl -X POST -H "Content-Type: application/json" -d '{"name": "赵明", "password": "123123"}' http://localhost:8081/api/patients/login

# 测试登录失败（用户名或密码错误）
curl -X POST -H "Content-Type: application/json" -d '{"name": "赵明", "password": "wrongpassword"}' http://localhost:8081/api/patients/login

# 测试登录失败（用户名为空）
curl -X POST -H "Content-Type: application/json" -d '{"name": "", "password": "123123"}' http://localhost:8081/api/patients/login

# 测试登录失败（密码为空）
curl -X POST -H "Content-Type: application/json" -d '{"name": "赵明", "password": ""}' http://localhost:8081/api/patients/login
```

### 3. 测试qa-service-user服务的API

#### 3.1 医生相关接口

**接口地址**：`http://localhost:8080/api/doctors`
**请求方法**：GET
**测试命令**：

```bash
# 获取所有医生
curl http://localhost:8080/api/doctors

# 获取活跃医生
curl http://localhost:8080/api/doctors/active

# 根据ID获取医生
curl http://localhost:8080/api/doctors/1

# 根据用户名获取医生
curl http://localhost:8080/api/doctors/username/admin
```

#### 3.2 问题相关接口

**接口地址**：`http://localhost:8080/api/questions`
**请求方法**：GET
**测试命令**：

```bash
# 获取所有问题
curl http://localhost:8080/api/questions

# 根据ID获取问题
curl http://localhost:8080/api/questions/1

# 根据患者ID获取问题
curl http://localhost:8080/api/questions/patient/1

# 根据医生ID获取问题
curl http://localhost:8080/api/questions/doctor/1

# 根据状态获取问题
curl http://localhost:8080/api/questions/status/pending
```

## 测试结果

### 1. qa-service-question服务测试结果

#### 1.1 患者登录接口

| 测试场景 | 预期结果 | 实际结果 | 状态 |
|---------|---------|---------|------|
| 登录成功 | 返回status: "success", message: "登录成功" | 返回status: "error", message: "用户名或密码错误" | 失败 |
| 登录失败（用户名或密码错误） | 返回status: "error", message: "用户名或密码错误" | 返回status: "error", message: "用户名或密码错误" | 成功 |
| 登录失败（用户名为空） | 返回status: "error", message: "用户名和密码不能为空" | 待测试 | 待测试 |
| 登录失败（密码为空） | 返回status: "error", message: "用户名和密码不能为空" | 待测试 | 待测试 |

### 2. qa-service-user服务测试结果

#### 2.1 医生相关接口

| 测试场景 | 预期结果 | 实际结果 | 状态 |
|---------|---------|---------|------|
| 获取所有医生 | 返回status: "success", data: [医生列表], count: 医生数量 | 返回status: "success", data: [5个医生], count: 5 | 成功 |
| 获取活跃医生 | 返回status: "success", data: [活跃医生列表], count: 活跃医生数量 | 返回status: "success", data: [4个活跃医生], count: 4 | 成功 |
| 根据ID获取医生 | 返回status: "success", data: 医生信息 | 待测试 | 待测试 |
| 根据用户名获取医生 | 返回status: "success", data: 医生信息 | 待测试 | 待测试 |

#### 2.2 问题相关接口

| 测试场景 | 预期结果 | 实际结果 | 状态 |
|---------|---------|---------|------|
| 获取所有问题 | 返回status: "success", data: [问题列表], count: 问题数量 | 返回status: "success", data: [7个问题], count: 7 | 成功 |
| 根据ID获取问题 | 返回status: "success", data: 问题信息 | 待测试 | 待测试 |
| 根据患者ID获取问题 | 返回status: "success", data: [患者问题列表], count: 问题数量 | 待测试 | 待测试 |
| 根据医生ID获取问题 | 返回status: "success", data: [医生问题列表], count: 问题数量 | 待测试 | 待测试 |
| 根据状态获取问题 | 返回status: "success", data: [状态问题列表], count: 问题数量 | 待测试 | 待测试 |

## 测试总结

1. **qa-service-question服务**：
   - 患者登录接口返回了"用户名或密码错误"的错误信息，可能是因为数据库中没有对应的患者数据，或者密码不匹配。

2. **qa-service-user服务**：
   - 所有测试的API接口都工作正常，返回了正确的数据。
   - 获取所有医生的接口返回了5个医生的数据。
   - 获取活跃医生的接口返回了4个活跃医生的数据。
   - 获取所有问题的接口返回了7个问题的数据。

3. **注意事项**：
   - 测试过程中发现中文显示为乱码，这可能是因为PowerShell的编码问题。
   - 患者登录接口需要确保数据库中有对应的患者数据，并且密码正确。

4. **建议**：
   - 检查qa-service-question服务的数据库连接配置，确保它连接到了正确的数据库。
   - 确保数据库中存在patient_user表，并且表中有患者数据。
   - 检查患者数据的密码是否与测试时使用的密码匹配。
