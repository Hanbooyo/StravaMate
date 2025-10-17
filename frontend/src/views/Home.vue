<template>
  <div style="display:flex;min-height:100dvh;align-items:center;justify-content:center;padding:32px;">
    <div style="max-width:560px;text-align:center;">
      <h1>StravaMate</h1>
      <p>Connect your Strava to view weekly distance, elevation, and time.</p>
      <div style="margin-top:24px;display:flex;gap:12px;justify-content:center;">
        <a :href="backendAuthUrl" class="btn">Connect with Strava</a>
        <button class="btn secondary" @click="gotoDashboard" v-if="userId">Go to Dashboard</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const userId = computed(() => auth.userId)
const backendBase = (import.meta.env.VITE_API_BASE_URL as string) || 'http://localhost:8080'
const backendAuthUrl = `${backendBase}/auth/strava/start`

function gotoDashboard() {
  router.push('/dashboard')
}
</script>

<style scoped>
.btn {
  padding: 10px 16px;
  border-radius: 8px;
  background: #fc4c02; /* Strava orange-ish */
  color: white;
  text-decoration: none;
  border: none;
}
.btn.secondary {
  background: #555;
}
</style>

