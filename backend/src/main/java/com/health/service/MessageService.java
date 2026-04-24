package com.health.service;

/**
 * 健康异常消息提醒服务（单体项目版，对接队友消息模块）
 * 无需SpringCloud、无需Feign，零依赖不报错
 */
public interface MessageService {

    /**
     * 发送健康异常预警消息
     * @param userId 用户ID
     * @param warnContent 异常描述内容
     * @param advice 健康建议文案
     */
    void sendHealthWarn(Long userId, String warnContent, String advice);
}