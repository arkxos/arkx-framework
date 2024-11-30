package com.rapidark.cloud.generator.server.mybatisplus.controller.param;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/21 11:01
 */
@Data
public class QueryTablesParams {
    //    @RequestParam(value = "type")
    private String type;
    //    @RequestParam(value = "driverName")
    private String driverName;
    //    @RequestParam(value = "url")
    private String url;
    //    @RequestParam(value = "username")
    private String username;
    //    @RequestParam(value = "password")
    private String password;
}
