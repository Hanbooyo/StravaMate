import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    userId: localStorage.getItem('user_id') as string | null
  }),
  actions: {
    setUserId(id: string) {
      this.userId = id
      localStorage.setItem('user_id', id)
    }
  }
})

