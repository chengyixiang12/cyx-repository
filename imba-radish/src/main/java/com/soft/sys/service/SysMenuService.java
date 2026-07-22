package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysMenu;
import com.soft.sys.model.request.EditMenuDTO;
import com.soft.sys.model.request.GetMenuListDTO;
import com.soft.sys.model.request.PageMenuTreeDTO;
import com.soft.sys.model.request.SaveMenuDTO;
import com.soft.sys.model.vo.*;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_menu(菜单信息表)】的数据库操作Service
* @createDate 2024-11-16 11:23:42
*/
public interface SysMenuService extends IService<SysMenu> {

    List<GetSelectMenuVO> getSelectMenu(String type);

    void saveMenu(SaveMenuDTO request);

    void editMenu(EditMenuDTO request);

    PageVO<GetMenuListVO> getMenuList(GetMenuListDTO request);

    GetMenuVO getMenu(Long id);

    void deleteMenu(Long id);

    void enableMenu(Long id);

    void disableMenu(Long id);

    List<MenusVO> getMenuRoute();

    List<GetMenuTreeVO> getMenuTree();

    List<GetAssignedMenuVO> getAssignedMenu(Long roleId);

    void menuShow(Long id);

    void menuHide(Long id);

    List<MenusVO> getLeftMenus();

    PageVO<PageMenuTreeVO> pageMenuTree(PageMenuTreeDTO request);
}
