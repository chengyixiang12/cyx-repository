// 解决引入爆红问题
declare module "*.vue" {
    import { DefineComponent } from "vue"
    const component: DefineComponent<{}, {}, any>
    export default component
}

declare module 'vue3-markdown-it' {
  import type { DefineComponent } from 'vue'
  export const VueMarkdownIt: DefineComponent<{
    source: string
  }>
}