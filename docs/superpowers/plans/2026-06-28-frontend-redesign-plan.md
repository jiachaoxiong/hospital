# 前端界面重设计 — 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 对医院预约挂号系统前端 13 个页面进行全面重设计，统一配色/圆角/阴影，将桌面端从侧边栏改为顶部导航布局。

**Architecture:** 新建全局 CSS 变量文件统一设计 Token，覆盖 Element Plus 和 Vant CSS 变量实现组件级主题同步，逐个页面改造模板和样式。

**Tech Stack:** Vue 3 + TypeScript + Vite + Element Plus 2.14 + Vant 4.9 + CSS Variables

---

### Task 1: 新建全局 CSS 变量文件

**Files:**
- Create: `gkzy/gkzy-web/src/styles/global.css` → 实际路径见下方

**实际路径：**
- Create: `D:\ccceshi\jianli\hospital\hospital-web\src\styles\global.css`

**说明：** 建立统一的设计 Token（CSS 变量），所有页面通过引用这些变量保持一致性。

- [ ] **Step 1: 创建 global.css**

```css
/* ========================================
 * 全局设计变量 — 医院预约挂号系统
 * 现代清新蓝风格，统一三端视觉
 * ======================================== */

/* ---------- CSS 变量定义 ---------- */
:root {
  /* 主色调 */
  --color-primary: #4facfe;
  --color-primary-end: #00f2fe;
  --color-primary-gradient: linear-gradient(135deg, #4facfe, #00f2fe);
  --color-dark-bg: #1e3a5f;
  --color-dark-bg-light: #2a4d7a;

  /* 功能色 */
  --color-success: #67c23a;
  --color-warning: #e6a23c;
  --color-danger: #f56c6c;
  --color-info: #909399;

  /* 背景 */
  --bg-page: #f5f7fa;
  --bg-card: #ffffff;

  /* 文字 */
  --text-primary: #2c3e50;
  --text-secondary: #666666;
  --text-placeholder: #999999;
  --text-light: #bbbbbb;

  /* 圆角 */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-round: 20px;

  /* 阴影 */
  --shadow-card: 0 2px 12px rgba(0, 0, 0, 0.06);
  --shadow-hover: 0 4px 16px rgba(0, 0, 0, 0.1);
  --shadow-dialog: 0 8px 24px rgba(0, 0, 0, 0.12);

  /* 字体 */
  --font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", sans-serif;
  --font-size-title: 18px;
  --font-size-subtitle: 15px;
  --font-size-body: 14px;
  --font-size-caption: 12px;

  /* 间距 */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 12px;
  --spacing-lg: 16px;
  --spacing-xl: 20px;
  --spacing-xxl: 24px;
}

/* ---------- 全局重置 ---------- */
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: var(--font-family);
  font-size: var(--font-size-body);
  color: var(--text-primary);
  background: var(--bg-page);
  -webkit-font-smoothing: antialiased;
}

/* ---------- 通用工具类 ---------- */

/* 渐变背景 */
.gradient-bg {
  background: var(--color-primary-gradient);
}

/* 卡片容器 */
.card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: var(--spacing-lg);
}

/* 统计卡片 */
.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: var(--spacing-lg);
  text-align: center;
  flex: 1;
}

.stat-card .stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-card .stat-label {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin-top: var(--spacing-xs);
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.page-header h3 {
  margin: 0;
  font-size: var(--font-size-title);
  font-weight: 700;
  color: var(--text-primary);
}

/* 统计卡片行 */
.stat-row {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

/* ======== 覆盖 Element Plus CSS 变量 ======== */
:root {
  --el-color-primary: #4facfe;
  --el-color-primary-light-3: #7ec4ff;
  --el-color-primary-light-5: #a0d4ff;
  --el-color-primary-light-7: #c2e4ff;
  --el-color-primary-light-9: #e4f3ff;
  --el-color-primary-dark-2: #3b8fd9;
  --el-color-success: #67c23a;
  --el-color-warning: #e6a23c;
  --el-color-danger: #f56c6c;
  --el-border-radius-base: var(--radius-md);
  --el-border-radius-small: var(--radius-sm);
  --el-border-radius-round: var(--radius-round);
  --el-box-shadow-light: var(--shadow-card);
  --el-font-family: var(--font-family);
}

/* Element Plus 表格美化 */
.el-table {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.el-table th.el-table__cell {
  background: #f0f7ff !important;
  color: var(--text-primary);
  font-weight: 600;
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: #fafcff;
}

.el-table .el-table__row:hover > td.el-table__cell {
  background: #e8f4fd !important;
}

/* Element Plus 按钮圆角增强 */
.el-button {
  border-radius: var(--radius-md);
}

.el-button--primary {
  background: var(--color-primary-gradient);
  border: none;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #5dbcf8, #1adde8);
  border: none;
}

/* Element Plus 对话框圆角 */
.el-dialog {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.el-dialog__header {
  background: var(--bg-page);
  border-bottom: 1px solid #e8e8e8;
}

/* Element Plus 分页 */
.el-pagination {
  --el-pagination-button-bg-color: var(--bg-card);
}

/* Element Plus 输入框 */
.el-input__wrapper {
  border-radius: var(--radius-md);
}

.el-select .el-input__wrapper {
  border-radius: var(--radius-md);
}

/* ======== 覆盖 Vant CSS 变量 ======== */
:root {
  --van-primary-color: #4facfe;
  --van-card-border-radius: 12px;
  --van-cell-font-size: 14px;
  --van-cell-line-height: 22px;
  --van-nav-bar-background: transparent;
  --van-nav-bar-title-text-color: #ffffff;
  --van-nav-bar-icon-color: #ffffff;
  --van-nav-bar-text-color: #ffffff;
  --van-tabbar-item-active-color: #4facfe;
  --van-button-primary-background: linear-gradient(135deg, #4facfe, #00f2fe);
  --van-button-primary-border-color: transparent;
  --van-button-border-radius: 20px;
  --van-button-default-border-radius: 20px;
  --van-tag-border-radius: 4px;
  --van-popup-round-border-radius: 16px;
  --van-cell-group-background: transparent;
}
```

