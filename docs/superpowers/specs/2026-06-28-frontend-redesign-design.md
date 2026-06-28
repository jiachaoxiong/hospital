# 前端界面重设计 — 设计文档

> 日期：2026-06-28 | 状态：待审核

## 1. 设计目标

对医院预约挂号系统前端进行全面重设计，统一三端视觉风格，提升用户体验和专业感。

### 当前问题
- Element Plus（PC端）和 Vant（移动端）混用，视觉割裂
- 缺乏统一的配色和品牌感
- 桌面端布局陈旧（传统侧边栏）
- 移动端首页功能引导弱

### 设计原则
- 三端统一配色和品牌识别
- 根据使用场景适配布局（移动端 Vant，桌面端 Element Plus）
- 现代清新风格，柔和圆角，轻阴影
- 改动控制在纯前端，不动后端接口

## 2. 设计系统

### 配色

| Token | 色值 | 用途 |
|-------|------|------|
| 主色渐变 | `#4facfe` → `#00f2fe` | 顶栏背景、主按钮、链接 |
| 深色背景 | `#1e3a5f` | 导航深色区 |
| 成功绿 | `#67c23a` | 已支付/已就诊 |
| 警告橙 | `#e6a23c` | 待支付 |
| 危险红 | `#f56c6c` | 已取消/删除 |
| 背景灰 | `#f5f7fa` | 页面背景 |
| 文字主色 | `#2c3e50` | 标题文字 |
| 文字辅色 | `#999` | 辅助说明 |

### 形状

| 元素 | 圆角 |
|------|------|
| 卡片 | 12px |
| 按钮 | 8px（桌面）/ 20px（移动端药丸按钮） |
| 标签/Tag | 4px |
| 输入框 | 8px |
| 对话框 | 12px |

### 阴影
- 卡片：`0 2px 12px rgba(0,0,0,0.06)`
- 悬浮：`0 4px 16px rgba(0,0,0,0.1)`
- 对话框：`0 8px 24px rgba(0,0,0,0.12)`

### 字体
- 系统默认字体栈：`-apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", sans-serif`
- 标题：18px/700 桌面端，16px/600 移动端
- 正文：14px/400
- 辅助文字：12px/400

## 3. 页面清单与改动

### 3.1 登录/注册（2页）
**文件**：`Login.vue`, `Register.vue`

**改动**：
- 全屏渐变背景（主色渐变 `#4facfe → #00f2fe`）
- 白色卡片式表单居中（border-radius: 12px, box-shadow）
- 系统 Logo/标题在卡片顶部
- 按钮改为圆角 + 主色渐变
- 整体居中垂直布局

### 3.2 患者端（5页）
**技术栈**：Vant 4 组件 + 自定义样式覆盖

#### 3.2.1 首页（Hospitals.vue）
- 渐变顶栏（主色渐变 + 城市选择 + 标题"预约挂号"）
- 搜索框（白色圆角 pill，带 🔍 图标）
- 科室快捷入口 4 列网格（内科🫀、外科🦴、儿科👶、眼科👁️ 等）
- 推荐医院卡片列表（圆角 12px, 医院名 + 等级标签 + 地址）
- 底部 Tabbar（首页/订单/我的）

#### 3.2.2 医院详情（HospitalDetail.vue）
- 医院信息头部卡片（名称、等级、地址、电话）
- 科室列表（横向滚动标签）
- 医生排班列表（日期 + 时间段 + 剩余号源 + 预约按钮）

#### 3.2.3 预约确认（BookingConfirm.vue）
- 信息汇总卡片（医院、科室、医生、日期、时间段、费用）
- 确认按钮（主色渐变 圆角 pill）

#### 3.2.4 我的订单（MyOrders.vue）
- 订单卡片（圆角 12px, 顶部医院名+状态标签, 详情行，底部金额）
- 下拉刷新 + 空状态

#### 3.2.5 订单详情（OrderDetail.vue）
- 详情卡片 + 状态流程指示
- 就诊信息汇总

### 3.3 医生端（2页）
**技术栈**：Element Plus 组件 + 自定义 CSS 变量覆盖

