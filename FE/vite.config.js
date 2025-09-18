import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Spring Boot 서버
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api') // 필요 시 경로 재작성
      }
    }
  }
})
