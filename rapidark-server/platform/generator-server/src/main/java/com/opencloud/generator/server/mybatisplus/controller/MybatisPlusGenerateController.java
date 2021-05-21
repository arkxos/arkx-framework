package com.opencloud.generator.server.mybatisplus.controller;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.google.common.collect.Maps;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.DateUtils;
import com.opencloud.generator.server.mybatisplus.controller.cmd.GenerateCodeCommand;
import com.opencloud.generator.server.mybatisplus.controller.param.QueryTablesParams;
import com.opencloud.generator.server.mybatisplus.service.GenerateConfig;
import com.opencloud.generator.server.mybatisplus.service.GeneratorService;
import com.opencloud.generator.server.utils.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author: liuyadu
 * @date: 2019/7/19 15:26
 * @description:
 */
@Api(tags = "在线代码生成器")
@RestController
@RequestMapping("/generate")
public class MybatisPlusGenerateController {
    /**
     * 获取所有表信息
     *
     * @return
     */
    @ApiOperation(value = "获取所有表信息", notes = "获取所有表信息")
    @PostMapping("/tables")
    public ResultBody<List<TableInfo>> tables(@RequestBody QueryTablesParams params) {
        GlobalConfig gc = new GlobalConfig();
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.getDbType(params.getType()));
        dsc.setDriverName(params.getDriverName());
        dsc.setUrl(params.getUrl());
        dsc.setUsername(params.getUsername());
        dsc.setPassword(params.getPassword());
        StrategyConfig strategy = new StrategyConfig();
        TemplateConfig templateConfig = new TemplateConfig();
        ConfigBuilder config = new ConfigBuilder(new PackageConfig(), dsc, strategy, templateConfig, gc);
        List<TableInfo> list = config.getTableInfoList();
        return ResultBody.ok().data(list);
    }

    @ApiOperation(value = "代码生成并下载", notes = "代码生成并下载")
    @PostMapping("/execute")
    public ResultBody<List<TableInfo>> execute(@RequestBody GenerateCodeCommand command) throws Exception {
        String outputDir = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "generator" + File.separator + DateUtils.getCurrentTimestampStr();
        GenerateConfig config = new GenerateConfig();
        config.setDbType(DbType.getDbType(command.getType()));
        config.setJdbcUrl(command.getUrl());
        config.setJdbcUserName(command.getUsername());
        config.setJdbcPassword(command.getPassword());
        config.setJdbcDriver(command.getDriverName());
        config.setAuthor(command.getAuthor());
        config.setParentPackage(command.getParentPackage());
        config.setModuleName(command.getModuleName());
        config.setIncludeTables(command.getIncludeTables().split(","));
        config.setTablePrefix(command.getTablePrefix().split(","));
        config.setOutputDir(outputDir);
        GeneratorService.execute(config);
        String fileName = command.getModuleName() + ".zip";
        String filePath = outputDir + File.separator + fileName;
        // 压缩目录
        String[] srcDir = {outputDir + File.separator + (command.getParentPackage().substring(0, command.getParentPackage().indexOf("."))), outputDir + File.separator + "src"};
        ZipUtil.toZip(srcDir, filePath, true);
        Map data = Maps.newHashMap();
        data.put("filePath", filePath);
        data.put("fileName", fileName);
        return ResultBody.ok().data(data);
    }
}
