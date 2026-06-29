<template>
  <!-- 深蓝背景全屏居中 -->
  <div class="register-page">
    <!-- 白色卡片 -->
    <div class="register-card">
      <!-- Logo区 -->
      <div class="register-logo">
        <h1 class="logo-title">创建账号</h1>
        <p class="logo-subtitle">注册后即可在线预约挂号</p>
      </div>

      <!-- 表单 -->
      <van-form @submit="onRegister">
        <div class="form-group">
          <van-field
            v-model="phone"
            name="phone"
            placeholder="请输入手机号"
            left-icon="phone-o"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
        </div>
        <!-- 短信验证码 -->
        <div class="form-group code-group">
          <van-field
            v-model="code"
            name="code"
            placeholder="请输入短信验证码"
            left-icon="shield-o"
            :rules="[{ required: true, message: '请输入验证码' }]"
          />
          <van-button
            class="send-code-btn"
            size="small"
            type="primary"
            :disabled="codeCountdown > 0"
            @click="sendCode"
          >
            {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '获取验证码' }}
          </van-button>
        </div>
        <div class="form-group">
          <van-field
            v-model="name"
            name="name"
            placeholder="请输入姓名"
            left-icon="user-o"
            :rules="[{ required: true, message: '请输入姓名' }]"
          />
        </div>
        <div class="form-group">
          <van-field
            v-model="password"
            name="password"
            placeholder="密码需至少8位含字母和数字"
            type="password"
            left-icon="lock"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </div>
        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" class="submit-btn">
            注 册
          </van-button>
        </div>
        <!-- 角色说明：患者直接注册，医生需联系管理员 -->
        <div class="role-notice">
          <div class="role-notice-title">
            <van-icon name="info-o" /> 当前注册为<strong>患者</strong>账号
          </div>
          <div class="role-notice-desc">
            如需注册<strong>医生</strong>账号，请联系医院管理员在后台创建（需关联医院、科室、职称等信息）。
          </div>
        </div>
        <div class="form-footer">
          <van-button size="small" type="default" @click="$router.push('/login')">
            已有账号？去登录
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
import { showToast, showSuccessToast } from 'vant';

const router = useRouter();
const phone = ref('');
const code = ref('');
const name = ref('');
const password = ref('');
/** 验证码按钮倒计时秒数，0表示可发送 */
const codeCountdown = ref(0);

/**
 * 发送短信验证码 — 调用后端接口，启动60秒倒计时
 * 验证码会打印在后端控制台日志中（模拟短信）
 */
const sendCode = async () => {
  if (!phone.value) {
    showToast('请先输入手机号');
    return;
  }
  try {
    const res: any = await request.post('/auth/send-code', null, { params: { phone: phone.value } });
    if (res.code === 200) {
      showSuccessToast('验证码已发送');
      codeCountdown.value = 60;
      const timer = setInterval(() => {
        codeCountdown.value--;
        if (codeCountdown.value <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    } else {
      showToast(res.message || '发送失败');
    }
  } catch (e: any) {
    showToast(e.response?.data?.message || '发送失败');
  }
};

/** 注册表单提交 */
const onRegister = async () => {
  try {
    const res: any = await request.post('/auth/register', {
      phone: phone.value,
      code: code.value,
      password: password.value,
      name: name.value,
      role: 'PATIENT',
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

<style scoped>
/* 全屏深蓝背景 */
.register-page {
  min-height: 100vh;
  background: #1e3a5f;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 白色卡片 */
.register-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dialog);
  padding: 36px 28px;
}

/* Logo区 */
.register-logo {
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

/* 验证码行：输入框 + 按钮横向排列 */
.code-group {
  display: flex;
  align-items: center;
  background: transparent;
  overflow: visible;
}

.code-group :deep(.van-field) {
  flex: 1;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.send-code-btn {
  margin-left: 8px;
  border-radius: var(--radius-md) !important;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 注册按钮 */
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

/* 角色提示卡片 */
.role-notice {
  background: #f0f5ff;
  border-left: 3px solid #1e3a5f;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 16px;
}

.role-notice-title {
  font-size: 13px;
  color: #1e3a5f;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.role-notice-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}
</style>
