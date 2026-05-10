#!/bin/bash

# 前端API集成快速验证脚本

echo "=========================================="
echo "  前端医生页面 API 集成验证"
echo "=========================================="
echo ""

# 1. 检查后端服务
echo "1️⃣  检查后端服务..."
if curl -s http://localhost:8080/api/doctors > /dev/null 2>&1; then
    echo "   ✅ 后端服务运行正常 (端口 8080)"
    DOCTOR_COUNT=$(curl -s http://localhost:8080/api/doctors | python3 -c "import sys, json; print(len(json.load(sys.stdin)))")
    echo "   📊 返回医生数量: $DOCTOR_COUNT"
else
    echo "   ❌ 后端服务未运行"
    echo "   💡 请启动后端服务: cd server/qa-service-user && ./mvnw spring-boot:run"
    exit 1
fi
echo ""

# 2. 检查前端服务
echo "2️⃣  检查前端服务..."
if lsof -ti:5173 > /dev/null 2>&1; then
    echo "   ✅ 前端服务运行正常 (端口 5173)"
else
    echo "   ⚠️  前端服务未运行"
    echo "   💡 启动前端服务: cd web/qa-web && npm run dev"
fi
echo ""

# 3. 检查依赖
echo "3️⃣  检查前端依赖..."
if [ -d "web/qa-web/node_modules/axios" ]; then
    echo "   ✅ axios 已安装"
else
    echo "   ❌ axios 未安装"
    echo "   💡 安装命令: cd web/qa-web && npm install axios"
fi
echo ""

# 4. 测试API端点
echo "4️⃣  测试API端点..."
echo "   测试 GET /api/doctors..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors)
if [ "$HTTP_CODE" = "200" ]; then
    echo "   ✅ 响应状态码: 200"
else
    echo "   ❌ 响应状态码: $HTTP_CODE"
fi

echo "   测试 GET /api/doctors/active..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/doctors/active)
if [ "$HTTP_CODE" = "200" ]; then
    echo "   ✅ 响应状态码: 200"
else
    echo "   ❌ 响应状态码: $HTTP_CODE"
fi
echo ""

# 5. 显示医生列表
echo "5️⃣  医生列表预览:"
curl -s http://localhost:8080/api/doctors | python3 -c "
import sys, json
data = json.load(sys.stdin)
for doctor in data:
    status = '🟢 在线' if doctor['isActive'] else '🔴 离线'
    print(f\"   {status} {doctor['name']} - {doctor['title']} - {doctor['department']}\")
"
echo ""

# 6. 访问提示
echo "=========================================="
echo "  验证完成！"
echo "=========================================="
echo ""
echo "📱 访问医生页面:"
echo "   http://localhost:5173/doctors"
echo ""
echo "🔍 预期效果:"
echo "   • 页面加载时显示 loading 状态"
echo "   • 加载成功后显示 5 个医生卡片"
echo "   • 4个在线，1个离线（刘敏医生）"
echo "   • 点击'进入诊室'可跳转到咨询页面"
echo ""
echo "🐛 如果遇到问题:"
echo "   1. 查看浏览器控制台错误信息"
echo "   2. 检查后端服务日志"
echo "   3. 参考文档: web/qa-web/API_INTEGRATION_TEST.md"
echo ""
