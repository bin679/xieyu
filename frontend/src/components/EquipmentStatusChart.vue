<template>
  <div ref="chartRef" class="chart-box" :style="{ height: height + 'px' }"></div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'EquipmentStatusChart',
  props: {
    data: { type: Array, default: () => [] },
    height: { type: Number, default: 320 },
    showLegend: { type: Boolean, default: false }
  },
  setup(props) {
    const chartRef = ref(null)
    let chart = null

    const statusMap = { available: '可用', borrowed: '已借用', maintenance: '维修中', scrapped: '已报废' }
    const colors = ['#4b8b6b', '#d4953a', '#5b8cbd', '#c45d5d']

    const render = () => {
      if (!chartRef.value || !props.data.length) return
      if (!chart) chart = echarts.init(chartRef.value)
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} 台 ({d}%)',
          backgroundColor: '#1f2937',
          borderColor: '#374151',
          textStyle: { color: '#e5e7eb' }
        },
        series: [{
          name: '设备状态',
          type: 'pie',
          radius: ['48%', '78%'],
          center: ['50%', props.showLegend ? '48%' : '55%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
          label: { show: props.showLegend, formatter: '{b}: {c} ({d}%)', color: '#6b7280' },
          emphasis: {
            label: { show: true, fontSize: 16, fontWeight: 'bold' },
            scaleSize: 10
          },
          data: props.data.map((d, i) => ({
            name: statusMap[d.status] || d.status,
            value: d.count,
            itemStyle: { color: colors[i % colors.length] }
          }))
        }]
      }
      if (props.showLegend) {
        option.legend = { bottom: 0, textStyle: { color: '#6b7280' } }
      }
      chart.setOption(option)
    }

    const resize = () => chart?.resize()

    onMounted(() => { nextTick(render) })
    onUnmounted(() => {
      window.removeEventListener('resize', resize)
      chart?.dispose()
    })

    watch(() => props.data, () => nextTick(render), { deep: true })
    window.addEventListener('resize', resize)

    return { chartRef }
  }
}
</script>

<style scoped>
.chart-box { width: 100%; animation: reveal-fade .6s .25s both; }
@keyframes reveal-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
</style>
