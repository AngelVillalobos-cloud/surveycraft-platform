import { defineConfig } from 'vite'

export default defineConfig({
  server: {
    host: true,
    watch: {
      usePolling: true
    },
    proxy: {
      '/api': {
        target: process.env.VITE_API_TARGET || 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
