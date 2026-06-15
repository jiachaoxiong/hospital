<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>订单管理</h3>
    </div>
    <el-table :data="orders" border stripe style="width:100%;" table-layout="auto">
      <el-table-column prop="id" label="ID" width="55" align="center" />
      <el-table-column prop="userId" label="用户ID" width="75" align="center" />
      <el-table-column prop="hospitalName" label="医院" min-width="150" show-overflow-tooltip />
      <el-table-column prop="deptName" label="科室" width="75" align="center" />
      <el-table-column prop="doctorName" label="医生" width="75" align="center" />
      <el-table-column prop="workDate" label="就诊日期" width="105" align="center" />
      <el-table-column prop="timeSlot" label="时段" width="135" align="center" />
      <el-table-column prop="amount" label="金额" width="75" align="center" />
      <el-table-column label="状态" width="75" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="165" align="center" />
      <el-table-column label="操作" width="85" align="center">
        <template #default="{ row }">
          <el-popconfirm title="确定删除该订单吗？" @confirm="doDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="display:flex;justify-content:flex-end;margin-top:16px;">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadList"
        @size-change="loadList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

const orders = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const statusTag = (s: string) =>
  ({ PAID: 'success', PENDING: 'warning', VISITED: '' as any, CANCELLED: 'danger' } as any)[s] || 'info';

const statusText = (s: string) =>
  ({ PAID: '已支付', PENDING: '待支付', VISITED: '已就诊', CANCELLED: '已取消' } as any)[s] || s;

// 分页加载订单
const loadList = async () => {
  const res: any = await request.get('/order/list', { params: { current: currentPage.value, size: pageSize.value } });
  if (res.code === 200) {
    orders.value = res.data.records || [];
    total.value = res.data.total || 0;
  }
};

loadList();

const doDelete = async (id: number) => {
  const res: any = await request.delete(`/order/${id}`);
  if (res.code === 200) {
    ElMessage.success('删除成功');
    await loadList();
  } else {
    ElMessage.error(res.message || '删除失败');
  }
};
</script>
