package com.soft.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysMenu;
import com.soft.base.exception.GlobelException;
import com.soft.base.mapper.SysMenuMapper;
import com.soft.base.request.EditMenuRequest;
import com.soft.base.request.SaveMenuRequest;
import com.soft.base.service.SysMenuService;
import com.soft.base.utils.SecurityUtil;
import com.soft.base.vo.MenusVo;
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
    public List<MenusVo> getMenus() {
        Long userId = securityUtil.getUserInfo().getId();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<MenusVo> menus = sysMenuMapper.getMenus(userId);
        return buildTree(menus);
    }

    @Override
    public void saveMenu(SaveMenuRequest request) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);
        sysMenuMapper.insert(sysMenu);
    }

    @Override
    public void editMenu(EditMenuRequest request) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);
        sysMenuMapper.updateById(sysMenu);
    }

    /**
     * 菜单架构树
     * @param menusVos
     * @return
     */
    private List<MenusVo> buildTree(List<MenusVo> menusVos) {
        if (menusVos == null || menusVos.isEmpty()) {
            throw new GlobelException("菜单为空");
        }

        Map<Long, MenusVo> map = new HashMap<>();
        List<MenusVo> tree = new ArrayList<>();

        // 将菜单存入映射
        for (MenusVo menu : menusVos) {
            map.put(menu.getId(), menu);
        }

        // 构建树结构
        for (MenusVo menu : menusVos) {
            if (menu.getParentId() == null) {
                tree.add(menu);
            } else {
                MenusVo parent = map.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return tree;
    }
}




