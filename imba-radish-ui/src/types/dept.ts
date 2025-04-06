export interface DeptTreeVo {
    id: number,
    code: string,
    name: string,
    parentId: number,
    children: DeptTreeVo[]
}