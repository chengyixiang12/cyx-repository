export interface MenuItem {
  id: number
  path: string
  component: string  // 对应views目录下的组件路径
  title: string
  icon?: string
  noAuth?: boolean   // 是否不需要认证
  parentId: number
  children?: MenuItem[]
}

export interface GetMenuListVo {
  id: number;
  name: string;
  type: string;
  status: number;
  orderNum: number;
  icon: string;
}

export interface GetMenuListRequest {
  menuName: string;
  pageNum: number;
  pageSize: number;
}

export enum MenuType {
  Directory = 0,
  Menu = 1,
  Button = 2
}

export interface GetMenuVo {
  id: number;
  parentId: number;
  name: string;
  path: string;
  component: string;
  icon: string;
  type: string;
  orderNum: number;
  status: number;
  visible: number;
  remark: string;
}
