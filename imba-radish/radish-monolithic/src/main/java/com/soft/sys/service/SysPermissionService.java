package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysPermission;
import com.soft.sys.model.request.EditPermissionDTO;
import com.soft.sys.model.request.PermissionsDTO;
import com.soft.sys.model.request.SavePermissionDTO;
import com.soft.sys.model.vo.*;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_permission】的数据库操作Service
* @createDate 2024-11-19 09:36:53
*/
public interface SysPermissionService extends IService<SysPermission> {

    PageVO<PermissionsVO> getPermissions(PermissionsDTO request);


    void savePermission(SavePermissionDTO request);

    List<String> getPermissionsByRoleCodes(List<String> roleCodes);

    boolean existCode(String code);

    List<GetAllPermissionVO> getAllPermission();

    List<GetAssignPerVO> getAssignPer(Long roleId);

    void editPermission(EditPermissionDTO request);

    void deletePermission(Long id);

    void enablePermission(Long id);

    void forbiddenPermission(Long id);

    boolean existEnableCode(String[] permissions);

    GetPermissionVO getPermission(Long id);
}
