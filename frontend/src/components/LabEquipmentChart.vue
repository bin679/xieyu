<template>
  <div ref="chartRef" class="chart-box" :style="{ height: height + 'px' }"></div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'LabEquipmentChart',
  props: {
    data: { type: Array, default: () => [] },
    height: { type: Number, default: 320 },
    colorStart: { type: String, default: '#c88d3d' },
    colorEnd: { type: String, default: '#d9a85e' }
  },
  setup(props) {
    const chartRef = ref(null)
    let chart = null

    const render = () => {
      if (!chartRef.value || !props.data.length) return
      if (!chart) chart = echarts.init(chartRef.value)
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: '#1f2937',
          borderColor: '#374151',
          textStyle: { color: '#e5e7eb' }
        },
        grid: { left: '3%', right: '8%', bottom: '8%', top: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: props.data.map(d => d.lab_name),
          axisLabel: { rotate: 20, color: '#6b7280', fontSize: 11 },
          axisLine: { lineStyle: { color: '#e5e7eb' } },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: '设备数量',
          nameTextStyle: { color: '#9ca3af', fontSize: 11 },
          axisLabel: { color: '#6b7280' },
          splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } }
        },
        series: [{
          type: 'bar',
          data: props.data.map(d => ({
            value: d.count,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: props.colorStart },
                { offset: 1, color: props.colorEnd }
              ]),
              borderRadius: [6, 6, 0, 0]
            }
          })),
          barWidth: '50%',
          label: { show: true, position: 'top', color: '#6b7280', fontSize: 12, fontWeight: 600 }
        }]
      })
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
.chart-box { width: 100%; animation: reveal-fade .6s .35s both; }
@keyframes reveal-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
</style>
