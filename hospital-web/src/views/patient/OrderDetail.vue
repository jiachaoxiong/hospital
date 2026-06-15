<template>
  <div>
    <van-nav-bar title="订单详情" left-arrow @click-left="$router.back()" />

    <template v-if="order">
      <!-- 状态卡片 -->
      <div style="margin:16px;padding:20px;background:#fff;border-radius:12px;text-align:center;">
        <div style="font-size:40px;margin-bottom:8px;">
          {{ order.status === 'PAID' ? '✅' : order.status === 'PENDING' ? '⏳' : order.status === 'CANCELLED' ? '❌' : '🏥' }}
        </div>
        <div style="font-size:20px;font-weight:bold;color:#333;">
          {{ statusText(order.status) }}
        </div>
        <div style="color:#999;font-size:13px;margin-top:4px;">
          订单号 #{{ order.id }}
        </div>
      </div>

      <!-- 就诊信息 -->
      <van-cell-group title="就诊信息">
        <van-cell title="就诊医院" :value="order.hospitalName || '——'" />
        <van-cell title="就诊科室" :value="order.deptName || '——'" />
        <van-cell title="医生" :value="(order.doctorName || '') + ' | ' + (order.doctorTitle || '')" />
        <van-cell title="就诊日期" :value="order.workDate || '——'" />
        <van-cell title="就诊时段" :value="order.timeSlot || '——'" />
      </van-cell-group>

      <!-- 订单信息 -->
      <van-cell-group title="订单信息">
        <van-cell title="订单金额" :value="'¥' + (order.amount || 0)" />
        <van-cell title="下单时间" :value="order.createTime || '——'" />
        <van-cell title="支付时间" :value="order.payTime || (order.status === 'PENDING' ? '待支付' : '——')" />
        <van-cell title="订单状态">
          <template #value>
            <van-tag :type="statusType(order.status)">{{ statusText(order.status) }}</van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 操作按钮 -->
      <div style="padding:20px 16px;text-align:center;">
        <van-button v-if="order.status === 'PENDING'" type="danger" block round
          @click="cancelOrder">取消订单</van-button>
        <van-button v-else plain block round @click="$router.back()">返回</van-button>
      </div>
    </template>

    <van-loading v-else style="display:flex;justify-content:center;padding:50px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const route = useRoute();
const order = ref<any>(null);

const statusType = (s: string) =>
  ({ PAID: 'success', PENDING: 'warning', VISITED: 'primary', CANCELLED: 'danger' } as any)[s] || 'default';

const statusText = (s: string) =>
  ({ PAID: '已支付', PENDING: '待支付', VISITED: '已就诊', CANCELLED: '已取消' } as any)[s] || s;

// 加载订单详情
onMounted(async () => {
  try {
    const res: any = await request.get(`/order/${route.params.id}`);
    if (res.code === 200) order.value = res.data;
  } catch (e: any) {
    showToast('加载失败');
  }
});

// 取消订单
const cancelOrder = async () => {
  try {
    const res: any = await request.post(`/order/cancel/${route.params.id}`);
    if (res.code === 200) {
      showToast('订单已取消');
      order.value.status = 'CANCELLED';
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '操作失败');
  }
};
</script>
