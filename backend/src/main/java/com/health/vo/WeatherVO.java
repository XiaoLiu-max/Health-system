package com.health.vo;

import lombok.Data;

@Data
public class WeatherVO {
    // 省份
    private String province;
    // 城市
    private String city;
    // 天气状况
    private String weather;
    // 实时温度
    private String temperature;
    // 湿度
    private String humidity;
    // 风向
    private String windDirection;
    // 风力
    private String windPower;
    // 发布时间
    private String reportTime;
}