- [ ] **Step 2: 验证文件创建成功**

```bash
ls -la D:/ccceshi/jianli/hospital/hospital-web/src/styles/global.css
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/styles/global.css && git commit -m "feat: 新建全局CSS变量文件，定义设计Token"
```

---

### Task 2: 更新 main.ts 引入全局样式

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\main.ts`

- [ ] **Step 1: 在 main.ts 顶部添加 global.css 引入**

修改 `main.ts`：

```typescript
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import Vant from 'vant';
import 'vant/lib/index.css';
import App from './App.vue';
import router from './router';
import '@/styles/global.css';  // 全局设计变量 + 主题覆盖

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(ElementPlus);
app.use(Vant);  // 全局注册 Vant 组件（van-form、van-field 等）
app.mount('#app');
```

- [ ] **Step 2: 验证前端编译通过**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/main.ts && git commit -m "feat: main.ts引入全局样式和主题覆盖"
```

---

### Task 3: 重设计登录页 (Login.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\Login.vue`

**设计：** 全屏渐变背景 + 居中白色卡片 + Logo + 圆角输入框 + 渐变按钮

- [ ] **Step 1: 重写 Login.vue**

```vue
<template>
  <!-- 全屏渐变背景容器 -->
  <div class="login-page">
    <!-- 白色卡片 -->
    <div class="login-card">
      <!-- Logo区 -->
      <div class="login-logo">
        <div class="logo-icon">🏥</div>
        <h1 class="logo-title">医院预约挂号系统</h1>
        <p class="logo-subtitle">在线挂号 · 便捷就医</p>
      </div>

      <!-- 表单 -->
      <van-form @submit="onLogin">
        <div class="form-group">
          <van-field
            v-model="phone"
            name="phone"
            placeholder="请输入手机号"
            left-icon="phone-o"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
        </div>
        <div class="form-group">
          <van-field
            v-model="password"
            name="password"
            placeholder="请输入密码"
            type="password"
            left-icon="lock"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </div>
        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" class="submit-btn">
            登 录
          </van-button>
        </div>
        <div class="form-footer">
          <van-button size="small" type="default" @click="$router.push('/register')">
            还没有账号？去注册
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const router = useRouter();
const phone = ref('');
const password = ref('');

/** 登录表单提交 */
const onLogin = async () => {
  try {
    const res: any = await request.post('/auth/login', { phone: phone.value, password: password.value });
    if (res.code === 200) {
      localStorage.setItem('accessToken', res.data.accessToken);
      localStorage.setItem('refreshToken', res.data.refreshToken);
      localStorage.setItem('role', res.data.role);
      localStorage.setItem('name', res.data.name);
      showToast('登录成功');
      const role = res.data.role;
      if (role === 'PATIENT') router.push('/patient/hospitals');
      else if (role === 'DOCTOR') router.push('/doctor/schedule');
      else if (role === 'ADMIN') router.push('/admin/hospitals');
    } else {
      showToast(res.message || '登录失败');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '登录失败');
  }
};
</script>

<style scoped>
/* 全屏渐变背景 */
.login-page {
  min-height: 100vh;
  background: var(--color-primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 白色卡片 */
.login-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dialog);
  padding: 36px 28px;
}

/* Logo区 */
.login-logo {
  text-align: center;
  margin-bottom: var(--spacing-xxl);
}

.logo-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-sm);
}

.logo-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
}

.logo-subtitle {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin: 0;
}

/* 表单组 */
.form-group {
  margin-bottom: var(--spacing-md);
  background: var(--bg-page);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.form-group :deep(.van-field) {
  background: var(--bg-page);
}

/* 登录按钮 */
.form-actions {
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-lg);
}

.submit-btn {
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: var(--radius-round) !important;
}

/* 底部链接 */
.form-footer {
  text-align: center;
}
</style>
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/Login.vue && git commit -m "feat: 重设计登录页 - 全屏渐变+卡片式表单"
```

