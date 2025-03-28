package com.soft.base.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.model.dto.GetUserDeptDto;
import com.soft.base.model.dto.GetUserDto;
import com.soft.base.model.dto.GetUserRoleDto;
import com.soft.base.entity.SysUser;
import com.soft.base.entity.SysUserRole;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.model.request.*;
import com.soft.base.service.*;
import com.soft.base.utils.RSAUtil;
import com.soft.base.model.vo.AllUserVo;
import com.soft.base.model.vo.GetUserVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.websocket.WebSocketConcreteHolder;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cyq
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2024-09-30 15:49:52
 */
@Service
@CacheConfig(cacheNames = "cyx:users")
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUser> implements SysUsersService {

    private final SysUsersMapper sysUsersMapper;

    private final PasswordEncoder passwordEncoder;

    private final RSAUtil rsaUtil;

    private final SecretKeyService secretKeyService;

    private final SysDeptService sysDeptService;

    private final SysRoleService sysRoleService;

    private final SysUserRoleService sysUserRoleService;

    @Autowired
    public SysUsersServiceImpl(SysUsersMapper sysUsersMapper,
                               PasswordEncoder passwordEncoder,
                               RSAUtil rsaUtil,
                               SecretKeyService secretKeyService,
                               SysDeptService sysDeptService,
                               SysRoleService sysRoleService,
                               SysUserRoleService sysUserRoleService) {
        this.sysUsersMapper = sysUsersMapper;
        this.passwordEncoder = passwordEncoder;
        this.rsaUtil = rsaUtil;
        this.secretKeyService = secretKeyService;
        this.sysDeptService = sysDeptService;
        this.sysRoleService = sysRoleService;
        this.sysUserRoleService = sysUserRoleService;
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
    @Transactional(rollbackFor = Exception.class)
    public void editPassword(String targetPass, Long id) {
        try {
            sendWebsocket(id);

            String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
            String encode = passwordEncoder.encode(rsaUtil.decrypt(targetPass, privateKey));

            SysUser sysUser = new SysUser();
            sysUser.setId(id);
            sysUser.setPassword(encode);

            sysUsersMapper.updateById(sysUser);
        } catch (NoSuchPaddingException
                 | IllegalBlockSizeException
                 | NoSuchAlgorithmException
                 | InvalidKeySpecException
                 | BadPaddingException
                 | IOException
                 | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) {
        try {
            sendWebsocket(request.getId());

            String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
            String encode = passwordEncoder.encode(rsaUtil.decrypt(request.getPassword(), privateKey));

            SysUser sysUser = new SysUser();
            sysUser.setId(request.getId());
            sysUser.setPassword(encode);

            sysUsersMapper.updateById(sysUser);
        } catch (NoSuchPaddingException
                 | IllegalBlockSizeException
                 | NoSuchAlgorithmException
                 | InvalidKeySpecException
                 | BadPaddingException
                 | IOException
                 | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
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
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SaveUserRequest request) throws Exception {
        String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
        request.setPassword(passwordEncoder.encode(rsaUtil.decrypt(request.getPassword(), privateKey)));
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        sysUser.setDefault();
        sysUsersMapper.insert(sysUser);

        // 保存角色
        List<Long> roleIds = request.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            roleIds.forEach(item -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(item);
                sysUserRole.setUserId(sysUser.getId());
                userRoles.add(sysUserRole);
            });
            sysUserRoleService.insertBatch(userRoles);
        }
    }

    @Override
    @CacheEvict(key = "#username")
    @Transactional(rollbackFor = Exception.class)
    public void editUser(EditUserRequest request, String username) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        sysUsersMapper.updateById(sysUser);

        List<Long> roleIds = request.getRoleIds();
        if (roleIds == null) {
            return;
        }
        //修改角色
        List<Long> roles = sysUserRoleService.getByUserId(request.getId());

        // 取两集合的交集
        Set<Long> intersection = roleIds.stream()
                .filter(new HashSet<>(roles)::contains)
                .collect(Collectors.toSet());
        roleIds.removeAll(intersection);
        roles.removeAll(intersection);

        if (!roles.isEmpty()) {
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, request.getId());
            wrapper.in(SysUserRole::getRoleId, roles);
            sysUserRoleService.remove(wrapper);
        }

        if (!roleIds.isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            roleIds.forEach(item -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(request.getId());
                sysUserRole.setRoleId(item);
                userRoles.add(sysUserRole);
            });
            sysUserRoleService.insertBatch(userRoles);
        }
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

    @Override
    public Long getManager(String managerRoleCode) {
        return sysUsersMapper.getManager(managerRoleCode);
    }

    @Override
    @CacheEvict(key = "#username")
    public void lockUser(String username) {
        sysUsersMapper.lockUser(username);
    }

    @Override
    @CacheEvict(key = "#username")
    public void unlockUser(String username) {
        sysUsersMapper.unlockUser(username);
    }

    @Override
    @CacheEvict(key = "#username")
    @Transactional(rollbackFor = Exception.class)
    public void resetUsername(ResetUsernameRequest request, String username) throws IOException {
        sendWebsocket(request.getId());

        SysUser sysUser = new SysUser();
        sysUser.setId(request.getId());
        sysUser.setUsername(request.getUsername());
        sysUsersMapper.updateById(sysUser);
    }

    @Override
    public boolean existsUsername(String newUsername) {
        return sysUsersMapper.exists(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, newUsername));
    }

    @Override
    public boolean existsEmail(String email) {
        return sysUsersMapper.exists(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getEmail, email));
    }

    @Override
    public boolean existsEmail(Long id, String email) {
        return sysUsersMapper.existsEmail(id, email) > 0;
    }

    @Override
    @CacheEvict(key = "#username")
    public void deleteUser(Long id, String username) throws IOException {
        sendWebsocket(id);

        sysUsersMapper.deleteById(id);
    }

    @Override
    public String getUsername(Long id) {
        return sysUsersMapper.getUsername(id);
    }

    /**
     * 调用websocket发送强制下线消息
     * @param id
     * @throws IOException
     */
    private void sendWebsocket(Long id) throws IOException {
        // 重置用户名后需强制用户离线
        @SuppressWarnings("unchecked")
        WebSocketConcreteHandler<String> webSocketConcreteHandler = (WebSocketConcreteHandler<String>) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.FORCE_OFFLINE.toString());
        JSONObject forceOfflineParam = new JSONObject();
        forceOfflineParam.put("order", WebSocketOrderEnum.FORCE_OFFLINE.toString());
        forceOfflineParam.put("receiver", id);
        TextMessage textMessage = new TextMessage(forceOfflineParam.toJSONString());
        webSocketConcreteHandler.handle(WebSocketSessionManager.getSession(id), textMessage);
    }
}




