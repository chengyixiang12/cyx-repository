export interface MenuItem {
  id: number
  path: string
  component?: string  // 对应views目录下的组件路径
  name: string
  icon?: string
  noAuth?: boolean   // 是否不需要认证
  parentId?: number
  children?: MenuItem[]
}

export interface GetMenuListVo {
  id: number;
  name: string;
  type: string;
  status: number;
  orderNum: number;
  icon: string;
  visible: number;
}

export interface GetMenuListRequest {
  pageNum: number;
  pageSize: number;
  keyword: string;
  status: number | null;
  type: string;
  visible: number | null
}

export enum MenuType {
  Directory = 0,
  Menu = 1,
  Button = 2
}

export interface GetMenuVo {
  id: number;
  parentId: number | null;
  name: string;
  path: string;
  component: string | null;
  icon: string;
  type: string;
  orderNum: number;
  status: number;
  visible: number;
  remark: string;
}

export interface SaveMenuRequest {
  parentId?: number;
  name: string;
  path: string;
  component: string;
  icon: string;
  type: string;
  orderNum: number;
  visible: number;
  remark?: string;
}

export interface EditMenuRequest {
  id: number;
  parentId?: number;
  name: string;
  path: string;
  component: string;
  icon: string;
  type: string;
  orderNum: number;
  visible: number;
  remark?: string;
}

export interface GetSelectMenuVo {
  id: number;
  name: string;
}

export interface GetMenuTreeVo {
  id: number;
  name: string;
  parentId: number;
  children: GetMenuTreeVo[];
}

export interface GetAssignedMenuVo {
  id: number
}
