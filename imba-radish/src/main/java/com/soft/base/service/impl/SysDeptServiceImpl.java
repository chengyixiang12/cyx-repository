package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.model.dto.ExportDeptDto;
import com.soft.base.model.dto.GetUserDeptDto;
import com.soft.base.entity.SysDept;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysDeptMapper;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.EditDeptRequest;
import com.soft.base.model.request.ExportDeptRequest;
import com.soft.base.model.request.SaveDeptRequest;
import com.soft.base.service.SysDeptService;
import com.soft.base.model.vo.DeptTreeVo;
import com.soft.base.model.vo.DeptUserVo;
import com.soft.base.model.vo.DeptVo;
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
        List<DeptUserVo> deptUserVos = sysUsersMapper.getAllUser();
        deptTreeVos = buildTree(deptTreeVos, deptUserVos);
        return deptTreeVos;
    }

    @Override
    public Boolean existCode(String code) {
        return sysDeptMapper.exists(Wrappers.lambdaQuery(SysDept.class).eq(SysDept::getCode, code));
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
        sysDept.setLevel(level);
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

    /**
     * 组织架构树
     * @param departments
     * @param users
     * @return
     */
    private List<DeptTreeVo> buildTree(List<DeptTreeVo> departments, List<DeptUserVo> users) {
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

        if (!(users == null || users.isEmpty())) {
            // 将用户分配到部门
            for (DeptUserVo user : users) {
                DeptTreeVo dept = map.get(user.getDeptId());
                if (dept != null) {
                    dept.getUsers().add(user);
                }
            }
        }

        return tree;
    }
}




