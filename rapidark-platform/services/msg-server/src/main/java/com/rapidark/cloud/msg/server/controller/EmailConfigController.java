package com.rapidark.cloud.msg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.msg.server.service.EmailConfigService;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.msg.client.model.entity.EmailConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 邮件发送配置 前端控制器
 *
 * @author admin
 * @date 2019-07-25
 */
@Schema(title = "邮件发送配置", name = "邮件发送配置")
@RestController
@RequestMapping("/emailConfig")
public class EmailConfigController {
    @Autowired
    private EmailConfigService targetService;

    /**
     * 获取分页数据
     *
     * @return
     */
    @Schema(title = "获取分页数据", name = "获取分页数据")
    @GetMapping(value = "/list")
    public ResponseResult<IPage<EmailConfig>> list(@RequestParam(required = false) Map map) {
        PageParams pageParams = new PageParams(map);
        EmailConfig query = pageParams.mapToObject(EmailConfig.class);
        QueryWrapper<EmailConfig> queryWrapper = new QueryWrapper();
        return ResponseResult.ok(targetService.page(new PageParams(map), queryWrapper));
    }

    /**
     * 根据ID查找数据
     */
    @Schema(title = "根据ID查找数据", name = "根据ID查找数据")
    @ResponseBody
    @GetMapping("/get")
    public ResponseResult<EmailConfig> get(@RequestParam("id") Long id) {
        EmailConfig entity = targetService.getById(id);
        return ResponseResult.ok(entity);
    }

    /**
     * 添加数据
     *
     * @return
     */
    @Schema(title = "添加数据", name = "添加数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", required = true, value = "配置名称", paramType = "form"),
//            @ApiImplicitParam(name = "smtpHost", required = true, value = "发件服务器域名", paramType = "form"),
//            @ApiImplicitParam(name = "smtpUsername", required = true, value = "发件服务器账户", paramType = "form"),
//            @ApiImplicitParam(name = "smtpPassword", required = true, value = "发件服务器密码", paramType = "form")
//    })
    @PostMapping("/add")
    public ResponseResult add(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "smtpHost") String smtpHost,
            @RequestParam(value = "smtpUsername") String smtpUsername,
            @RequestParam(value = "smtpPassword") String smtpPassword
    ) {
        EmailConfig entity = new EmailConfig();
        entity.setName(name);
        entity.setSmtpHost(smtpHost);
        entity.setSmtpUsername(smtpUsername);
        entity.setSmtpPassword(smtpPassword);
        targetService.save(entity);
        return ResponseResult.ok();
    }

    /**
     * 更新数据
     *
     * @return
     */
    @Schema(title = "更新数据", name = "更新数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "configId", required = true, value = "", paramType = "form"),
//            @ApiImplicitParam(name = "name", required = true, value = "配置名称", paramType = "form"),
//            @ApiImplicitParam(name = "smtpHost", required = true, value = "发件服务器域名", paramType = "form"),
//            @ApiImplicitParam(name = "smtpUsername", required = true, value = "发件服务器账户", paramType = "form"),
//            @ApiImplicitParam(name = "smtpPassword", required = true, value = "发件服务器密码", paramType = "form")
//    })
    @PostMapping("/update")
    public ResponseResult add(
            @RequestParam(value = "configId") Long configId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "smtpHost") String smtpHost,
            @RequestParam(value = "smtpUsername") String smtpUsername,
            @RequestParam(value = "smtpPassword") String smtpPassword
    ) {
        EmailConfig entity = new EmailConfig();
        entity.setConfigId(configId);
        entity.setName(name);
        entity.setSmtpHost(smtpHost);
        entity.setSmtpUsername(smtpUsername);
        entity.setSmtpPassword(smtpPassword);
        targetService.updateById(entity);
        return ResponseResult.ok();
    }

    /**
     * 删除数据
     *
     * @return
     */
    @Schema(title = "删除数据", name = "删除数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", required = true, value = "id", paramType = "form")
//    })
    @PostMapping("/remove")
    public ResponseResult remove(
            @RequestParam(value = "id") Long id
    ) {
        targetService.removeById(id);
        return ResponseResult.ok();
    }

    /**
     * 批量删除数据
     *
     * @return
     */
    @Schema(title = "批量删除数据", name = "批量删除数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", required = true, value = "id", paramType = "form")
//    })
    @PostMapping("/batch/remove")
    public ResponseResult batchRemove(
            @RequestParam(value = "ids") String ids
    ) {
        targetService.removeByIds(Arrays.asList(ids.split(",")));
        return ResponseResult.ok();
    }
}
