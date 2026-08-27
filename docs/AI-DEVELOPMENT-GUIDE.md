# AI 开发指导文档

## 概述

本文档指导AI如何使用PRD模板编写需求文档，并根据PRD文档完成特性开发。

---

## 第一步：编写PRD文档

### 1.1 使用模板
- PRD模板位置：`docs/PRD-TEMPLATE.md`
- 复制模板并重命名为：`docs/PRD-[特性名称].md`

### 1.2 填写要求
1. **概述部分**：清晰描述背景、目标和范围
2. **用户故事**：从用户角度描述需求
3. **功能需求**：详细列出功能点和业务规则
4. **数据设计**：定义数据模型和API接口
5. **开发计划**：拆分任务，明确依赖关系

### 1.3 PRD示例
参考：`docs/PRD-PATIENT-LOGIN.md`

---

## 第二步：根据PRD进行开发

### 2.1 开发流程

```
1. 阅读PRD文档，理解需求
2. 按照任务拆分顺序开发
3. 每完成一个任务，更新PRD状态
4. 完成后进行功能验证
```

### 2.2 代码规范

#### 后端开发
1. **实体类**：放在 `entity` 包下
2. **Repository**：放在 `repository` 包下
3. **Service**：放在 `service` 包下
4. **Controller**：放在 `controller` 包下

#### 前端开发
1. **页面组件**：放在 `views` 目录下
2. **公共组件**：放在 `components` 目录下
3. **状态管理**：修改 `store/index.ts`
4. **路由配置**：修改 `router/index.ts`

### 2.3 文件命名规范
- Vue组件：PascalCase（如 `PatientLogin.vue`）
- TypeScript文件：camelCase（如 `patientService.ts`）
- Java类：PascalCase（如 `PatientController.java`）

---

## 第三步：任务执行示例

以"患者用户名密码登录"特性为例：

### 3.1 阶段1：数据库与后端

**任务T1.1：创建patients表SQL脚本**
```sql
-- 在 init.sql 中添加
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    gender VARCHAR(10),
    birthday DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**任务T1.2：创建Patient实体类**
```java
// src/main/java/.../entity/Patient.java
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private String id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String gender;
    private LocalDate birthday;
    // getters/setters
}
```

**任务T1.3：创建PatientRepository**
```java
// src/main/java/.../repository/PatientRepository.java
@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

**任务T1.4：创建PatientService**
```java
// src/main/java/.../service/PatientService.java
@Service
public class PatientService {
    // register(), login(), findById() 方法
}
```

**任务T1.5：创建PatientController**
```java
// src/main/java/.../controller/PatientController.java
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    // POST /register, POST /login, GET /{id} 端点
}
```

### 3.2 阶段2：前端开发

**任务T2.1：创建PatientLogin.vue**
```vue
<template>
  <!-- 登录表单 -->
</template>
<script setup lang="ts">
// 登录逻辑
</script>
```

**任务T2.2：创建PatientRegister.vue**
```vue
<template>
  <!-- 注册表单 -->
</template>
<script setup lang="ts">
// 注册逻辑
</script>
```

**任务T2.3：修改store添加患者登录方法**
```typescript
// store/index.ts 中添加
async loginPatient(username: string, password: string) { ... },
async registerPatient(data: RegisterData) { ... },
logoutPatient() { ... }
```

**任务T2.4：修改路由**
```typescript
// router/index.ts 中添加
{ path: '/patient/login', component: PatientLogin },
{ path: '/patient/register', component: PatientRegister }
```

**任务T2.5：修改Header**
```vue
<!-- AppHeader.vue 中添加患者登录按钮 -->
```

---

## 第四步：验证清单

完成开发后，按以下清单验证：

### 后端验证
- [ ] 数据库表创建成功
- [ ] API端点可访问
- [ ] 返回数据格式正确
- [ ] 错误处理正常

### 前端验证
- [ ] 页面正常加载
- [ ] 表单验证正常
- [ ] API调用成功
- [ ] 页面跳转正确
- [ ] 状态管理正常

### 集成验证
- [ ] 注册流程完整
- [ ] 登录流程完整
- [ ] 退出登录正常
- [ ] 刷新页面状态保持

---

## 常见问题

### Q: 如何处理中文乱码？
A: 确保以下配置：
1. MySQL连接URL包含 `characterEncoding=UTF-8`
2. 数据库和表使用 `utf8mb4` 字符集
3. 执行SQL前设置 `SET NAMES utf8mb4`

### Q: 如何处理跨域问题？
A: 后端已配置CORS，确保 `application.properties` 中：
```properties
spring.web.cors.allowed-origins=*
```

### Q: 如何重启服务？
A: 
```bash
# 停止服务
taskkill //F //PID [进程ID]

# 启动后端
export JAVA_HOME="/d/develop/Java/jdk-17.0.20.101-hotspot"
./mvnw.cmd spring-boot:run

# 启动前端
npm run dev
```
