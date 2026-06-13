<template>
  <div>
    <h3>预约患者列表</h3>
    <el-table :data="patients" border stripe style="width:100%;">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="patientPhone" label="手机号" />
      <el-table-column prop="hospitalName" label="医院" />
      <el-table-column prop="departmentName" label="科室" />
      <el-table-column prop="workDate" label="就诊日期" />
      <el-table-column prop="startTime" label="时间段" />
      <el-table-column prop="status" label="状态" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import request from '@/utils/request';

const patients = ref<any[]>([]);

// 加载预约患者列表
onMounted(async () => {
  const res: any = await request.get('/booking/doctor/patients');
  if (res.code === 200) patients.value = res.data;
});
</script>
