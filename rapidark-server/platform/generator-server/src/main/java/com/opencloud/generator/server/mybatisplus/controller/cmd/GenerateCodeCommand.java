package com.opencloud.generator.server.mybatisplus.controller.cmd;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/21 11:04
 */
@Data
public class GenerateCodeCommand {

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
    //    @RequestParam(value = "author")
    private String author;
    //    @RequestParam(value = "parentPackage")
    private String parentPackage;
    //    @RequestParam(value = "moduleName")
    private String moduleName;
    //    @RequestParam(value = "includeTables")
    private String includeTables;
    //    @RequestParam(value = "tablePrefix")
    private String tablePrefix;

}
