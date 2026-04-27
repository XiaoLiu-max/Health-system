package com.health.controller;

import com.alibaba.fastjson.JSONObject;
import com.health.common.Result;
import com.health.utils.WeatherUtil;
import com.health.vo.WeatherVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Resource
    private WeatherUtil weatherUtil;

    // 封装后的最终接口
    @GetMapping("/local")
    public Result<WeatherVO> getLocalWeather() {
        WeatherVO vo = weatherUtil.getLocalWeather();
        if (vo == null) {
            return Result.fail("获取天气失败");
        }
        return Result.success(vo);
    }

    // ====================== 【APP 专用接口】精准定位天气 ======================
    @GetMapping("/app")
    public Result<WeatherVO> getAppWeather(
            @RequestParam String longitude, // APP 传过来的经度
            @RequestParam String latitude  // APP 传过来的纬度
    ) {
        WeatherVO vo = weatherUtil.getWeatherByLocation(longitude, latitude);
        return vo == null ? Result.fail("定位天气失败") : Result.success(vo);
    }

}