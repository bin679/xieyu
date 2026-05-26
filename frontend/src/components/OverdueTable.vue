<template>
  <el-card class="overdue-card" v-if="data.length > 0">
    <template #header>
      <div class="overdue-header">
        <span class="overdue-dot"></span>
        <span>借用超时提醒</span>
        <el-tag type="danger" size="small" effect="dark">{{ data.length }} 条</el-tag>
      </div>
    </template>
    <el-table :data="data" size="small" class="overdue-table">
      <el-table-column prop="equipmentName" label="设备" />
      <el-table-column prop="userName" label="借用人" />
      <el-table-column prop="borrowTime" label="借用时间" />
      <el-table-column prop="plannedReturnTime" label="计划归还">
        <template #default="{row}">
          <span class="overdue-time">{{ row.plannedReturnTime }}</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script>
export default {
  name: 'OverdueTable',
  props: {
    data: { type: Array, default: () => [] }
  }
}
</script>

<style scoped>
.overdue-card { margin-bottom: 24px; }
.overdue-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #c45d5d;
  font-weight: 600;
}
.overdue-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #c45d5d;
  animation: pulse-dot 2s infinite;
}
@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: .3; }
}
.overdue-time { color: #c45d5d; font-weight: 600; }
</style>
