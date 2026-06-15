<template>
  <div>
    <van-nav-bar title="选择医院" />
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
        <van-cell v-for="h in hospitals" :key="h.id" :title="h.name"
          :label="h.level + ' | ' + h.city" is-link :to="`/patient/hospital/${h.id}`" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import request from '@/utils/request';

const hospitals = ref<any[]>([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
let page = 1;

// 加载医院列表
const onLoad = async () => {
  loading.value = true;
  try {
    const res: any = await request.get('/hospital/list', { params: { current: page, size: 10 } });
    if (res.code === 200) {
      hospitals.value.push(...res.data.records);
      // 判断是否加载完毕：返回条数少于请求条数，或已加载全部记录
      finished.value = res.data.records.length < 10 || hospitals.value.length >= (res.data.total || 0);
      page++;
    }
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

// 下拉刷新
const onRefresh = () => { page = 1; hospitals.value = []; finished.value = false; onLoad(); };
</script>
