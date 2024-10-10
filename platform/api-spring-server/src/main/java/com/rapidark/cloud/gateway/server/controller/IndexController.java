package com.rapidark.cloud.gateway.server.controller;

import com.rapidark.cloud.gateway.server.configuration.ApiProperties;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author darkness
 * @date 2021/7/6 16:03
 * @version 1.0
 */
@Controller
public class IndexController {

    @Autowired
    private ApiProperties apiProperties;

    @GetMapping("/")
    public String index() {
        if (apiProperties.getApiDebug()) {
            return "redirect:doc.html";
        }
        return "index";
    }

    @ResponseBody
    @GetMapping("/test")
    public ResultBody testGet() {
        Map<String, String> data = new HashMap<>();
        data.put("code", "0001");
        return ResultBody.ok().data(data);
    }

    @ResponseBody
    @PostMapping("/test")
    public ResultBody testPost(@Valid @RequestBody TestCommand command) {
        Map<String, String> data = new HashMap<>();
        data.put("code", "0001");
        return ResultBody.ok().data(data);
    }

    @PostMapping("/testFile")
    public ResultBody testFile(MultipartFile file) {
//        WebUtils.getParameterMap(request);
        Map<String, String> data = new HashMap<>();
        data.put("code", "0001");
        return ResultBody.ok().data(data);
    }

}
