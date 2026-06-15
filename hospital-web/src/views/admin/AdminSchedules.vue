<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>排班管理</h3>
      <el-button type="primary" @click="showAdd">新增排班</el-button>
    </div>
    <el-table :data="schedules" border stripe style="width:100%;" table-layout="auto">
      <el-table-column prop="id" label="ID" width="55" align="center" />
      <el-table-column prop="hospitalName" label="医院" min-width="155" show-overflow-tooltip />
      <el-table-column prop="departmentName" label="科室" width="75" align="center" />
      <el-table-column prop="doctorName" label="医生" width="75" align="center" />
      <el-table-column prop="doctorTitle" label="职称" width="90" align="center" />
      <el-table-column prop="workDate" label="日期" width="105" align="center" />
      <el-table-column prop="startTime" label="开始" width="85" align="center" />
      <el-table-column prop="endTime" label="结束" width="85" align="center" />
      <el-table-column prop="totalQuota" label="总号源" width="75" align="center" />
      <el-table-column prop="remainQuota" label="剩余" width="70" align="center" />
      <el-table-column prop="price" label="价格" width="75" align="center" />
      <el-table-column label="操作" width="155" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑排班' : '新增排班'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="医院">
          <el-select v-model="form.hospitalId" placeholder="请选择医院" @change="onHospChange">
            <el-option v-for="h in hospitals" :key="h.id" :label="h.name" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.departmentId" placeholder="请选择科室" @change="onDeptChange">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="form.doctorId" placeholder="请选择医生">
            <el-option v-for="d in docOptions" :key="d.id" :label="d.name + ' (' + d.title + ')'" :value="d.id" />
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
          <el-input-number v-model="form.totalQuota" :min="1" />
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
import { ref } from 'vue';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const schedules = ref<any[]>([]);
const hospitals = ref<any[]>([]);
const departments = ref<any[]>([]);
const docOptions = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = ref({ hospitalId: null as any, departmentId: null as any, doctorId: null as any, workDate: '', startTime: '', endTime: '', totalQuota: 20, price: 50, doctorName: '', doctorTitle: '' });

// 分页加载排班列表
const loadList = async () => {
  const res: any = await request.get('/schedule/list', { params: { current: currentPage.value, size: pageSize.value } });
  if (res.code === 200) {
    schedules.value = res.data.records || [];
    total.value = res.data.total || 0;
  }
};

const onHospChange = async (hid: number) => {
  form.value.departmentId = null; form.value.doctorId = null; docOptions.value = [];
  if (hid) {
    const res: any = await request.get('/department/list', { params: { hospitalId: hid } });
    if (res.code === 200) departments.value = res.data || [];
  }
};

const onDeptChange = async (did: number) => {
  form.value.doctorId = null; docOptions.value = [];
  if (did) {
    const res: any = await request.get('/doctor/list', { params: { departmentId: did } });
    if (res.code === 200) docOptions.value = res.data || [];
  }
};

// 初始化
loadList();
(async () => {
  const res: any = await request.get('/hospital/list', { params: { current: 1, size: 100 } });
  if (res.code === 200) hospitals.value = res.data.records || res.data;
})();

const showAdd = () => { editingId.value = null; form.value = { hospitalId: null, departmentId: null, doctorId: null, workDate: '', startTime: '', endTime: '', totalQuota: 20, price: 50, doctorName: '', doctorTitle: '' }; docOptions.value = []; dialogVisible.value = true; };
const showEdit = (row: any) => { editingId.value = row.id; form.value = { hospitalId: row.hospitalId, departmentId: row.departmentId, doctorId: row.doctorId, workDate: row.workDate, startTime: row.startTime, endTime: row.endTime, totalQuota: row.totalQuota || 20, price: row.price, doctorName: row.doctorName || '', doctorTitle: row.doctorTitle || '' }; dialogVisible.value = true; };

const doSave = async () => {
  const url = editingId.value ? `/schedule/update` : '/schedule/add';
  const selDoc = docOptions.value.find((d: any) => d.id === form.value.doctorId);
  const payload: any = { ...form.value };
  if (selDoc) { payload.doctorName = selDoc.name; payload.doctorTitle = selDoc.title; }
  if (editingId.value) payload.id = editingId.value;
  const res: any = await request.post(url, payload);
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
