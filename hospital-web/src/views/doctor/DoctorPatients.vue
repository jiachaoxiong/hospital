<template>
  <div>
    <div class="page-header">
      <h3>预约患者列表</h3>
    </div>
    <el-table :data="pagedPatients" border stripe style="width:100%;" table-layout="auto">
      <el-table-column prop="appointmentId" label="预约ID" width="75" align="center" />
      <el-table-column prop="patientName" label="患者姓名" width="85" align="center" />
      <el-table-column prop="patientPhone" label="手机号" width="125" align="center" />
      <el-table-column prop="hospitalName" label="医院" min-width="150" show-overflow-tooltip />
      <el-table-column prop="departmentName" label="科室" width="75" align="center" />
      <el-table-column prop="doctorName" label="医生" width="75" align="center" />
      <el-table-column prop="workDate" label="就诊日期" width="105" align="center" />
      <el-table-column prop="timeSlot" label="时间段" width="135" align="center" />
      <el-table-column label="状态" width="75" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

<script setup lang="ts">
import { ref, computed } from 'vue';
import request from '@/utils/request';

const allPatients = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);

// 前端分页
const total = computed(() => allPatients.value.length);
const pagedPatients = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return allPatients.value.slice(start, start + pageSize.value);
});

const statusTag = (s: string) =>
  ({ PAID: 'success', PENDING: 'warning', VISITED: '' as any, CANCELLED: 'danger' } as any)[s] || 'info';

const statusText = (s: string) =>
  ({ PAID: '已支付', PENDING: '待支付', VISITED: '已就诊', CANCELLED: '已取消' } as any)[s] || s;

// 加载全部患者
(async () => {
  const res: any = await request.get('/booking/doctor/patients');
  if (res.code === 200) allPatients.value = res.data || [];
})();
</script>
