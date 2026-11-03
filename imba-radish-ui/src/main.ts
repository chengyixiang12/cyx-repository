import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router/routers'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/styles/theme.css' // UI 设计令牌 + Element Plus 主题覆盖 (必须在 element-plus 之后引入)
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 导入中文语言包
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import './assets/styles/global.css'

const app = createApp(App)
// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(router)
.use(ElementPlus, {
    locale: zhCn, // 设置为中文
})
.mount('#app')