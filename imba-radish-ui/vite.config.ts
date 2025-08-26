import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import { base_url } from './src/common/global-config'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5174,
    proxy: {
      '/api': {
        target: base_url, // 设置代理目标
        changeOrigin: true, // 是否改变请求源地址
        rewrite: (path) => path.replace(/^\/api/, '') // 将 /api 替换为空字符串
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    },
  },
  build: {
    rollupOptions: {
      output: {
        // 所有入口点使用相同命名模式
        entryFileNames: 'js/[hash].js',
        // 对chunk使用数字序列而不是基于组件名
        chunkFileNames: 'js/[hash].js',
        // 手动配置chunk分割
        manualChunks: {
          vendor: ['vue', 'vue-router'],
          // 其他依赖分组...
        }
      }
    }
  }
})

