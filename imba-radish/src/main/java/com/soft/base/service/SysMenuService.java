package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysMenu;
import com.soft.base.request.EditMenuRequest;
import com.soft.base.request.SaveMenuRequest;
import com.soft.base.vo.MenusVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_menu(菜单信息表)】的数据库操作Service
* @createDate 2024-11-16 11:23:42
*/
public interface SysMenuService extends IService<SysMenu> {

    List<MenusVo> getMenus();

    void saveMenu(SaveMenuRequest request);

    void editMenu(EditMenuRequest request);
}
