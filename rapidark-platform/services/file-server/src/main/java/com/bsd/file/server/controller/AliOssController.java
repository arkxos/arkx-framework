package com.bsd.file.server.controller;

import com.bsd.file.server.service.OssFileService;
import com.google.common.collect.Maps;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.FileHelper;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

/**
 * 阿里云OSS文件操作
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Schema(title = "阿里云OSS服务接口")
@RequestMapping("/oss")
@RestController
public class AliOssController {
    @Autowired
    private OssFileService ossFileService;

    /**
     * 文件上传
     *
     * @return
     */
    @Schema(title = "文件上传", name = "文件上传")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "file", required = true, value = "文件流对象,接收数组格式", dataType = "__File", paramType = "form"),
//            @ApiImplicitParam(name = "prefix", value = "上传文件夹", paramType = "form")
//    })
    @PostMapping("/upload")
    @ResponseBody
    public ResponseResult saveFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "prefix") String prefix) {
        try {
            if (file.isEmpty()) {
                throw new OpenAlertException("上传文件不能为空");
            }

            if (prefix == null) {
                prefix = "";
            }

            String fileName = FileHelper.getPath(prefix, Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")));
            String url = ossFileService.saveFile(file, fileName);
            if (url == null) {
                throw new OpenAlertException("上传失败");
            }
            Map<String, String> map = Maps.newHashMap();
            map.put("fileName", fileName);
            map.put("fileUrl", url);
            return ResponseResult.ok(map);
        } catch (Exception e) {
            return ResponseResult.failed(e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param name 文件名
     */
    @Schema(title = "删除文件", name = "删除文件")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", required = true, value = "文件名", paramType = "form")
//    })
    @PostMapping(value = "/delete")
    @ResponseBody
    public ResponseResult deleteFile(@RequestParam(value = "name") String name) {
        ossFileService.deleteFile(name);

        //返回结果
        return ResponseResult.ok().msg("删除成功");
    }

    /**
     * 获取文件访问地址
     */
    @Schema(title = "获取文件访问地址", name = "获取文件访问地址")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", required = true, value = "文件名", paramType = "form")
//    })
    @GetMapping(value = "/url")
    @ResponseBody
    public ResponseResult getFileUrl(@RequestParam(value = "name") String name) {
        String url = ossFileService.getFileUrl(name);
        if (url != null) {
            Map<String, String> map = Maps.newHashMap();
            map.put("url", url);
            return ResponseResult.ok(map);
        }

        //返回结果
        return ResponseResult.failed("获取失败");
    }
}


