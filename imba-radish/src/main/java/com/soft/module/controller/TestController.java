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

    @GetMapping(value = "/getWeather")
    public R<String> getWeather(@RequestParam(value = "code") String code) {
        return R.ok(thirdInterface.getDistrictCode(code));
    }
}
