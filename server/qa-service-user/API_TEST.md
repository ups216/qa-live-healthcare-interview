# 医生接口测试文档

## 接口信息
- 接口地址：GET /api/doctors
- 功能：分页获取医生列表

## 请求参数
- current：当前页，默认1
- size：每页条数，默认10

## curl 测试脚本
```bash
curl http://localhost:8080/api/doctors