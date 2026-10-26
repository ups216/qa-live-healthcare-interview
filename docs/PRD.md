
# 医疗问诊平台特性 PRD

## 1. 背景与目标
### 1.1 现状
当前应用数据完全存储在前端的 JSON 文件中，医生数据与用户登录逻辑均未与后端交互。
### 1.2 目标
1. 实现 Web 端首页中英文动态切换。
2. 将医生数据迁移至 MySQL 数据库，并实现前后端 API 联调。
3. 为问诊用户提供基于用户名密码的登录功能（BCrypt 加密）。

## 2. 功能需求（FR）
### FR-01：首页中英文切换
- **验收标准**：
  - Given 用户处于首页
  - When 点击右上角语言下拉菜单选择 English
  - Then 页面所有静态文案动态切换为英文，无需刷新。

### FR-02：医生数据 MySQL 化与 API
- **验收标准**：
  - Given Docker 环境已启动
  - When 前端请求 `GET /api/doctors`
  - Then 返回 MySQL 中的医生列表，前端页面成功渲染医生卡片。

### FR-03：用户名密码登录
- **验收标准**：
  - Given 用户在登录页输入正确的账号密码
  - When 发送 `POST /api/auth/login`
  - Then 后端使用 BCrypt 校验通过，返回 JWT Token。

## 3. 技术方案
### 3.1 数据库 DDL（MySQL）
```sql
CREATE TABLE `doctor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) NOT NULL,
  `title` varchar(50) DEFAULT NULL,
  `department` varchar(50) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `years_of_experience` int DEFAULT NULL,
  `specialty` json DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL COMMENT 'BCrypt加密',
  `real_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
);