---

### Task 4: 重设计注册页 (Register.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\Register.vue`

**设计：** 与登录页完全一致的布局和配色，仅表单内容不同。

- [ ] **Step 1: 重写 Register.vue**

```vue
<template>
  <!-- 全屏渐变背景容器 -->
  <div class="register-page">
    <!-- 白色卡片 -->
    <div class="register-card">
      <!-- Logo区 -->
      <div class="register-logo">
        <div class="logo-icon">📝</div>
        <h1 class="logo-title">创建账号</h1>
        <p class="logo-subtitle">注册后即可在线预约挂号</p>
      </div>

      <!-- 表单 -->
      <van-form @submit="onRegister">
        <div class="form-group">
          <van-field
            v-model="phone"
            name="phone"
            placeholder="请输入手机号"
            left-icon="phone-o"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
        </div>
        <div class="form-group">
          <van-field
            v-model="password"
            name="password"
            placeholder="至少6位密码"
            type="password"
            left-icon="lock"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </div>
        <div class="form-group">
          <van-field
            v-model="name"
            name="name"
            placeholder="请输入姓名"
            left-icon="user-o"
            :rules="[{ required: true, message: '请输入姓名' }]"
          />
        </div>
        <div class="form-group">
          <van-field
            v-model="role"
            name="role"
            placeholder="角色：PATIENT / DOCTOR / ADMIN"
            left-icon="manager-o"
            :rules="[{ required: true, message: '请输入角色' }]"
          />
        </div>
        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" class="submit-btn">
            注 册
          </van-button>
        </div>
        <div class="form-footer">
          <van-button size="small" type="default" @click="$router.push('/login')">
            已有账号？去登录
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const router = useRouter();
const phone = ref('');
const password = ref('');
const name = ref('');
const role = ref('PATIENT');

/** 注册表单提交 */
const onRegister = async () => {
  try {
    const res: any = await request.post('/auth/register', {
      phone: phone.value,
      password: password.value,
      name: name.value,
      role: role.value,
    });
    if (res.code === 200) {
      showToast('注册成功，请登录');
      router.push('/login');
    } else {
      showToast(res.message || '注册失败');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '注册失败');
  }
};
</script>

<style scoped>
/* 全屏渐变背景 */
.register-page {
  min-height: 100vh;
  background: var(--color-primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 白色卡片 */
.register-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dialog);
  padding: 36px 28px;
}

/* Logo区 */
.register-logo {
  text-align: center;
  margin-bottom: var(--spacing-xxl);
}

.logo-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-sm);
}

.logo-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
}

.logo-subtitle {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin: 0;
}

/* 表单组 */
.form-group {
  margin-bottom: var(--spacing-md);
  background: var(--bg-page);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.form-group :deep(.van-field) {
  background: var(--bg-page);
}

/* 注册按钮 */
.form-actions {
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-lg);
}

.submit-btn {
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: var(--radius-round) !important;
}

/* 底部链接 */
.form-footer {
  text-align: center;
}
</style>
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/Register.vue && git commit -m "feat: 重设计注册页 - 全屏渐变+卡片式表单"
```

---

### Task 5: 重设计患者端首页 (Hospitals.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\patient\Hospitals.vue`

**设计：** 渐变顶栏 + 搜索框 + 科室快捷入口 4 列 + 医院卡片列表 + Tabbar

- [ ] **Step 1: 重写 Hospitals.vue**

