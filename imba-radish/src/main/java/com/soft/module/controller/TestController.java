package com.soft.module.controller;

import com.soft.module.thirdapi.gaode.ThirdInterface;
import com.soft.module.thirdapi.gaode.weather.entity.Weather;
import com.soft.sys.resultapi.R;
import com.soft.sys.utils.HttpUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author cyx
 * @date 2026-08-07
 */

@RestController
@RequestMapping(value = "/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "测试接口")
public class TestController {

    private final ThirdInterface thirdInterface;

    private final HttpUtil httpUtil;

    @GetMapping(value = "/getWeather")
    public R<Weather> getWeather(@RequestParam(value = "code") String code) {
        return R.ok(thirdInterface.getWeather(code));
    }

//    @GetMapping(value = "/getCaptcha")
//    public ResponseEntity<Byte> getCaptcha() {
//        Map<String, Object> param = new HashMap<>();
//        param.put("uuid", "1234567891234567");
//
//        Map<String, String> header = new HashMap<>();
//        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
//        header.put("Accept", "application/json, text/plain, */*");
//        try (InputStream inputStream = httpUtil.syncMonoGet("http://imba-radish.top", "/api/auth/getGraphicCaptcha", param, new HashMap<>(), InputStream.class)) {
//
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return R.ok();
//    }
}
