interface MenuItem {
    id: number
    path: string
    component: string  // 对应views目录下的组件路径
    title: string
    icon?: string
    noAuth?: boolean   // 是否不需要认证
    parentId: number
    children?: MenuItem[]
  }