import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // 把所有 /api 请求代理到 qa-service-user(8080)。
    // 这样前端始终发起同源请求，既绕开开发期跨域，也让部署只需改一处 target。
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // Actuator 健康检查同样转发，便于在浏览器直接查看
      '/actuator': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
