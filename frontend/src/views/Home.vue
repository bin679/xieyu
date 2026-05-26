<template>
  <div class="dashboard">
    <StatCards :cards="cards" />

    <div class="chart-grid">
      <el-card class="chart-card">
        <template #header>
          <span class="chart-title">设备状态分布</span>
        </template>
        <EquipmentStatusChart :data="equipmentByStatus" />
      </el-card>
      <el-card class="chart-card">
        <template #header>
          <span class="chart-title">各实验室设备数量</span>
        </template>
        <LabEquipmentChart :data="equipmentByLab" />
      </el-card>
    </div>

    <OverdueTable :data="overdueList" />
  </div>
</template>

<script>
import { getDashboard } from '@/api/statistics'
import { getOverdueBorrows } from '@/api/borrow'
import { ref, onMounted } from 'vue'
import StatCards from '@/components/StatCards.vue'
import EquipmentStatusChart from '@/components/EquipmentStatusChart.vue'
import LabEquipmentChart from '@/components/LabEquipmentChart.vue'
import OverdueTable from '@/components/OverdueTable.vue'

export default {
  name: 'Home',
  components: { StatCards, EquipmentStatusChart, LabEquipmentChart, OverdueTable },
  setup() {
    const cards = ref([
      { key: 'lab', label: '实验室总数', value: 0, color: '#5b8cbd' },
      { key: 'equip', label: '设备总数', value: 0, color: '#4b8b6b' },
      { key: 'user', label: '用户总数', value: 0, color: '#d4953a' },
      { key: 'borrow', label: '借用中设备', value: 0, color: '#c45d5d' }
    ])
    const overdueList = ref([])
    const equipmentByStatus = ref([])
    const equipmentByLab = ref([])

    const loadData = async () => {
      try {
        const res = await getDashboard()
        const d = res.data
        cards.value[0].value = d.labCount || 0
        cards.value[1].value = d.equipmentCount || 0
        cards.value[2].value = d.userCount || 0
        cards.value[3].value = d.borrowingCount || 0
        equipmentByStatus.value = d.equipmentByStatus || []
        equipmentByLab.value = d.equipmentByLab || []
      } catch (e) { /* handled */ }

      try {
        const overdueRes = await getOverdueBorrows()
        overdueList.value = overdueRes.data || []
      } catch (e) { /* handled */ }
    }

    onMounted(loadData)
    return { cards, overdueList, equipmentByStatus, equipmentByLab }
  }
}
</script>

<style scoped>
.dashboard { max-width: 1400px; }

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}
.chart-card { background: #fff; }
.chart-title { font-weight: 600; color: #374151; }

@media (max-width: 1000px) {
  .chart-grid { grid-template-columns: 1fr; }
}
</style>
