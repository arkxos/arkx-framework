package com.flying.fish.demo;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.rapidark.cloud.gateway.formwork.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/06/01
 * @Version V1.0
 */
public class TestMain {

    @Test
    public void test0(){
        String ip = "192.168.11.45";
        String authIps = "127.0.0.1,192.168.11.*";
        List<String> ipList = new ArrayList<>();
        if (authIps.indexOf(Constants.SEPARATOR_SIGN) != -1){
            String [] ips = authIps.split(Constants.SEPARATOR_SIGN);
            ipList = Arrays.asList(ips);
        }
        boolean isPass = false;
        for (String value : ipList){
            //如果有*号，表示支持正则表达示配置（只支持*号表达式）
            if (value.indexOf("*") != -1){
                isPass = Pattern.matches(value, ip);
            }else if(StringUtils.equals(value, ip)) {
                isPass = true;
            }
            if (isPass){
                break;
            }
        }
        System.out.println("ip:" + ip + ",访问权限：" + isPass);
    }

}
