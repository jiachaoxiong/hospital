<template>
  <!-- 全屏渐变背景容器 -->
  <div class="login-page">
    <!-- 白色卡片 -->
    <div class="login-card">
      <!-- Logo区 -->
      <div class="login-logo">
        <h1 class="logo-title">医院预约挂号系统</h1>
        <p class="logo-subtitle">在线挂号 · 便捷就医</p>
      </div>

      <!-- 表单 -->
      <van-form @submit="onLogin">
        <div class="form-group">
          <van-field
            v-model="phone"
            name="phone"
            placeholder="请输入手机号"
            left-icon="phone-o"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
        </div>
        <div class="form-group">
          <van-field
            v-model="password"
            name="password"
            placeholder="请输入密码"
            type="password"
            left-icon="lock"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </div>
        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" class="submit-btn">
            登 录
          </van-button>
        </div>
        <div class="form-footer">
          <van-button size="small" type="default" @click="$router.push('/register')">
            还没有账号？去注册
          </van-button>
        </div>
      </van-form>
    </div>
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

/** 登录表单提交 */
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

<style scoped>
/* 全屏渐变背景 */
.login-page {
  min-height: 100vh;
  background: #1e3a5f;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 白色卡片 */
.login-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dialog);
  padding: 36px 28px;
}

/* Logo区 */
.login-logo {
  text-align: center;
  margin-bottom: var(--spacing-xxl);
}

.logo-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
}

.logo-subtitle {
  font-size: var(--font-size-caption);
  color: var(--text-placeholder);
  margin: 0;
}

/* 表单组 */
.form-group {
  margin-bottom: var(--spacing-md);
  background: var(--bg-page);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.form-group :deep(.van-field) {
  background: var(--bg-page);
}

/* 登录按钮 */
.form-actions {
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-lg);
}

.submit-btn {
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: var(--radius-md) !important;
}

/* 底部链接 */
.form-footer {
  text-align: center;
}
</style>
