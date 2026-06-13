<template>
  <el-container style="height:100vh;">
    <el-aside width="200px" style="background:#304156;">
      <div style="color:#fff;text-align:center;padding:20px 0;font-size:18px;">医生工作台</div>
      <el-menu :default-active="activeMenu" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF"
        router>
        <el-menu-item index="/doctor/schedule">
          <el-icon><Calendar /></el-icon>
          <span>排班管理</span>
        </el-menu-item>
        <el-menu-item index="/doctor/patients">
          <el-icon><User /></el-icon>
          <span>患者列表</span>
        </el-menu-item>
        <el-menu-item @click="onLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #dcdfe6;display:flex;align-items:center;justify-content:space-between;">
        <span>医生工作台</span>
        <span>{{ userName }}</span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Calendar, User, SwitchButton } from '@element-plus/icons-vue';
import { showToast } from 'vant';

const route = useRoute();
const router = useRouter();
const activeMenu = computed(() => route.path);
const userName = localStorage.getItem('name') || '医生';

// 退出登录
const onLogout = () => {
  localStorage.clear();
  showToast('已退出');
  router.push('/login');
};
</script>
