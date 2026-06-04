# Java图书馆管理系统

## 项目简介
这是一个基于Java Swing、Spring6和MyBatis开发的图书馆管理系统，用于管理图书、读者、借阅记录等信息。该系统提供了图书借阅、归还、查询、入库、出库等完整的图书馆管理功能。

## 技术栈

### 前端技术
- Java Swing - 用于构建桌面图形用户界面

### 后端技术
- Java 17 - 主要开发语言
- MyBatis 3 - ORM框架，用于数据库操作
- Spring6 - 依赖注入框架

### 数据库
- MySQL - 关系型数据库

### 构建工具
- Maven - 项目构建和依赖管理

## 功能模块

### 图书管理
- 图书添加：录入新图书信息
- 图书查询：支持按ISBN、书名、作者等条件查询
- 图书更新：修改已有图书信息
- 图书分类管理：添加和更新图书分类

### 读者管理
- 读者添加：录入新读者信息
- 读者查询：查询读者信息
- 读者信息更新：修改已有读者信息

### 借阅管理
- 图书借阅：读者借阅图书
- 图书归还：读者归还图书
- 借阅记录查询：查看所有借阅记录

### 库存管理
- 图书入库：增加图书库存
- 图书出库：减少图书库存
- 库存查询：查看图书库存情况

### 系统管理
- 操作员管理：添加和更新操作员信息
- 密码修改：操作员修改登录密码

## 安装和运行

### 环境要求
- JDK 17或更高版本
- Maven 3.6或更高版本
- MySQL 5.7或更高版本

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd JavaLibraryManagementSystem
   ```

2. **配置数据库**
   - 创建数据库：`CREATE DATABASE library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   - 导入数据库脚本：`source src/main/resources/sql/database.sql`
   - 修改数据库连接配置：编辑 `src/main/resources/database.properties` 文件，配置数据库连接信息

3. **构建项目**
   ```bash
   mvn compile
   ```

4. **运行项目**
   ```bash
   mvn exec:java
   ```

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── wsy/
│   │           ├── auxiliary/      # 辅助工具类
│   │           ├── iframe/         # 界面框架类
│   │           ├── main/           # 主程序入口
│   │           ├── mapper/         # MyBatis映射接口
│   │           └── model/          # 实体类
│   └── resources/
│       ├── mappers/               # MyBatis映射文件
│       ├── res/                   # 资源文件
│       ├── sql/                   # 数据库脚本
│       ├── applicationContext.xml # Spring配置文件
│       ├── database.properties    # 数据库配置
│       ├── log4j.properties       # 日志配置
│       └── mybatis-config.xml     # MyBatis配置
└── test/                           # 测试代码
```

## 数据库设计

### 主要表结构

1. **tb_bookInfo** - 图书信息表
   - bookISBN：图书ISBN号
   - bookname：书名
   - writer：作者
   - translator：译者
   - publisher：出版社
   - date：出版日期
   - category：类别
   - price：价格

2. **tb_bookType** - 图书类别表
   - id：类别ID
   - name：类别名称
   - desc：类别描述

3. **tb_reader** - 读者表
   - id：读者ID
   - barcode：读者编号
   - name：姓名
   - gender：性别
   - major：专业
   - grade：年级
   - department：院系

4. **tb_borrow** - 借阅记录表
   - borrowId：借阅ID
   - readerNumber：读者编号
   - bookISBN：图书ISBN
   - operatorId：操作员ID
   - borrowDate：借阅日期
   - returnDate：归还日期
   - isBack：是否归还（1:已还，0:未还）

5. **tb_operator** - 操作员表
   - id：操作员ID
   - name：姓名
   - password：密码
   - department：部门

6. **tb_stockpile** - 库存表
   - bookISBN：图书ISBN
   - stockQuantity：库存量

## 使用说明

### 登录系统
运行程序后，输入用户名和密码登录系统。

### 图书借阅
1. 点击"图书借阅"模块
2. 在左侧输入读者编号和图书ISBN
3. 系统自动填充读者和图书信息
4. 点击"借出当前图书"按钮完成借阅

### 图书归还
1. 点击"图书归还"模块
2. 输入读者编号或图书ISBN
3. 选择要归还的图书
4. 点击"归还"按钮完成归还

### 图书查询
1. 点击"图书查询"模块
2. 选择查询方式（按ISBN、书名、作者等）
3. 输入查询关键词
4. 点击"查询"按钮查看结果

### 新书验收页面最新交互
- 列表点击不再自动填充；在 ISBN 输入框输入后移开焦点，系统会自动补全该 ISBN 的未验收订单信息。
- 新增“取消验收”按钮：仅当“是否验收”选择“否”时可用，用于删除未验收的订购记录。

## 开发人员
- 姓名：[张红闯]
- 学号：[239074340]
- 班级：[软232]

## 版本信息

### v1.0 (2025-12-20)
- 初始版本发布
- 实现了基本的图书管理功能
- 实现了读者管理功能
- 实现了借阅管理功能

## 注意事项

1. 确保数据库服务已启动
2. 第一次运行前请先导入数据库脚本
3. 修改数据库配置时请确保信息正确
4. 系统使用的是MySQL数据库，请确保已正确安装MySQL

## 许可证

MIT License