```vue
<template>
  <div class="home-page">
    <!-- 渐变顶栏 -->
    <div class="home-header">
      <div class="header-top">
        <span class="header-city">📍 北京市</span>
      </div>
      <h1 class="header-title">预约挂号</h1>
      <!-- 搜索框 -->
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <span class="search-placeholder">搜索医院、科室或医生</span>
      </div>
    </div>

    <!-- 科室快捷入口 -->
    <div class="dept-grid">
      <div class="dept-item" v-for="d in quickDepts" :key="d.name">
        <div class="dept-icon">{{ d.icon }}</div>
        <div class="dept-name">{{ d.name }}</div>
      </div>
    </div>

    <!-- 医院列表 -->
    <div class="hospital-section">
      <h3 class="section-title">推荐医院</h3>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
          <div v-for="h in hospitals" :key="h.id" class="hospital-card" @click="$router.push(`/patient/hospital/${h.id}`)">
            <div class="hospital-main">
              <div class="hospital-icon">🏥</div>
              <div class="hospital-info">
                <div class="hospital-name">
                  {{ h.name }}
                  <span class="hospital-level">{{ h.level }}</span>
                </div>
                <div class="hospital-addr">📍 {{ h.address }}</div>
              </div>
              <div class="hospital-arrow">›</div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import request from '@/utils/request';

/** 快捷科室入口（静态展示） */
const quickDepts = [
  { name: '内科', icon: '🫀' },
  { name: '外科', icon: '🦴' },
  { name: '儿科', icon: '👶' },
  { name: '眼科', icon: '👁️' },
  { name: '妇科', icon: '👩' },
  { name: '口腔科', icon: '🦷' },
  { name: '皮肤科', icon: '🧴' },
  { name: '骨科', icon: '🦵' },
];

const hospitals = ref<any[]>([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
let page = 1;

/** 加载医院列表 */
const onLoad = async () => {
  loading.value = true;
  try {
    const res: any = await request.get('/hospital/list', { params: { current: page, size: 10 } });
    if (res.code === 200) {
      hospitals.value.push(...res.data.records);
      finished.value = res.data.records.length < 10 || hospitals.value.length >= (res.data.total || 0);
      page++;
    }
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

/** 下拉刷新 */
const onRefresh = () => {
  page = 1;
  hospitals.value = [];
  finished.value = false;
  onLoad();
};
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding-bottom: 50px;
}

/* ====== 渐变顶栏 ====== */
.home-header {
  background: var(--color-primary-gradient);
  padding: 24px 16px 32px;
  color: #fff;
  border-radius: 0 0 20px 20px;
}

.header-top {
  font-size: var(--font-size-caption);
  opacity: 0.85;
  margin-bottom: 4px;
}

.header-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 16px 0;
}

/* 搜索框 */
.search-box {
  background: #fff;
  border-radius: var(--radius-round);
  padding: 12px 16px;
  color: var(--text-placeholder);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-icon {
  font-size: 16px;
}

/* ====== 科室快捷入口 ====== */
.dept-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 16px;
  background: #fff;
  margin: -8px 12px 0;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  position: relative;
  z-index: 1;
}

.dept-item {
  text-align: center;
}

.dept-icon {
  width: 44px;
  height: 44px;
  background: #e8f4fd;
  border-radius: 12px;
  margin: 0 auto 6px;
  line-height: 44px;
  font-size: 22px;
}

.dept-name {
  font-size: var(--font-size-caption);
  color: var(--text-secondary);
}

/* ====== 医院列表 ====== */
.hospital-section {
  padding: 16px 12px;
}

.section-title {
  font-size: var(--font-size-subtitle);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px 4px;
}

/* 医院卡片 */
.hospital-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform 0.15s ease;
}

.hospital-card:active {
  transform: scale(0.98);
}

.hospital-main {
  display: flex;
  align-items: center;
  gap: 10px;
}

.hospital-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #e8f4fd, #d4edfc);
  border-radius: 10px;
  text-align: center;
  line-height: 44px;
  font-size: 22px;
  flex-shrink: 0;
}

.hospital-info {
  flex: 1;
  min-width: 0;
}

.hospital-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.hospital-level {
  font-size: 10px;
  color: var(--color-primary);
  background: #e8f4fd;
  padding: 1px 6px;
  border-radius: var(--radius-sm);
}

.hospital-addr {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hospital-arrow {
  color: var(--color-primary);
  font-size: 24px;
  flex-shrink: 0;
}
</style>
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/patient/Hospitals.vue && git commit -m "feat: 重设计患者首页 - 渐变顶栏+科室入口+医院卡片"
```

---

### Task 6: 美化医院详情页 (HospitalDetail.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\patient\HospitalDetail.vue`

**设计：** 医院头部卡片 + 步骤引导优化 + 卡片式列表

- [ ] **Step 1: 修改 HospitalDetail.vue 模板**

