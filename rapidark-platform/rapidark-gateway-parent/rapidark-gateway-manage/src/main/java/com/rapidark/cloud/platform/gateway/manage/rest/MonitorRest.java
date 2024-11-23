package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.bean.MonitorReq;
import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.service.MonitorService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @Description 接口监控
 * @Author JL
 * @Date 2021/04/14
 * @Version V1.0
 */
@RestController
@RequestMapping("/monitor")
public class MonitorRest extends BaseRest {

    @Resource
    private MonitorService monitorService;

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult list(@RequestBody MonitorReq monitorReq){
        return ResponseResult.ok(monitorService.list(monitorReq));
    }

    /**
     * 关闭本次告警，状态置为0正常
     * @param id
     * @return
     */
    @RequestMapping(value = "/close", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult close(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Monitor monitor = monitorService.findById(id);
        Assert.notNull(monitor, "未获取到对象");
        monitor.setStatus(Constants.YES);
        monitorService.update(monitor);
        return ResponseResult.ok();
    }

}
