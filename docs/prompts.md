# 项目开发提示词记录

## 说明

- 本文档整理的是当前项目开发过程中，用户在当前对话中实际使用过的提示词。
- 按时间顺序记录。
- 对于重复发送、语义相同的提示词，保留原文并标注“重复”。

## 提示词列表

### 1. 项目初始开发

```text
根据项目中的readme.md文件开发项目
```

### 2. 本地 MySQL 环境说明

```text
我已经在本地安装好了MySQL，端口是3306，账户密码都是root
```

### 3. npm / Vite 启动问题

```text
现在我本地npm启动失败，错误信息如下
'vite' 不是内部或外部命令，也不是可运行的程序
或批处理文件。
```

```text
现在我本地npm启动失败，错误信息如下
'vite' 不是内部或外部命令，也不是可运行的程序
或批处理文件。
```

### 4. 数据初始化与 ORM 改造

```text
数据库的数据在web/qa-web/src/data下的三个json文件中，现在重新根据这三个json文件生成表并把json中的数据填进去，另外将项目从Hibernate改为mybatis-plus
```

```text
不用每次启动都重新根据这三个 JSON 生成表并填数据，只需要做一次这个行为就可以了，另外我看patient_users表中的数据没有从json文通同步，放心同步，这个表里面的数据是之前测试生成的，可以删掉
```

```text
不用每次启动都重新根据这三个 JSON 生成表并填数据，只需要做一次这个行为就可以了，另外我看patient_users表中的数据没有从json文通同步，放心同步，这个表里面的数据是之前测试生成的，可以删掉
```

```text
只需要导入一次json的数据就可以了，这个是初始化数据，导入一次以后就不用在执行该操作了
```

### 5. 前端页面报错与修复

```text
npm启动报错了
```

```text
我把数据库中的表删除了，现在启动项目报错Failed to execute CommandLineRunner
```

```text
json文件没有问题，不要改
```

```text
现在qa-service-user启动还是报错Caused by: java.nio.file.NoSuchFileException: C:\Users\Jinming You\web\qa-web\src\data\doctor-user-list.json
qa-service-question现在启动报错Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'questionService': Invocation of init method failed
先修复错误并让这些错误在这两个项目都不会出现
```

### 6. 医生登录与日志需求

```text
现在页面中医生登录没有走后端接口而是直接报密码错误
```

```text
另外现在项目添加mybatis-plus的日志
```

### 7. 问诊页面样式问题

```text
患者登录后，点击问诊-提交问题，然后页面有样式bug
```

```text
患者登录后，点击问诊-提交问题，出现的弹窗在选择完医生后，页面有样式bug，医生的头像会显示不完全
```

### 8. 登录态持久化

```text
现在不管是医生还是患者，登录后一刷新就没有登录状态了
```

### 9. PRD 文档相关

```text
现在根据当前项目，在/docs目录中编写规范的PRD文档模版，如果需要进行拆分请在PRD文档中提供拆分步骤
```

```text
继续基于这份模板，直接再帮你生成一份“当前项目患者登录/医生问诊功能”的正式 PRD
```

### 10. 接口测试与测试报告

```text
1. 使用 curl 构建对于当前项目需求中所涉及的所有接口的测试 
2. 提供 /docs/API_TEST.md 用于描述以上接口测试流程，并使用这份文档完成测试并生成测试报告
```

### 11. 提示词归档文档

```text
/docs/prompts.md 中列出在开发这个项目过程中我所使用的所有提示词
```
