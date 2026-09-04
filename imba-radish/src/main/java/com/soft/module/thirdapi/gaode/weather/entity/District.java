package com.soft.module.thirdapi.gaode.weather.entity;

import lombok.Data;

import java.util.List;

/**
 *
 * @author cyx
 * @date 2026-08-10
 */

@Data
public class District {

    private Integer total;

    private List<Records> records;

    @Data
    public static class Records {

        private String id;

        private String place_code;

        private String standard_name;

        private String roman_alphabet_spelling;

        private String ethnic_minorities_writing;

        private String place_type;

        private String place_type_code;

        private String province_name;

        private String city_name;

        private String area_name;

        private String area;

        private String city;

        private String province;

        private Gdm gdm;

        private String pdm;

        @Data
        public static class Gdm {

            private String type;

            private List<Double[]> coordinates;
        }
    }
}
