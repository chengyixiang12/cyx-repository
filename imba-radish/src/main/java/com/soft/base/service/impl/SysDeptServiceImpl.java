package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysDept;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysDeptMapper;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.model.dto.ExportDeptDto;
import com.soft.base.model.dto.GetUserDeptDto;
import com.soft.base.model.request.*;
import com.soft.base.model.vo.DeptTreeVo;
import com.soft.base.model.vo.DeptVo;
import com.soft.base.model.vo.GetDeptsVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysDeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
    implements SysDeptService{

    private final SysDeptMapper sysDeptMapper;

    private final SysUsersMapper sysUsersMapper;

    @Autowired
    public SysDeptServiceImpl(SysDeptMapper sysDeptMapper,
                              SysUsersMapper sysUsersMapper) {
        this.sysDeptMapper = sysDeptMapper;
        this.sysUsersMapper = sysUsersMapper;
    }

    @Override
    public List<DeptTreeVo> getDeptTree() {
        List<DeptTreeVo> deptTreeVos = sysDeptMapper.getAllDept();
        deptTreeVos = buildTree(deptTreeVos);
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
    public void deleteDeptBatch(DeleteRequest request) {
        sysDeptMapper.deleteDeptBatch(request);
    }

    @Override
    public DeptVo getDept(Long id) {
        return sysDeptMapper.getDept(id);
    }

    @Override
    public GetUserDeptDto getUserDept(Long deptId) {
        return sysDeptMapper.getUserDept(deptId);
    }

    @Override
    public List<ExportDeptDto> exportDept(ExportDeptRequest request) {
        return sysDeptMapper.exportDept(request.getIds());
    }

    @Override
    public PageVo<GetDeptsVo> getDepts(GetDeptsRequest request) {
        IPage<GetDeptsVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDeptMapper.getDepts(page, request);
    }

    @Override
    public List<Long> getChildDeptIds(List<Long> deptIds) {
        return sysDeptMapper.getChildDeptIds(deptIds);
    }

    /**
     * 组织架构树
     * @param departments
     * @return
     */
    private List<DeptTreeVo> buildTree(List<DeptTreeVo> departments) {
        if (departments == null || departments.isEmpty()) {
            throw new GlobalException("组织架构为空");
        }

        Map<Long, DeptTreeVo> map = new HashMap<>();
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




