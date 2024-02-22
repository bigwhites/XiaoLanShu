package com.ricky.apicommon.utils.result;

import java.io.Serializable;
import java.util.Date;

public class R<T> implements Serializable {
    public T data;
    public  boolean success;
    public String msg;
    public Date datetime;

    public R(T data, boolean success, String msg) {
        this.data = data;
        this.success = success;
        this.msg = msg;
        datetime = new Date();
    }
}
