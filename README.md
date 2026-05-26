# 高校实验室设备管理系统

## 项目简介
基于 Spring Boot + Vue 3 + MyBatis-Plus + MySQL 的高校实验室设备管理系统，实现实验室管理、设备管理、设备借用归还、维修管理等核心功能。

## 技术栈
- **后端**: Spring Boot 2.7 + MyBatis-Plus 3.5 + Spring Security Crypto (BCrypt)
- **前端**: Vue 3 + Element Plus + ECharts + Axios
- **数据库**: MySQL 8.0
- **开发工具**: Visual Studio Code

## 项目结构
```
├── backend/          # 后端项目 (Spring Boot)
│   ├── pom.xml
│   └── src/main/java/com/labmanager/
│       ├── config/       # 配置类 (CORS, MyBatis-Plus, Security)
│       ├── common/       # 通用类 (Result, GlobalExceptionHandler)
│       ├── entity/       # 实体类
│       ├── mapper/       # Mapper 接口
│       ├── service/      # 服务接口及实现
│       ├── controller/   # RESTful 控制器
│       └── dto/          # 数据传输对象
├── frontend/         # 前端项目 (Vue 3)
│   ├── package.json
│   ├── vue.config.js
│   └── src/
│       ├── api/          # API 请求模块
│       ├── router/       # 路由配置
│       ├── utils/        # 工具类 (Axios封装)
│       └── views/        # 页面组件
├── sql/              # 数据库初始化脚本
└── docs/             # 项目设计文档
```

## 功能模块
1. 用户登录与注册（BCrypt 密码加密）
2. 用户管理（管理员 CRUD + 分页 + 搜索）
3. 实验室管理（名称、地点、容量、状态）
4. 设备管理（名称、型号、所属实验室、状态）
5. 设备借用管理（借用、归还、状态跟踪）
6. 维修记录管理（报修、维修中、完成）
7. **设备统计报表**（按状态/实验室可视化）
8. **借用超时提醒**（列表标红 + 首页提醒）

## 快速开始

### 1. 数据库配置
1. 安装并启动 MySQL 8.0
2. 执行 `sql/init.sql` 初始化数据库
3. 修改 `backend/src/main/resources/application.yml` 中的数据库用户名和密码

### 2. 后端启动
```bash
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
# 或使用 VS Code 直接运行 LabManagerApplication.java
```
后端运行在 `http://localhost:8080`

### 3. 前端启动
```bash
cd frontend
npm install
npm run serve
```
前端运行在 `http://localhost:8081`

### 4. 访问系统
- 浏览器访问: `http://localhost:8081`
- 默认管理员: 通过注册页面创建用户后，手动修改数据库 role 字段为 admin
- 或直接执行 SQL: `UPDATE user SET role='admin' WHERE username='你的用户名';`

## API 接口概览
| 方法 | URL | 说明 |
|------|-----|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| GET | /api/users | 用户列表(分页) |
| POST | /api/users | 新增用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |
| GET | /api/labs | 实验室列表 |
| GET | /api/equipment | 设备列表 |
| POST | /api/borrows | 借用设备 |
| PUT | /api/borrows/{id}/return | 归还设备 |
| GET | /api/repairs/details | 维修记录详情 |
| GET | /api/statistics/dashboard | 统计仪表盘 |
| GET | /api/statistics/overdue | 超时借用列表 |

## 注意事项
- 首次运行请在 `application.yml` 中修改数据库连接用户名和密码
- 前端通过 `/api` 代理到后端 `localhost:8080`，确保后端先启动
- 密码使用 BCrypt 加密存储，请勿直接修改数据库密码字段
# xieyu
