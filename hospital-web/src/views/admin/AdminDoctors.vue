<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>医生管理</h3>
      <el-button type="primary" @click="showAdd">新增医生</el-button>
    </div>
    <el-table :data="doctors" border stripe style="width:100%;">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="hospitalName" label="所属医院" />
      <el-table-column prop="departmentName" label="科室" />
      <el-table-column prop="title" label="职称" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑医生' : '新增医生'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="所属医院">
          <el-select v-model="form.hospitalId" placeholder="请选择医院">
            <el-option v-for="h in hospitals" :key="h.id" :label="h.name" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.departmentId" placeholder="请选择科室">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="form.title" />
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

const doctors = ref<any[]>([]);
const hospitals = ref<any[]>([]);
const departments = ref<any[]>([]);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = ref({ name: '', phone: '', hospitalId: null as any, departmentId: null as any, title: '' });

// 加载医生列表
const loadList = async () => {
  const res: any = await request.get('/doctor/list', { params: { current: 1, size: 100 } });
  if (res.code === 200) doctors.value = res.data.records || res.data;
};

onMounted(async () => {
  await loadList();
  const res1: any = await request.get('/hospital/list', { params: { current: 1, size: 100 } });
  if (res1.code === 200) hospitals.value = res1.data.records || res1.data;
  const res2: any = await request.get('/department/list');
  if (res2.code === 200) departments.value = res2.data;
});

const showAdd = () => { editingId.value = null; form.value = { name: '', phone: '', hospitalId: null, departmentId: null, title: '' }; dialogVisible.value = true; };
const showEdit = (row: any) => { editingId.value = row.id; form.value = { name: row.name, phone: row.phone, hospitalId: row.hospitalId, departmentId: row.departmentId, title: row.title }; dialogVisible.value = true; };

const doSave = async () => {
  const url = editingId.value ? `/doctor/update` : '/doctor/add';
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
  await ElMessageBox.confirm('确定删除该医生吗？', '提示', { type: 'warning' });
  const res: any = await request.delete(`/doctor/${id}`);
  if (res.code === 200) {
    ElMessage.success('删除成功');
    await loadList();
  } else {
    ElMessage.error(res.message || '删除失败');
  }
};
</script>
