<template>
  <div style="padding:16px;max-width:1100px;margin:0 auto;">
    <header style="display:flex;align-items:center;gap:12px;justify-content:space-between;">
      <h2>Dashboard</h2>
      <div style="display:flex;gap:8px;align-items:center;">
        <button class="btn" @click="doBackfill">Backfill 90 days</button>
      </div>
    </header>

    <div style="margin-top:16px">
      <SummaryCards :daily="daily" />
    </div>

    <div style="margin-top:24px">
      <ActivityChart :daily="daily" />
    </div>

    <section style="margin-top:32px">
      <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
        <h3>Yearly View</h3>
        <div style="display:flex;gap:8px;align-items:center;">
          <label for="year">Year</label>
          <select id="year" v-model.number="year" class="select">
            <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
          </select>
          <button class="btn" @click="reloadMonthly">Load</button>
        </div>
      </div>
      <MonthlyChart :monthly="monthly" />
    </section>

    <section style="margin-top:32px">
      <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
        <h3>Custom Range</h3>
        <div style="display:flex;gap:8px;align-items:center;">
          <label>Start</label>
          <input type="datetime-local" v-model="startLocal" class="select" />
          <label>End</label>
          <input type="datetime-local" v-model="endLocal" class="select" />
          <button class="btn" @click="loadRange">Load</button>
        </div>
      </div>
      <div style="margin-top:12px;display:grid;grid-template-columns:repeat(3,1fr);gap:12px;">
        <div class="card">
          <div class="label">Distance (km)</div>
          <div class="value">{{ (rangeDistance / 1000).toFixed(1) }}</div>
        </div>
        <div class="card">
          <div class="label">Time (h)</div>
          <div class="value">{{ (rangeTime / 3600).toFixed(1) }}</div>
        </div>
        <div class="card">
          <div class="label">Elev Gain (m)</div>
          <div class="value">{{ rangeElev.toFixed(0) }}</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useSummaryStore } from '../stores/summary'
import SummaryCards from '../components/SummaryCards.vue'
import ActivityChart from '../components/ActivityChart.vue'
import MonthlyChart from '../components/MonthlyChart.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const summary = useSummaryStore()

const daily = summary.daily
const monthly = summary.monthly
const rangeActivities = computed(() => summary.rangeActivities)

const thisYear = new Date().getUTCFullYear()
const years = computed(() => {
  const arr:number[] = []
  for (let y = thisYear; y >= thisYear - 5; y--) arr.push(y)
  return arr
})
const year = ref(thisYear)

onMounted(async () => {
  const userIdQ = route.query.user_id as string | undefined
  if (userIdQ) auth.setUserId(userIdQ)
  if (!auth.userId) {
    router.push('/')
    return
  }
  await Promise.all([
    summary.loadDaily(auth.userId, 7),
    summary.loadWeekly(auth.userId),
    summary.loadMonthly(auth.userId, year.value)
  ])
})

watch(() => route.query.user_id, (val) => {
  if (val) auth.setUserId(String(val))
})

async function doBackfill() {
  if (!auth.userId) return
  await summary.backfill(auth.userId, 90)
  await summary.loadDaily(auth.userId, 7)
}

async function reloadMonthly() {
  if (!auth.userId) return
  await summary.loadMonthly(auth.userId, year.value)
}

const startLocal = ref('')
const endLocal = ref('')

const rangeDistance = computed(() => (rangeActivities.value || []).reduce((s: number, a: any) => s + (a.distance || 0), 0))
const rangeTime = computed(() => (rangeActivities.value || []).reduce((s: number, a: any) => s + (a.movingTime || 0), 0))
const rangeElev = computed(() => (rangeActivities.value || []).reduce((s: number, a: any) => s + (a.elevGain || 0), 0))

async function loadRange() {
  if (!auth.userId || !startLocal.value || !endLocal.value) return
  // Convert local to ISO with 'Z' (assume local time -> UTC naive)
  const startIso = new Date(startLocal.value).toISOString()
  const endIso = new Date(endLocal.value).toISOString()
  await summary.loadActivities(auth.userId, startIso, endIso)
}
</script>

<style scoped>
.btn {
  padding: 8px 12px;
  border-radius: 6px;
}
.select {
  padding: 6px 10px;
  border-radius: 6px;
}
.card {
  border: 1px solid #4444;
  border-radius: 10px;
  padding: 12px;
}
.label { opacity: 0.7; }
.value { font-size: 1.2rem; font-weight: 600; }
</style>
