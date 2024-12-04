package com.rapidark.cloud.msg.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.msg.server.service.DelayMessageService;
import com.rapidark.cloud.msg.server.service.WebHookLogsService;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.msg.client.model.WebHookMessage;
import com.rapidark.cloud.msg.client.model.entity.WebHookLogs;
import com.rapidark.cloud.msg.client.service.IWebHookClient;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author woodev
 */
@RestController
@Schema(title = "异步通知", name = "异步通知")
public class WebHookController implements IWebHookClient {
    @Autowired
    private DelayMessageService delayMessageService;
    @Autowired
    private WebHookLogsService webHookLogsService;

    @Schema(title = "Webhook异步通知", name = "即时推送，重试通知时间间隔为 5s、10s、2min、5min、10min、30min、1h、2h、6h、15h，直到你正确回复状态 200 并且返回 success 或者超过最大重发次数")
    @Override
    @PostMapping("/webhook")
    public ResponseResult<String> send(
            @RequestBody WebHookMessage message
    ) throws Exception {
        delayMessageService.send(message);
        return ResponseResult.ok();
    }

    /**
     * 获取分页异步通知列表
     *
     * @return
     */
    @Schema(title = "获取分页异步通知列表", name = "获取分页异步通知列表")
    @GetMapping("/webhook/logs")
    public ResponseResult<IPage<WebHookLogs>> getLogsListPage(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(webHookLogsService.findListPage(new PageParams(map)));
    }
}
