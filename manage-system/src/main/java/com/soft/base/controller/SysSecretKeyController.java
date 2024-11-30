package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.resultapi.R;
import com.soft.base.service.SecretKeyService;
import com.soft.base.vo.PublicKeyVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/26 16:22
 **/
@RestController
@RequestMapping(value = "/secretKey")
@Tag(name = "密钥")
@Slf4j
public class SysSecretKeyController {

    private final SecretKeyService secretKeyService;

    @Autowired
    public SysSecretKeyController(SecretKeyService secretKeyService) {
        this.secretKeyService = secretKeyService;
    }

    @SysLog(value = "获取公钥", module = LogModuleEnum.AUTHORIZATION, name = "#username")
    @GetMapping(value = "/getPublicKey")
    @Operation(summary = "获取公钥")
    @Parameters(value = {@Parameter(name = "username", description = "用户名", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "type", description = "密钥类型", required = true, in = ParameterIn.QUERY)})
    public R<PublicKeyVo> getPublicKey(@RequestParam(value = "username", required = false) String username,
                                       @RequestParam(value = "type", required = false) Integer type) {
        if (StringUtils.isBlank(username)) {
            return R.fail("用户名不能为空");
        }
        if (type == null) {
            return R.fail("密钥类型不能为空");
        }
        try {
            String publicKey = secretKeyService.getPublicKey(type);
            PublicKeyVo publicKeyVo = new PublicKeyVo();
            publicKeyVo.setPublicKey(publicKey);
            return R.ok(publicKeyVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PreAuthorize(value = "@cps.hasPermission('sys_secret_key_generate')")
    @SysLog(value = "生成密钥", module = LogModuleEnum.SECRET_KEY)
    @GetMapping(value = "/generateKey")
    @Operation(summary = "生成密钥")
    @Parameter(name = "type", description = "类型", required = true, in = ParameterIn.QUERY)
    public R generateKey(@RequestParam(value = "type", required = false) Integer type) {
        if (type == null) {
            return R.fail("类型不能为空");
        }
        try {
            secretKeyService.generateKey(type);
            R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
        return R.ok();
    }
}
