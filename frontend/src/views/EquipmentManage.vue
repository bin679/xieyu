<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">设备管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon style="margin-right:4px"><svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg></el-icon>
            新增设备
          </el-button>
        </div>
      </template>

      <div class="filter-row">
        <el-input v-model="keyword" placeholder="搜索名称 / 型号" style="width:200px" clearable @keyup.enter="loadData">
          <template #prefix>
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          </template>
        </el-input>
        <el-select v-model="labFilter" placeholder="所属实验室" clearable style="width:180px" @change="loadData">
          <el-option v-for="lab in labList" :key="lab.id" :label="lab.name" :value="lab.id" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width:130px" @change="loadData">
          <el-option label="可用" value="available" />
          <el-option label="已借用" value="borrowed" />
          <el-option label="维修中" value="maintenance" />
          <el-option label="已报废" value="scrapped" />
        </el-select>
        <el-button @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="model" label="型号" width="160" />
        <el-table-column prop="labName" label="所属实验室" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="statusType(row.status)" effect="light">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="doDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager-row">
        <span class="pager-label">第 {{ current }} 页</span>
        <el-pagination
          background
          v-model:current-page="current" v-model:page-size="size"
          :total="total" :page-sizes="[5,10,20]" layout="prev,pager,next,sizes"
          prev-text="上一页" next-text="下一页"
          @size-change="loadData" @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="所属实验室" prop="labId">
          <el-select v-model="form.labId" style="width:100%">
            <el-option v-for="lab in labList" :key="lab.id" :label="lab.name" :value="lab.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="可用" value="available" />
            <el-option label="已借用" value="borrowed" />
            <el-option label="维修中" value="maintenance" />
            <el-option label="已报废" value="scrapped" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getEquipments, addEquipment, updateEquipment, deleteEquipment } from '@/api/equipment'
import { getAllLabs } from '@/api/lab'
import { ElMessage } from 'element-plus'

export default {
  name: 'EquipmentManage',
  data() {
    return {
      loading: false, submitting: false,
      tableData: [], current: 1, size: 10, total: 0, keyword: '', labFilter: null, statusFilter: null,
      labList: [], dialogVisible: false, dialogTitle: '新增设备',
      form: { id: null, name: '', model: '', labId: null, status: 'available', description: '' },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        labId: [{ required: true, message: '请选择实验室', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  async mounted() {
    const res = await getAllLabs()
    this.labList = res.data || []
    this.loadData()
  },
  methods: {
    statusType(s) {
      return s === 'available' ? 'success' : s === 'borrowed' ? 'warning' : s === 'maintenance' ? 'info' : 'danger'
    },
    statusLabel(s) {
      return s === 'available' ? '可用' : s === 'borrowed' ? '已借用' : s === 'maintenance' ? '维修中' : '已报废'
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getEquipments({
          current: this.current, size: this.size, keyword: this.keyword,
          labId: this.labFilter || undefined, status: this.statusFilter || undefined
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) { /* handled */ }
      this.loading = false
    },
    openDialog(row) {
      this.dialogTitle = row ? '编辑设备' : '新增设备'
      this.form = row ? { ...row } : { id: null, name: '', model: '', labId: null, status: 'available', description: '' }
      this.dialogVisible = true
    },
    resetForm() {
      this.form = { id: null, name: '', model: '', labId: null, status: 'available', description: '' }
    },
    async doSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.submitting = true
        try {
          if (this.form.id) {
            await updateEquipment(this.form.id, this.form)
            ElMessage.success('更新成功')
          } else {
            await addEquipment(this.form)
            ElMessage.success('添加成功')
          }
          this.dialogVisible = false
          this.loadData()
        } catch (e) { /* handled */ }
        this.submitting = false
      })
    },
    async doDelete(id) {
      await deleteEquipment(id)
      ElMessage.success('删除成功')
      this.loadData()
    }
  }
}
</script>

<style scoped>
.page-wrap { max-width: 1400px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-weight: 600; font-size: 1rem; color: #374151; }
.filter-row { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; flex-wrap: wrap; }
.pager-row { margin-top: 16px; display: flex; justify-content: flex-end; align-items: center; gap: 12px; }
.pager-label { font-size: .85rem; color: #6b7280; }
</style>
