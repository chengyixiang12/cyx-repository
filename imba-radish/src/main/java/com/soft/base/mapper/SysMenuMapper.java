package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysMenu;
import com.soft.base.model.request.GetMenuListRequest;
import com.soft.base.model.vo.GetMenuListVo;
import com.soft.base.model.vo.GetMenuVo;
import com.soft.base.model.vo.MenusVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_menu(菜单信息表)】的数据库操作Mapper
* @createDate 2024-11-16 11:23:42
* @Entity com.soft.base.entity.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<MenusVo> getMenuTree(@Param("userId") Long userId);

    IPage<GetMenuListVo> getMenuList(@Param("page") IPage<GetMenuListVo> page,
                     @Param("request") GetMenuListRequest request);

    GetMenuVo getMenu(@Param("id") Long id);

    List<Long> getByIds(@Param("ids") List<Long> ids);
}




