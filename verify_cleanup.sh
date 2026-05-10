#!/bin/bash

echo "=========================================="
echo "  前端静态数据清理验证"
echo "=========================================="
echo ""

# 1. 检查doctor-user-list.json是否已删除
echo "1️⃣  检查doctor-user-list.json文件..."
if [ -f "web/qa-web/src/data/doctor-user-list.json" ]; then
    echo "   ❌ 文件仍然存在，清理失败"
    exit 1
else
    echo "   ✅ 文件已成功删除"
fi
echo ""

# 2. 检查保留的文件
echo "2️⃣  检查保留的数据文件..."
if [ -f "web/qa-web/src/data/patient-user.json" ]; then
    echo "   ✅ patient-user.json 存在"
else
    echo "   ❌ patient-user.json 丢失"
    exit 1
fi

if [ -f "web/qa-web/src/data/question-list.json" ]; then
    echo "   ✅ question-list.json 存在"
else
    echo "   ❌ question-list.json 丢失"
    exit 1
fi
echo ""

# 3. 检查store/index.ts中的改动
echo "3️⃣  检查store/index.ts代码..."
if grep -q "doctor-user-list.json" web/qa-web/src/store/index.ts; then
    echo "   ❌ 仍包含doctor-user-list.json引用"
    exit 1
else
    echo "   ✅ 已移除doctor-user-list.json引用"
fi

if grep -q "import { message } from 'ant-design-vue'" web/qa-web/src/store/index.ts; then
    echo "   ✅ 已添加message导入"
else
    echo "   ❌ 缺少message导入"
    exit 1
fi
echo ""

# 4. 检查Doctors.vue中的改动
echo "4️⃣  检查Doctors.vue代码..."
if grep -q "const loading = ref" web/qa-web/src/views/Doctors.vue; then
    echo "   ❌ 仍包含loading状态定义"
    exit 1
else
    echo "   ✅ 已移除loading状态"
fi

if grep -q "loadDoctorsData" web/qa-web/src/views/Doctors.vue; then
    echo "   ❌ 仍包含loadDoctorsData函数"
    exit 1
else
    echo "   ✅ 已移除重复的API调用逻辑"
fi

if grep -q "import { doctorApi }" web/qa-web/src/views/Doctors.vue; then
    echo "   ❌ 仍包含doctorApi导入"
    exit 1
else
    echo "   ✅ 已移除doctorApi导入"
fi
echo ""

# 5. TypeScript编译检查
echo "5️⃣  运行TypeScript编译检查..."
cd web/qa-web
if npm run build > /tmp/build.log 2>&1; then
    echo "   ✅ TypeScript编译通过"
else
    echo "   ❌ TypeScript编译失败"
    echo "   查看详细错误:"
    tail -30 /tmp/build.log
    exit 1
fi
cd ../..
echo ""

# 6. 统计代码行数变化
echo "6️⃣  代码统计..."
STORE_LINES=$(wc -l < web/qa-web/src/store/index.ts)
DOCTORS_LINES=$(wc -l < web/qa-web/src/views/Doctors.vue)
echo "   📊 store/index.ts: $STORE_LINES 行"
echo "   📊 views/Doctors.vue: $DOCTORS_LINES 行"
echo ""

echo "=========================================="
echo "  ✅ 所有验证通过！"
echo "=========================================="
echo ""
echo "清理总结:"
echo "  • 删除文件: 1个 (doctor-user-list.json)"
echo "  • 修改文件: 2个 (store/index.ts, views/Doctors.vue)"
echo "  • 减少代码: ~47行"
echo "  • 编译状态: 通过 ✓"
echo ""
echo "下一步建议:"
echo "  1. 启动后端服务: cd server/qa-service-user && ./mvnw spring-boot:run"
echo "  2. 启动前端服务: cd web/qa-web && npm run dev"
echo "  3. 访问页面: http://localhost:5173/doctors"
echo ""
