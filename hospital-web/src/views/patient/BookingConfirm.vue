<template>
  <div>
    <van-nav-bar title="确认预约" left-arrow @click-left="$router.back()" />

    <!-- 预约信息卡 -->
    <van-cell-group>
      <van-cell title="就诊医院" :value="query.hospitalName" />
      <van-cell title="就诊科室" :value="query.deptName" />
      <van-cell title="医生" :value="query.doctorName + ' | ' + query.doctorTitle" />
      <van-cell title="就诊日期" :value="query.workDate" />
      <van-cell title="就诊时段">
        <template #value>
          <van-tag type="primary" size="medium">
            {{ formatTimeSlot(query.startTime, query.endTime) }}
          </van-tag>
        </template>
      </van-cell>
      <van-cell title="挂号费" :value="'¥' + query.price" />
    </van-cell-group>

    <!-- 确认按钮 -->
    <div style="padding: 24px 16px; text-align: center;">
      <van-button type="primary" block round :loading="submitting" @click="createOrder"
        size="large" class="confirm-btn">确认挂号 · ¥{{ query.price }}</van-button>
    </div>

    <!-- ============ 模拟支付弹窗 ============ -->
    <van-popup v-model:show="showPayPopup" position="bottom" round
      :close-on-click-overlay="false" :style="{ background: '#f7f8fa', padding: '24px 16px 32px' }">

      <div style="text-align: center;">
        <h3 style="margin: 0 0 4px;">微信支付（模拟）</h3>
        <p style="color: #999; font-size: 13px; margin: 0 0 20px;">
          本系统为演示项目，支付环节为模拟
        </p>

        <!-- 支付金额 -->
        <div style="background: #fff; border-radius: 12px; padding: 20px; margin-bottom: 20px;">
          <div style="color: #999; font-size: 14px;">支付金额</div>
          <div style="font-size: 36px; font-weight: bold; color: #333; margin: 8px 0;">
            ¥{{ query.price }}
          </div>
          <div style="color: #999; font-size: 12px;">
            订单号：{{ orderNo || '——' }}
          </div>
        </div>

        <!-- 支付按钮 -->
        <van-button type="primary" block round size="large"
          :loading="paying" @click="doPay" style="margin-bottom: 12px;">
          {{ paying ? '支付处理中...' : '立即支付（模拟）' }}
        </van-button>
        <van-button plain block round size="large" @click="cancelPay"
          :disabled="paying">取消支付</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const route = useRoute();
const router = useRouter();
const submitting = ref(false);   // 创建订单 loading
const showPayPopup = ref(false); // 支付弹窗
const paying = ref(false);       // 支付中 loading
const orderNo = ref('');         // 当前订单号
const currentOrderId = ref(0);   // 当前订单ID

// 从 URL 参数读取预约信息
const query = computed(() => ({
  hospitalName: (route.query.hospitalName as string) || '',
  deptName: (route.query.deptName as string) || '',
  doctorName: (route.query.doctorName as string) || '',
  doctorTitle: (route.query.doctorTitle as string) || '',
  workDate: (route.query.workDate as string) || '',
  startTime: (route.query.startTime as string) || '',
  endTime: (route.query.endTime as string) || '',
  price: (route.query.price as string) || '0',
}));

// 格式化时段
const formatTimeSlot = (start: string, end: string) => {
  if (start.startsWith('08')) return '上午 08:00-12:00';
  return '下午 14:00-17:00';
};

// 第一步：创建预约 + 创建订单，然后弹出支付弹窗
const createOrder = async () => {
  submitting.value = true;
  try {
    // 创建预约
    const res: any = await request.post('/booking/create', null, {
      params: { scheduleId: route.params.scheduleId }
    });
    if (res.code !== 200) { showToast(res.message || '预约失败'); return; }

    // 创建订单（携带就诊信息）
    const amount = parseInt(query.value.price) || 50;
    const orderRes: any = await request.post('/order/create', null, {
      params: {
        appointmentId: res.data, amount,
        hospitalName: query.value.hospitalName,
        deptName: query.value.deptName,
        doctorName: query.value.doctorName,
        doctorTitle: query.value.doctorTitle,
        workDate: query.value.workDate,
        timeSlot: formatTimeSlot(query.value.startTime, query.value.endTime),
      }
    });
    if (orderRes.code !== 200) { showToast(orderRes.message || '创建订单失败'); return; }

    currentOrderId.value = orderRes.data;
    orderNo.value = 'HOSP' + Date.now();
    showPayPopup.value = true; // 弹出支付弹窗
  } catch (e: any) {
    showToast(e.response?.data?.message || '操作失败');
  } finally {
    submitting.value = false;
  }
};

// 第二步：在弹窗中执行模拟支付
const doPay = async () => {
  paying.value = true;
  try {
    const amount = parseInt(query.value.price) || 50;

    // 发起支付
    const payRes: any = await request.post('/payment/pay', null, {
      params: { orderId: currentOrderId.value, amount }
    });
    if (payRes.code !== 200) { showToast(payRes.message || '支付失败'); return; }

    // 模拟 1 秒处理延迟
    await new Promise(r => setTimeout(r, 800));

    // 支付回调
    await request.post('/payment/callback', null, {
      params: { orderId: currentOrderId.value, tradeNo: payRes.data.tradeNo }
    });
    // 通知订单服务
    await request.post(`/order/pay-success/${currentOrderId.value}`);

    showPayPopup.value = false;
    showToast('支付成功！挂号完成');
    router.push('/patient/orders');
  } catch (e: any) {
    showToast(e.response?.data?.message || '支付失败');
  } finally {
    paying.value = false;
  }
};

// 取消支付
const cancelPay = () => {
  showPayPopup.value = false;
  showToast('已取消支付');
};
</script>

<style scoped>
:deep(.van-nav-bar) {
  background: #1e3a5f;
}
.confirm-btn {
  height: 48px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 2px;
}
</style>
