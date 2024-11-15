package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.base.entity.SysMenu;
import com.soft.base.vo.MenusVo;

/**
* @author cyq
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Mapper
* @createDate 2024-11-09 23:49:58
* @Entity com.soft.base.entity.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    Page<MenusVo> getMenus();
}




