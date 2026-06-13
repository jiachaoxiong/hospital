<template>
  <div style="max-width:400px;margin:50px auto;padding:20px;">
    <h2 style="text-align:center;">用户注册</h2>
    <van-form @submit="onRegister">
      <van-cell-group>
        <van-field v-model="phone" name="phone" label="手机号" placeholder="请输入手机号"
          :rules="[{ required: true, message: '请输入手机号' }]" />
        <van-field v-model="password" name="password" label="密码" placeholder="至少6位密码" type="password"
          :rules="[{ required: true, message: '请输入密码' }]" />
        <van-field v-model="name" name="name" label="姓名" placeholder="请输入姓名"
          :rules="[{ required: true, message: '请输入姓名' }]" />
        <van-field v-model="role" name="role" label="角色" placeholder="PATIENT/DOCTOR/ADMIN"
          :rules="[{ required: true, message: '请输入角色' }]" />
      </van-cell-group>
      <div style="margin:16px;">
        <van-button round block type="primary" native-type="submit">注册</van-button>
      </div>
      <div style="text-align:center;">
        <van-button size="small" type="default" @click="$router.push('/login')">已有账号？去登录</van-button>
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
const name = ref('');
const role = ref('PATIENT');

// 注册表单提交
const onRegister = async () => {
  try {
    const res: any = await request.post('/auth/register', {
      phone: phone.value, password: password.value,
      name: name.value, role: role.value
    });
    if (res.code === 200) {
      showToast('注册成功，请登录');
      router.push('/login');
    } else {
      showToast(res.message || '注册失败');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '注册失败');
  }
};
</script>
