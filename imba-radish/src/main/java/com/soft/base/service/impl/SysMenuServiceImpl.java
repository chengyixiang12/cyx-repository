package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysMenu;
import com.soft.base.mapper.SysMenuMapper;
import com.soft.base.model.request.EditMenuRequest;
import com.soft.base.model.request.GetMenuListRequest;
import com.soft.base.model.request.SaveMenuRequest;
import com.soft.base.model.vo.GetMenuListVo;
import com.soft.base.model.vo.GetMenuVo;
import com.soft.base.model.vo.MenusVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysMenuService;
import com.soft.base.utils.SecurityUtil;
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
    public List<MenusVo> getMenuTree() {
        Long userId = securityUtil.getUserInfo().getId();
        if (userId == null) {
            return new ArrayList<>();
        }
        List<MenusVo> menus = sysMenuMapper.getMenuTree(userId);
        if (menus != null && !menus.isEmpty()) {
            return buildTree(menus);
        }
        return menus;
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

    @Override
    public PageVo<GetMenuListVo> getMenuList(GetMenuListRequest request) {
        IPage<GetMenuListVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        sysMenuMapper.getMenuList(page, request);
        PageVo<GetMenuListVo> pageVo = new PageVo<>();
        pageVo.setResult(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public GetMenuVo getMenu(Long id) {
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

    /**
     * 菜单架构树
     * @param menusVos
     * @return
     */
    private List<MenusVo> buildTree(List<MenusVo> menusVos) {

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




