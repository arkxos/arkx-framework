package com.rapidark.cloud.msg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.msg.client.model.entity.EmailLogs;
import com.rapidark.cloud.msg.server.service.EmailLogsService;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.model.ResultBody;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮件发送日志 前端控制器
 *
 * @author admin
 * @date 2019-07-25
 */
@Schema(title = "邮件发送日志", name = "邮件发送日志")
@RestController
@RequestMapping("/emailLogs")
public class EmailLogsController {
    @Autowired
    private EmailLogsService targetService;

    /**
     * 获取分页数据
     *
     * @return
     */
    @Schema(title = "获取分页数据", name = "获取分页数据")
    @GetMapping(value = "/list")
    public ResultBody<IPage<EmailLogs>> list(@RequestParam(required = false) Map map) {
        PageParams pageParams = new PageParams(map);
        EmailLogs query = pageParams.mapToObject(EmailLogs.class);
        QueryWrapper<EmailLogs> queryWrapper = new QueryWrapper();
        return ResultBody.ok(targetService.page(new PageParams(map), queryWrapper));
    }

    /**
     * 根据ID查找数据
     */
    @Schema(title = "根据ID查找数据", name = "根据ID查找数据")
    @ResponseBody
    @GetMapping("/get")
    public ResultBody<EmailLogs> get(@RequestParam("id") Long id) {
        EmailLogs entity = targetService.getById(id);
        return ResultBody.ok(entity);
    }
}