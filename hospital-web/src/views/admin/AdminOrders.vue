<template>
  <div>
    <h3>订单管理</h3>
    <el-table :data="orders" border stripe style="width:100%;">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="patientName" label="患者" />
      <el-table-column prop="hospitalName" label="医院" />
      <el-table-column prop="departmentName" label="科室" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PAID' ? 'success' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import request from '@/utils/request';

const orders = ref<any[]>([]);

// 加载全部订单列表
onMounted(async () => {
  const res: any = await request.get('/order/list', { params: { current: 1, size: 100 } });
  if (res.code === 200) orders.value = res.data.records || res.data;
});
</script>
