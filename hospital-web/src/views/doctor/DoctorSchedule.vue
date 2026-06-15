<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>我的排班</h3>
      <el-button type="primary" @click="showDialog = true">新增排班</el-button>
    </div>
    <el-table :data="pagedSchedules" border stripe style="width:100%;" table-layout="auto">
      <el-table-column prop="id" label="ID" width="55" align="center" />
      <el-table-column prop="hospitalName" label="医院" min-width="155" show-overflow-tooltip />
      <el-table-column prop="departmentName" label="科室" width="75" align="center" />
      <el-table-column prop="workDate" label="日期" width="105" align="center" />
      <el-table-column prop="startTime" label="开始时间" width="95" align="center" />
      <el-table-column prop="endTime" label="结束时间" width="95" align="center" />
      <el-table-column prop="totalQuota" label="最大号源" width="85" align="center" />
      <el-table-column prop="remainQuota" label="剩余号源" width="85" align="center" />
      <el-table-column prop="price" label="价格" width="75" align="center" />
    </el-table>

    <!-- 分页 -->
    <div style="display:flex;justify-content:flex-end;margin-top:16px;">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="onPageChange"
        @size-change="onPageChange"
      />
    </div>

    <!-- 新增排班对话框 -->
    <el-dialog v-model="showDialog" title="新增排班" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="医院">
          <el-select v-model="form.hospitalId" placeholder="请选择医院" @change="onHospitalChange">
            <el-option v-for="h in hospitals" :key="h.id" :label="h.name" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.departmentId" placeholder="请选择科室">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.workDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="form.startTime" placeholder="开始时间" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="form.endTime" placeholder="结束时间" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="号源数">
          <el-input-number v-model="form.maxQuota" :min="1" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="addSchedule">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

const allSchedules = ref<any[]>([]);
const hospitals = ref<any[]>([]);
const departments = ref<any[]>([]);
const showDialog = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const form = ref({ hospitalId: null as any, departmentId: null as any, workDate: '', startTime: '', endTime: '', maxQuota: 10, price: 50 });

// 前端分页：总条数
const total = computed(() => allSchedules.value.length);
// 前端分页：当前页数据
const pagedSchedules = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return allSchedules.value.slice(start, start + pageSize.value);
});

const onPageChange = () => {}; // v-model已处理，占位

// 加载全部排班
const loadSchedules = async () => {
  const res: any = await request.get('/schedule/doctor/my');
  if (res.code === 200) allSchedules.value = res.data || [];
  currentPage.value = 1;
};

// 加载医院列表
const loadHospitals = async () => {
  const res: any = await request.get('/hospital/list', { params: { current: 1, size: 100 } });
  if (res.code === 200) hospitals.value = res.data.records || res.data;
};

loadSchedules();
loadHospitals();

const onHospitalChange = async (hospitalId: number) => {
  form.value.departmentId = null;
  departments.value = [];
  const res: any = await request.get('/department/list', { params: { hospitalId } });
  if (res.code === 200) departments.value = res.data;
};

const addSchedule = async () => {
  const body = {
    hospitalId: form.value.hospitalId,
    departmentId: form.value.departmentId,
    workDate: form.value.workDate,
    startTime: form.value.startTime,
    endTime: form.value.endTime,
    totalQuota: form.value.maxQuota,
    price: form.value.price,
  };
  const res: any = await request.post('/schedule/add', body);
  if (res.code === 200) {
    ElMessage.success('排班新增成功');
    showDialog.value = false;
    await loadSchedules();
  } else {
    ElMessage.error(res.message || '新增失败');
  }
};
</script>