**布局改造**（DoctorLayout.vue）：
- 当前：侧边栏（el-aside 200px）+ 顶栏 + 主体
- 改为：顶部导航栏（渐变背景，Logo + 菜单项 + 用户信息）+ 内容区
- 仪表盘统计行：今日排班数 / 预约患者数 / 剩余号源数（3 个统计卡片）

#### 3.3.1 排班管理（DoctorSchedule.vue）
- 顶部统计卡片行
- 美化表格：表头背景色、圆角、行悬浮效果
- 新增按钮改为渐变圆角样式
- 分页保持

#### 3.3.2 患者列表（DoctorPatients.vue）
- 统计卡片 + 美化表格
- 状态标签统一风格

### 3.4 管理端（4页）
**技术栈**：Element Plus 组件 + 自定义 CSS 变量覆盖

**布局改造**（AdminLayout.vue）：
- 同医生端布局：顶部导航栏 + 内容区
- 仪表盘统计行：医院总数 / 医生总数 / 排班总数 / 订单总数（4 个统计卡片）

#### 3.4.1 医院管理（AdminHospitals.vue）
- 统计卡片 + 美化表格 + 圆角对话框

#### 3.4.2 医生管理（AdminDoctors.vue）
- 同医院管理风格

#### 3.4.3 排班管理（AdminSchedules.vue）
- 同医院管理风格

#### 3.4.4 订单管理（AdminOrders.vue）
- 同医院管理风格

## 4. 实现策略

### 4.1 Element Plus 主题覆盖
在 `main.ts` 或独立 SCSS 文件中覆盖 Element Plus CSS 变量：
- `--el-color-primary: #4facfe`
- `--el-border-radius-base: 8px`
- `--el-box-shadow-light: 0 2px 12px rgba(0,0,0,.06)`
- 表格、按钮、对话框等组件统一受影响

### 4.2 Vant 主题覆盖
在 App.vue 或全局 CSS 中覆盖 Vant CSS 变量：
- `--van-primary-color: #4facfe`
- `--van-card-border-radius: 12px`
- NavBar、Tabbar、Button 等组件统一受影响

### 4.3 全局样式文件
新建 `src/styles/global.css`：
- CSS 变量定义（颜色、圆角、阴影）
- 通用工具类
- 渐变背景类
- 卡片类

### 4.4 不涉及
- 路由结构无变化
- API 接口无变化
- 数据逻辑无变化
- 后端代码不动

## 5. 文件变更清单

| 操作 | 文件 | 说明 |
|------|------|------|
| 新建 | `src/styles/global.css` | 全局设计变量 |
| 修改 | `src/main.ts` | 引入 global.css |
| 修改 | `src/views/Login.vue` | 全屏渐变背景 |
| 修改 | `src/views/Register.vue` | 全屏渐变背景 |
| 修改 | `src/views/patient/Hospitals.vue` | 科室入口+卡片 |
| 修改 | `src/views/patient/HospitalDetail.vue` | 卡片优化 |
| 修改 | `src/views/patient/BookingConfirm.vue` | 卡片优化 |
| 修改 | `src/views/patient/MyOrders.vue` | 卡片圆角优化 |
| 修改 | `src/views/patient/OrderDetail.vue` | 卡片优化 |
| 修改 | `src/layouts/DoctorLayout.vue` | 侧边栏→顶部导航 |
| 修改 | `src/views/doctor/DoctorSchedule.vue` | 统计卡片+美化 |
| 修改 | `src/views/doctor/DoctorPatients.vue` | 美化表格 |
| 修改 | `src/layouts/AdminLayout.vue` | 侧边栏→顶部导航 |
| 修改 | `src/views/admin/AdminHospitals.vue` | 美化表格 |
| 修改 | `src/views/admin/AdminDoctors.vue` | 美化表格 |
| 修改 | `src/views/admin/AdminSchedules.vue` | 美化表格 |
| 修改 | `src/views/admin/AdminOrders.vue` | 美化表格 |

共 **17 个文件**（2 新建 + 15 修改）。
