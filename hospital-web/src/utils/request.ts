import axios from 'axios';
import router from '@/router';

// Axios 封装：自动携带 token、401 跳转登录
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (res) => res.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      router.push('/login');
    }
    return Promise.reject(error);
  }
);

export default request;
