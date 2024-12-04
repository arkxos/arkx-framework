package com.rapidark.cloud.gateway.server.controller;

import com.rapidark.cloud.gateway.server.configuration.ApiProperties;
import com.rapidark.framework.common.model.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
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
    public ResponseResult testGet() {
        Map<String, String> data = new HashMap<>();
        data.put("code", "0001");
        return ResponseResult.ok(data);
    }

    @ResponseBody
    @PostMapping("/test")
    public ResponseResult testPost(@Valid @RequestBody TestCommand command) {
        Map<String, String> data = new HashMap<>();
        data.put("code", "0001");
        return ResponseResult.ok(data);
    }

    @PostMapping("/testFile")
    public ResponseResult testFile(MultipartFile file) {
//        WebUtils.getParameterMap(request);
        Map<String, String> data = new HashMap<>();
        data.put("code", "0001");
        return ResponseResult.ok(data);
    }

}
