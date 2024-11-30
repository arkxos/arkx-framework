package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.entity.GatewayAccessLogs;
import com.rapidark.cloud.base.server.service.GatewayAccessLogsService;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.model.ResultBody;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 网关智能路由
 *
 * @author: liuyadu
 * @date: 2019/3/12 15:12
 * @description:
 */
@Schema(title = "网关访问日志")
@RestController
public class GatewayAccessLogsController {

    @Autowired
    private GatewayAccessLogsService gatewayAccessLogsService;

    /**
     * 获取分页列表
     *
     * @return
     */
    @Schema(title = "获取分页访问日志列表", name = "获取分页访问日志列表")
    @GetMapping("/gateway/access/logs")
    public ResultBody<Page<GatewayAccessLogs>> getAccessLogListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok(gatewayAccessLogsService.findListPage(new PageParams(map)));
    }

}
