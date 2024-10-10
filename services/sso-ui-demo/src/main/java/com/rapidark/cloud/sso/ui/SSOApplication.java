package com.rapidark.cloud.sso.ui;

import com.rapidark.boot.RapidArkApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuyadu
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class SSOApplication {
    public static void main(String[] args) {
        RapidArkApplication.run(SSOApplication.class, args);
    }
}
