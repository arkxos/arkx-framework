package com.bsd.dingtalk.server.controller;

import com.alibaba.fastjson.JSON;
import com.bsd.dingtalk.server.configuration.DingtalkProperties;
import com.bsd.dingtalk.server.constants.URLConstant;
import com.bsd.dingtalk.server.model.ProcessInstanceInputVO;
import com.bsd.dingtalk.server.util.AccessTokenUtil;
import com.bsd.dingtalk.server.util.FileHelper;
import com.bsd.dingtalk.server.util.LogFormatter;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.google.common.collect.Maps;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.StringUtils;
import com.taobao.api.internal.util.json.JSONWriter;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * 审批基础功能
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Slf4j
@Schema(title = "审批基础功能")
@EnableConfigurationProperties({DingtalkProperties.class})
@RestController
public class ProcessinstanceController {
    @Autowired
    private DingtalkProperties dingtalkProperties;

    /**
     * 上传媒体文件
     */
    @Schema(title = "上传媒体文件", name = "上传媒体文件")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "type", required = true, value = "媒体文件类型，分别有图片（image）、语音（voice）、普通文件(file)"),
//            @ApiImplicitParam(name = "file", required = true, value = "文件流对象,接收数组格式", dataType = "__File", paramType = "form")
//    })
    @PostMapping("/media/upload")
    @ResponseBody
    public ResponseResult uploadFile(@RequestParam("type") String type, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }

            File convFile = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            //上传文件
            String mediaId = FileHelper.uploadFile(AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()), type, convFile);
            convFile.delete();

            Map<String, String> map = Maps.newHashMap();
            map.put("mediaId", mediaId);
            return ResponseResult.ok(map);
        } catch (Exception e) {
            return ResponseResult.ok().msg(e.getMessage());
        }
    }

    /**
     * 发起审批
     */
    @Schema(title = "发起审批", name = "发起审批")
    @RequestMapping(value = "/processinstance/start", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult startProcessInstance(@RequestBody ProcessInstanceInputVO processInstance) {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_CREATE);
            OapiProcessinstanceCreateRequest request = new OapiProcessinstanceCreateRequest();
            request.setAgentId(StringUtils.toLong(dingtalkProperties.getAgentid()));
            request.setProcessCode(processInstance.getProcessCode());
            request.setOriginatorUserId(processInstance.getOriginatorUserId());
            request.setDeptId(processInstance.getDeptId());
            request.setApprovers(processInstance.getApprovers());
            request.setApproversV2((new JSONWriter(false, false, true)).write(processInstance.getApproversV2()));
            request.setCcList(processInstance.getCcList());
            request.setCcPosition(processInstance.getCcPosition());
            request.setFormComponentValues(processInstance.generateForms());

            OapiProcessinstanceCreateResponse response = client.execute(request, AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()));

            int errorCode = Integer.valueOf(response.getErrorCode());
            if (errorCode != 0) {
                return ResponseResult.failed().code(errorCode).msg(response.getErrmsg());
            }
            Map<String, String> map = Maps.newHashMap();
            map.put("instanceId", response.getProcessInstanceId());
            return ResponseResult.ok(map);
        } catch (Exception e) {
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END, LogFormatter.KeyValue.getNew("processInstance", JSON.toJSONString(processInstance)));
            log.info(errLog, e);
            return ResponseResult.failed().code(-1).msg("系统繁忙");
        }
    }

    /**
     * 根据审批实例id获取审批详情
     *
     * @param instanceId
     * @return
     */
    @Schema(title = "根据审批实例id获取审批详情", name = "根据审批实例id获取审批详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "instanceId", required = true, value = "审批实例id")
//    })
    @RequestMapping(value = "/processinstance/get", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult getProcessinstanceById(@RequestParam String instanceId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_PROCESSINSTANCE);
            OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
            request.setProcessInstanceId(instanceId);
            OapiProcessinstanceGetResponse response = client.execute(request, AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()));
            int errorCode = Integer.valueOf(response.getErrorCode());
            if (errorCode != 0) {
                return ResponseResult.failed().code(errorCode).msg(response.getErrmsg());
            }
            return ResponseResult.ok(response.getProcessInstance());
        } catch (Exception e) {
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END, LogFormatter.KeyValue.getNew("instanceId", instanceId));
            log.info(errLog, e);
            return ResponseResult.failed().code(-1).msg("系统繁忙");
        }
    }
}


