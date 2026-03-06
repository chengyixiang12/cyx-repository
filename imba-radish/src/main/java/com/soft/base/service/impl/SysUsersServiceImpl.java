package com.soft.base.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.RegexConstant;
import com.soft.base.entity.SysUser;
import com.soft.base.entity.SysUserRole;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.model.dto.GetUsersDto;
import com.soft.base.model.request.EditUserRequest;
import com.soft.base.model.request.GetUsersRequest;
import com.soft.base.model.request.ResetUsernameRequest;
import com.soft.base.model.request.SaveUserRequest;
import com.soft.base.model.vo.GetUserVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.UsersVo;
import com.soft.base.rabbitmq.producer.EmailProduce;
import com.soft.base.service.*;
import com.soft.base.utils.RSAUtil;
import com.soft.base.utils.UniversalUtil;
import com.soft.base.websocket.WebSocketConcreteHolder;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.concrete.ForceOfflineHandler;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cyq
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2024-09-30 15:49:52
 */
@Service
@CacheConfig(cacheNames = "radish:users")
@RequiredArgsConstructor
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUser> implements SysUsersService {

    private final SysUsersMapper sysUsersMapper;

    private final PasswordEncoder passwordEncoder;

    private final RSAUtil rsaUtil;

    private final SecretKeyService secretKeyService;

    private final SysDeptService sysDeptService;

    private final SysRoleService sysRoleService;

    private final SysUserRoleService sysUserRoleService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UniversalUtil universalUtil;

    private final EmailProduce emailProduce;

    @Override
    public PageVo<UsersVo> getUsers(GetUsersRequest request) {
        IPage<UsersVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        List<Long> deptIds = new ArrayList<>();
        GetUsersDto getUsersDto = new GetUsersDto();

        // 获取部门id集合
        Long deptId = request.getDeptId();
        if (deptId != null) {
            getChildDeptIds(Stream.of(deptId).toList(), deptIds);
            deptIds.add(deptId);
        }

        BeanUtils.copyProperties(request, getUsersDto);
        getUsersDto.setDeptIds(deptIds);
        Page<UsersVo> allUsers = sysUsersMapper.getUsers(page, getUsersDto);
        PageVo<UsersVo> pageVo = new PageVo<>();
        allUsers.getRecords().forEach(item -> {
            item.setIsOnline(redisTemplate.hasKey(RedisConstant.WS_USER_SESSION + item.getId()) ? 1 : 0);
            if (StringUtils.isNotBlank(item.getPhone())) {
                item.setPhone(item.getPhone().replaceAll(RegexConstant.PHONE_HIDDEN_REGEX, RegexConstant.PHONE_HIDDEN_EXP));
            }
        });
        pageVo.setRecords(allUsers.getRecords());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        String password = universalUtil.generatePassword();

        String encode = passwordEncoder.encode(password);

        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setPassword(encode);

        sysUsersMapper.updateById(sysUser);

        sysUser = sysUsersMapper.selectById(id);

        redisTemplate.delete(RedisConstant.USER_INFO + sysUser.getUsername());

        String content = "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>重置密码邮件</title><style>body {background-color: #f9f9f9;margin: 0;padding: 0;font-family: Arial, sans-serif;color: #333;}.email-container {max-width: 600px;margin: 50px auto;background-color: #ffffff;padding: 20px;border-radius: 8px;box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);text-align: center;}.email-header {font-size: 18px;font-weight: bold;margin-bottom: 20px;}.email-content p {font-size: 16px;line-height: 1.6;margin: 10px 0;} .email-content strong{color: #d9534f; }.email-footer{font-size: 12px;color: #999;margin-top: 20px;}</style></head><body><div class=\"email-container\"><div class=\"email-header\">重置密码邮件</div><div class=\"email-content\"><p>尊敬的用户您好！</p><p>您的重置密码是：<strong>" +
                password +
                "</strong>，请您及时登录系统修改密码。</p><p>如果该密码重置操作不是您本人申请的，请尽快联系客服处理。</p><p>感谢您的使用！</p></div><div class=\"email-footer\">此邮件由系统自动发送，请勿回复。</div></div></body></html>";
        emailProduce.send(sysUser.getEmail(), content);
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
    public void saveUser(SaveUserRequest request) {
        String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
        request.setPassword(passwordEncoder.encode(rsaUtil.decrypt(request.getPassword(), privateKey)));
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        sysUser.setDefault();
        sysUsersMapper.insert(sysUser);

        // 保存角色
        List<Long> roleIds = request.getRoleIds();
        Long defaultRoleId = sysRoleService.getDefaultRole(BaseConstant.Role.DEFAULT_ROLE_FLAG);
        if (roleIds == null) {
            roleIds = new ArrayList<>();

        }
        if (!roleIds.contains(defaultRoleId)) {
            roleIds.add(defaultRoleId);
        }
        List<SysUserRole> userRoles = new ArrayList<>();
        roleIds.forEach(item -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(item);
            sysUserRole.setUserId(sysUser.getId());
            userRoles.add(sysUserRole);
        });
        sysUserRoleService.insertBatch(userRoles);
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
        GetUserVo getUserVo = sysUsersMapper.getUser(id);
        getUserVo.setRoleIds(sysRoleService.getUserRole(id));
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
    public void resetUsername(ResetUsernameRequest request, String username) {
        try {
            sendWebsocket(request.getId());

            SysUser sysUser = new SysUser();
            sysUser.setId(request.getId());
            sysUser.setUsername(request.getUsername());
            sysUsersMapper.updateById(sysUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return sysUsersMapper.existsEmail(id, email);
    }

    @Override
    @CacheEvict(key = "#username")
    public void deleteUser(Long id, String username) {
        try {
            sendWebsocket(id);

            sysUsersMapper.deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsername(Long id) {
        return sysUsersMapper.getUsername(id);
    }

    @Override
    @CacheEvict(key = "#username")
    public void enableUser(String username) {
        sysUsersMapper.enableUser(username);
    }

    @Override
    @CacheEvict(key = "#username")
    public void forbiddenUser(String username) {
        sysUsersMapper.forbiddenUser(username);
    }

    @Override
    public SysUser getUserByEmail(String email) {
        return sysUsersMapper.getUserByEmail(email);
    }

    @Override
    public String getAvatar(Long id) {
        return sysUsersMapper.getAvatar(id);
    }

    @Override
    public Long getPrimaryKeyByUsername(String username) {
        return sysUsersMapper.getPrimaryKeyByUsername(username);
    }

    /**
     * 调用websocket发送强制下线消息
     * @param id
     * @throws IOException
     */
    private void sendWebsocket(Long id) throws IOException {
        // 重置用户名后需强制用户离线
        ForceOfflineHandler concreteHandler = (ForceOfflineHandler) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.FORCE_OFFLINE.toString());
        JSONObject forceOfflineParam = new JSONObject();
        forceOfflineParam.put("order", WebSocketOrderEnum.FORCE_OFFLINE.toString());
        forceOfflineParam.put("receiver", id);
        TextMessage textMessage = new TextMessage(forceOfflineParam.toJSONString());
        concreteHandler.handle(WebSocketSessionManager.getSession(id), textMessage);
    }

    /**
     * 获取当前部门下的所有部门
     * @param deptIds
     * @param childDeptIds
     * @return
     */
    private void getChildDeptIds(List<Long> deptIds, List<Long> childDeptIds) {
        if (CollectionUtil.isEmpty(deptIds)) {
            return;
        }
        List<Long> tmpList = sysDeptService.getChildDeptIds(deptIds);
        childDeptIds.addAll(tmpList);
        getChildDeptIds(tmpList, childDeptIds);
    }
}




