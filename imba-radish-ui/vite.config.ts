import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8081', // 设置代理目标
        changeOrigin: true, // 是否改变请求源地址
        rewrite: (path) => path.replace(/^\/api/, '') // 将 /api 替换为空字符串
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
})

