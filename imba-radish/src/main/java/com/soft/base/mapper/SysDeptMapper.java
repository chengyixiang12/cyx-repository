package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysDept;
import com.soft.base.model.dto.ExportDeptDto;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.GetDeptsRequest;
import com.soft.base.model.vo.DeptTreeVo;
import com.soft.base.model.vo.DeptVo;
import com.soft.base.model.vo.GetDeptsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Mapper
* @createDate 2024-10-26 09:06:18
* @Entity com.soft.base.entity.SysDept
*/
public interface SysDeptMapper extends BaseMapper<SysDept> {

    String getLevel(@Param("parentId") Long parentId);

    List<DeptTreeVo> getAllDept();

    void deleteDeptBatch(@Param("request") DeleteRequest request);

    DeptVo getDept(@Param("id") Long id);

    List<ExportDeptDto> exportDept(@Param("ids") List<Long> ids);

    IPage<GetDeptsVo> getDepts(@Param("page") IPage<GetDeptsVo> page,
                                @Param("request") GetDeptsRequest request);

    List<Long> getChildDeptIds(@Param("deptIds") List<Long> deptIds);

    Long getRootDept();
}




