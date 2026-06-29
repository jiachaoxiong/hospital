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
        <span class="hosp-icon"><van-icon name="shop-o" /></span>
        <span class="hosp-name">{{ hospital.name }}</span>
        <span class="hosp-level">{{ hospital.level }}</span>
      </div>
      <div class="hosp-address">{{ hospital.address }}</div>
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
        <div class="doc-avatar"><van-icon name="manager-o" size="32" /></div>
        <div class="doc-info">
          <div class="doc-name">{{ doc.doctorName }} <span class="doc-title">{{ doc.doctorTitle }}</span></div>
          <div class="doc-quota">
            <span class="quota-item">上午剩余 {{ doc.morningQuota || 0 }} 号</span>
            <span class="quota-item">下午剩余 {{ doc.afternoonQuota || 0 }} 号</span>
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
            {{ (s.startTime+'').startsWith('08') ? '上午 08:00-12:00' : '下午 14:00-17:00' }}
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

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const route = useRoute();
const router = useRouter();

// 步骤控制：1=科室 2=日期 3=医生 4=时段
const step = ref(1);
const hospital = ref<any>(null);
const departments = ref<any[]>([]);
const showDatePicker = ref(false);
const currentValues = ref<string[]>([]);
const allSchedules = ref<any[]>([]);
const selectedDept = ref<any>(null);
const selectedDateStr = ref('');
const selectedDoctor = ref<any>(null);

const minDate = new Date();
const maxDate = new Date(Date.now() + 30 * 86400000);

// 按医生聚合排班数据（去重、统计上下千号源）
const doctorList = computed(() => {
  const map = new Map<number, any>();
  for (const s of allSchedules.value) {
    const key = s.doctorId;
    if (!map.has(key)) {
      map.set(key, {
        doctorId: s.doctorId,
        doctorName: s.doctorName,
        doctorTitle: s.doctorTitle,
        morningQuota: 0,
        afternoonQuota: 0,
      });
    }
    const doc = map.get(key);
    if ((s.startTime + '').startsWith('08')) {
      doc.morningQuota = Math.max(doc.morningQuota, s.remainQuota || 0);
    } else {
      doc.afternoonQuota = Math.max(doc.afternoonQuota, s.remainQuota || 0);
    }
  }
  return Array.from(map.values());
});

// 选中医生后筛选其排班
const doctorSchedules = computed(() => {
  if (!selectedDoctor.value) return [];
  return allSchedules.value.filter((s: any) => s.doctorId === selectedDoctor.value.doctorId);
});

// 加载医院详情和科室列表
onMounted(async () => {
  const id = route.params.id;
  const res1: any = await request.get(`/hospital/${id}`);
  if (res1.code === 200) hospital.value = res1.data;
  const res2: any = await request.get('/department/list', { params: { hospitalId: id } });
  if (res2.code === 200) departments.value = res2.data;
});

// 返回上一步
const goBack = () => {
  if (step.value === 1) { router.back(); return; }
  if (step.value === 2) { step.value = 1; return; }
  if (step.value === 3) { step.value = 2; return; }
  if (step.value === 4) { step.value = 3; selectedDoctor.value = null; return; }
};

// 步骤1→2：选择科室 → 弹出日期选择器
const selectDept = (dept: any) => {
  selectedDept.value = dept;
  step.value = 2;
};

// 步骤2→3：确认日期 → 加载排班
const onDateConfirm = async (result: any) => {
  showDatePicker.value = false;
  const vals = result?.selectedValues || currentValues.value;
  if (!vals || vals.length < 3) { showToast('请选择完整日期'); return; }
  const [y, m, d] = vals;
  selectedDateStr.value = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;

  try {
    const res: any = await request.get('/schedule/available', {
      params: { hospitalId: hospital.value.id, departmentId: selectedDept.value.id, date: selectedDateStr.value }
    });
    if (res.code === 200 && res.data?.length) {
      allSchedules.value = res.data;
      step.value = 3;
    } else {
      showToast('该日期暂无排班，请换一天');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '网络错误');
  }
};

// 步骤3→4：选择医生 → 显示时段
const selectDoctor = (doc: any) => {
  selectedDoctor.value = doc;
  step.value = 4;
};

// 跳转预约确认页（携带完整信息）
const goBooking = (schedule: any) => {
  const params = new URLSearchParams({
    hospitalName: hospital.value.name,
    deptName: selectedDept.value.name,
    doctorName: schedule.doctorName,
    doctorTitle: schedule.doctorTitle,
    workDate: schedule.workDate,
    startTime: schedule.startTime,
    endTime: schedule.endTime,
    price: schedule.price,
  });
  router.push(`/patient/booking/${schedule.id}?${params.toString()}`);
};
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding-bottom: 20px;
}

/* 覆盖 van-nav-bar 背景 */
:deep(.van-nav-bar) {
  background: #1e3a5f;
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
