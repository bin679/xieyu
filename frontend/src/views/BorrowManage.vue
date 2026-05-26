<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">设备借用管理</span>
          <el-button type="primary" @click="openBorrowDialog">
            <el-icon style="margin-right:4px"><svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg></el-icon>
            借用设备
          </el-button>
        </div>
      </template>

      <el-alert
        v-if="overdueList.length > 0"
        :title="`有 ${overdueList.length} 条借用超时记录，请及时处理`"
        type="warning" show-icon :closable="false"
        style="margin-bottom:16px"
      />

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="equipmentName" label="设备" width="160" />
        <el-table-column prop="userName" label="借用人" width="100" />
        <el-table-column prop="labName" label="所属实验室" width="150" />
        <el-table-column prop="borrowTime" label="借用时间" width="170" />
        <el-table-column prop="plannedReturnTime" label="计划归还" width="170">
          <template #default="{row}">
            <span :class="{ 'overdue-text': row.status === 'overdue' }">
              {{ row.plannedReturnTime }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="actualReturnTime" label="实际归还" width="170" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status==='borrowing'?'warning':row.status==='overdue'?'danger':'success'" effect="light">
              {{ row.status === 'borrowing' ? '借用中' : row.status === 'overdue' ? '超时' : '已归还' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button v-if="row.status === 'borrowing' || row.status === 'overdue'" size="small" type="success" @click="doReturn(row.id)">归还</el-button>
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

    <!-- Borrow Dialog -->
    <el-dialog title="借用设备" v-model="borrowDialogVisible" width="500px" @close="resetBorrowForm">
      <el-form :model="borrowForm" :rules="borrowRules" ref="borrowFormRef" label-width="100px">
        <el-form-item label="设备" prop="equipmentId">
          <el-select v-model="borrowForm.equipmentId" style="width:100%" filterable placeholder="请选择可用设备">
            <el-option v-for="e in availableEquipments" :key="e.id" :label="`${e.name} (${e.labName || ''})`" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="借用人" prop="userId">
          <el-select v-model="borrowForm.userId" style="width:100%" filterable placeholder="请选择借用人">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.username})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划归还时间" prop="plannedReturnTime">
          <el-date-picker v-model="borrowForm.plannedReturnTime" type="datetime" placeholder="选择时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="borrowForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doBorrow" :loading="submitting">确定借用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getBorrows, getBorrowDetails, addBorrow, returnBorrow, deleteBorrow, getOverdueBorrows } from '@/api/borrow'
import { getAllEquipments } from '@/api/equipment'
import { getUsers } from '@/api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'BorrowManage',
  data() {
    return {
      loading: false, submitting: false,
      tableData: [], current: 1, size: 10, total: 0,
      overdueList: [], availableEquipments: [], userList: [],
      borrowDialogVisible: false,
      borrowForm: { equipmentId: null, userId: null, plannedReturnTime: '', remark: '' },
      borrowRules: {
        equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
        userId: [{ required: true, message: '请选择借用人', trigger: 'change' }],
        plannedReturnTime: [{ required: true, message: '请选择计划归还时间', trigger: 'change' }]
      }
    }
  },
  async mounted() {
    await this.loadData()
    this.loadOverdue()
    this.loadAvailableEquipments()
    this.loadUsers()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getBorrowDetails()
        this.tableData = res.data || []
        this.total = this.tableData.length
      } catch (e) { /* handled */ }
      this.loading = false
    },
    async loadOverdue() {
      try {
        const res = await getOverdueBorrows()
        this.overdueList = res.data || []
      } catch (e) { /* handled */ }
    },
    async loadAvailableEquipments() {
      const res = await getAllEquipments()
      this.availableEquipments = (res.data || []).filter(e => e.status === 'available')
    },
    async loadUsers() {
      const res = await getUsers({ current: 1, size: 100 })
      this.userList = res.data.records || []
    },
    openBorrowDialog() {
      this.borrowDialogVisible = true
      this.loadAvailableEquipments()
    },
    resetBorrowForm() {
      this.borrowForm = { equipmentId: null, userId: null, plannedReturnTime: '', remark: '' }
    },
    async doBorrow() {
      this.$refs.borrowFormRef.validate(async (valid) => {
        if (!valid) return
        this.submitting = true
        try {
          await addBorrow(this.borrowForm)
          ElMessage.success('借用成功')
          this.borrowDialogVisible = false
          this.loadData()
        } catch (e) { /* handled */ }
        this.submitting = false
      })
    },
    async doReturn(id) {
      try {
        await returnBorrow(id)
        ElMessage.success('归还成功')
        this.loadData()
      } catch (e) { /* handled */ }
    },
    async doDelete(id) {
      await deleteBorrow(id)
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
.pager-row { margin-top: 16px; display: flex; justify-content: flex-end; align-items: center; gap: 12px; }
.pager-label { font-size: .85rem; color: #6b7280; }
.overdue-text { color: #c45d5d; font-weight: 600; }
</style>
