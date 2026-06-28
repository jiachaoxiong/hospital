<template>
  <div>
    <van-nav-bar title="我的订单" />

    <!-- 空状态 -->
    <van-empty v-if="!loading && orders.length === 0" description="暂无订单" />

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="loadOrders">
      <div v-for="o in orders" :key="o.id" style="margin: 12px 16px;"
        @click="goDetail(o)">
        <!-- 订单卡片 -->
        <div style="background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.08);">
          <!-- 顶部：医院名 + 状态标签 -->
          <div style="display:flex;justify-content:space-between;align-items:center;padding:12px 16px;background:#f0f5ff;border-bottom:1px solid #e8e8e8;">
            <span style="font-weight:600;font-size:15px;color:#333;">
              🏥 {{ o.hospitalName || '——' }}
            </span>
            <van-tag :type="statusType(o.status)" size="medium">
              {{ statusText(o.status) }}
            </van-tag>
          </div>

          <!-- 订单详情 -->
          <div style="padding:12px 16px;">
            <div style="display:flex;justify-content:space-between;margin-bottom:6px;">
              <span style="color:#666;font-size:13px;">科室</span>
              <span style="color:#333;font-size:13px;">{{ o.deptName || '——' }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;margin-bottom:6px;">
              <span style="color:#666;font-size:13px;">医生</span>
              <span style="color:#333;font-size:13px;">{{ o.doctorName }} {{ o.doctorTitle }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;margin-bottom:6px;">
              <span style="color:#666;font-size:13px;">就诊时间</span>
              <span style="color:#333;font-size:13px;">{{ o.workDate }} {{ o.timeSlot }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;align-items:center;border-top:1px dashed #eee;padding-top:8px;margin-top:4px;">
              <span style="color:#999;font-size:12px;">订单号 #{{ o.id }}</span>
              <span style="font-weight:bold;color:#e74c3c;font-size:16px;">¥{{ o.amount }}</span>
            </div>
          </div>
        </div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();
const orders = ref<any[]>([]);
const loading = ref(true);
const refreshing = ref(false);

// 状态映射
const statusType = (s: string) =>
  ({ PAID: 'success', PENDING: 'warning', VISITED: 'primary', CANCELLED: 'danger' } as any)[s] || 'default';

const statusText = (s: string) =>
  ({ PAID: '已支付', PENDING: '待支付', VISITED: '已就诊', CANCELLED: '已取消' } as any)[s] || s;

// 加载订单列表
const loadOrders = async () => {
  refreshing.value = true;
  try {
    const res: any = await request.get('/order/my');
    if (res.code === 200) orders.value = res.data || [];
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

// 跳转订单详情
const goDetail = (order: any) => {
  router.push(`/patient/order-detail/${order.id}`);
};

onMounted(loadOrders);
</script>

<style scoped>
:deep(.van-nav-bar) {
  background: var(--color-primary-gradient);
}
</style>
