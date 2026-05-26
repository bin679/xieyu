<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">用户管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon style="margin-right:4px"><svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <div class="filter-row">
        <el-input v-model="keyword" placeholder="搜索用户名 / 姓名" style="width:260px" clearable @clear="loadData" @keyup.enter="loadData">
          <template #prefix>
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          </template>
        </el-input>
        <el-button @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{row}">
            <el-tag :type="row.role==='admin'?'warning':'info'" effect="light">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="resetPwd(row.id)">重置密码</el-button>
            <el-popconfirm title="确定删除该用户吗？" @confirm="doDelete(row.id)">
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

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" :prop="form.id ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空则不修改' : ''" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
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
import { getUsers, addUser, updateUser, deleteUser, resetPassword } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserManage',
  data() {
    return {
      loading: false, submitting: false,
      tableData: [], current: 1, size: 10, total: 0, keyword: '',
      dialogVisible: false, dialogTitle: '新增用户',
      form: { id: null, username: '', password: '', realName: '', phone: '', email: '', role: 'user' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  mounted() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getUsers({ current: this.current, size: this.size, keyword: this.keyword })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) { /* handled */ }
      this.loading = false
    },
    openDialog(row) {
      if (row) {
        this.dialogTitle = '编辑用户'
        this.form = { ...row, password: '' }
      } else {
        this.dialogTitle = '新增用户'
      }
      this.dialogVisible = true
    },
    resetForm() {
      this.form = { id: null, username: '', password: '', realName: '', phone: '', email: '', role: 'user' }
    },
    async doSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.submitting = true
        try {
          if (this.form.id) {
            await updateUser(this.form.id, this.form)
            ElMessage.success('更新成功')
          } else {
            await addUser(this.form)
            ElMessage.success('添加成功')
          }
          this.dialogVisible = false
          this.loadData()
        } catch (e) { /* handled */ }
        this.submitting = false
      })
    },
    async doDelete(id) {
      await deleteUser(id)
      ElMessage.success('删除成功')
      this.loadData()
    },
    async resetPwd(id) {
      try {
        const { value } = await ElMessageBox.prompt('请输入新密码（至少6位）', '重置密码', {
          inputType: 'password', inputValidator: v => v && v.length >= 6 ? true : '密码至少6位'
        })
        await resetPassword(id, value)
        ElMessage.success('密码已重置')
      } catch (e) { /* cancelled */ }
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
