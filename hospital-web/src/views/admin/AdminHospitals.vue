<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h3>医院管理</h3>
      <el-button type="primary" @click="showAdd">新增医院</el-button>
    </div>
    <el-table :data="hospitals" border stripe style="width:100%;" table-layout="auto">
      <el-table-column prop="id" label="ID" width="55" align="center" />
      <el-table-column prop="name" label="医院名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="level" label="等级" width="85" align="center" />
      <el-table-column prop="city" label="城市" width="65" align="center" />
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
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
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑医院' : '新增医院'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="等级"><el-input v-model="form.level" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
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

const hospitals = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = ref({ name: '', level: '', city: '', address: '' });

// 分页加载医院列表
const loadList = async () => {
  const res: any = await request.get('/hospital/list', { params: { current: currentPage.value, size: pageSize.value } });
  if (res.code === 200) {
    hospitals.value = res.data.records || [];
    total.value = res.data.total || 0;
  }
};

// 初始化加载
loadList();

const showAdd = () => { editingId.value = null; form.value = { name: '', level: '', city: '', address: '' }; dialogVisible.value = true; };
const showEdit = (row: any) => { editingId.value = row.id; form.value = { name: row.name, level: row.level, city: row.city, address: row.address }; dialogVisible.value = true; };

const doSave = async () => {
  const url = editingId.value ? `/hospital/update` : '/hospital/add';
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
  await ElMessageBox.confirm('确定删除该医院吗？', '提示', { type: 'warning' });
  const res: any = await request.delete(`/hospital/${id}`);
  if (res.code === 200) {
    ElMessage.success('删除成功');
    await loadList();
  } else {
    ElMessage.error(res.message || '删除失败');
  }
};
</script>
