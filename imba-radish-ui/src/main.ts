import { createApp } from 'vue'
import App from './App.vue'
import router from './router/routers'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import { ElementIcons } from '@/components/index';

const app = createApp(App)
    .use(router)
    .use(ElementPlus)
    .use(ElementIcons) //ElementIcon
    .mount('#app')