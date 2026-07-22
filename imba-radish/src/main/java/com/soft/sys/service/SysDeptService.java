package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDept;
import com.soft.sys.model.dto.ExportDeptExcelDTO;
import com.soft.sys.model.request.EditDeptDTO;
import com.soft.sys.model.request.ExportDeptDTO;
import com.soft.sys.model.request.GetDeptsDTO;
import com.soft.sys.model.request.SaveDeptDTO;
import com.soft.sys.model.vo.DeptTreeVO;
import com.soft.sys.model.vo.DeptVO;
import com.soft.sys.model.vo.GetDeptsVO;
import com.soft.sys.model.vo.PageVO;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Service
* @createDate 2024-10-26 09:06:18
*/
public interface SysDeptService extends IService<SysDept> {

    List<DeptTreeVO> getDeptTree(Long id);

    Boolean existCode(String code);

    Boolean existCode(String code, Long id);

    Boolean isNotEmpty();

    void saveDept(SaveDeptDTO request);

    void editDept(EditDeptDTO request);

    DeptVO getDept(Long id);

    List<ExportDeptExcelDTO> exportDept(ExportDeptDTO request);

    PageVO<GetDeptsVO> getDepts(GetDeptsDTO request);

    List<Long> getChildDeptIds(List<Long> deptIds);

    Long getRootDept();
}
