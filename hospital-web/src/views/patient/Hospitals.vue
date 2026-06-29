<template>
  <div class="home-page">
    <!-- 顶栏 -->
    <div class="home-header">
      <div class="header-top">
        <span class="header-city">北京市</span>
      </div>
      <h1 class="header-title">预约挂号</h1>
      <!-- 搜索框 -->
      <div class="search-box">
        <van-icon name="search" size="16" color="#999" />
        <span class="search-placeholder">搜索医院、科室或医生</span>
      </div>
    </div>

    <!-- 科室快捷入口 -->
    <div class="dept-grid">
      <div class="dept-item" v-for="d in quickDepts" :key="d">
        <div class="dept-name">{{ d }}</div>
      </div>
    </div>

    <!-- 医院列表 -->
    <div class="hospital-section">
      <h3 class="section-title">推荐医院</h3>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
          <div v-for="h in hospitals" :key="h.id" class="hospital-card" @click="$router.push(`/patient/hospital/${h.id}`)">
            <div class="hospital-main">
              <div class="hospital-info">
                <div class="hospital-name">
                  {{ h.name }}
                  <span class="hospital-level">{{ h.level }}</span>
                </div>
                <div class="hospital-addr">{{ h.address }}</div>
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
const quickDepts = ['内科', '外科', '儿科', '眼科', '妇科', '口腔科', '皮肤科', '骨科'];

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
    } else {
      // 业务错误时停止加载，防止无限循环
      finished.value = true;
    }
  } catch (e: any) {
    // 网络错误或 401 时停止加载
    finished.value = true;
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
  background: #1e3a5f;
  padding: 24px 16px 32px;
  color: #fff;
  border-radius: 0;
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
  padding: 8px 0;
}

.dept-name {
  font-size: var(--font-size-body);
  color: var(--text-primary);
  font-weight: 500;
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
