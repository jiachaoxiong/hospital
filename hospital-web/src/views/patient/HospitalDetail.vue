<template>
  <div>
    <van-nav-bar title="医院详情" left-arrow @click-left="$router.back()" />
    <van-cell-group v-if="hospital">
      <van-cell :title="hospital.name" :label="hospital.level + ' | ' + hospital.address" />
    </van-cell-group>
    <h3 style="padding:12px;">选择科室</h3>
    <van-cell v-for="dept in departments" :key="dept.id" :title="dept.name" is-link
      @click="selectDept(dept)" />
    <van-action-sheet v-model:show="showDatePicker" title="选择日期">
      <van-datetime-picker v-model="selectedDate" type="date" :min-date="minDate" :max-date="maxDate"
        @confirm="loadSchedules" @cancel="showDatePicker = false" />
    </van-action-sheet>
    <van-cell-group v-if="schedules.length">
      <van-cell v-for="s in schedules" :key="s.id"
        :title="s.workDate + ' ' + s.startTime + '-' + s.endTime"
        :label="'剩余号源: ' + s.remainQuota + ' | ¥' + s.price"
        is-link @click="goBooking(s)" />
    </van-cell-group>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();
const hospital = ref<any>(null);
const departments = ref<any[]>([]);
const schedules = ref<any[]>([]);
const showDatePicker = ref(false);
const selectedDate = ref(new Date());
const minDate = new Date();
const maxDate = new Date(Date.now() + 30 * 86400000);
let selectedDept: any = null;

// 加载医院详情和科室列表
onMounted(async () => {
  const id = route.params.id;
  const res1: any = await request.get(`/hospital/${id}`);
  if (res1.code === 200) hospital.value = res1.data;
  const res2: any = await request.get('/department/list', { params: { hospitalId: id } });
  if (res2.code === 200) departments.value = res2.data;
});

// 选择科室，弹出日期选择器
const selectDept = (dept: any) => { selectedDept = dept; showDatePicker.value = true; };

// 加载排班数据
const loadSchedules = async () => {
  showDatePicker.value = false;
  const dateStr = selectedDate.value.toISOString().split('T')[0];
  const res: any = await request.get('/schedule/available', {
    params: { hospitalId: hospital.value.id, departmentId: selectedDept.id, date: dateStr }
  });
  if (res.code === 200) schedules.value = res.data;
};

// 跳转预约确认页
const goBooking = (schedule: any) => router.push(`/patient/booking/${schedule.id}`);
</script>
