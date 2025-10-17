<template>
  <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:12px;">
    <div class="card">
      <div class="label">Distance (km)</div>
      <div class="value">{{ (totalDistance / 1000).toFixed(1) }}</div>
    </div>
    <div class="card">
      <div class="label">Time (h)</div>
      <div class="value">{{ (totalTime / 3600).toFixed(1) }}</div>
    </div>
    <div class="card">
      <div class="label">Elev Gain (m)</div>
      <div class="value">{{ totalElev.toFixed(0) }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ daily: { totalDistance: number, totalMovingTime: number, totalElevGain: number }[] }>()

const totalDistance = computed(() => props.daily.reduce((s, d) => s + (d.totalDistance || 0), 0))
const totalTime = computed(() => props.daily.reduce((s, d) => s + (d.totalMovingTime || 0), 0))
const totalElev = computed(() => props.daily.reduce((s, d) => s + (d.totalElevGain || 0), 0))
</script>

<style scoped>
.card {
  border: 1px solid #4444;
  border-radius: 10px;
  padding: 12px;
}
.label { opacity: 0.7; }
.value { font-size: 1.8rem; font-weight: 600; }
</style>