```vue
<template>
  <div class="detail-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="预约挂号" left-arrow @click-left="goBack">
      <template #title>
        <span style="color:#fff;">预约挂号</span>
      </template>
    </van-nav-bar>

    <!-- 医院信息卡片 -->
    <div class="hospital-info-card" v-if="hospital">
      <div class="hospital-name-row">
        <span class="hosp-icon">🏥</span>
        <span class="hosp-name">{{ hospital.name }}</span>
        <span class="hosp-level">{{ hospital.level }}</span>
      </div>
      <div class="hosp-address">📍 {{ hospital.address }}</div>
    </div>

    <!-- 步骤1：选择科室 -->
    <template v-if="step === 1">
      <h3 class="step-title">选择科室</h3>
      <div class="dept-tags">
        <div
          v-for="dept in departments"
          :key="dept.id"
          class="dept-tag"
          @click="selectDept(dept)"
        >
          {{ dept.name }}
          <span class="tag-arrow">›</span>
        </div>
      </div>
    </template>

    <!-- 步骤2：选择日期 -->
    <template v-if="step === 2">
      <div class="selected-info" v-if="selectedDept">
        <span class="selected-label">已选科室</span>
        <span class="selected-value">{{ selectedDept.name }}</span>
      </div>
      <h3 class="step-title">选择日期</h3>
      <div class="date-selector" @click="showDatePicker = true">
        <span>{{ selectedDateStr || '点击选择就诊日期' }}</span>
        <span class="tag-arrow">›</span>
      </div>
    </template>

    <!-- 步骤3：选择医生 -->
    <template v-if="step === 3">
      <div class="selected-info">
        <span class="selected-label">{{ selectedDept?.name }}</span>
        <span class="selected-value">{{ selectedDateStr }}</span>
      </div>
      <h3 class="step-title">选择医生</h3>
      <div
        v-for="doc in doctorList"
        :key="doc.doctorId"
        class="doctor-card"
        @click="selectDoctor(doc)"
      >
        <div class="doc-avatar">👨‍⚕️</div>
        <div class="doc-info">
          <div class="doc-name">{{ doc.doctorName }} <span class="doc-title">{{ doc.doctorTitle }}</span></div>
          <div class="doc-quota">
            <span class="quota-item">🌅 上午剩余 {{ doc.morningQuota || 0 }} 号</span>
            <span class="quota-item">🌇 下午剩余 {{ doc.afternoonQuota || 0 }} 号</span>
          </div>
        </div>
        <span class="tag-arrow">›</span>
      </div>
    </template>

    <!-- 步骤4：选择时段 -->
    <template v-if="step === 4">
      <div class="selected-info" v-if="selectedDoctor">
        <span class="selected-label">{{ selectedDoctor.doctorName }} | {{ selectedDoctor.doctorTitle }}</span>
        <span class="selected-value">{{ selectedDateStr }}</span>
      </div>
      <h3 class="step-title">选择时段</h3>
      <div
        v-for="s in doctorSchedules"
        :key="s.id"
        class="time-card"
        @click="goBooking(s)"
      >
        <div class="time-left">
          <div class="time-slot">
            {{ (s.startTime+'').startsWith('08') ? '🌅 上午 08:00-12:00' : '🌇 下午 14:00-17:00' }}
          </div>
          <div class="time-remain">剩余号源：{{ s.remainQuota }}</div>
        </div>
        <div class="time-right">
          <div class="time-price">¥{{ s.price }}</div>
          <span class="tag-arrow">›</span>
        </div>
      </div>
    </template>

    <!-- 日期选择器弹窗 -->
    <van-popup v-model:show="showDatePicker" position="bottom" round :style="{ background: '#fff' }">
      <van-date-picker
        v-model="currentValues"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>
```

- [ ] **Step 2: 保持 script 不变，替换 style**

保持原有 `<script setup>` 完全不变，仅替换 `<style scoped>` 为：

```css
<style scoped>
.detail-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding-bottom: 20px;
}

/* 覆盖 van-nav-bar 背景 */
:deep(.van-nav-bar) {
  background: var(--color-primary-gradient);
}

/* 医院信息卡片 */
.hospital-info-card {
  background: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.hospital-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.hosp-icon {
  font-size: 20px;
}

.hosp-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.hosp-level {
  font-size: 10px;
  color: var(--color-primary);
  background: #e8f4fd;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.hosp-address {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin-top: 8px;
  padding-left: 28px;
}

/* 步骤标题 */
.step-title {
  font-size: var(--font-size-subtitle);
  font-weight: 600;
  color: var(--text-primary);
  padding: 12px 16px 8px;
  margin: 0;
}

/* 已选信息 */
.selected-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  margin: 12px 12px 0;
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.selected-label {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
}

.selected-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
}

/* 科室标签网格 */
.dept-tags {
  padding: 0 12px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.dept-tag {
  background: #fff;
  padding: 14px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--text-primary);
  cursor: pointer;
}

.dept-tag:active {
  background: #f0f7ff;
}

/* 日期选择器 */
.date-selector {
  background: #fff;
  margin: 0 12px;
  padding: 16px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--text-primary);
  cursor: pointer;
}

/* 医生卡片 */
.doctor-card {
  background: #fff;
  margin: 0 12px 10px;
  padding: 14px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.doctor-card:active {
  background: #f0f7ff;
}

.doc-avatar {
  font-size: 32px;
  flex-shrink: 0;
}

.doc-info {
  flex: 1;
}

.doc-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.doc-title {
  font-size: 12px;
  color: var(--color-primary);
  font-weight: 400;
}

.doc-quota {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

.quota-item {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
}

/* 时段卡片 */
.time-card {
  background: #fff;
  margin: 0 12px 10px;
  padding: 14px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.time-card:active {
  background: #f0f7ff;
}

.time-slot {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.time-remain {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin-top: 4px;
}

.time-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-danger);
}

.tag-arrow {
  color: var(--color-primary);
  font-size: 20px;
}
</style>
```

- [ ] **Step 3: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 4: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/patient/HospitalDetail.vue && git commit -m "feat: 美化医院详情页 - 卡片+圆角+统一配色"
```

---

### Task 7: 美化预约确认页 + 订单列表 + 订单详情 (3页)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\patient\BookingConfirm.vue`
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\patient\MyOrders.vue`
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\patient\OrderDetail.vue`

