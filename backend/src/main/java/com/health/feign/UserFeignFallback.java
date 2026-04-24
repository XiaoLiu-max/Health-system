package com.health.feign;

import org.springframework.stereotype.Component;

/**
 * Feign降级Mock：不用调用队友服务，本地直接返回
 */
@Component
public class UserFeignFallback implements UserFeignClient {

    /**
     * 校验用户是否存在的方法
     * 本地Mock固定返回true，模拟用户真实存在，自测业务全流程
     */
    @Override
    public Boolean checkUserExist(Long uid) {
        // 本地兜底假数据，完全不走网络远程调用
        return true;
    }

}