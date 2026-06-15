<template>
  <div>
    <van-nav-bar title="预约挂号" left-arrow @click-left="goBack" />

    <!-- 步骤1：选择科室 -->
    <template v-if="step === 1">
      <van-cell-group v-if="hospital">
        <van-cell :title="hospital.name" :label="hospital.level + ' | ' + hospital.address" />
      </van-cell-group>
      <h3 style="padding:12px;">选择科室</h3>
      <van-cell v-for="dept in departments" :key="dept.id" :title="dept.name" is-link
        @click="selectDept(dept)" />
    </template>

    <!-- 步骤2：选择日期 -->
    <template v-if="step === 2">
      <van-cell-group>
        <van-cell :title="'已选科室：' + selectedDept?.name" />
      </van-cell-group>
      <h3 style="padding:12px;">选择日期</h3>
      <van-cell title="请选择日期" :value="selectedDateStr || '点击选择'" is-link @click="showDatePicker = true" />
    </template>

    <!-- 步骤3：选择医生 -->
    <template v-if="step === 3">
      <van-cell-group>
        <van-cell :title="'科室：' + selectedDept?.name" :label="'日期：' + selectedDateStr" />
      </van-cell-group>
      <h3 style="padding:12px;">选择医生</h3>
      <van-cell v-for="doc in doctorList" :key="doc.doctorId"
        :title="doc.doctorName + ' | ' + doc.doctorTitle"
        :label="'上午剩余 ' + (doc.morningQuota || 0) + ' 号 | 下午剩余 ' + (doc.afternoonQuota || 0) + ' 号'"
        is-link @click="selectDoctor(doc)" />
    </template>

    <!-- 步骤4：选择上/下午时段 -->
    <template v-if="step === 4">
      <van-cell-group>
        <van-cell :title="selectedDoctor?.doctorName + ' | ' + selectedDoctor?.doctorTitle"
          :label="selectedDateStr" />
      </van-cell-group>
      <h3 style="padding:12px;">选择时段</h3>
      <van-cell v-for="s in doctorSchedules" :key="s.id"
        :title="(s.startTime+'').startsWith('08') ? '上午 08:00-12:00' : '下午 14:00-17:00'"
        :label="'剩余号源：' + s.remainQuota + ' | ¥' + s.price"
        is-link @click="goBooking(s)" />
    </template>

    <!-- 日期选择器弹窗 -->
    <van-popup v-model:show="showDatePicker" position="bottom" round :style="{ background: '#fff' }">
      <van-date-picker v-model="currentValues" :min-date="minDate" :max-date="maxDate"
        @confirm="onDateConfirm" @cancel="showDatePicker = false" />
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
