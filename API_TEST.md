# API 测试说明

## 测试脚本

测试脚本位于项目根目录：`test-doctors-api.sh`

## 前置条件

1. 确保 MySQL 数据库已启动
2. 确保后端服务 (qa-service-user) 已启动并运行在 `http://localhost:8080`
3. 确保已安装 `jq` 工具（用于格式化 JSON 输出）

### 安装 jq (Windows PowerShell)

```powershell
winget install jqlang.jq
```

或使用 chocolatey:

```powershell
choco install jq
```

## 执行测试

### 方式一：执行完整测试脚本

```bash
# Windows PowerShell
.\test-doctors-api.sh

# Linux/macOS
bash test-doctors-api.sh
```

### 方式二：逐个测试 API

#### 1. 获取所有医生（中文）

```bash
curl -s http://localhost:8080/api/doctors?locale=zh-CN | jq '.'
```

#### 2. 获取所有医生（英文）

```bash
curl -s http://localhost:8080/api/doctors?locale=en-US | jq '.'
```

#### 3. 获取活跃医生（中文）

```bash
curl -s http://localhost:8080/api/doctors/active?locale=zh-CN | jq '.'
```

#### 4. 获取活跃医生（英文）

```bash
curl -s http://localhost:8080/api/doctors/active?locale=en-US | jq '.'
```

#### 5. 根据用户名获取单个医生（中文）

```bash
curl -s http://localhost:8080/api/doctors/dr-wang-qiang?locale=zh-CN | jq '.'
```

#### 6. 根据用户名获取单个医生（英文）

```bash
curl -s http://localhost:8080/api/doctors/dr-wang-qiang?locale=en-US | jq '.'
```

#### 7. 测试 404 场景（获取不存在的医生）

```bash
curl -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/api/doctors/dr-not-exist?locale=zh-CN
```

## 测试用例说明

| 测试编号 | API 端点 | 参数 | 预期结果 |
|---------|----------|------|---------|
| 1 | `/api/doctors` | `locale=zh-CN` | 返回所有医生的中文数据 |
| 2 | `/api/doctors` | `locale=en-US` | 返回所有医生的英文翻译数据 |
| 3 | `/api/doctors/active` | `locale=zh-CN` | 返回活跃医生的中文数据 |
| 4 | `/api/doctors/active` | `locale=en-US` | 返回活跃医生的英文翻译数据 |
| 5 | `/api/doctors/dr-wang-qiang` | `locale=zh-CN` | 返回王强医生的中文信息 |
| 6 | `/api/doctors/dr-wang-qiang` | `locale=en-US` | 返回王强医生的英文翻译信息 |
| 7 | `/api/doctors/dr-not-exist` | `locale=zh-CN` | 返回 404 状态码 |

## 验证要点

### 中文环境验证

- `name`: 应为中文姓名，如 "王强医生"
- `title`: 应为中文职称，如 "主治医师"
- `department`: 应为中文科室，如 "骨科"
- `experience`: 应包含中文年份，如 "8年临床经验"
- `specialties`: 应为中文数组，如 ["骨折", "关节炎", "运动损伤"]

### 英文环境验证

- `name`: 应为英文姓名，如 "Dr. Wang Qiang"
- `title`: 应为英文职称，如 "Doctor-in-charge" 或 "Attending Physician"
- `department`: 应为英文科室，如 "Orthopedics"
- `experience`: 应包含英文年份，如 "8 years clinical experience"
- `specialties`: 应为英文数组，如 ["Fracture", "Arthritis", "Sports Injury"]

## 医生数据参考

| 用户名 | 姓名 | 职称 | 科室 | 活跃状态 |
|--------|------|------|------|----------|
| dr-zhang-wei | 张伟医生 | 主任医师 | 心内科 | 是 |
| dr-li-na | 李娜医生 | 副主任医师 | 儿科 | 是 |
| dr-wang-qiang | 王强医生 | 主治医师 | 骨科 | 是 |
| dr-liu-min | 刘敏医生 | 主任医师 | 妇产科 | 否 |
| dr-chen-jie | 陈杰医生 | 副主任医师 | 消化内科 | 是 |

## 故障排除

### 服务未启动

如果收到连接错误，请确保服务已启动：

```bash
# 启动后端服务
cd server/qa-service-user
mvn spring-boot:run
```

### 数据库连接问题

检查数据库是否正常运行：

```bash
# 使用 Docker 检查 MySQL 状态
docker-compose ps
```

### 翻译超时

如果英文翻译响应较慢或超时（超过 5 秒），系统会自动回退到返回原始中文数据。
