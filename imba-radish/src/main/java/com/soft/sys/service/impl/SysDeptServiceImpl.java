package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.entity.SysDept;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.mapper.SysDeptMapper;
import com.soft.sys.model.dto.ExportDeptExcelDTO;
import com.soft.sys.model.request.EditDeptDTO;
import com.soft.sys.model.request.ExportDeptDTO;
import com.soft.sys.model.request.GetDeptsDTO;
import com.soft.sys.model.request.SaveDeptDTO;
import com.soft.sys.model.vo.DeptTreeVO;
import com.soft.sys.model.vo.DeptVO;
import com.soft.sys.model.vo.GetDeptsVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Service实现
* @createDate 2024-10-26 09:06:18
*/
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
    implements SysDeptService{

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<DeptTreeVO> getDeptTree(Long id) {
        List<DeptTreeVO> deptTreeVos = sysDeptMapper.getAllDept();
        deptTreeVos = buildTree(deptTreeVos, id);
        return deptTreeVos;
    }

    @Override
    public Boolean existCode(String code) {
        return sysDeptMapper.exists(Wrappers.lambdaQuery(SysDept.class).eq(SysDept::getCode, code));
    }

    @Override
    public Boolean existCode(String code, Long id) {
        return sysDeptMapper.exists(Wrappers.lambdaQuery(SysDept.class).eq(SysDept::getCode, code).ne(SysDept::getId, id));
    }

    @Override
    public Boolean isNotEmpty() {
        return sysDeptMapper.selectCount(Wrappers.lambdaQuery(SysDept.class)) > 0;
    }

    @Override
    public void saveDept(SaveDeptDTO request) {
        String level = sysDeptMapper.getLevel(request.getParentId());
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(request, sysDept);
        sysDept.setLevel(String.valueOf(Integer.parseInt(level) + BaseConstant.DEPT_LEVEL_ADD_ONE));
        sysDeptMapper.insert(sysDept);
    }

    @Override
    public void editDept(EditDeptDTO request) {
        String level = sysDeptMapper.getLevel(request.getParentId());
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(request, sysDept);
        sysDept.setLevel(level);
        sysDeptMapper.updateById(sysDept);
    }

    @Override
    public DeptVO getDept(Long id) {
        return sysDeptMapper.getDept(id);
    }

    @Override
    public List<ExportDeptExcelDTO> exportDept(ExportDeptDTO request) {
        return sysDeptMapper.exportDept(request.getIds());
    }

    @Override
    public PageVO<GetDeptsVO> getDepts(GetDeptsDTO request) {
        IPage<GetDeptsVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDeptMapper.getDepts(page, request);
        PageVO<GetDeptsVO> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    @Override
    public List<Long> getChildDeptIds(List<Long> deptIds) {
        return sysDeptMapper.getChildDeptIds(deptIds);
    }

    @Override
    public Long getRootDept() {
        return sysDeptMapper.getRootDept();
    }

    /**
     * 组织架构树
     * @param departments
     * @return
     */
    private List<DeptTreeVO> buildTree(List<DeptTreeVO> departments, Long id) {
        if (departments == null || departments.isEmpty()) {
            throw new GlobalException("组织架构为空");
        }

        // 去掉当前部门的结构
        if (id != null) {
            departments = departments.stream().filter(item -> id != Long.parseLong(item.getId())).toList();
        }

        Map<String, DeptTreeVO> map = new HashMap<>();
        List<DeptTreeVO> tree = new ArrayList<>();

        // 将部门存入映射
        for (DeptTreeVO dept : departments) {
            map.put(dept.getId(), dept);
        }

        // 构建树结构并映射用户
        for (DeptTreeVO dept : departments) {
            if (dept.getParentId() == null) {
                tree.add(dept);
            } else {
                DeptTreeVO parent = map.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                }
            }
        }

        return tree;
    }
}




