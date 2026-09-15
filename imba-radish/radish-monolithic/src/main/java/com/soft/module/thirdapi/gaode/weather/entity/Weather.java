package com.soft.module.thirdapi.gaode.weather.entity;

import lombok.Data;

import java.util.List;

/**
 *
 * @author cyx
 * @date 2026-08-07
 */
@Data
public class Weather {

    private String status;

    private String count;

    private String info;

    private String infocode;

    private List<Live> lives;

    @Data
    public static class Live {

        private String province;

        private String city;

        private String adcode;

        private String weather;

        private String temperature;

        private String winddirection;

        private String windpower;

        private String humidity;

        private String reporttime;

        private String temperature_float;

        private String humidity_float;
    }
}
