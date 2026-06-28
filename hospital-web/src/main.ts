import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import Vant from 'vant';
import 'vant/lib/index.css';
import '@/styles/global.css';  // 全局设计变量 + 主题覆盖
import App from './App.vue';
import router from './router';

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(ElementPlus);
app.use(Vant);  // 全局注册 Vant 组件（van-form、van-field 等）
app.mount('#app');
