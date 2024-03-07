package com.ricky.apicommon.utils.result;

public class ResultFactory {
    /**
     * @param data 请求的响应结果
     * @return 返回包装后的结果对象
     * @description 返回成功后的结果
     * @author Ricky01
     * @since 2024/3/2
     **/
    public static <T> R<T> success(T data) {
        return new R<>(data, true, "ok");
    }

    public static R<String> ok() {
        return new R<>("ok", true, "ok");
    }

    public static <T> R<T> success(T data, String msg) {
        return new R<>(data, true, msg);
    }

    public static <T> R<T> fail(String msg, T data) {
        return new R<>(data, false, msg);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(null, false, msg);
    }

    public static <T> R<T> fail() {
        return new R<>(null, false, "未知错误");
    }


}
