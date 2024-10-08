//package com.rapidark.cloud.gateway.formwork.util;
//
//import lombok.Data;
//
//import java.util.Date;
//
///**
// * @Description 所有接口调用返回的统一包装结果类
// * @Author jianglong
// * @Date 2020/05/14
// * @Version V1.0
// */
//@Data
//public class ResultBody implements java.io.Serializable {
//
//    private String code;
//    private Date timestamp;
//    private String message = "";
//    private Object data;
//
//    public ResultBody(){
//        this.code = Constants.SUCCESS;
//    }
//
//    public ResultBody(final String code){
//        this.code = code;
//    }
//
//    public ResultBody(final String code, final Object data){
//        this.code = code;
//        this.data = data;
//    }
//
//    public ResultBody(final String code, final String message, final Object data){
//        this.code = code;
//        this.message = message;
//        this.data = data;
//    }
//
//    public ResultBody(final Object data){
//        this.code = Constants.SUCCESS;
//        this.data = data;
//    }
//
//    public Date getTimestamp() {
//        return timestamp == null ? new Date(): timestamp;
//    }
//}
