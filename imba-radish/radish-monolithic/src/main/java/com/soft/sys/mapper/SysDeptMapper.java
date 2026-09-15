package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysDept;
import com.soft.sys.model.dto.ExportDeptExcelDTO;
import com.soft.sys.model.request.GetDeptsDTO;
import com.soft.sys.model.vo.DeptTreeVO;
import com.soft.sys.model.vo.DeptVO;
import com.soft.sys.model.vo.GetDeptsVO;
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

    List<DeptTreeVO> getAllDept();

    DeptVO getDept(@Param("id") Long id);

    List<ExportDeptExcelDTO> exportDept(@Param("ids") List<Long> ids);

    IPage<GetDeptsVO> getDepts(@Param("page") IPage<GetDeptsVO> page,
                                @Param("request") GetDeptsDTO request);

    List<Long> getChildDeptIds(@Param("deptIds") List<Long> deptIds);

    Long getRootDept();
}




