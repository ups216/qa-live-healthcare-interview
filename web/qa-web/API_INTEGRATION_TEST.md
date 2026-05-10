# 前端 API 集成测试指南

## 测试步骤

### 1. 确保后端服务运行
```bash
# 检查 qa-service-user 服务是否运行在 8080 端口
curl http://localhost:8080/api/doctors
```

### 2. 启动前端开发服务器
```bash
cd /Users/lixiaohao/workspace/interview/qa-live-healthcare-interview/web/qa-web
npm run dev
```

### 3. 访问医生页面
打开浏览器访问: http://localhost:5173/doctors

### 4. 验证功能

#### 预期行为：
1. **页面加载时**：
   - 显示"加载中..."的旋转图标
   - 控制台输出："医生数据加载成功: 5 条记录"

2. **数据加载成功后**：
   - 显示5个医生卡片
   - 每个卡片包含：
     - 医生头像
     - 姓名、职称、科室
     - 从业经验
     - 专长标签
     - 在线状态徽章
     - "进入诊室"或"暂未开放"按钮

3. **在线状态**：
   - 张伟医生（心内科）- 在线 ✓
   - 李娜医生（儿科）- 在线 ✓
   - 王强医生（骨科）- 在线 ✓
   - 刘敏医生（妇产科）- 离线 ✗
   - 陈杰医生（消化内科）- 在线 ✓

4. **交互功能**：
   - 点击在线医生的"进入诊室"按钮，跳转到咨询页面
   - 离线医生的按钮为禁用状态

#### 如果API调用失败：
- 显示错误提示："无法连接到服务器，请确保后端服务正在运行"
- 提供"重试"按钮
- 控制台会显示错误信息
- Store会自动fallback到本地JSON数据

## API 端点

所有API请求都会发送到：`http://localhost:8080/api/doctors`

### 可用的端点：
- `GET /api/doctors` - 获取所有医生
- `GET /api/doctors/active` - 获取在线医生
- `GET /api/doctors/{id}` - 根据ID获取医生
- `GET /api/doctors/username/{username}` - 根据用户名获取医生

## 代码结构

### 新增文件：
1. **src/api/doctor.ts** - API服务模块
   - 封装axios请求
   - 定义DoctorDTO接口
   - 提供4个API方法

2. **修改的文件**：
   - **src/store/index.ts** - 状态管理
     - 从API加载医生数据
     - 提供fallback机制
   
   - **src/views/Doctors.vue** - 医生列表页面
     - 添加加载状态
     - 添加错误处理
     - 支持重试功能

## 技术要点

### 1. 数据转换
后端返回的DoctorDTO与前端Doctor接口略有不同：
- 后端不返回password字段
- 前端需要password用于登录验证
- 通过convertToDoctor函数进行转换

### 2. 错误处理
- 使用try-catch捕获API错误
- 显示友好的错误提示
- 提供重试机制
- Fallback到本地数据

### 3. 响应式更新
- 使用Vue 3的ref和computed
- 自动更新UI状态
- 加载状态、错误状态、数据状态分离

### 4. CORS配置
- 后端已配置允许跨域
- 前端可以直接调用API
- 无需额外配置代理

## 常见问题

### Q1: 页面一直显示"加载中..."
**原因**: 后端服务未启动或端口不正确
**解决**: 
```bash
# 检查后端服务
curl http://localhost:8080/api/doctors

# 如果失败，启动后端服务
cd server/qa-service-user
./mvnw spring-boot:run
```

### Q2: 显示错误提示
**原因**: API连接失败
**解决**:
1. 确认Docker MySQL容器运行
2. 确认qa-service-user服务运行
3. 检查控制台错误信息
4. 点击"重试"按钮

### Q3: 数据显示但中文乱码
**原因**: JSON编码问题（正常现象）
**说明**: 浏览器会自动解码Unicode，实际显示正常

### Q4: 点击"进入诊室"无反应
**原因**: 路由配置问题或consultation页面未实现
**解决**: 检查路由配置和Consultation.vue组件

## 性能优化建议

1. **缓存策略**: 可以考虑缓存医生数据，避免重复请求
2. **懒加载**: 如果医生数量很多，可以实现分页或虚拟滚动
3. **图片优化**: 使用CDN或图片压缩
4. **错误边界**: 添加Vue错误边界组件

## 下一步改进

1. 添加下拉刷新功能
2. 实现医生搜索和筛选
3. 添加骨架屏代替loading
4. 实现数据预取
5. 添加单元测试
