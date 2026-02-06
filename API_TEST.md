# 医生列表 API 测试说明

本文件说明如何使用 `curl` 测试 `qa-service-user` 服务中与医生列表相关的 API。

## 前置条件

- 已通过 `docker compose up -d` 启动 MySQL 与 phpMyAdmin，且已执行 `mysql-init/01_doctors.sql` 初始化数据（首次启动会自动执行）。
- 已在 `server/qa-service-user` 目录运行：

```bash
mvn spring-boot:run
```

默认服务地址为 `http://localhost:8080`。

## 1. 测试医生列表 API

### 1.1 获取全部医生列表

```bash
curl -i http://localhost:8080/api/doctors
```

期望返回 HTTP 200，并包含类似：

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/...",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "active": true
  }
]
```

### 1.2 只查看在线医生（在客户端过滤）

后端当前返回全部医生，是否在线由字段 `active` 控制，前端会在页面上过滤。可以用 `jq` 辅助过滤：

```bash
curl -s http://localhost:8080/api/doctors | jq '.[] | select(.active == true)'
```

## 2. CORS 测试（验证前端跨域访问）

使用浏览器访问前端 `web/qa-web` 的首页与医生列表页面，打开开发者工具 Network 面板，确认有对：

- `GET http://localhost:8080/api/doctors`

的请求，并且响应状态为 `200`，页面能够正常展示医生列表。

