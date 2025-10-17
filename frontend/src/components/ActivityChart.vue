<template>
  <div>
    <v-chart :option="option" autoresize style="height:360px" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, TitleComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, TitleComponent, LegendComponent])

const props = defineProps<{ daily: { day: string, totalDistance: number, totalMovingTime: number, totalElevGain: number }[] }>()

const option = computed(() => {
  const days = props.daily.map(d => d.day)
  return {
    title: { text: 'Last 7 Days' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['Distance (km)', 'Time (h)', 'Elev (m)'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: days },
    yAxis: { type: 'value' },
    series: [
      { name: 'Distance (km)', type: 'line', data: props.daily.map(d => (d.totalDistance || 0) / 1000) },
      { name: 'Time (h)', type: 'line', data: props.daily.map(d => (d.totalMovingTime || 0) / 3600) },
      { name: 'Elev (m)', type: 'line', data: props.daily.map(d => (d.totalElevGain || 0)) }
    ]
  }
})

</script>

<script lang="ts">
export default {
  components: { 'v-chart': VChart }
}
</script>

