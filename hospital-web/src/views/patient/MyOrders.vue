<template>
  <div>
    <van-nav-bar title="我的订单" />
    <van-pull-refresh v-model="refreshing" @refresh="loadOrders">
      <van-cell v-for="o in orders" :key="o.id" :title="'订单 #' + o.id"
        :label="'金额: ¥' + o.amount + ' | 状态: ' + o.status" />
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import request from '@/utils/request';

const orders = ref<any[]>([]);
const refreshing = ref(false);

// 加载我的订单列表
const loadOrders = async () => {
  refreshing.value = true;
  try {
    const res: any = await request.get('/order/my');
    if (res.code === 200) orders.value = res.data;
  } finally { refreshing.value = false; }
};

onMounted(loadOrders);
</script>
