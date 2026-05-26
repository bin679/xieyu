# 高校实验室设备管理系统 — 项目设计报告

---

## 目录

1. [项目背景与目的](#1-项目背景与目的)
2. [系统架构设计](#2-系统架构设计)
3. [功能模块结构](#3-功能模块结构)
4. [数据库设计](#4-数据库设计)
5. [核心接口文档](#5-核心接口文档)
6. [前端页面设计](#6-前端页面设计)
7. [额外功能说明](#7-额外功能说明)
8. [项目运行与部署步骤](#8-项目运行与部署步骤)
9. [总结与心得体会](#9-总结与心得体会)

---

## 1. 项目背景与目的

### 1.1 项目背景

随着高校办学规模不断扩大，实验室及实验设备的数量和种类急剧增加。传统的人工管理模式存在以下问题：

- **信息孤岛**: 实验室、设备、借用记录等信息分散在纸质文档或多个独立系统中
- **效率低下**: 人工登记借用归还流程繁琐，查询统计困难
- **状态不透明**: 设备是否可用、是否被借用、是否在维修等信息无法实时掌握
- **数据无沉淀**: 无法对设备使用情况进行统计分析，资源调配缺乏数据支撑

### 1.2 项目目的

本系统旨在为高校实验室管理提供一站式信息化解决方案，主要目标包括：

1. 实现实验室、设备、借用、归还、维修的全面数字化管理
2. 提供便捷的设备借用与归还操作流程
3. 支持数据统计与可视化分析，辅助管理决策
4. 支持多角色管理（管理员与普通用户），权限清晰
5. 通过超时提醒等功能降低设备管理风险

### 1.3 目标用户

| 角色 | 描述 | 权限 |
|------|------|------|
| 系统管理员 | 实验室管理员 | 全部功能：用户管理、实验室管理、设备管理、借用管理、维修管理、统计分析 |
| 普通用户 | 教师/学生 | 查看实验室与设备信息、借用归还设备、报修申请、查看统计报表 |

### 1.4 项目范围

本系统涵盖以下核心业务模块：
- 用户管理（登录注册、用户CRUD）
- 实验室管理（实验室信息维护）
- 设备管理（设备信息维护，关联实验室）
- 设备借用管理（借用申请、归还确认）
- 维修记录管理（报修申请、维修状态跟踪）
- 数据统计（设备状态分布、实验室设备数量统计、超时提醒）

---

## 2. 系统架构设计

### 2.1 总体架构

本系统采用前后端分离的 B/S 架构：

```
┌──────────────────────────────────────────────────────┐
│                    客户端浏览器                        │
│        (Vue 3 + Element Plus + ECharts)              │
└───────────────┬──────────────────────────────────────┘
                │  HTTP / JSON (RESTful API)
                │  Axios 请求 → /api/*
                │
┌───────────────▼──────────────────────────────────────┐
│              前端开发服务器 (Webpack Dev Server)        │
│              Vue CLI Service :8081                     │
│              └─ /api 代理 → localhost:8080             │
└──────────────────────────────────────────────────────┘
                │
┌───────────────▼──────────────────────────────────────┐
│            Spring Boot 后端服务器 :8080                │
│  ┌─────────────────────────────────────────────────┐ │
│  │  Controller 层 (RESTful API)                     │ │
│  │  - AuthController  - UserController              │ │
│  │  - LabController   - EquipmentController         │ │
│  │  - BorrowRecordController                        │ │
│  │  - RepairRecordController                        │ │
│  │  - StatisticsController                          │ │
│  └──────────────────┬──────────────────────────────┘ │
│  ┌──────────────────▼──────────────────────────────┐ │
│  │  Service 层 (业务逻辑)                            │ │
│  │  - UserService     - LabService                  │ │
│  │  - EquipmentService                              │ │
│  │  - BorrowRecordService                           │ │
│  │  - RepairRecordService                           │ │
│  └──────────────────┬──────────────────────────────┘ │
│  ┌──────────────────▼──────────────────────────────┐ │
│  │  Mapper 层 (MyBatis-Plus / 数据访问)              │ │
│  │  - UserMapper      - LabMapper                   │ │
│  │  - EquipmentMapper - BorrowRecordMapper          │ │
│  │  - RepairRecordMapper                            │ │
│  └──────────────────┬──────────────────────────────┘ │
└────────────────────┬─────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────┐
│              MySQL 8.0 数据库 (lab_manager)            │
│  ┌──────────┐ ┌──────┐ ┌───────────┐                 │
│  │  user    │ │ lab  │ │ equipment │                 │
│  └──────────┘ └──────┘ └───────────┘                 │
│  ┌──────────────┐ ┌───────────────┐                   │
│  │ borrow_record│ │ repair_record │                   │
│  └──────────────┘ └───────────────┘                   │
└──────────────────────────────────────────────────────┘
```

### 2.2 技术架构

**后端技术栈：**

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 后端框架 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架，简化 CRUD |
| MySQL Connector | 8.x | 数据库驱动 |
| Spring Security Crypto | 5.x | BCrypt 密码加密 |
| Lombok | 1.x | 简化实体类代码 |
| Spring Boot Validation | 2.7.x | 参数校验 |

**前端技术栈：**

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.3.x | 前端框架 |
| Vue Router | 4.2.x | 前端路由 |
| Element Plus | 2.4.x | UI 组件库 |
| Axios | 1.6.x | HTTP 请求库 |
| ECharts | 5.4.x | 数据可视化图表 |
| Vue CLI | 5.0.x | 前端构建工具 |

### 2.3 数据交互流程

```
用户操作 → Vue 组件 → Axios 请求 → Spring Boot Controller
    → Service 层处理 → Mapper 查询 (MyBatis-Plus)
    → MySQL 数据库 → 返回结果
    → 统一封装 Result { code, msg, data }
    → Axios 响应拦截器处理
    → Vue 响应式数据更新 → 页面重新渲染
```

### 2.4 统一返回格式

所有后端接口均返回统一格式的 JSON：

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

错误格式：

```json
{
  "code": 400,
  "msg": "设备当前不可借用",
  "data": null
}
```

---

## 3. 功能模块结构

### 3.1 模块结构图

```
高校实验室设备管理系统
│
├── 1. 认证模块
│   ├── 1.1 用户登录 (POST /api/auth/login)
│   └── 1.2 用户注册 (POST /api/auth/register)
│
├── 2. 用户管理模块 (管理员)
│   ├── 2.1 用户列表查询 (GET /api/users) - 分页 + 搜索
│   ├── 2.2 新增用户 (POST /api/users)
│   ├── 2.3 编辑用户 (PUT /api/users/{id})
│   ├── 2.4 删除用户 (DELETE /api/users/{id})
│   └── 2.5 重置密码 (PUT /api/users/{id}/reset-password)
│
├── 3. 实验室管理模块
│   ├── 3.1 实验室列表 (GET /api/labs) - 分页 + 搜索
│   ├── 3.2 全部实验室 (GET /api/labs/all)
│   ├── 3.3 新增实验室 (POST /api/labs)
│   ├── 3.4 编辑实验室 (PUT /api/labs/{id})
│   └── 3.5 删除实验室 (DELETE /api/labs/{id})
│
├── 4. 设备管理模块
│   ├── 4.1 设备列表 (GET /api/equipment) - 分页 + 筛选
│   ├── 4.2 全部设备 (GET /api/equipment/all)
│   ├── 4.3 新增设备 (POST /api/equipment)
│   ├── 4.4 编辑设备 (PUT /api/equipment/{id})
│   └── 4.5 删除设备 (DELETE /api/equipment/{id})
│
├── 5. 借用管理模块
│   ├── 5.1 借用记录列表 (GET /api/borrows/details)
│   ├── 5.2 借用设备 (POST /api/borrows)
│   ├── 5.3 归还设备 (PUT /api/borrows/{id}/return)
│   └── 5.4 删除记录 (DELETE /api/borrows/{id})
│
├── 6. 维修管理模块
│   ├── 6.1 维修记录列表 (GET /api/repairs/details)
│   ├── 6.2 提交报修 (POST /api/repairs)
│   ├── 6.3 更新维修状态 (PUT /api/repairs/{id})
│   └── 6.4 删除记录 (DELETE /api/repairs/{id})
│
└── 7. 统计报表模块 (额外功能)
    ├── 7.1 仪表盘数据 (GET /api/statistics/dashboard)
    └── 7.2 超时借用列表 (GET /api/statistics/overdue)
```

### 3.2 业务流程

**设备借用归还流程：**

```
用户选择可用设备
    │
    ▼
选择借用人 + 计划归还时间
    │
    ▼
提交借用申请 → 设备状态更新为 "borrowed"
    │
    ▼
用户使用设备中...
    │
    ▼ (超时未归还 → 状态变为 "overdue" → 列表标红提醒)
    │
    ▼
归还设备 → 设备状态更新为 "available"
    │        借用记录更新为 "returned"
    │        记录实际归还时间
    ▼
流程完成
```

**设备维修流程：**

```
设备故障 → 提交报修申请 (状态: pending)
    │
    ▼
管理员开始维修 (状态: repairing, 设备状态: maintenance)
    │
    ▼
维修完成 (状态: completed, 设备状态: available)
    │
    ▼
记录维修结果
```

---

## 4. 数据库设计

### 4.1 E-R 图（文字描述）

```
┌─────────┐         ┌───────────┐         ┌──────────────┐
│   user  │         │ equipment │         │  borrow_record│
├─────────┤         ├───────────┤         ├──────────────┤
│ id (PK) │         │ id (PK)   │         │ id (PK)      │
│username │         │ name      │         │ equipment_id │──→ equipment.id
│password │         │ model     │         │ user_id      │──→ user.id
│real_name│         │ lab_id    │──→ lab.id│ borrow_time  │
│ phone   │         │ status    │         │ planned_rt   │
│ email   │         │ desc      │         │ actual_rt    │
│ role    │         │ create_t  │         │ status       │
│create_t │         │ update_t  │         │ remark       │
│update_t │         └───────────┘         │ create_t     │
└─────────┘                               │ update_t     │
      │                                   └──────────────┘
      │                                   ┌──────────────┐
      │         ┌──────┐                  │repair_record │
      │         │ lab  │                  ├──────────────┤
      │         ├──────┤                  │ id (PK)      │
      │         │id(PK)│                  │ equipment_id │──→ equipment.id
      │         │ name │                  │ user_id      │──→ user.id
      └───────→ │ loc  │                  │ description  │
                │capac │                  │ status       │
                │status│                  │ repair_time  │
                │ desc │                  │complete_t    │
                │create│                  │ result       │
                │update│                  │ create_t     │
                └──────┘                  │ update_t     │
                                          └──────────────┘
```

### 4.2 表结构详细说明

#### 4.2.1 用户表 (user)

| 字段名 | 类型 | 允许空 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) | NOT NULL | - | 用户名 (UNIQUE) |
| password | VARCHAR(255) | NOT NULL | - | 密码 (BCrypt 加密) |
| real_name | VARCHAR(50) | NOT NULL | - | 真实姓名 |
| phone | VARCHAR(20) | YES | NULL | 手机号 |
| email | VARCHAR(100) | YES | NULL | 邮箱 |
| role | VARCHAR(20) | NOT NULL | 'user' | 角色: admin/user |
| create_time | DATETIME | NOT NULL | NOW() | 创建时间 |
| update_time | DATETIME | NOT NULL | NOW() | 更新时间 |

索引: PRIMARY KEY (id), UNIQUE KEY uk_username (username), KEY idx_role (role)

#### 4.2.2 实验室表 (lab)

| 字段名 | 类型 | 允许空 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL | - | 实验室名称 |
| location | VARCHAR(200) | NOT NULL | - | 实验室地点 |
| capacity | INT | NOT NULL | 0 | 容纳人数 |
| status | VARCHAR(20) | NOT NULL | 'available' | available/maintenance/closed |
| description | VARCHAR(500) | YES | NULL | 描述 |
| create_time | DATETIME | NOT NULL | NOW() | 创建时间 |
| update_time | DATETIME | NOT NULL | NOW() | 更新时间 |

索引: PRIMARY KEY (id), KEY idx_status (status)

#### 4.2.3 设备表 (equipment)

| 字段名 | 类型 | 允许空 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL | - | 设备名称 |
| model | VARCHAR(100) | YES | NULL | 设备型号 |
| lab_id | BIGINT | NOT NULL | - | 所属实验室ID (FK) |
| status | VARCHAR(20) | NOT NULL | 'available' | available/borrowed/maintenance/scrapped |
| description | VARCHAR(500) | YES | NULL | 描述 |
| create_time | DATETIME | NOT NULL | NOW() | 创建时间 |
| update_time | DATETIME | NOT NULL | NOW() | 更新时间 |

外键: fk_equipment_lab → lab(id) ON DELETE CASCADE
索引: PRIMARY KEY (id), KEY idx_lab_id (lab_id), KEY idx_status (status)

#### 4.2.4 借用记录表 (borrow_record)

| 字段名 | 类型 | 允许空 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键 |
| equipment_id | BIGINT | NOT NULL | - | 设备ID (FK) |
| user_id | BIGINT | NOT NULL | - | 借用人ID (FK) |
| borrow_time | DATETIME | NOT NULL | - | 借用时间 |
| planned_return_time | DATETIME | NOT NULL | - | 计划归还时间 |
| actual_return_time | DATETIME | YES | NULL | 实际归还时间 |
| status | VARCHAR(20) | NOT NULL | 'borrowing' | borrowing/returned/overdue |
| remark | VARCHAR(500) | YES | NULL | 备注 |
| create_time | DATETIME | NOT NULL | NOW() | 创建时间 |
| update_time | DATETIME | NOT NULL | NOW() | 更新时间 |

外键: fk_borrow_equipment → equipment(id), fk_borrow_user → user(id)
索引: PRIMARY KEY (id), KEY idx_equipment_id, KEY idx_user_id, KEY idx_status, KEY idx_borrow_time

#### 4.2.5 维修记录表 (repair_record)

| 字段名 | 类型 | 允许空 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| id | BIGINT | NOT NULL | AUTO_INCREMENT | 主键 |
| equipment_id | BIGINT | NOT NULL | - | 设备ID (FK) |
| user_id | BIGINT | NOT NULL | - | 报修人ID (FK) |
| description | VARCHAR(500) | NOT NULL | - | 故障描述 |
| status | VARCHAR(20) | NOT NULL | 'pending' | pending/repairing/completed |
| repair_time | DATETIME | YES | NULL | 维修开始时间 |
| complete_time | DATETIME | YES | NULL | 维修完成时间 |
| result | VARCHAR(500) | YES | NULL | 维修结果 |
| create_time | DATETIME | NOT NULL | NOW() | 创建时间 |
| update_time | DATETIME | NOT NULL | NOW() | 更新时间 |

外键: fk_repair_equipment → equipment(id), fk_repair_user → user(id)
索引: PRIMARY KEY (id), KEY idx_equipment_id, KEY idx_user_id, KEY idx_status

### 4.3 初始化数据

系统初始化时预置以下数据：

- 管理员账号: admin / admin123
- 普通用户: zhangsan / 123456, lisi / 123456
- 4 个实验室: 计算机网络实验室、软件工程实验室、嵌入式系统实验室、人工智能实验室
- 8 台设备: 台式计算机、网络交换机、开发工作站等

---

## 5. 核心接口文档

### 5.1 用户登录

- **URL**: `/api/auth/login`
- **Method**: POST
- **Content-Type**: application/json

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功返回**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "phone": "13800000000",
    "email": "admin@lab.com",
    "role": "admin"
  }
}
```

**失败返回**:
```json
{
  "code": 400,
  "msg": "用户名或密码错误",
  "data": null
}
```

### 5.2 用户注册

- **URL**: `/api/auth/register`
- **Method**: POST

**请求参数**:
```json
{
  "username": "wangwu",
  "password": "123456",
  "realName": "王五",
  "phone": "13800000003",
  "email": "wangwu@lab.com"
}
```

**成功返回**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 4,
    "username": "wangwu",
    "realName": "王五",
    "role": "user"
  }
}
```

### 5.3 用户列表（分页+搜索）

- **URL**: `/api/users`
- **Method**: GET
- **参数**: current=1&size=10&keyword=张

**成功返回**:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 2,
        "username": "zhangsan",
        "realName": "张三",
        "phone": "13800000001",
        "email": "zhangsan@lab.com",
        "role": "user",
        "createTime": "2026-05-20T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 5.4 新增用户

- **URL**: `/api/users`
- **Method**: POST

**请求参数**:
```json
{
  "username": "newuser",
  "password": "123456",
  "realName": "新用户",
  "phone": "13811111111",
  "email": "new@lab.com",
  "role": "user"
}
```

### 5.5 更新用户

- **URL**: `/api/users/{id}`
- **Method**: PUT

**请求参数** (密码留空则不修改):
```json
{
  "username": "zhangsan",
  "realName": "张三丰",
  "phone": "13800000001",
  "email": "zhangsan@new.com",
  "role": "user"
}
```

### 5.6 删除用户

- **URL**: `/api/users/{id}`
- **Method**: DELETE

### 5.7 实验室列表

- **URL**: `/api/labs`
- **Method**: GET
- **参数**: current=1&size=10&keyword=计算机

### 5.8 新增实验室

- **URL**: `/api/labs`
- **Method**: POST

```json
{
  "name": "新实验室",
  "location": "教学楼D-101",
  "capacity": 50,
  "status": "available",
  "description": "新建实验室"
}
```

### 5.9 设备列表

- **URL**: `/api/equipment`
- **Method**: GET
- **参数**: current=1&size=10&keyword=Dell&labId=1&status=available

### 5.10 新增设备

- **URL**: `/api/equipment`
- **Method**: POST

```json
{
  "name": "新设备",
  "model": "型号X100",
  "labId": 1,
  "status": "available",
  "description": "新采购设备"
}
```

### 5.11 借用设备

- **URL**: `/api/borrows`
- **Method**: POST

```json
{
  "equipmentId": 1,
  "userId": 2,
  "plannedReturnTime": "2026-06-01T18:00:00",
  "remark": "课程实验用"
}
```

业务规则：只有状态为 "available" 的设备才能借用，借用后设备状态自动变为 "borrowed"。

### 5.12 归还设备

- **URL**: `/api/borrows/{id}/return`
- **Method**: PUT

业务规则：归还后借用记录状态变为 "returned"，设备状态恢复为 "available"，记录实际归还时间。

### 5.13 维修记录详情

- **URL**: `/api/repairs/details`
- **Method**: GET

返回关联了设备名称和用户姓名的维修记录列表。

### 5.14 统计仪表盘

- **URL**: `/api/statistics/dashboard`
- **Method**: GET

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "labCount": 4,
    "equipmentCount": 8,
    "userCount": 3,
    "borrowingCount": 1,
    "overdueCount": 0,
    "equipmentByStatus": [
      { "status": "available", "count": 5 },
      { "status": "borrowed", "count": 1 },
      { "status": "maintenance", "count": 2 }
    ],
    "equipmentByLab": [
      { "lab_name": "计算机网络实验室", "count": 2 },
      { "lab_name": "软件工程实验室", "count": 2 },
      { "lab_name": "嵌入式系统实验室", "count": 3 },
      { "lab_name": "人工智能实验室", "count": 1 }
    ]
  }
}
```

---

## 6. 前端页面设计

### 6.1 登录页面

**页面布局描述**:
- 居中显示登录卡片，背景为蓝色渐变
- 登录卡片包含系统标题"高校实验室设备管理系统"
- 用户名输入框、密码输入框（支持显示/隐藏）
- 登录按钮（全宽蓝色按钮）
- 底部"还没有账号？立即注册"链接

### 6.2 注册页面

**页面布局描述**:
- 与登录页面风格一致
- 包含用户名、密码、真实姓名、手机号、邮箱五个输入框
- 注册按钮及"已有账号？去登录"链接

### 6.3 首页/工作台

**页面布局描述**:
- 顶部四个统计卡片：实验室总数、设备总数、用户总数、借用中设备
- 左侧饼图：设备状态分布（可用/已借用/维修中/已报废）
- 右侧柱状图：各实验室设备数量对比
- 底部：借用超时提醒表格（仅有超时记录时显示，标红提示）

### 6.4 用户管理页面

**页面布局描述**:
- 顶部：标题"用户管理"+ 新增用户按钮
- 搜索栏：用户名/姓名搜索输入框 + 搜索按钮
- 数据表格：ID、用户名、姓名、手机号、邮箱、角色(标签显示)、创建时间、操作列
- 操作列：编辑、重置密码、删除按钮
- 底部：分页组件（支持每页条数切换）
- 弹窗：新增/编辑用户表单（用户名、密码、姓名、手机号、邮箱、角色下拉）

### 6.5 实验室管理页面

**页面布局描述**:
- 搜索栏：名称/地点搜索
- 数据表格：ID、名称、地点、容纳人数、状态(标签)、描述、创建时间、操作
- 弹窗表单：名称、地点、容纳人数(数字输入)、状态(下拉)、描述(文本域)

### 6.6 设备管理页面

**页面布局描述**:
- 搜索栏：名称/型号搜索、所属实验室下拉筛选、状态下拉筛选
- 数据表格：ID、名称、型号、所属实验室、状态(彩色标签)、描述、创建时间、操作
- 弹窗表单：名称、型号、所属实验室(下拉选择)、状态、描述

### 6.7 借用管理页面

**页面布局描述**:
- 顶部超时提醒 Alert 条（红色警告图标）
- 借用按钮（蓝色，页面右上角）
- 数据表格：ID、设备名称、借用人、所属实验室、借用时间、计划归还时间（超时标红加粗）、实际归还时间、状态(标签)、操作
- 操作列：归还按钮（仅借用中/超时状态显示）+ 删除按钮
- 借用弹窗：选择设备(下拉搜索可用设备)、选择借用人(下拉搜索)、计划归还时间(日期选择器)、备注

### 6.8 维修管理页面

**页面布局描述**:
- 报修申请按钮（页面右上角）
- 数据表格：ID、设备名称、报修人、故障描述、状态(标签)、维修开始时间、维修完成时间、维修结果、操作
- 操作列：状态流转按钮（待处理→开始维修→完成维修）+ 删除
- 报修弹窗：选择设备、选择报修人、故障描述(文本域)
- 完成维修弹窗：维修结果(文本域)

### 6.9 统计报表页面

**页面布局描述**:
- 顶部统计卡片（同首页）
- 左侧饼图：设备状态分布（带百分比标签和图例）
- 右侧柱状图：各实验室设备数量（带数值标签）
- 底部：超时记录详细表格

---

## 7. 额外功能说明

### 7.1 设备统计报表

**设计思路**: 将原本分散在各级页面中的数据进行聚合展示，通过 ECharts 图表直观呈现设备状态分布和实验室设备分布。

**实现方案**:
- 后端新增 `StatisticsController`，聚合查询各表统计数据
- `/api/statistics/dashboard` 返回设备状态统计、实验室设备统计、借用概况等
- 前端使用 ECharts 渲染饼图和柱状图
- 饼图展示设备状态分布（可用/已借用/维修中/已报废）
- 柱状图展示各实验室设备数量对比

**价值**: 管理者可快速掌握全局设备状况，发现资源分配不均、设备闲置率高等问题，为采购和维护决策提供数据支撑。

### 7.2 借用超时提醒

**设计思路**: 系统自动检测计划归还时间已过但实际未归还的借用记录，在多个位置进行醒目提醒。

**实现方案**:
- 后端 `BorrowRecordMapper.selectOverdueRecords()` 查询超时记录
- 借用管理页面顶部显示红色 Alert 提示条，告知超时记录数量
- 超时记录在表格中计划归还时间列标红并加粗显示
- 首页底部显示超时记录表格
- 统计报表页面独立展示超时记录列表

**价值**: 减少设备"借而不还"的情况，提高设备周转率，降低设备丢失风险。

---

## 8. 项目运行与部署步骤

### 8.1 环境要求

| 软件 | 版本要求 |
|------|----------|
| JDK | 1.8+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |
| Node.js | 16.x+ |
| npm | 8.x+ |

### 8.2 数据库初始化

1. 启动 MySQL 服务
2. 使用 MySQL 客户端连接数据库
3. 执行 `sql/init.sql` 脚本:
   ```bash
   mysql -u root -p < sql/init.sql
   ```
4. 确认数据库 `lab_manager` 创建成功，5张表已生成

### 8.3 后端项目运行

**方式一：VS Code 运行**
1. 用 VS Code 打开 `backend/` 目录
2. 确保已安装 Java Extension Pack 和 Spring Boot Extension Pack
3. 打开 `application.yml`，修改数据库用户名密码
4. 找到 `LabManagerApplication.java`，右键 → Run Java

**方式二：命令行运行**
```bash
cd backend
mvn clean package -DskipTests
java -jar target/lab-manager-1.0.0.jar
```

启动成功后在控制台看到: `Tomcat started on port(s): 8080`

### 8.4 前端项目运行

1. 安装依赖:
   ```bash
   cd frontend
   npm install
   ```

2. 启动开发服务器:
   ```bash
   npm run serve
   ```

3. 启动成功后在浏览器访问: `http://localhost:8081`

### 8.5 生产环境部署

**后端部署**:
```bash
cd backend
mvn clean package -DskipTests
# 将 target/lab-manager-1.0.0.jar 部署到服务器
nohup java -jar lab-manager-1.0.0.jar > app.log 2>&1 &
```

**前端部署**:
```bash
cd frontend
npm run build
# 将 dist/ 目录部署到 Nginx/Apache 静态服务器
# Nginx 配置示例:
# location /api {
#     proxy_pass http://localhost:8080;
# }
# location / {
#     root /path/to/dist;
#     try_files $uri $uri/ /index.html;
# }
```

### 8.6 验证方法

1. 访问 `http://localhost:8081` 进入登录页面
2. 点击注册，创建一个新用户
3. 在数据库中将该用户的 role 修改为 `admin`
4. 重新登录，即可访问全部功能模块
5. 逐一测试各模块的增删改查功能

---

## 9. 总结与心得体会

### 9.1 项目总结

本项目成功实现了一套功能完整的高校实验室设备管理系统，涵盖了用户管理、实验室管理、设备管理、借用管理、维修管理五大核心业务模块，并额外扩展了统计报表和超时提醒两个创新功能。

**技术成果**:
- 采用前后端分离架构，RESTful API 设计规范，接口统一返回格式
- 使用 MyBatis-Plus 简化 CRUD 操作，大幅提升开发效率
- 使用 BCrypt 加密存储密码，保障用户信息安全
- 全局异常处理与参数校验，提高系统健壮性
- Element Plus + ECharts 实现美观的数据可视化界面
- 跨域配置完善，前后端独立开发互不影响

### 9.2 数据库设计体会

良好的数据库设计是系统成功的基石。本项目中：
- 通过外键约束保证了数据一致性
- 合理设置了索引，优化了查询性能
- 表之间的关联关系清晰，便于扩展维护
- ON DELETE CASCADE 策略简化了级联删除逻辑

### 9.3 后端开发心得

- MyBatis-Plus 的 `BaseMapper` 提供了约 90% 的常规 CRUD 操作，自定义 SQL 仅用于多表关联查询
- Spring Boot Validation 的 `@Valid` 注解配合全局异常处理可优雅地处理参数校验
- 统一返回格式 `Result<T>` 让前端处理响应更一致
- 事务管理 `@Transactional` 确保借用归还等关键操作的原子性

### 9.4 前端开发心得

- Vue 3 Composition API 结合 Element Plus 组件库，开发体验流畅
- Axios 拦截器统一处理错误，避免每个请求重复编写错误逻辑
- Vue Router 路由守卫实现登录状态检查，保障系统安全
- ECharts 图表组件通过 ref 绑定 DOM 元素，实现数据可视化

### 9.5 项目亮点

1. **设备状态联动**: 借用/归还/维修操作自动更新设备状态，减少手动管理
2. **多维度筛选**: 设备列表支持按实验室、状态筛选，提升查找效率
3. **超时自动检测**: SQL 实时查询超时记录，无需定时任务
4. **可视化仪表盘**: 首页和统计页面用图表直观展示数据分布
5. **密码安全**: BCrypt 单向加密，即便数据库泄露也不会暴露明文密码

### 9.6 后续改进方向

1. **JWT 身份认证**: 替代当前简单的 sessionStorage 方案，支持 token 过期刷新
2. **操作日志**: 记录用户的关键操作（登录、借用、归还等），形成审计日志
3. **设备预约**: 支持提前预约设备，到期自动提醒
4. **移动端适配**: 响应式设计或开发独立移动端应用
5. **导入导出**: 支持 Excel 批量导入设备和导出报表
6. **消息通知**: 超时提醒通过邮件或短信实时通知借用人
7. **权限细化**: 更细粒度的角色权限控制（RBAC）

---

> **文档版本**: v1.0
> **编写日期**: 2026-05-25
> **作者**: 系统开发团队
