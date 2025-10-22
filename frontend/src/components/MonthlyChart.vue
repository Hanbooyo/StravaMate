<template>
  <div>
    <v-chart :option="option" autoresize style="height:360px" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, TitleComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'

use([CanvasRenderer, BarChart, LineChart, GridComponent, TooltipComponent, TitleComponent, LegendComponent])

const props = defineProps<{ monthly: { month: string, totalDistance: number, totalMovingTime: number, totalElevGain: number }[] }>()

const option = computed(() => {
  const labels = props.monthly.map(m => m.month)
  return {
    title: { text: 'Monthly Summary' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['Distance (km)', 'Time (h)', 'Elev (m)'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: labels },
    yAxis: { type: 'value' },
    series: [
      { name: 'Distance (km)', type: 'bar', data: props.monthly.map(d => (d.totalDistance || 0) / 1000) },
      { name: 'Time (h)', type: 'bar', data: props.monthly.map(d => (d.totalMovingTime || 0) / 3600) },
      { name: 'Elev (m)', type: 'line', data: props.monthly.map(d => (d.totalElevGain || 0)) }
    ]
  }
})

</script>

<script lang="ts">
export default {
  components: { 'v-chart': VChart }
}
</script>

