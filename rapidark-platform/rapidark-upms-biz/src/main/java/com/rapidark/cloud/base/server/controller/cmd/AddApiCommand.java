package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/13 17:29
 */
@Data
@Schema(description = "创建Api命令")
public class AddApiCommand {
//    @RequestParam(value = "apiCode")
    private String apiCode;
//    @RequestParam(value = "apiName")
    private String apiName;
//    @RequestParam(value = "apiCategory")
    private String apiCategory;
//    @RequestParam(value = "serviceId")
    private String serviceId;
//    @RequestParam(value = "path", required = false, defaultValue = "")
    private String path;
//    @RequestParam(value = "status", defaultValue = "1")
    private Integer status;
//    @RequestParam(value = "priority", required = false, defaultValue = "0")
    private  Integer priority;
//    @RequestParam(value = "apiDesc", required = false, defaultValue = "")
    private  String apiDesc;
//    @RequestParam(value = "isAuth", required = false, defaultValue = "1")
    private Integer isAuth;
//    @RequestParam(value = "isOpen", required = false, defaultValue = "0")
    private  Integer isOpen;

}
