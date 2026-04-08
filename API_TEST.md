\# 接口测试说明



\## 1. 后端启动方式



进入后端服务目录：



&#x20;   cd server/qa-service-user

&#x20;   mvnw.cmd spring-boot:run



后端启动后访问地址为：



&#x20;   http://localhost:8080



\---



\## 2. 使用 curl 测试接口



测试医生列表接口：



&#x20;   curl http://localhost:8080/api/doctors



测试健康检查接口：



&#x20;   curl http://localhost:8080/api/ping



\---



\## 3. 预期返回结果



\### 3.1 医生列表接口返回示例



&#x20;   \[

&#x20;     {

&#x20;       "id": 1,

&#x20;       "name": "Dr. Sarah Chen",

&#x20;       "title": "Chief Physician",

&#x20;       "department": "Cardiology",

&#x20;       "hospital": "City General Hospital",

&#x20;       "avatar": "/images/doctor1.jpg",

&#x20;       "specialty": "Heart disease treatment"

&#x20;     },

&#x20;     {

&#x20;       "id": 2,

&#x20;       "name": "Dr. Michael Lee",

&#x20;       "title": "Attending Physician",

&#x20;       "department": "Neurology",

&#x20;       "hospital": "City General Hospital",

&#x20;       "avatar": "/images/doctor2.jpg",

&#x20;       "specialty": "Stroke and headache"

&#x20;     }

&#x20;   ]



\### 3.2 健康检查接口返回示例



&#x20;   {

&#x20;     "message": "ok"

&#x20;   }



\---



\## 4. 测试步骤



1\. 启动后端服务。

2\. 使用 curl 访问 `/api/ping`，确认服务已正常启动。

3\. 使用 curl 访问 `/api/doctors`，确认接口可正常返回医生列表数据。

4\. 启动前端服务后，打开医生页面，确认前端能正确展示接口返回的数据。



\---



\## 5. 说明



\- 项目根目录已提供 `docker-compose.yml` 和 MySQL 初始化 SQL 文件。

\- 由于当前电脑受终端安全策略限制，Docker Desktop 无法启动，因此本地演示时后端接口暂时使用本地 fallback 数据返回医生列表。

\- 前端医生页面已经完成对该接口的联调，可以正常展示医生数据。

