package com.bsd.file.server.controller;

import com.bsd.file.server.dto.UploadDto;
import com.bsd.file.server.service.QiNiuService;
import com.google.common.collect.Maps;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.ResponseResult;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 七牛云文件存储
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Slf4j
@RequestMapping("/qiniu")
@Schema(title = "七牛云服务接口")
@RestController
public class QiNiuController {
    @Autowired
    private QiNiuService qiniuService;

    /**
     * 获取token
     *
     * @return
     * @throws Exception
     */
    @Schema(title = "获取token", name = "获取token")
    @ResponseBody
    @GetMapping("/token")
    public ResponseResult token() {
        Map<String, String> map = Maps.newHashMap();
        map.put("token", qiniuService.getToken());
        return ResponseResult.ok(map);
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Schema(title = "上传文件", name = "上传文件")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "file", required = true, value = "文件流对象,接收数组格式", dataType = "__File", paramType = "form")
//    })
    @ResponseBody
    @PostMapping("/upload")
    public ResponseResult uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new OpenAlertException("上传文件不能为空");
        }

        //上传文件
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = qiniuService.uploadSuffix(file.getBytes(), suffix);

        Map<String, String> map = Maps.newHashMap();
        map.put("fileName", fileName);
        map.put("fileUrl", qiniuService.getFileUrl(fileName));
        return ResponseResult.ok(map);
    }

    /**
     * 批量上传
     *
     * @param files
     * @return
     * @throws Exception
     */
    @Schema(title = "批量上传", name = "批量上传")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "files", value = "文件流对象,接收数组格式", required = true, dataType = "MultipartFile", allowMultiple = true, paramType = "form")
//    })
    @ResponseBody
    @PostMapping("/batch/upload")
    public ResponseResult batchUploadFile(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < files.length; ++i) {
            if (!files[i].isEmpty()) {
                //上传文件
                String suffix = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
                String fileName = qiniuService.uploadSuffix(files[i].getBytes(), suffix);

                Map<String, String> map = Maps.newHashMap();
                map.put("fileName", fileName);
                map.put("fileUrl", qiniuService.getFileUrl(fileName));
                list.add(map);
            }
        }
        return ResponseResult.ok(list);
    }

    /**
     * 文件上传完回调
     *
     * @param request HttpServletRequest
     * @return JsonObject 返回值
     */
    @Schema(title = "文件上传完回调", name = "文件上传完回调")
    @PostMapping("/callback")
    public Object callback(HttpServletRequest request) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 设置返回给七牛的数据
        log.info("回调内容：{}", sb.toString());
        UploadDto file = qiniuService.fileCallback(request.getHeader("Authorization"), sb.toString());
        return file;
    }
}
