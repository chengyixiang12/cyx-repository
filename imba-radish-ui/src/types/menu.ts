export interface MenuItem {
  id: string;
  path: string;
  component?: string;  // 对应views目录下的组件路径
  name: string;
  icon?: string;
  noAuth?: boolean;  // 是否不需要认证
  parentId?: string;
  visible: number;
  children?: MenuItem[];
}

export interface GetMenuListVo {
  id: string;
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
  id: string;
  parentId: string;
  name: string;
  path: string;
  component: string | null;
  icon: string;
  type: string;
  orderNum: number | null;
  status: number;
  visible: number;
  remark: string;
}

export interface SaveMenuRequest {
  parentId?: string | null;
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
  id: string | null;
  parentId?: string | null;
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
  id: string;
  name: string;
}

export interface GetMenuTreeVo {
  id: string;
  name: string;
  parentId: string;
  children: GetMenuTreeVo[];
}

export interface GetAssignedMenuVo {
  id: string;
}
