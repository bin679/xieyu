<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">维修记录管理</span>
          <el-button type="primary" @click="openRepairDialog">
            <el-icon style="margin-right:4px"><svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg></el-icon>
            报修申请
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="equipmentName" label="设备" width="160" />
        <el-table-column prop="userName" label="报修人" width="100" />
        <el-table-column prop="description" label="故障描述" show-overflow-tooltip min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status==='pending'?'info':row.status==='repairing'?'warning':'success'" effect="light">
              {{ row.status === 'pending' ? '待处理' : row.status === 'repairing' ? '维修中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="repairTime" label="维修开始" width="170" />
        <el-table-column prop="completeTime" label="维修完成" width="170" />
        <el-table-column prop="result" label="维修结果" show-overflow-tooltip width="150" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{row}">
            <el-button v-if="row.status === 'pending'" size="small" type="warning" @click="updateStatus(row, 'repairing')">开始维修</el-button>
            <el-button v-if="row.status === 'repairing'" size="small" type="success" @click="openCompleteDialog(row)">完成维修</el-button>
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

    <!-- Repair Dialog -->
    <el-dialog title="报修申请" v-model="repairDialogVisible" width="500px" @close="resetRepairForm">
      <el-form :model="repairForm" :rules="repairRules" ref="repairFormRef" label-width="100px">
        <el-form-item label="设备" prop="equipmentId">
          <el-select v-model="repairForm.equipmentId" style="width:100%" filterable>
            <el-option v-for="e in equipmentList" :key="e.id" :label="`${e.name} (${e.labName || ''})`" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修人" prop="userId">
          <el-select v-model="repairForm.userId" style="width:100%" filterable>
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName} (${u.username})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="description">
          <el-input v-model="repairForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="repairDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doRepair" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- Complete Dialog -->
    <el-dialog title="完成维修" v-model="completeDialogVisible" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="维修结果">
          <el-input v-model="completeForm.result" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getRepairs, getRepairDetails, addRepair, updateRepair, deleteRepair } from '@/api/repair'
import { getAllEquipments } from '@/api/equipment'
import { getUsers } from '@/api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'RepairManage',
  data() {
    return {
      loading: false, submitting: false,
      tableData: [], current: 1, size: 10, total: 0,
      equipmentList: [], userList: [],
      repairDialogVisible: false, completeDialogVisible: false,
      repairForm: { equipmentId: null, userId: null, description: '' },
      repairRules: {
        equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
        userId: [{ required: true, message: '请选择报修人', trigger: 'change' }],
        description: [{ required: true, message: '请输入故障描述', trigger: 'blur' }]
      },
      completeForm: { id: null, result: '' },
      currentRow: null
    }
  },
  async mounted() {
    await this.loadData()
    const [eqRes, usRes] = await Promise.all([getAllEquipments(), getUsers({ current: 1, size: 100 })])
    this.equipmentList = eqRes.data || []
    this.userList = usRes.data.records || []
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getRepairDetails()
        this.tableData = res.data || []
        this.total = this.tableData.length
      } catch (e) { /* handled */ }
      this.loading = false
    },
    openRepairDialog() {
      this.repairDialogVisible = true
    },
    resetRepairForm() {
      this.repairForm = { equipmentId: null, userId: null, description: '' }
    },
    async doRepair() {
      this.$refs.repairFormRef.validate(async (valid) => {
        if (!valid) return
        this.submitting = true
        try {
          await addRepair(this.repairForm)
          ElMessage.success('报修申请已提交')
          this.repairDialogVisible = false
          this.loadData()
        } catch (e) { /* handled */ }
        this.submitting = false
      })
    },
    async updateStatus(row, status) {
      await updateRepair(row.id, { ...row, status })
      ElMessage.success(status === 'repairing' ? '已开始维修' : '操作成功')
      this.loadData()
    },
    openCompleteDialog(row) {
      this.currentRow = row
      this.completeForm = { id: row.id, result: '' }
      this.completeDialogVisible = true
    },
    async doComplete() {
      const row = { ...this.currentRow, status: 'completed', result: this.completeForm.result }
      await updateRepair(this.currentRow.id, row)
      ElMessage.success('维修已完成')
      this.completeDialogVisible = false
      this.loadData()
    },
    async doDelete(id) {
      await deleteRepair(id)
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
</style>
