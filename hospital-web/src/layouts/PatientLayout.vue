<template>
  <div>
    <router-view />
    <van-tabbar v-model="active" route>
      <van-tabbar-item icon="home-o" to="/patient/hospitals">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/patient/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" @click="onLogout">退出</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';

const router = useRouter();
const active = ref(0);

// 退出登录
const onLogout = () => {
  localStorage.clear();
  showToast('已退出');
  router.push('/login');
};
</script>
