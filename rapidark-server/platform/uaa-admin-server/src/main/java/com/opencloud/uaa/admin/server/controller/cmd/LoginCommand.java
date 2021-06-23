package com.opencloud.uaa.admin.server.controller.cmd;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/23 16:10
 */
@Data
public class LoginCommand {

    private String loginInfo;

}
