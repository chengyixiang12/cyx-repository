package com.soft.module.thirdapi.gaode;

import com.soft.module.thirdapi.gaode.weather.entity.District;
import com.soft.module.thirdapi.gaode.weather.entity.Weather;
import com.soft.sys.utils.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口
 *
 * @author cyx
 * @date 2026-08-07
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdInterface {

    private final HttpUtil httpUtil;

    @Value("${geo.gaode.api-key}")
    private String apiKey;

    @Tool(description = "用于查询天气，需要传入行政区划的编码，如北京：110000")
    public Weather getWeather(@ToolParam(description = "行政区划编码，如北京：110000") String code) {
        log.info("开始调用getWeather");
        Map<String, Object> param = new HashMap<>();
        param.put("city", code);
        param.put("key", apiKey);
        Map<String, String> header = new HashMap<>();
        return httpUtil.syncMonoGet("https://restapi.amap.com", "/v3/weather/weatherInfo", param, header, Weather.class);
    }

    @Tool(description = "用于查询行政区划编码，通过地名来查询编码。如北京：110000")
    public String getDistrictCode(@ToolParam(description = "地名，如：北京") String name) {
        log.info("开始调用getDistrictCode");
        Map<String, Object> param = new HashMap<>();
        param.put("code", name);
        param.put("maxLevel", 0);
        Map<String, String> header = new HashMap<>();
        District district = httpUtil.syncMonoGet("https://dmfw.mca.gov.cn", "/9095/stname/listPub", param, header, District.class);
        return district.getRecords().getFirst().getPlace_type_code();
    }


}
