# 单条 curl 测试命令参考
# 基础地址（可按需修改）
# export BASE_URL=http://localhost:8080

# =============================================================================
# TC-01 健康检查
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/actuator/health

# =============================================================================
# TC-02 CORS GET
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/test/cors

# =============================================================================
# TC-03 获取全部医生列表
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/doctors

# =============================================================================
# TC-04 获取在线医生列表
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" "http://localhost:8080/api/doctors?active=true"

# =============================================================================
# TC-05 按用户名查询医生
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/doctors/username/dr-zhang-wei

# =============================================================================
# TC-06 医生不存在 (预期 404)
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" http://localhost:8080/api/doctors/username/not-exist-user

# =============================================================================
# TC-07 医生登录成功
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"dr-zhang-wei\",\"password\":\"123456\"}"

# =============================================================================
# TC-08 医生登录失败 - 密码错误 (预期 401)
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"dr-zhang-wei\",\"password\":\"wrong-password\"}"

# =============================================================================
# TC-09 医生登录失败 - 用户不存在 (预期 401)
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/doctors/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"unknown-user\",\"password\":\"123456\"}"

# =============================================================================
# TC-10 CORS POST
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json" \
  -d "{\"test\":\"hello\"}"

# =============================================================================
# TC-11 患者登录成功
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhaoming\",\"password\":\"123456\"}"

# =============================================================================
# TC-12 患者登录失败 (预期 401)
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhaoming\",\"password\":\"wrong-password\"}"

# =============================================================================
# TC-13 患者注册成功（请将 USERNAME 替换为唯一值）
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"test-patient-001\",\"password\":\"123456\",\"name\":\"测试用户\",\"birthday\":\"1992-01-01\",\"gender\":\"男\"}"

# =============================================================================
# TC-14 患者注册失败 - 用户名重复 (预期 400)
# =============================================================================
curl -s -w "\nHTTP_STATUS:%{http_code}\n" \
  -X POST http://localhost:8080/api/patients/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhaoming\",\"password\":\"123456\",\"name\":\"重复用户\"}"
