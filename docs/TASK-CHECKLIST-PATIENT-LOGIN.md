# 患者登录特性 - 开发任务执行清单

## 开发任务清单

基于 PRD 文档：`docs/PRD-PATIENT-LOGIN.md`

---

## 阶段1：数据库与后端开发

### T1.1 创建 patients 表
- [ ] 修改 `init.sql` 添加 patients 表
- [ ] 重启 MySQL 容器执行初始化脚本
- [ ] 验证表创建成功

**执行命令：**
```bash
docker rm -f qa-mysql qa-phpmyadmin
docker volume rm qa-zjl_mysql_data
cd D:/code_workspace/qa-zjl && docker-compose up -d
```

---

### T1.2 创建 Patient 实体类
- [ ] 创建 `Patient.java` 实体类
- [ ] 添加 JPA 注解
- [ ] 添加 getter/setter

**文件位置：** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/entity/Patient.java`

---

### T1.3 创建 PatientRepository
- [ ] 创建 `PatientRepository.java` 接口
- [ ] 添加自定义查询方法

**文件位置：** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/repository/PatientRepository.java`

---

### T1.4 创建 PatientService
- [ ] 创建 `PatientService.java` 服务类
- [ ] 实现注册方法（密码加密）
- [ ] 实现登录方法（密码验证）

**文件位置：** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/service/PatientService.java`

---

### T1.5 创建 PatientController
- [ ] 创建 `PatientController.java` 控制器
- [ ] 实现 POST /api/patients/register
- [ ] 实现 POST /api/patients/login
- [ ] 实现 GET /api/patients/{id}

**文件位置：** `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/PatientController.java`

---

### T1.6 后端验证
- [ ] 重启后端服务
- [ ] 测试注册API
- [ ] 测试登录API
- [ ] 验证中文正常显示

**测试命令：**
```bash
# 测试注册
curl -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","name":"测试用户"}'

# 测试登录
curl -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

---

## 阶段2：前端开发

### T2.1 创建 PatientLogin.vue
- [ ] 创建登录页面组件
- [ ] 添加表单（用户名、密码）
- [ ] 添加表单验证
- [ ] 调用登录API
- [ ] 登录成功跳转首页

**文件位置：** `web/qa-web/src/views/PatientLogin.vue`

---

### T2.2 创建 PatientRegister.vue
- [ ] 创建注册页面组件
- [ ] 添加表单（用户名、密码、确认密码、姓名、手机号）
- [ ] 添加表单验证
- [ ] 调用注册API
- [ ] 注册成功自动登录

**文件位置：** `web/qa-web/src/views/PatientRegister.vue`

---

### T2.3 修改 store/index.ts
- [ ] 添加 Patient 接口（更新字段）
- [ ] 添加 loginPatient 方法
- [ ] 添加 registerPatient 方法
- [ ] 添加 logoutPatient 方法
- [ ] 添加 fetchPatientInfo 方法

**文件位置：** `web/qa-web/src/store/index.ts`

---

### T2.4 修改路由
- [ ] 添加 /patient/login 路由
- [ ] 添加 /patient/register 路由

**文件位置：** `web/qa-web/src/router/index.ts`

---

### T2.5 修改 AppHeader.vue
- [ ] 添加"患者登录"按钮
- [ ] 登录后显示患者姓名
- [ ] 添加退出登录功能

**文件位置：** `web/qa-web/src/components/AppHeader.vue`

---

## 阶段3：联调测试

### T3.1 功能测试
- [ ] 测试注册流程
  - [ ] 正常注册
  - [ ] 用户名重复
  - [ ] 表单验证
- [ ] 测试登录流程
  - [ ] 正常登录
  - [ ] 错误密码
  - [ ] 不存在的用户
- [ ] 测试退出登录
- [ ] 测试登录状态保持

### T3.2 页面测试
- [ ] 首页显示正确
- [ ] 医生页面显示正确
- [ ] 问诊页面访问正常
- [ ] 移动端适配正常

---

## 完成标准

### 后端
- [ ] 所有API正常工作
- [ ] 数据库操作正确
- [ ] 错误处理完善
- [ ] 中文显示正常

### 前端
- [ ] 所有页面正常加载
- [ ] 表单验证正常
- [ ] API调用成功
- [ ] 页面跳转正确
- [ ] 状态管理正常

### 集成
- [ ] 完整注册流程通过
- [ ] 完整登录流程通过
- [ ] 退出登录正常
- [ ] 刷新页面状态保持

---

## 问题记录

| 问题 | 状态 | 解决方案 |
|------|------|----------|
| | | |

---

## 完成时间

- 开始时间：________
- 完成时间：________
- 总工时：________
