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
        // 统一命名入口文件
        entryFileNames: 'js/main.[hash].js',
        // 统一命名chunk文件
        chunkFileNames: 'js/chunk.[hash].js',
        // 资源文件命名
        assetFileNames: 'assets/[name].[hash].[ext]',
        // 手动配置chunk分割，避免基于组件名的chunk
        manualChunks: {
          // 将Vue相关库打包在一起
          vue: ['vue', 'vue-router'],
          // 将Element Plus打包
          element: ['element-plus'],
          // 将工具库打包
          utils: ['axios', 'crypto-js', 'uuid', 'shortid'],
          // 其他依赖分组
          markdown: ['markdown-it', 'vue3-markdown-it', 'github-markdown-css'],
          cron: ['@vue-js-cron/element-plus', '@vue-js-cron/light', 'cronstrue']
        }
      }
    }
  }
})

