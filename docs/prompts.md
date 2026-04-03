给web端的首页添加中英文切换能力，在页面右上角添加语言切换下拉菜单，在用户选择 中文/English 选项时动态切换页面显示内容到对应语言。中英文语言资源文件需要保存在 web/qa-web/src/locales 目录中。只需要处理首页本身，无需处理其他页面

当前应用的数据全部使用json文件的方式在前端项目中存储，请将医生相关的数据使用mysql数据库存储。具体要求如下：
mysql用8.0版本，字符集用utf8mb4_0900_ai_ci
创建 docker-compose.yml 用于docker创建mysql数据库，并挂接phpmyadmin作为数据库服务器管理界面
在 qa-service-user 服务中创建可以支持前端 医生页面 显示医生列表所需要的api，注意跨域报错
完成前后端联调



