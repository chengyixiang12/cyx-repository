package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDept;
import com.soft.sys.model.dto.ExportDeptDto;
import com.soft.sys.model.request.EditDeptRequest;
import com.soft.sys.model.request.ExportDeptRequest;
import com.soft.sys.model.request.GetDeptsRequest;
import com.soft.sys.model.request.SaveDeptRequest;
import com.soft.sys.model.vo.DeptTreeVo;
import com.soft.sys.model.vo.DeptVo;
import com.soft.sys.model.vo.GetDeptsVo;
import com.soft.sys.model.vo.PageVO;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Service
* @createDate 2024-10-26 09:06:18
*/
public interface SysDeptService extends IService<SysDept> {

    List<DeptTreeVo> getDeptTree(Long id);

    Boolean existCode(String code);

    Boolean existCode(String code, Long id);

    Boolean isNotEmpty();

    void saveDept(SaveDeptRequest request);

    void editDept(EditDeptRequest request);

    DeptVo getDept(Long id);

    List<ExportDeptDto> exportDept(ExportDeptRequest request);

    PageVO<GetDeptsVo> getDepts(GetDeptsRequest request);

    List<Long> getChildDeptIds(List<Long> deptIds);

    Long getRootDept();
}
