<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>排班管理</h3>
      <el-button type="primary" @click="showAdd">新增排班</el-button>
    </div>
    <el-table :data="schedules" border stripe style="width:100%;">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="hospitalName" label="医院" />
      <el-table-column prop="departmentName" label="科室" />
      <el-table-column prop="doctorName" label="医生" />
      <el-table-column prop="workDate" label="日期" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="maxQuota" label="最大号源" />
      <el-table-column prop="remainQuota" label="剩余号源" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑排班' : '新增排班'" width="500px">
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
        <el-form-item label="医生">
          <el-select v-model="form.doctorId" placeholder="请选择医生">
            <el-option v-for="d in doctors" :key="d.id" :label="d.name" :value="d.id" />
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const schedules = ref<any[]>([]);
const hospitals = ref<any[]>([]);
const departments = ref<any[]>([]);
const doctors = ref<any[]>([]);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = ref({ hospitalId: null as any, departmentId: null as any, doctorId: null as any, workDate: '', startTime: '', endTime: '', maxQuota: 10, price: 50 });

// 加载排班列表
const loadList = async () => {
  const res: any = await request.get('/schedule/list', { params: { current: 1, size: 100 } });
  if (res.code === 200) schedules.value = res.data.records || res.data;
};

onMounted(async () => {
  await loadList();
  const res1: any = await request.get('/hospital/list', { params: { current: 1, size: 100 } });
  if (res1.code === 200) hospitals.value = res1.data.records || res1.data;
  const res2: any = await request.get('/department/list');
  if (res2.code === 200) departments.value = res2.data;
  const res3: any = await request.get('/doctor/list', { params: { current: 1, size: 100 } });
  if (res3.code === 200) doctors.value = res3.data.records || res3.data;
});

const showAdd = () => { editingId.value = null; form.value = { hospitalId: null, departmentId: null, doctorId: null, workDate: '', startTime: '', endTime: '', maxQuota: 10, price: 50 }; dialogVisible.value = true; };
const showEdit = (row: any) => { editingId.value = row.id; form.value = { hospitalId: row.hospitalId, departmentId: row.departmentId, doctorId: row.doctorId, workDate: row.workDate, startTime: row.startTime, endTime: row.endTime, maxQuota: row.maxQuota, price: row.price }; dialogVisible.value = true; };

const doSave = async () => {
  const url = editingId.value ? `/schedule/update` : '/schedule/add';
  const res: any = await request.post(url, editingId.value ? { id: editingId.value, ...form.value } : form.value);
  if (res.code === 200) {
    ElMessage.success(editingId.value ? '更新成功' : '新增成功');
    dialogVisible.value = false;
    await loadList();
  } else {
    ElMessage.error(res.message || '操作失败');
  }
};

const doDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该排班吗？', '提示', { type: 'warning' });
  const res: any = await request.delete(`/schedule/${id}`);
  if (res.code === 200) {
    ElMessage.success('删除成功');
    await loadList();
  } else {
    ElMessage.error(res.message || '删除失败');
  }
};
</script>
