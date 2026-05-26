<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">实验室管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon style="margin-right:4px"><svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg></el-icon>
            新增实验室
          </el-button>
        </div>
      </template>

      <div class="filter-row">
        <el-input v-model="keyword" placeholder="搜索名称 / 地点" style="width:260px" clearable @clear="loadData" @keyup.enter="loadData">
          <template #prefix>
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          </template>
        </el-input>
        <el-button @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="180" />
        <el-table-column prop="location" label="地点" min-width="180" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status==='available'?'success':row.status==='maintenance'?'warning':'info'" effect="light">
              {{ row.status === 'available' ? '可用' : row.status === 'maintenance' ? '维护中' : '关闭' }}
            </el-tag>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="可用" value="available" />
            <el-option label="维护中" value="maintenance" />
            <el-option label="关闭" value="closed" />
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
import { getLabs, addLab, updateLab, deleteLab } from '@/api/lab'
import { ElMessage } from 'element-plus'

export default {
  name: 'LabManage',
  data() {
    return {
      loading: false, submitting: false,
      tableData: [], current: 1, size: 10, total: 0, keyword: '',
      dialogVisible: false, dialogTitle: '新增实验室',
      form: { id: null, name: '', location: '', capacity: 0, status: 'available', description: '' },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  mounted() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getLabs({ current: this.current, size: this.size, keyword: this.keyword })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) { /* handled */ }
      this.loading = false
    },
    openDialog(row) {
      this.dialogTitle = row ? '编辑实验室' : '新增实验室'
      this.form = row ? { ...row } : { id: null, name: '', location: '', capacity: 0, status: 'available', description: '' }
      this.dialogVisible = true
    },
    resetForm() {
      this.form = { id: null, name: '', location: '', capacity: 0, status: 'available', description: '' }
    },
    async doSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.submitting = true
        try {
          if (this.form.id) {
            await updateLab(this.form.id, this.form)
            ElMessage.success('更新成功')
          } else {
            await addLab(this.form)
            ElMessage.success('添加成功')
          }
          this.dialogVisible = false
          this.loadData()
        } catch (e) { /* handled */ }
        this.submitting = false
      })
    },
    async doDelete(id) {
      await deleteLab(id)
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
.filter-row { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.pager-row { margin-top: 16px; display: flex; justify-content: flex-end; align-items: center; gap: 12px; }
.pager-label { font-size: .85rem; color: #6b7280; }
</style>
