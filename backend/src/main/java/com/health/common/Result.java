package com.health.common;

import lombok.Data;

/**
 * 统一返回结果
 * 所有登录/注册接口都用这个格式返回
 */
@Data
public class Result<T> {
    private int code;    // 200=成功 500=失败
    private String msg;  // 提示信息（给前端看）
    private T data;      // 返回数据

    // 成功（无数据）
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // 成功（带数据）
    public static <T> Result<T> success(String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 失败
    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    // 判断当前返回结果是否成功
    public boolean isSuccess() {
        return this.code == 200;
    }
}