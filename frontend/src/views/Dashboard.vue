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
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useSummaryStore } from '../stores/summary'
import SummaryCards from '../components/SummaryCards.vue'
import ActivityChart from '../components/ActivityChart.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const summary = useSummaryStore()

const daily = summary.daily

onMounted(async () => {
  const userIdQ = route.query.user_id as string | undefined
  if (userIdQ) auth.setUserId(userIdQ)
  if (!auth.userId) {
    router.push('/')
    return
  }
  await Promise.all([
    summary.loadDaily(auth.userId, 7),
    summary.loadWeekly(auth.userId)
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
</script>

<style scoped>
.btn {
  padding: 8px 12px;
  border-radius: 6px;
}
</style>