**设计：** 三类页面统一卡片圆角 12px + 阴影 + 配色

- [ ] **Step 1: 更新 BookingConfirm.vue 样式**

模板保持不动，仅替换 `<style scoped>` 部分。在当前文件末尾追加/替换：

在 BookingConfirm.vue 中，修改模板中 `van-nav-bar` 添加透明背景样式，并在 `<style scoped>` 末尾添加：

```css
/* 统一背景 */
:deep(.van-nav-bar) {
  background: var(--color-primary-gradient);
}
```

以及将确认按钮区改为：

```html
<div style="padding: 24px 16px; text-align: center;">
  <van-button type="primary" block round :loading="submitting" @click="createOrder"
    size="large" class="confirm-btn">确认挂号 · ¥{{ query.price }}</van-button>
</div>
```

同时在 `<style scoped>` 添加：

```css
.confirm-btn {
  height: 48px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 2px;
}
```

- [ ] **Step 2: 更新 MyOrders.vue 样式**

模板保持不动（已经是卡片式设计，仅微调样式变量），在 `<style scoped>` 中确保 NavBar 使用渐变背景：

```css
:deep(.van-nav-bar) {
  background: var(--color-primary-gradient);
}
```

- [ ] **Step 3: 更新 OrderDetail.vue 样式**

NavBar 渐变背景，模板不变：

```css
:deep(.van-nav-bar) {
  background: var(--color-primary-gradient);
}
```

- [ ] **Step 4: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 5: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/patient/BookingConfirm.vue hospital-web/src/views/patient/MyOrders.vue hospital-web/src/views/patient/OrderDetail.vue && git commit -m "feat: 美化患者端预约/订单/详情页 - 统一配色圆角"
```

---

### Task 8: 改造医生端布局 (DoctorLayout.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\layouts\DoctorLayout.vue`

**设计：** 侧边栏 → 顶部导航栏（渐变背景 + 菜单项 + 用户信息）+ 统计卡片行 + 内容

- [ ] **Step 1: 重写 DoctorLayout.vue**

```vue
<template>
  <!-- 医生工作台 — 顶部导航布局 -->
  <div class="doctor-layout">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <span class="nav-logo">🏥</span>
        <span class="nav-title">医院预约系统</span>
      </div>
      <nav class="nav-menu">
        <router-link to="/doctor/schedule" class="nav-item" active-class="nav-item--active">
          📅 排班管理
        </router-link>
        <router-link to="/doctor/patients" class="nav-item" active-class="nav-item--active">
          👥 患者列表
        </router-link>
      </nav>
      <div class="nav-right">
        <span class="nav-user">👤 {{ userName }}</span>
        <button class="nav-logout" @click="onLogout">退出</button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <div class="stat-row">
      <div class="stat-card">
        <div class="stat-number">{{ stats.todaySchedules }}</div>
        <div class="stat-label">今日排班</div>
      </div>
      <div class="stat-card">
        <div class="stat-number" style="color: #67c23a;">{{ stats.todayPatients }}</div>
        <div class="stat-label">预约患者</div>
      </div>
      <div class="stat-card">
        <div class="stat-number" style="color: #e6a23c;">{{ stats.remainQuota }}</div>
        <div class="stat-label">剩余号源</div>
      </div>
    </div>

    <!-- 主体内容 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const router = useRouter();
const userName = localStorage.getItem('name') || '医生';

/** 统计数据 */
const stats = ref({ todaySchedules: 0, todayPatients: 0, remainQuota: 0 });

/** 加载医生统计数据 */
const loadStats = async () => {
  try {
    const res: any = await request.get('/schedule/doctor/my');
    if (res.code === 200) {
      const list = res.data || [];
      const today = new Date().toISOString().slice(0, 10);
      const todayList = list.filter((s: any) => s.workDate === today);
      stats.value = {
        todaySchedules: todayList.length,
        todayPatients: todayList.reduce((sum: number, s: any) => sum + ((s.totalQuota || 0) - (s.remainQuota || 0)), 0),
        remainQuota: todayList.reduce((sum: number, s: any) => sum + (s.remainQuota || 0), 0),
      };
    }
  } catch { /* 静默处理 */ }
};

onMounted(loadStats);

/** 退出登录 */
const onLogout = () => {
  localStorage.clear();
  showToast('已退出');
  router.push('/login');
};
</script>

<style scoped>
.doctor-layout {
  min-height: 100vh;
  background: var(--bg-page);
}

/* ====== 顶部导航栏 ====== */
.top-nav {
  background: linear-gradient(135deg, #1e3a5f 0%, #2a4d7a 100%);
  color: #fff;
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 56px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 32px;
}

.nav-logo {
  font-size: 20px;
}

.nav-title {
  font-size: 16px;
  font-weight: 700;
}

/* 菜单项 */
.nav-menu {
  display: flex;
  gap: 4px;
  flex: 1;
}

.nav-item {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  padding: 8px 20px;
  border-radius: var(--radius-md);
  font-size: 14px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item--active {
  color: #fff !important;
  background: rgba(255, 255, 255, 0.2) !important;
  font-weight: 600;
}

/* 右侧用户区 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
}

.nav-user {
  opacity: 0.9;
}

.nav-logout {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: #fff;
  padding: 6px 16px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 12px;
  transition: background 0.2s;
}

.nav-logout:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* ====== 统计卡片 ====== */
.stat-row {
  display: flex;
  gap: 16px;
  padding: 20px 24px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-card);
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-label {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin-top: 4px;
}

/* ====== 主体内容 ====== */
.main-content {
  padding: 0 24px 24px;
}
</style>
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/layouts/DoctorLayout.vue && git commit -m "feat: 改造医生端布局 - 侧边栏改为顶部导航+统计卡片"
```

