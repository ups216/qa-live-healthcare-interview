# QA Service User 接口文档

## 基础地址

    http://localhost:8080

---

## 1. 获取医生列表

### 请求信息

- 请求方式：GET
- 请求路径：`/api/doctors`

### 接口说明

返回前端医生页面使用的医生列表数据。

### 请求示例

    curl http://localhost:8080/api/doctors

### 返回示例

    [
      {
        "id": 1,
        "name": "Dr. Sarah Chen",
        "title": "Chief Physician",
        "department": "Cardiology",
        "hospital": "City General Hospital",
        "avatar": "/images/doctor1.jpg",
        "specialty": "Heart disease treatment"
      },
      {
        "id": 2,
        "name": "Dr. Michael Lee",
        "title": "Attending Physician",
        "department": "Neurology",
        "hospital": "City General Hospital",
        "avatar": "/images/doctor2.jpg",
        "specialty": "Stroke and headache"
      }
    ]

### 字段说明

| 字段名 | 类型 | 说明 |
|------|------|------|
| id | number | 医生编号 |
| name | string | 医生姓名 |
| title | string | 医生职称 |
| department | string | 所属科室 |
| hospital | string | 所属医院 |
| avatar | string | 医生头像路径 |
| specialty | string | 擅长方向 |

---

## 2. 健康检查接口

### 请求信息

- 请求方式：GET
- 请求路径：`/api/ping`

### 请求示例

    curl http://localhost:8080/api/ping

### 返回示例

    {
      "message": "ok"
    }

---

## 3. 补充说明

- 项目仓库中已包含 `docker-compose.yml` 和 `docker/mysql/init/01_init.sql`，用于 MySQL 部署初始化。
- 当前本地环境因终端安全策略限制，无法启动 Docker Desktop。
- 为保证面试任务能够完成前后端联调与演示，当前 `/api/doctors` 接口使用本地 fallback 数据返回。
- 前端医生页面已经完成与该接口的联调，并可成功展示医生信息。