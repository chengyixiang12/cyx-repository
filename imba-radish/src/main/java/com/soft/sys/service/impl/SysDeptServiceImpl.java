package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.entity.SysDept;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.mapper.SysDeptMapper;
import com.soft.sys.model.dto.ExportDeptDto;
import com.soft.sys.model.request.EditDeptRequest;
import com.soft.sys.model.request.ExportDeptRequest;
import com.soft.sys.model.request.GetDeptsRequest;
import com.soft.sys.model.request.SaveDeptRequest;
import com.soft.sys.model.vo.DeptTreeVo;
import com.soft.sys.model.vo.DeptVo;
import com.soft.sys.model.vo.GetDeptsVo;
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
    public List<DeptTreeVo> getDeptTree(Long id) {
        List<DeptTreeVo> deptTreeVos = sysDeptMapper.getAllDept();
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
    public void saveDept(SaveDeptRequest request) {
        String level = sysDeptMapper.getLevel(request.getParentId());
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(request, sysDept);
        sysDept.setLevel(String.valueOf(Integer.parseInt(level) + BaseConstant.DEPT_LEVEL_ADD_ONE));
        sysDeptMapper.insert(sysDept);
    }

    @Override
    public void editDept(EditDeptRequest request) {
        String level = sysDeptMapper.getLevel(request.getParentId());
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(request, sysDept);
        sysDept.setLevel(level);
        sysDeptMapper.updateById(sysDept);
    }

    @Override
    public DeptVo getDept(Long id) {
        return sysDeptMapper.getDept(id);
    }

    @Override
    public List<ExportDeptDto> exportDept(ExportDeptRequest request) {
        return sysDeptMapper.exportDept(request.getIds());
    }

    @Override
    public PageVO<GetDeptsVo> getDepts(GetDeptsRequest request) {
        IPage<GetDeptsVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDeptMapper.getDepts(page, request);
        PageVO<GetDeptsVo> pageVo = new PageVO<>();
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
    private List<DeptTreeVo> buildTree(List<DeptTreeVo> departments, Long id) {
        if (departments == null || departments.isEmpty()) {
            throw new GlobalException("组织架构为空");
        }

        // 去掉当前部门的结构
        if (id != null) {
            departments = departments.stream().filter(item -> id != Long.parseLong(item.getId())).toList();
        }

        Map<String, DeptTreeVo> map = new HashMap<>();
        List<DeptTreeVo> tree = new ArrayList<>();

        // 将部门存入映射
        for (DeptTreeVo dept : departments) {
            map.put(dept.getId(), dept);
        }

        // 构建树结构并映射用户
        for (DeptTreeVo dept : departments) {
            if (dept.getParentId() == null) {
                tree.add(dept);
            } else {
                DeptTreeVo parent = map.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                }
            }
        }

        return tree;
    }
}




