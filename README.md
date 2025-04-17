# 牙科诊所管理系统

## 项目简介

牙科诊所管理系统是一个全面的解决方案，旨在帮助牙科诊所实现数字化管理。系统提供患者信息管理、预约排班、诊疗记录、治疗计划、药物管理、账单管理等功能，提高诊所工作效率和服务质量。

## 技术栈

### 后端
- 框架：Spring Boot
- 数据库：MySQL
- ORM框架：MyBatis
- API文档：Swagger
- 构建工具：Maven

### 前端
- 框架：Vue.js 2.x
- UI组件库：Element UI
- 状态管理：Vuex
- 路由管理：Vue Router
- HTTP客户端：Axios
- 图表库：ECharts

## 系统功能

- **患者管理**：患者信息录入、查询、分类管理
- **预约排班**：在线预约、日程安排、提醒通知
- **诊疗管理**：病历记录、治疗方案、随访计划
- **医生管理**：医生信息、专长、排班管理
- **药品管理**：药品库存、处方管理
- **财务管理**：收费记录、账单统计、财务报表
- **系统管理**：用户权限、系统设置、数据备份

## 项目结构

```
dental-system/
├── backend/                # 后端项目
│   ├── src/                # 源代码
│   │   ├── main/
│   │   │   ├── java/       # Java代码
│   │   │   └── resources/  # 配置文件
│   │   └── test/           # 测试代码
│   └── pom.xml             # Maven配置
│
├── frontend/               # 前端项目
│   ├── src/                # 源代码
│   │   ├── assets/         # 静态资源
│   │   ├── components/     # 组件
│   │   ├── views/          # 页面
│   │   ├── router/         # 路由
│   │   ├── store/          # Vuex状态管理
│   │   ├── api/            # API接口
│   │   ├── utils/          # 工具函数
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── package.json        # NPM配置
│   └── vue.config.js       # Vue配置
│
└── README.md               # 项目说明
```

## 开发环境搭建

### 后端环境要求
- JDK 8+
- Maven 3.6+
- MySQL 5.7+

### 前端环境要求
- Node.js 12+
- npm 6+ 或 yarn 1.22+

### 后端启动步骤
1. 配置数据库
   ```sql
   CREATE DATABASE dental_system;
   ```

2. 修改数据库配置
   编辑 `backend/src/main/resources/application.properties` 文件，配置数据库连接信息

3. 构建并运行
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```
   服务将在 http://localhost:8080 启动

### 前端启动步骤
1. 安装依赖
   ```bash
   cd frontend
   npm install
   # 或
   yarn install
   ```

2. 启动开发服务器
   ```bash
   npm run serve
   # 或
   yarn serve
   ```
   服务将在 http://localhost:8081 启动

## 生产环境部署

### 后端打包
```bash
cd backend
mvn clean package
```
生成的JAR文件位于 `target/` 目录下

### 前端打包
```bash
cd frontend
npm run build
# 或
yarn build
```
生成的静态文件位于 `dist/` 目录下

## 访问信息

- 后端API地址：http://localhost:8080/api
- 前端页面地址：http://localhost:8081
- API文档地址：http://localhost:8080/swagger-ui.html

## 开发团队

- 产品经理：[姓名]
- 后端开发：[姓名]
- 前端开发：[姓名]
- UI设计：[姓名]
- 测试：[姓名]

## 版本历史

- v0.1.0 (2025-04-15)：项目初始版本
