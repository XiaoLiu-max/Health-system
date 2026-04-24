package com.health.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 绑定上面写好的降级Mock类
@FeignClient(value = "user-service", fallback = UserFeignFallback.class)
public interface UserFeignClient {

    @GetMapping("/user/exist/{uid}")
    Boolean checkUserExist(@PathVariable("uid") Long uid);

}