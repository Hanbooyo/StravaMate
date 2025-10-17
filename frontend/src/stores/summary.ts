import { defineStore } from 'pinia'
import api from '../utils/api'

export interface DailySummary {
  day: string
  totalDistance: number
  totalMovingTime: number
  totalElevGain: number
}

export const useSummaryStore = defineStore('summary', {
  state: () => ({
    daily: [] as DailySummary[],
    weekly: [] as any[]
  }),
  actions: {
    async loadDaily(userId: string, days = 7) {
      const res = await api.get('/summary/daily', { params: { user_id: userId, days } })
      this.daily = res.data.data || []
    },
    async loadWeekly(userId: string) {
      const res = await api.get('/summary/weekly', { params: { user_id: userId } })
      this.weekly = res.data.data || []
    },
    async backfill(userId: string, days = 90) {
      await api.post('/activities/backfill', null, { params: { user_id: userId, days } })
    }
  }
})

