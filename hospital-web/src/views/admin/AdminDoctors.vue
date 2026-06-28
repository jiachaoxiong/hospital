<template>
  <div>
    <div class="page-header">
      <h3>医生管理</h3>
      <el-button type="primary" @click="showAdd">＋ 新增医生</el-button>
    </div>
    <el-table :data="doctors" border stripe style="width:100%;" table-layout="auto">
      <el-table-column prop="id" label="ID" width="55" align="center" />
      <el-table-column prop="name" label="姓名" width="75" align="center" />
      <el-table-column prop="title" label="职称" width="95" align="center" />
      <el-table-column prop="hospitalId" label="医院ID" width="70" align="center" />
      <el-table-column prop="departmentId" label="科室ID" width="70" align="center" />
      <el-table-column prop="description" label="简介" min-width="140" show-overflow-tooltip />
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
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑医生' : '新增医生'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="所属医院">
          <el-select v-model="form.hospitalId" placeholder="请选择医院" @change="onHospitalChange">
            <el-option v-for="h in hospitals" :key="h.id" :label="h.name" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.departmentId" placeholder="请选择科室">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="form.title" placeholder="请选择职称">
            <el-option label="主任医师" value="主任医师" />
            <el-option label="副主任医师" value="副主任医师" />
            <el-option label="主治医师" value="主治医师" />
            <el-option label="住院医师" value="住院医师" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" /></el-form-item>
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

const doctors = ref<any[]>([]);
const hospitals = ref<any[]>([]);
const departments = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = ref({ name: '', hospitalId: null as any, departmentId: null as any, title: '', description: '' });

// 分页加载医生列表
const loadList = async () => {
  const res: any = await request.get('/doctor/page', { params: { current: currentPage.value, size: pageSize.value } });
  if (res.code === 200) {
    doctors.value = res.data.records || [];
    total.value = res.data.total || 0;
  }
};

// 加载医院列表（用于下拉框）
const loadHospitals = async () => {
  const res: any = await request.get('/hospital/list', { params: { current: 1, size: 100 } });
  if (res.code === 200) hospitals.value = res.data.records || res.data;
};

const onHospitalChange = async (hospitalId: number) => {
  form.value.departmentId = null;
  if (hospitalId) {
    const res: any = await request.get('/department/list', { params: { hospitalId } });
    if (res.code === 200) departments.value = res.data || [];
  }
};

// 初始化
loadList();
loadHospitals();

const showAdd = () => { editingId.value = null; form.value = { name: '', hospitalId: null, departmentId: null, title: '', description: '' }; dialogVisible.value = true; };
const showEdit = (row: any) => { editingId.value = row.id; form.value = { name: row.name, hospitalId: row.hospitalId, departmentId: row.departmentId, title: row.title, description: row.description || '' }; dialogVisible.value = true; };

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
