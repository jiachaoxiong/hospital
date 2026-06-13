<template>
  <div style="max-width:400px;margin:50px auto;padding:20px;">
    <h2 style="text-align:center;">医院预约挂号系统</h2>
    <van-form @submit="onLogin">
      <van-cell-group>
        <van-field v-model="phone" name="phone" label="手机号" placeholder="请输入手机号"
          :rules="[{ required: true, message: '请输入手机号' }]" />
        <van-field v-model="password" name="password" label="密码" placeholder="请输入密码" type="password"
          :rules="[{ required: true, message: '请输入密码' }]" />
      </van-cell-group>
      <div style="margin:16px;">
        <van-button round block type="primary" native-type="submit">登录</van-button>
      </div>
      <div style="text-align:center;">
        <van-button size="small" type="default" @click="$router.push('/register')">还没有账号？去注册</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { showToast } from 'vant';

const router = useRouter();
const phone = ref('');
const password = ref('');

// 登录表单提交
const onLogin = async () => {
  try {
    const res: any = await request.post('/auth/login', { phone: phone.value, password: password.value });
    if (res.code === 200) {
      localStorage.setItem('accessToken', res.data.accessToken);
      localStorage.setItem('refreshToken', res.data.refreshToken);
      localStorage.setItem('role', res.data.role);
      localStorage.setItem('name', res.data.name);
      showToast('登录成功');
      const role = res.data.role;
      if (role === 'PATIENT') router.push('/patient/hospitals');
      else if (role === 'DOCTOR') router.push('/doctor/schedule');
      else if (role === 'ADMIN') router.push('/admin/hospitals');
    } else {
      showToast(res.message || '登录失败');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '登录失败');
  }
};
</script>
