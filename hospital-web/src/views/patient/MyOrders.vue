<template>
  <div>
    <van-nav-bar title="我的订单" />

    <!-- 空状态 -->
    <van-empty v-if="!loading && orders.length === 0" description="暂无订单" />

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="loadOrders">
      <div v-for="o in orders" :key="o.id" style="margin: 12px 16px;">
        <!-- 订单卡片 -->
        <div style="background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.08);">
          <!-- 顶部：医院名 + 状态标签 -->
          <div @click="goDetail(o)" style="display:flex;justify-content:space-between;align-items:center;padding:12px 16px;background:#f0f5ff;border-bottom:1px solid #e8e8e8;">
            <span style="font-weight:600;font-size:15px;color:#333;">
              {{ o.hospitalName || '——' }}
            </span>
            <van-tag :type="statusType(o.status)" size="medium">
              {{ statusText(o.status) }}
            </van-tag>
          </div>

          <!-- 订单详情（点击查看详情） -->
          <div @click="goDetail(o)" style="padding:12px 16px;">
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

          <!-- 操作按钮区：待支付订单显示取消按钮 -->
          <div v-if="o.status === 'PENDING'"
            style="display:flex;justify-content:space-between;align-items:center;padding:10px 16px;border-top:1px solid #f0f0f0;background:#fff;">
            <span style="color:#999;font-size:12px;">订单待支付，可取消</span>
            <van-button size="small" type="danger" plain round
              @click.stop="cancelOrder(o)">
              取消订单
            </van-button>
          </div>
          <div v-else
            style="display:flex;justify-content:flex-end;align-items:center;padding:10px 16px;border-top:1px solid #f0f0f0;background:#fafafa;">
            <span style="color:#999;font-size:12px;">点击卡片查看详情</span>
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
import { showToast, showConfirmDialog } from 'vant';

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

// 取消订单（带确认弹窗）
const cancelOrder = async (order: any) => {
  try {
    await showConfirmDialog({
      title: '确认取消',
      message: `确定要取消 ${order.hospitalName} ${order.doctorName} 医生的预约吗？取消后将释放号源。`,
      confirmButtonText: '确定取消',
      confirmButtonColor: '#e74c3c',
      cancelButtonText: '暂不取消',
    });
    const res: any = await request.post(`/order/cancel/${order.id}`);
    if (res.code === 200) {
      showToast('订单已取消，号源已释放');
      order.status = 'CANCELLED';
    }
  } catch (e: any) {
    if (e.response?.data?.message) {
      showToast(e.response.data.message);
    }
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
  background: #1e3a5f;
}
</style>
