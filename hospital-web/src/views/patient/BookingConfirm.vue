<template>
  <div>
    <van-nav-bar title="确认预约" left-arrow @click-left="$router.back()" />
    <van-cell-group>
      <van-cell title="预约信息" label="请确认您的挂号信息" />
    </van-cell-group>
    <div style="padding:16px;text-align:center;">
      <van-button type="primary" block round @click="doBooking" :loading="submitting">确认挂号</van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const route = useRoute();
const router = useRouter();
const submitting = ref(false);

// 确认预约：创建预约 -> 创建订单 -> 模拟支付 -> 支付回调
const doBooking = async () => {
  submitting.value = true;
  try {
    const res: any = await request.post('/booking/create', null, {
      params: { scheduleId: route.params.scheduleId }
    });
    if (res.code === 200) {
      showToast('预约成功！请支付');
      // 创建订单
      const orderRes: any = await request.post('/order/create', null, {
        params: { appointmentId: res.data, amount: 50 }
      });
      if (orderRes.code === 200) {
        // 模拟支付
        const payRes: any = await request.post('/payment/pay', null, {
          params: { orderId: orderRes.data, amount: 50 }
        });
        if (payRes.code === 200) {
          // 模拟支付回调
          await request.post('/payment/callback', null, {
            params: { orderId: orderRes.data, tradeNo: payRes.data.tradeNo }
          });
          // 通知订单服务支付成功
          await request.post(`/order/pay-success/${orderRes.data}`);
          showToast('支付成功！挂号完成');
          router.push('/patient/orders');
        }
      }
    } else {
      showToast(res.message || '预约失败');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '预约失败');
  } finally {
    submitting.value = false;
  }
};
</script>
