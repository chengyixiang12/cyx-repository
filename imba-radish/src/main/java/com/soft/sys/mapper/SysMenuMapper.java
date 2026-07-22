package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysMenu;
import com.soft.sys.model.request.GetMenuListDTO;
import com.soft.sys.model.request.PageMenuTreeDTO;
import com.soft.sys.model.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_menu(菜单信息表)】的数据库操作Mapper
* @createDate 2024-11-16 11:23:42
* @Entity com.soft.base.entity.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<MenusVO> getMenuRoute(@Param("userId") Long userId);

    IPage<GetMenuListVO> getMenuList(@Param("page") IPage<GetMenuListVO> page,
                     @Param("request") GetMenuListDTO request);

    GetMenuVO getMenu(@Param("id") Long id);

    List<Long> getByIds(@Param("ids") List<Long> ids);

    void enableMenu(@Param("id") Long id);

    void disableMenu(@Param("id") Long id);

    List<GetSelectMenuVO> getSelectMenu(@Param("type") String type);

    List<GetMenuTreeVO> getMenuTree();

    List<GetAssignedMenuVO> getAssignedMenu(@Param("roleId") Long roleId);

    void menuShow(@Param("id") Long id);

    void menuHide(@Param("id") Long id);

    List<MenusVO> getLeftMenus(@Param("userId") Long userId);

    IPage<PageMenuTreeVO> pageMenuTree(IPage<PageMenuTreeVO> page,
                                       @Param("request") PageMenuTreeDTO request);
}




