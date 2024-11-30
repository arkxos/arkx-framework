package com.rapidark.cloud.generator.server.mybatisplus.controller;

import com.rapidark.framework.common.utils.WebUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author: liuyadu
 * @date: 2019/7/19 15:26
 * @description:
 */
@Schema(title = "在线代码生成器")
@Controller
@RequestMapping("/generate")
public class MybatisPlusDownLoadController {

    @Schema(title = "文件下载", name = "文件下载")
    @GetMapping(value = "/download")
    public void download(
            @RequestParam("filePath") String filePath,
            HttpServletResponse response
    ) throws Exception {
        File file = new File(filePath);
        download(response, filePath, file.getName());
    }

    /**
     * 文件下载
     *
     * @param response
     * @param filePath
     * @param fileName
     * @throws IOException
     */
    private void download(HttpServletResponse response, String filePath, String fileName) throws IOException {
        WebUtils.setFileDownloadHeader(response, fileName);
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(filePath));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.flush();
        inStream.close();
    }
}
