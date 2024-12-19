package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.dto.ExportDeptDto;
import com.soft.base.dto.GetUserDeptDto;
import com.soft.base.entity.SysDept;
import com.soft.base.request.DeleteRequest;
import com.soft.base.request.EditDeptRequest;
import com.soft.base.request.ExportDeptRequest;
import com.soft.base.request.SaveDeptRequest;
import com.soft.base.vo.DeptTreeVo;
import com.soft.base.vo.DeptVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Service
* @createDate 2024-10-26 09:06:18
*/
public interface SysDeptService extends IService<SysDept> {

    List<DeptTreeVo> getDeptTree();

    Boolean existCode(String code);

    Boolean isNotEmpty();

    void saveDept(SaveDeptRequest request);

    void editDept(EditDeptRequest request);

    void deleteDeptBatch(DeleteRequest request);

    DeptVo getDept(Long id);

    GetUserDeptDto getUserDept(Long deptId);

    List<ExportDeptDto> exportDept(ExportDeptRequest request);
}
