package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.dto.GetUserDto;
import com.soft.base.dto.GetUserRoleDto;
import com.soft.base.entity.SysUser;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.request.EditUserRequest;
import com.soft.base.request.PageRequest;
import com.soft.base.request.ResetPasswordRequest;
import com.soft.base.request.SaveUserRequest;
import com.soft.base.service.SecretKeyService;
import com.soft.base.service.SysDeptService;
import com.soft.base.service.SysRoleService;
import com.soft.base.service.SysUsersService;
import com.soft.base.utils.RSAUtil;
import com.soft.base.vo.AllUserVo;
import com.soft.base.dto.GetUserDeptDto;
import com.soft.base.vo.GetUserVo;
import com.soft.base.vo.PageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-09-30 15:49:52
*/
@Service
@CacheConfig(cacheNames = "cyx::users")
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUser> implements SysUsersService {

    private final SysUsersMapper sysUsersMapper;

    private final PasswordEncoder passwordEncoder;

    private final RSAUtil rsaUtil;

    private final SecretKeyService secretKeyService;

    private final SysDeptService sysDeptService;

    private final SysRoleService sysRoleService;

    @Autowired
    public SysUsersServiceImpl(SysUsersMapper sysUsersMapper,
                               PasswordEncoder passwordEncoder,
                               RSAUtil rsaUtil,
                               SecretKeyService secretKeyService,
                               SysDeptService sysDeptService,
                               SysRoleService sysRoleService) {
        this.sysUsersMapper = sysUsersMapper;
        this.passwordEncoder = passwordEncoder;
        this.rsaUtil = rsaUtil;
        this.secretKeyService = secretKeyService;
        this.sysDeptService = sysDeptService;
        this.sysRoleService = sysRoleService;
    }

    @Override
    public PageVo<AllUserVo> getAllUsers(PageRequest request) {
        IPage<AllUserVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<AllUserVo> allUsers = sysUsersMapper.getAllUsers(page);
        PageVo<AllUserVo> pageVo = new PageVo<>();
        pageVo.setResult(allUsers.getRecords());
        pageVo.setTotal(allUsers.getTotal());
        return pageVo;
    }

    @Override
    @CacheEvict(key = "#username")
    public void editPassword(String targetPass, String username) throws Exception {
        String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
        String encode = passwordEncoder.encode(rsaUtil.decrypt(targetPass, privateKey));
        sysUsersMapper.editPassword(username, encode);
    }

    @Override
    @CacheEvict(key = "#username")
    public void resetPassword(ResetPasswordRequest request) throws Exception {
        String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
        String encode = passwordEncoder.encode(rsaUtil.decrypt(request.getPassword(), privateKey));
        sysUsersMapper.editPassword(request.getUsername(), encode);
    }

    @Override
    public String getEmail(String username) {
        return sysUsersMapper.getEmail(username);
    }

    @Override
    public boolean checkUsernameExist(String username) {
        return sysUsersMapper.exists(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username).or().eq(SysUser::getEmail, username));
    }

    @Override
    public void saveUser(SaveUserRequest request) throws Exception{
        String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
        request.setPassword(passwordEncoder.encode(rsaUtil.decrypt(request.getPassword(), privateKey)));
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        sysUser.setDefault();
        sysUsersMapper.insert(sysUser);
    }

    @Override
    @CacheEvict(key = "#username")
    public void editUser(EditUserRequest request) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        sysUsersMapper.updateById(sysUser);
    }

    @Override
    public GetUserVo getUser(Long id) {
        GetUserDto getUserDto = sysUsersMapper.getUser(id);
        GetUserDeptDto getUserDeptDto = sysDeptService.getUserDept(getUserDto.getDeptId());
        List<GetUserRoleDto> getUserRoleDto = sysRoleService.getUserRole(id);
        GetUserVo getUserVo = new GetUserVo();
        BeanUtils.copyProperties(getUserDto, getUserVo);
        getUserVo.setGetUserDeptDto(getUserDeptDto);
        getUserVo.setGetUserRoleDtoList(getUserRoleDto);
        return getUserVo;
    }
}




