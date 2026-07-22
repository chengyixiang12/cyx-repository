package com.soft.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.entity.SysMenu;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.mapper.SysMenuMapper;
import com.soft.sys.model.ctf.MenuTree;
import com.soft.sys.model.request.EditMenuDTO;
import com.soft.sys.model.request.GetMenuListDTO;
import com.soft.sys.model.request.PageMenuTreeDTO;
import com.soft.sys.model.request.SaveMenuDTO;
import com.soft.sys.model.vo.*;
import com.soft.sys.service.SysMenuService;
import com.soft.sys.utils.SecurityUtil;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author cyq
* @description 针对表【sys_menu(菜单信息表)】的数据库操作Service实现
* @createDate 2024-11-16 11:23:42
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{

    private final SysMenuMapper sysMenuMapper;

    private final SecurityUtil securityUtil;

    @Autowired
    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper,
                              SecurityUtil securityUtil) {
        this.sysMenuMapper = sysMenuMapper;
        this.securityUtil = securityUtil;
    }

    @Override
    public List<GetSelectMenuVO> getSelectMenu(String type) {
        switch (type) {
            case BaseConstant.MENU_TYPE_MENU: {
                type = BaseConstant.MENU_TYPE_DIRECTORY;
                break;
            }
            case BaseConstant.MENU_TYPE_BUTTON: {
                type = BaseConstant.MENU_TYPE_MENU;
                break;
            }
            default: {
                throw new GlobalException("未知的菜单类型");
            }
        }

        return sysMenuMapper.getSelectMenu(type);
    }

    @Override
    public void saveMenu(SaveMenuDTO request) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);
        sysMenuMapper.insert(sysMenu);
    }

    @Override
    public void editMenu(EditMenuDTO request) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);
        sysMenuMapper.updateById(sysMenu);
    }

    @Override
    public PageVO<GetMenuListVO> getMenuList(GetMenuListDTO request) {
        IPage<GetMenuListVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        sysMenuMapper.getMenuList(page, request);
        PageVO<GetMenuListVO> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public GetMenuVO getMenu(Long id) {
        return sysMenuMapper.getMenu(id);
    }

    @Override
    public void deleteMenu(Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<Long> tmpList = new ArrayList<>();
        do {
            tmpList.addAll(ids);
            ids = sysMenuMapper.getByIds(ids);
        } while (!ids.isEmpty());
        sysMenuMapper.deleteByIds(tmpList);
    }

    @Override
    public void enableMenu(Long id) {
        sysMenuMapper.enableMenu(id);
    }

    @Override
    public void disableMenu(Long id) {
        sysMenuMapper.disableMenu(id);
    }

    @Override
    public List<MenusVO> getMenuRoute() {
        Long userId = securityUtil.getUserInfo().getId();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<MenusVO> menus = sysMenuMapper.getMenuRoute(userId);
        return getMenusVos(menus);
    }

    @Override
    public List<GetMenuTreeVO> getMenuTree() {
        List<GetMenuTreeVO> menuTreeVos = sysMenuMapper.getMenuTree();
        if (CollectionUtil.isNotEmpty(menuTreeVos)) {
            return buildTree(menuTreeVos);
        }
        return menuTreeVos;
    }

    @Override
    public List<GetAssignedMenuVO> getAssignedMenu(Long roleId) {
        return sysMenuMapper.getAssignedMenu(roleId);
    }

    @Override
    public void menuShow(Long id) {
        sysMenuMapper.menuShow(id);
    }

    @Override
    public void menuHide(Long id) {
        sysMenuMapper.menuHide(id);
    }

    @Override
    public List<MenusVO> getLeftMenus() {
        Long userId = securityUtil.getUserInfo().getId();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<MenusVO> menus = sysMenuMapper.getLeftMenus(userId);
        return getMenusVos(menus);
    }

    @Override
    public PageVO<PageMenuTreeVO> pageMenuTree(PageMenuTreeDTO request) {
        IPage<PageMenuTreeVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysMenuMapper.pageMenuTree(page, request);
        return PageVO.<PageMenuTreeVO>builder().records(page.getRecords()).total(page.getTotal()).build();
    }

    /**
     * 获取菜单树结构数据
     *
     * @param menus 菜单列表数据
     * @return 处理后的菜单树结构，如果输入为空则返回原列表，否则返回构建好的树形结构
     */
    @Nullable
    private List<MenusVO> getMenusVos(List<MenusVO> menus) {
        if (CollectionUtil.isNotEmpty(menus)) {
            menus = buildTree(menus);
            // 移除没有子菜单的一级菜单项
            for (int i = 0; i < menus.size();) {
                MenusVO menusVo = menus.get(i);
                if (CollectionUtil.isEmpty(menusVo.getChildren())) {
                    menus.remove(i);
                } else {
                    i++;
                }
            }
        }

        return menus;
    }

    /**
     * 菜单结构树
     * @param menusVos
     * @return
     */
    private <T extends MenuTree<T>> List<T> buildTree(List<T> menusVos) {

        Map<String, T> map = new HashMap<>();
        List<T> tree = new ArrayList<>();

        // 将菜单存入映射
        for (T menu : menusVos) {
            map.put(menu.getId(), menu);
        }

        // 构建树结构
        for (T menu : menusVos) {
            if (menu.getParentId() == null) {
                tree.add(menu);
            } else {
                T parent = map.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return tree;
    }
}




