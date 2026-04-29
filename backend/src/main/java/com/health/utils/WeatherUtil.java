package com.health.utils;

import com.alibaba.fastjson.JSONObject;
import com.health.vo.WeatherVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherUtil {

    @Value("${amap.key}")
    private String amapKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1. 自动获取当前IP所在城市 → 定位功能
    private String getCityCodeByIp() {
        try {
            String url = "https://restapi.amap.com/v3/ip?key=" + amapKey;
            String res = restTemplate.getForObject(url, String.class);
            JSONObject obj = JSONObject.parseObject(res);
            return obj.getString("adcode"); // 直接返回城市编码
        } catch (Exception e) {
            return "420100"; // 定位失败默认武汉
        }
    }

    // 2. 获取定位后的当地天气（全自动）
    // 2. 获取定位后的当地天气（全自动 → 现在返回封装好的VO）
    public WeatherVO getLocalWeather() {
        try {
            String cityCode = getCityCodeByIp();
            String url = "https://restapi.amap.com/v3/weather/weatherInfo?city=" + cityCode + "&key=" + amapKey;
            String res = restTemplate.getForObject(url, String.class);
            JSONObject json = JSONObject.parseObject(res);

            // 判断是否成功
            if(!"1".equals(json.getString("status"))){
                return null;
            }

            // 取出天气数据
            JSONObject live = json.getJSONArray("lives").getJSONObject(0);

            // 封装进 VO
            WeatherVO vo = new WeatherVO();
            vo.setProvince(live.getString("province"));
            vo.setCity(live.getString("city"));
            vo.setWeather(live.getString("weather"));
            vo.setTemperature(live.getString("temperature"));
            vo.setHumidity(live.getString("humidity"));
            vo.setWindDirection(live.getString("winddirection"));
            vo.setWindPower(live.getString("windpower") == null ? "未知" : live.getString("windpower"));
            vo.setReportTime(live.getString("reporttime"));

            return vo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ====================== 【APP 专用】根据经纬度查天气 ======================
    public WeatherVO getWeatherByLocation(String longitude, String latitude) {
        try {
            // 1. 经纬度 转 城市编码
            String regeoUrl = "https://restapi.amap.com/v3/geocode/regeo?location=" + longitude + "," + latitude + "&key=" + amapKey;
            JSONObject locationJson = restTemplate.getForObject(regeoUrl, JSONObject.class);
            String cityCode = locationJson.getJSONObject("regeocode").getJSONObject("addressComponent").getString("adcode");

            // 2. 根据城市编码查天气（和你现在逻辑一样）
            String weatherUrl = "https://restapi.amap.com/v3/weather/weatherInfo?city=" + cityCode + "&key=" + amapKey;
            JSONObject json = restTemplate.getForObject(weatherUrl, JSONObject.class);

            if (!"1".equals(json.getString("status"))) return null;

            // 3. 封装返回（和你现在格式完全一样）
            JSONObject live = json.getJSONArray("lives").getJSONObject(0);
            WeatherVO vo = new WeatherVO();
            vo.setProvince(live.getString("province"));
            vo.setCity(live.getString("city"));
            vo.setWeather(live.getString("weather"));
            vo.setTemperature(live.getString("temperature"));
            vo.setHumidity(live.getString("humidity"));
            vo.setWindDirection(live.getString("winddirection"));
            vo.setWindPower(live.getString("windpower") == null ? "未知" : live.getString("windpower"));
            vo.setReportTime(live.getString("reporttime"));
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}