---

### Task 9: 美化医生排班管理页 (DoctorSchedule.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\doctor\DoctorSchedule.vue`

**设计：** 触发统计更新 + 美化按钮 + 表格已有分页保持

- [ ] **Step 1: 新增按钮改为渐变样式**

> 统计卡片已由 DoctorLayout 自行通过 API 加载，子页面无需 emit。

在模板中修改新增按钮：

```html
<el-button type="primary" @click="showDialog = true" class="add-btn">＋ 新增排班</el-button>
```

在 `<style scoped>` 中添加：

```css
.add-btn {
  background: var(--color-primary-gradient) !important;
  border: none !important;
  border-radius: var(--radius-md) !important;
  font-weight: 500;
}
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/doctor/DoctorSchedule.vue && git commit -m "feat: 美化医生排班页 - 渐变按钮"
```

---

### Task 10: 美化医生患者列表页 (DoctorPatients.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\doctor\DoctorPatients.vue`

**设计：** 页面标题优化 + 统计卡片可复用 Layout 级别统计

- [ ] **Step 1: 微调模板**

标题区增加统一风格：

```html
<template>
  <div>
    <div class="page-header">
      <h3>预约患者列表</h3>
    </div>
    <!-- 表格和分页保持不变 -->
    <el-table :data="pagedPatients" border stripe style="width:100%;" table-layout="auto">
      <!-- ... 保持所有列不变 ... -->
    </el-table>
    <div style="display:flex;justify-content:flex-end;margin-top:16px;">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
      />
    </div>
  </div>
</template>
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/doctor/DoctorPatients.vue && git commit -m "feat: 美化医生患者列表 - 统一标题栏风格"
```

---

### Task 11: 改造管理端布局 (AdminLayout.vue)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\layouts\AdminLayout.vue`

**设计：** 侧边栏 → 顶部导航栏（与医生端同款，菜单项不同）+ 4个统计卡片

- [ ] **Step 1: 重写 AdminLayout.vue**

```vue
<template>
  <!-- 管理后台 — 顶部导航布局 -->
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <span class="nav-logo">⚙️</span>
        <span class="nav-title">管理后台</span>
      </div>
      <nav class="nav-menu">
        <router-link to="/admin/hospitals" class="nav-item" active-class="nav-item--active">
          🏥 医院管理
        </router-link>
        <router-link to="/admin/doctors" class="nav-item" active-class="nav-item--active">
          👨‍⚕️ 医生管理
        </router-link>
        <router-link to="/admin/schedules" class="nav-item" active-class="nav-item--active">
          📅 排班管理
        </router-link>
        <router-link to="/admin/orders" class="nav-item" active-class="nav-item--active">
          📋 订单管理
        </router-link>
      </nav>
      <div class="nav-right">
        <span class="nav-user">👤 {{ userName }}</span>
        <button class="nav-logout" @click="onLogout">退出</button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <div class="stat-row">
      <div class="stat-card">
        <div class="stat-number">{{ stats.hospitalCount }}</div>
        <div class="stat-label">医院总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number" style="color: #67c23a;">{{ stats.doctorCount }}</div>
        <div class="stat-label">医生总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number" style="color: #e6a23c;">{{ stats.scheduleCount }}</div>
        <div class="stat-label">排班总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number" style="color: #f56c6c;">{{ stats.orderCount }}</div>
        <div class="stat-label">订单总数</div>
      </div>
    </div>

    <!-- 主体内容 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const router = useRouter();
const userName = localStorage.getItem('name') || '管理员';

const stats = ref({ hospitalCount: 0, doctorCount: 0, scheduleCount: 0, orderCount: 0 });

/** 加载统计数据 */
const loadStats = async () => {
  try {
    const [hRes, dRes, sRes, oRes] = await Promise.all([
      request.get('/hospital/list', { params: { current: 1, size: 1 } }),
      request.get('/doctor/page', { params: { current: 1, size: 1 } }),
      request.get('/schedule/list', { params: { current: 1, size: 1 } }),
      request.get('/order/list', { params: { current: 1, size: 1 } }),
    ]);
    stats.value = {
      hospitalCount: (hRes as any).data?.total || 0,
      doctorCount: (dRes as any).data?.total || 0,
      scheduleCount: (sRes as any).data?.total || 0,
      orderCount: (oRes as any).data?.total || 0,
    };
  } catch { /* 静默处理 */ }
};

onMounted(loadStats);

/** 退出登录 */
const onLogout = () => {
  localStorage.clear();
  showToast('已退出');
  router.push('/login');
};
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: var(--bg-page);
}

/* ====== 顶部导航栏 ====== */
.top-nav {
  background: linear-gradient(135deg, #1e3a5f 0%, #2a4d7a 100%);
  color: #fff;
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 56px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 32px;
}

.nav-logo {
  font-size: 20px;
}

.nav-title {
  font-size: 16px;
  font-weight: 700;
}

/* 菜单项 */
.nav-menu {
  display: flex;
  gap: 4px;
  flex: 1;
}

.nav-item {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  padding: 8px 20px;
  border-radius: var(--radius-md);
  font-size: 14px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item--active {
  color: #fff !important;
  background: rgba(255, 255, 255, 0.2) !important;
  font-weight: 600;
}

/* 右侧用户区 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
}

.nav-user {
  opacity: 0.9;
}

.nav-logout {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: #fff;
  padding: 6px 16px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 12px;
  transition: background 0.2s;
}

.nav-logout:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* ====== 统计卡片 ====== */
.stat-row {
  display: flex;
  gap: 16px;
  padding: 20px 24px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-card);
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-label {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin-top: 4px;
}

/* ====== 主体内容 ====== */
.main-content {
  padding: 0 24px 24px;
}
</style>
```

