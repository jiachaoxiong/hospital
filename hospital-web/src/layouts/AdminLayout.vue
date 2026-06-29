<template>
  <!-- 管理后台 — 顶部导航布局 -->
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <span class="nav-title">管理后台</span>
      </div>
      <nav class="nav-menu">
        <router-link to="/admin/hospitals" class="nav-item" active-class="nav-item--active">
          <el-icon><OfficeBuilding /></el-icon> 医院管理
        </router-link>
        <router-link to="/admin/doctors" class="nav-item" active-class="nav-item--active">
          <el-icon><UserFilled /></el-icon> 医生管理
        </router-link>
        <router-link to="/admin/schedules" class="nav-item" active-class="nav-item--active">
          <el-icon><Calendar /></el-icon> 排班管理
        </router-link>
        <router-link to="/admin/orders" class="nav-item" active-class="nav-item--active">
          <el-icon><Document /></el-icon> 订单管理
        </router-link>
      </nav>
      <div class="nav-right">
        <span class="nav-user">{{ userName }}</span>
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
import { OfficeBuilding, UserFilled, Calendar, Document } from '@element-plus/icons-vue';

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
