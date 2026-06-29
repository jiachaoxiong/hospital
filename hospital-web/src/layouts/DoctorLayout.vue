<template>
  <!-- 医生工作台 — 顶部导航布局 -->
  <div class="doctor-layout">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <span class="nav-title">医院预约系统</span>
      </div>
      <nav class="nav-menu">
        <router-link to="/doctor/schedule" class="nav-item" active-class="nav-item--active">
          <el-icon><Calendar /></el-icon> 排班管理
        </router-link>
        <router-link to="/doctor/patients" class="nav-item" active-class="nav-item--active">
          <el-icon><User /></el-icon> 患者列表
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
import { Calendar, User } from '@element-plus/icons-vue';

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
