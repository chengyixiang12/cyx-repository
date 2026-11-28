import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

const srcPath = path.resolve(__dirname, './src')

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5147,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8081', // 设置代理目标
        changeOrigin: true, // 是否改变请求源地址
        rewrite: (path) => path.replace(/^\/api/, '') // 将 /api 替换为空字符串
      },
      '/ws': {
        target: 'ws://127.0.0.1:8081', // 设置代理目标
        changeOrigin: true, // 是否改变请求源地址
      },
      '/download': {
        target: 'http://192.168.226.130:9000',
        changeOrigin: true,  // 是否改变请求源地址
        rewrite: (path) => path.replace(/^\/download/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': srcPath
    }
  }
})

