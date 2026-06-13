<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>我的排班</h3>
      <el-button type="primary" @click="showDialog = true">新增排班</el-button>
    </div>
    <el-table :data="schedules" border stripe style="width:100%;">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="hospitalName" label="医院" />
      <el-table-column prop="departmentName" label="科室" />
      <el-table-column prop="workDate" label="日期" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="maxQuota" label="最大号源" />
      <el-table-column prop="remainQuota" label="剩余号源" />
      <el-table-column prop="price" label="价格" />
    </el-table>

    <!-- 新增排班对话框 -->
    <el-dialog v-model="showDialog" title="新增排班" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="医院">
          <el-select v-model="form.hospitalId" placeholder="请选择医院">
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
import { ref, onMounted } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

const schedules = ref<any[]>([]);
const hospitals = ref<any[]>([]);
const departments = ref<any[]>([]);
const showDialog = ref(false);
const form = ref({ hospitalId: null, departmentId: null, workDate: '', startTime: '', endTime: '', maxQuota: 10, price: 50 });

// 加载排班列表
const loadSchedules = async () => {
  const res: any = await request.get('/schedule/doctor/my');
  if (res.code === 200) schedules.value = res.data;
};

// 加载医院和科室数据（用于新增选框）
onMounted(async () => {
  await loadSchedules();
  const res1: any = await request.get('/hospital/list', { params: { current: 1, size: 100 } });
  if (res1.code === 200) hospitals.value = res1.data.records || res1.data;
  const res2: any = await request.get('/department/list');
  if (res2.code === 200) departments.value = res2.data;
});

// 新增排班
const addSchedule = async () => {
  const res: any = await request.post('/schedule/add', form.value);
  if (res.code === 200) {
    ElMessage.success('排班新增成功');
    showDialog.value = false;
    await loadSchedules();
  } else {
    ElMessage.error(res.message || '新增失败');
  }
};
</script>
