package com.soft.sys.controller;

import com.soft.sys.constants.BaseConstant;
import com.soft.sys.core.annotation.SysLog;
import com.soft.sys.enums.LogModuleEnum;
import com.soft.sys.model.request.GenerateKeyDTO;
import com.soft.sys.model.request.GetSecretKeyListDTO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.PublicKeyVO;
import com.soft.sys.model.vo.SysSecretKeyVO;
import com.soft.sys.resultapi.R;
import com.soft.sys.service.SecretKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

/**
 * @Author: cyx
 * @Description: 
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

    @PostMapping(value = "/list")
    @Operation(summary = "获取密钥列表")
    public R<PageVO<SysSecretKeyVO>> list(@RequestBody GetSecretKeyListDTO request) {
        PageVO<SysSecretKeyVO> pageVo = secretKeyService.getSecretKeyList(request);
        return R.ok(pageVo);
    }

    @GetMapping(value = "/getPublicKey")
    @Operation(summary = "获取登录密码加密公钥")
    public R<PublicKeyVO> getPublicKey() {
        String publicKey = secretKeyService.getPublicKey(BaseConstant.KeyType.LOGIN_PASSWORD_ENCRYPT);
        PublicKeyVO publicKeyVo = new PublicKeyVO();
        publicKeyVo.setPublicKey(publicKey);
        return R.ok(publicKeyVo);
    }

    @PreAuthorize(value = "@cps.hasPermission('sys_secret_key_generate')")
    @SysLog(value = "生成密钥对", module = LogModuleEnum.SECRET_KEY)
    @PostMapping(value = "/generateKey")
    @Operation(summary = "生成密钥对")
    @Parameter(name = "type", description = "类型", required = true, in = ParameterIn.QUERY)
    public R<Object> generateKey(@RequestBody GenerateKeyDTO request) throws NoSuchAlgorithmException {
        secretKeyService.generateKey(request);
        return R.ok("密钥对生成成功");
    }
}
