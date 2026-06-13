# 🏥 医院预约挂号系统

基于 Spring Cloud 微服务架构的医院预约挂号系统，支持患者移动端预约挂号、医生排班查看、管理员后台管理。

## 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Spring Boot 3.2.5 + Spring Cloud 2023.0.1 |
| 注册配置中心 | Nacos 2.3 |
| API 网关 | Spring Cloud Gateway |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 5.7（6 个独立库） |
| 缓存 | Redis 7 |
| 消息队列 | RabbitMQ 3 |
| 认证 | Spring Security + JWT (jjwt 0.12.5) |
| 文档 | Knife4j 4.5 (Swagger) |
| 前端 | Vue 3 + TypeScript + Vite |
| 患者端 UI | Vant 4（移动端） |
| 管理/医生端 UI | Element Plus（PC 端） |
| 部署 | Docker Compose |

## 项目结构

```
hospital/
├── hospital-parent/         # Maven 父 POM，统一版本管理
├── hospital-common/         # 公共模块：BaseEntity、R、JWT 工具
├── hospital-gateway/        # API 网关：8080，路由转发 + JWT 统一鉴权
├── hospital-auth/           # 认证服务：8081，注册登录、JWT 签发
├── hospital-hospital/       # 医院服务：8082，医院/科室/医生 CRUD
├── hospital-booking/        # 预约服务：8083，排班管理 + Redis 防超卖
├── hospital-order/          # 订单服务：8084，订单创建、RabbitMQ 消息
├── hospital-payment/        # 支付服务：8085，模拟微信支付
├── hospital-notify/         # 通知服务：8086，RabbitMQ 消费 + 模拟短信
├── hospital-web/            # Vue 3 前端（患者+医生+管理三端）
├── hospital-data/           # SQL 建表脚本 + 10 所真实三甲医院数据
├── docker-compose.yml       # 一键启动全部中间件
├── nginx.conf               # Nginx 配置
└── README.md
```

## 快速启动

### 1. 启动中间件

```bash
docker-compose up -d
```

启动 6 个 MySQL 数据库 + Redis + RabbitMQ + Nacos，共 9 个容器。

### 2. 编译后端

```bash
mvn clean package -DskipTests
```

### 3. 启动后端服务（按顺序）

```bash
java -jar hospital-auth/target/hospital-auth-1.0.0-SNAPSHOT.jar &
java -jar hospital-hospital/target/hospital-hospital-1.0.0-SNAPSHOT.jar &
java -jar hospital-booking/target/hospital-booking-1.0.0-SNAPSHOT.jar &
java -jar hospital-order/target/hospital-order-1.0.0-SNAPSHOT.jar &
java -jar hospital-payment/target/hospital-payment-1.0.0-SNAPSHOT.jar &
java -jar hospital-notify/target/hospital-notify-1.0.0-SNAPSHOT.jar &
java -jar hospital-gateway/target/hospital-gateway-1.0.0-SNAPSHOT.jar &
```

### 4. 启动前端

```bash
cd hospital-web
npm install
npm run dev
```

### 5. 访问

| 地址 | 说明 |
|------|------|
| http://localhost:5173/patient/hospitals | 患者端（移动端） |
| http://localhost:5173/doctor/schedule | 医生端（PC 端） |
| http://localhost:5173/admin/hospitals | 管理端（PC 端） |
| http://localhost:8081/doc.html | Auth 服务 API 文档 |
| http://localhost:8848/nacos | Nacos 控制台 (nacos/nacos) |
| http://localhost:15672 | RabbitMQ 管理 (admin/admin123) |

## 默认账号

| 角色 | 手机号 | 密码 |
|------|--------|------|
| 管理员 | 13800000001 | admin123 |
| 患者 | 自行注册 | - |
| 医生 | 自行注册（role 选 DOCTOR） | - |

## 核心业务流程

```
注册/登录 → 选择医院 → 选择科室 → 选择日期 → 查看排班 → 确认预约
→ 生成订单 → 模拟支付 → 短信通知 → 挂号成功
```

## 微服务端口

| 服务 | 端口 | 数据库 |
|------|------|--------|
| hospital-gateway | 8080 | - |
| hospital-auth | 8081 | hospital_auth |
| hospital-hospital | 8082 | hospital_hospital |
| hospital-booking | 8083 | hospital_booking |
| hospital-order | 8084 | hospital_order |
| hospital-payment | 8085 | hospital_payment |
| hospital-notify | 8086 | hospital_notify |
