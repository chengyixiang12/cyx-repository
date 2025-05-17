package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysDept;
import com.soft.base.model.dto.ExportDeptDto;
import com.soft.base.model.request.*;
import com.soft.base.model.vo.DeptTreeVo;
import com.soft.base.model.vo.DeptVo;
import com.soft.base.model.vo.GetDeptsVo;
import com.soft.base.model.vo.PageVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Service
* @createDate 2024-10-26 09:06:18
*/
public interface SysDeptService extends IService<SysDept> {

    List<DeptTreeVo> getDeptTree();

    Boolean existCode(String code);

    Boolean existCode(String code, Long id);

    Boolean isNotEmpty();

    void saveDept(SaveDeptRequest request);

    void editDept(EditDeptRequest request);

    void deleteDeptBatch(DeleteRequest request);

    DeptVo getDept(Long id);

    List<ExportDeptDto> exportDept(ExportDeptRequest request);

    PageVo<GetDeptsVo> getDepts(GetDeptsRequest request);

    List<Long> getChildDeptIds(List<Long> deptIds);

    Long getRootDept();
}