- [ ] **Step 2: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 3: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/layouts/AdminLayout.vue && git commit -m "feat: 改造管理端布局 - 侧边栏改为顶部导航+统计卡片"
```

---

### Task 12: 美化管理端表格页面 (4页)

**Files:**
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\admin\AdminHospitals.vue`
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\admin\AdminDoctors.vue`
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\admin\AdminSchedules.vue`
- Modify: `D:\ccceshi\jianli\hospital\hospital-web\src\views\admin\AdminOrders.vue`

**设计：** 4 个管理页 template 基本保持不变（仅标题行和按钮微调），全局 CSS 已覆盖 Element Plus 表格/按钮/对话框样式，无需大量改动。

- [ ] **Step 1: 更新 AdminHospitals.vue 标题行**

将模板中标题行改为使用统一的 `page-header` 类：

```html
<div class="page-header">
  <h3>医院管理</h3>
  <el-button type="primary" @click="showAdd">＋ 新增医院</el-button>
</div>
```

- [ ] **Step 2: 更新 AdminDoctors.vue 标题行**

同上：

```html
<div class="page-header">
  <h3>医生管理</h3>
  <el-button type="primary" @click="showAdd">＋ 新增医生</el-button>
</div>
```

- [ ] **Step 3: 更新 AdminSchedules.vue 标题行**

```html
<div class="page-header">
  <h3>排班管理</h3>
  <el-button type="primary" @click="showAdd">＋ 新增排班</el-button>
</div>
```

- [ ] **Step 4: 更新 AdminOrders.vue 标题行**

```html
<div class="page-header">
  <h3>订单管理</h3>
</div>
```

- [ ] **Step 5: 验证编译**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1 | head -20
```

- [ ] **Step 6: 提交**

```bash
cd D:/ccceshi/jianli/hospital && git add hospital-web/src/views/admin/AdminHospitals.vue hospital-web/src/views/admin/AdminDoctors.vue hospital-web/src/views/admin/AdminSchedules.vue hospital-web/src/views/admin/AdminOrders.vue && git commit -m "feat: 美化管理端表格页面 - 统一标题栏和按钮样式"
```

---

### Task 13: 最终验证与启动

**说明：** 所有页面改造完成后，启动前端验证编译和运行。

- [ ] **Step 1: TypeScript 类型检查**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vue-tsc --noEmit 2>&1
```

预期：无错误（或仅有轻微的 unused variable 警告）

- [ ] **Step 2: Vite build 验证**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npx vite build 2>&1 | tail -15
```

预期：`✓ built in ...`

- [ ] **Step 3: 启动 dev server 确认页面正常**

```bash
cd D:/ccceshi/jianli/hospital/hospital-web && npm run dev
```

手动验证：
- http://localhost:5173/login — 全屏渐变 + 白色卡片
- http://localhost:5173/patient/hospitals — 渐变顶栏 + 科室入口 + 医院卡片
- http://localhost:5173/doctor/schedule — 顶部导航 + 统计卡片 + 表格
- http://localhost:5173/admin/hospitals — 顶部导航 + 统计卡片 + 表格

- [ ] **Step 4: 最终提交**

```bash
cd D:/ccceshi/jianli/hospital && git add -A && git status
```

确认只有预期的 17 个文件变更后：

```bash
git commit -m "feat: 前端全面重设计完成 - 统一现代清新蓝风格"
```
