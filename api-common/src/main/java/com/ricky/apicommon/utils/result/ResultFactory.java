package com.ricky.apicommon.utils.result;

public class ResultFactory {
    public static <T> R<T> success(T data){
        return  new R<>(data,true,"ok");
    }
    public static  R<String> ok(){
        return  new R<>("ok",true,"ok");
    }
    public static <T> R<T> success(T data,String msg){
        return  new R<>(data,true,msg);
    }
    public static <T> R<T> fail(String msg,T data){
        return  new R<>(data,false,msg);
    }
    public static <T> R<T> fail(String msg){
        return  new R<>(null,false,msg);
    }




}
