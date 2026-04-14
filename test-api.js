// 测试API服务
const http = require('http');

// 测试API端点
const endpoints = [
  'http://localhost:8080/api/doctors/active',
  'http://localhost:8080/api/doctors',
  'http://localhost:8080/api/doctors/statistics'
];

// 简单的HTTP请求函数
function testEndpoint(url) {
  return new Promise((resolve, reject) => {
    const req = http.get(url, (res) => {
      let data = '';
      
      res.on('data', (chunk) => {
        data += chunk;
      });
      
      res.on('end', () => {
        try {
          const jsonData = JSON.parse(data);
          resolve({
            url,
            statusCode: res.statusCode,
            data: jsonData,
            success: jsonData.success || false
          });
        } catch (error) {
          resolve({
            url,
            statusCode: res.statusCode,
            error: 'JSON解析失败: ' + error.message,
            rawData: data.substring(0, 200) + '...'
          });
        }
      });
    });
    
    req.on('error', (error) => {
      resolve({
        url,
        error: error.message,
        statusCode: 0
      });
    });
    
    req.setTimeout(5000, () => {
      req.destroy();
      resolve({
        url,
        error: '请求超时',
        statusCode: 0
      });
    });
  });
}

// 运行测试
async function runTests() {
  console.log('🔍 开始测试医生API接口...\n');
  
  for (const endpoint of endpoints) {
    console.log(`测试端点: ${endpoint}`);
    const result = await testEndpoint(endpoint);
    
    if (result.error) {
      console.log(`❌ 失败: ${result.error}`);
    } else if (result.statusCode === 200 && result.success) {
      console.log(`✅ 成功: 状态码 ${result.statusCode}`);
      console.log(`   数据: ${JSON.stringify(result.data, null, 2).substring(0, 200)}...`);
    } else {
      console.log(`⚠️  警告: 状态码 ${result.statusCode}, success: ${result.success}`);
    }
    console.log('');
  }
  
  console.log('📊 测试完成！');
  console.log('\n如果需要启动后端服务，请执行以下步骤:');
  console.log('1. 确保Java 17已安装');
  console.log('2. 进入目录: cd server/qa-service-user');
  console.log('3. 运行: ./mvnw spring-boot:run');
  console.log('\n或者使用测试数据文件:');
  console.log('- 项目根目录的 api-test.html 文件包含完整的API测试页面');
}

runTests().catch(console.error);