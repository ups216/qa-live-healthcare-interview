给web端的首页添加中英文切换能力，在页面右上角添加语言切换下拉菜单，在用户选择 中文/English 选项时动态切换页面显示内容到对应语言。中英文语言资源文件需要保存在 web/qa-web/src/locales 目录中。只需要处理首页本身，无需处理其他页面。

<img src="https://images.pexels.com/photos/4386467/pexels-photo-4386467.jpeg?auto=compress&cs=tinysrgb&w=600" alt="Healthcare" />是个图片不能改为span修改不改变原有结构

@Home.vue (44-47) 没有改为中英文切换

@zh-CN.json (1-35) imageAlt还是英文需要改为中文

首页没有 语言切换的功能 请检查一下 Home.vue 中语言切换器的样式

生成一个 docker-compose.yml 用于创建mysql数据库，并挂接phpmyadmin作为数据库服务器管理界面 MySQL版本使用8.4 phpmyadmin需要兼容mysql的版本

phpmyadmin 不要使用最新的版本 使用 长期稳定版本

把医生相关的json数据转为mysql8.4.9适配的数据格式 并给出初始化脚本 并列出 对前后端的影响

在 qa-service-user 服务中创建可以支持前端 Doctors.vue  显示医生列表所需要的api 使用上述创建的mysql 表操作

将 前端代码中医生相关的调用 都由json改为从后端 DoctorController 请求mysql 数据库做查询

现在你是前端测试 你开始调用web/qa-web 前端的所有/api/doctors相关的接口做 功能测试 如果接口调用失败 请列出相关接口  后端已经启动 健康检查API为 http://127.0.0.1:8080/api/doctors/active

测试过程中是否存在乱码以及跨域问题

@01-doctor-data.sql 1-67 写入数据库就是中文乱码 要如何改动


Doctors.vue 有无其他未实现的功能


/docs 目录中编写规范的PRD文档模版

你现在是一个高级架构师 根据 doc/PRD-template.md作为模板 现在目的是给问诊的用户提供用户名密码登录方式 现在搜索全部代码 判断 当前patient是否存在用户名密码 如果不存在 添加密码 密码 初始可以设置为默认密码 比如手机号后四位 支持后续修改 请写出此特性的PRD文档


系统继续保留原有的用户名加生日的方式登录 请重新生成 PRD-患者用户名密码登录.md


现在你是一个后端工程师 根据doc/PRD-患者用户名密码登录.md 你开始设计实现方案 其中哪些步骤 是需要继续拆分的 如果需要拆分 请在 PRD-患者用户名密码登录.md中添加并更新版本

现在你是一个前端工程师 根据doc/PRD-患者用户名密码登录.md 你开始设计实现方案 其中哪些步骤 是需要继续拆分的 如果需要拆分 请在 PRD-患者用户名密码登录.md中添加并更新版本

关于患者用户名密码登录 无需做国际化适配 请删除 doc/PRD-患者用户名密码登录.md中关于国际化相关改动


患者原有数据存储在patient-user.json中现在 将数据初始化至mysql中 请在 PRD-患者用户名密码登录.md中 对数据的初始化做处理


现在你是一个后端测试工程师 根据doc/PRD-患者用户名密码登录.md 你开始设计实现测试方案 其中哪些步骤 是需要继续拆分的 如果需要拆分 请在 PRD-患者用户名密码登录.md中添加并更新版本


现在 你是前端开发 请根据 doc/PRD-患者用户名密码登录.md 对当前系统进行改造


现在你是运维工程师 根据 PRD-患者用户名密码登录.md 文档你将初始化患者数据到mysql

现在 你是后端开发 请根据 doc/PRD-患者用户名密码登录.md 对当前系统进行改造 

你是全栈工程师 现在前后端环境已经启动 ，请根据doc/PRD-患者用户名密码登录.md开始自测联调 数据库的链接密码是 MYSQL_USER: qa_user MYSQL_PASSWORD: qa123456


执行 SQL 脚本修复初始数据的密码


现在你是测试工程师 请使用 curl 构建server/qa-service-user中所有接口测试

现在你是测试工程师 根据 PRD-患者用户名密码登录.md 输出一份API_TEST.md 如果 docs/API_TEST.md有缺少请添加


doc/PRD-患者用户名密码登录.md 敏感信息脱敏以及登录失败锁定 暂时不做处理 请按要求修改 doc/PRD-患者用户名密码登录.md

API_TEST.md 中 敏感信息脱敏 和 登录失败锁定 标记为暂不处理


移除后端代码中的所有 System.out.println


现在注释掉 patient-user.json doctor-user-list.json


作为测试工程师，我将重启前端服务并验证在移除本地 JSON 数据源后，前端是否能正常通过 API 与后端交互。


