package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.bean.RegServerReq;
import com.rapidark.cloud.platform.gateway.framework.bean.TokenReq;
import com.rapidark.cloud.platform.gateway.framework.command.LongIdCommand;
import com.rapidark.cloud.platform.gateway.framework.command.ClientIdCommand;
import com.rapidark.cloud.platform.gateway.framework.command.RouteIdCommand;
import com.rapidark.cloud.platform.gateway.framework.entity.RegServer;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import com.rapidark.cloud.platform.gateway.framework.service.RegServerService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * @Description 客户端注册服务控制器类
 * @Author JL
 * @Date 2020/05/16
 * @Version V1.0
 */
@RestController
@RequestMapping("/regServer")
public class RegServerRest extends BaseRest {

    @Resource
    private RegServerService regServerService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    /**
     * 添加注册到网关路由的客户端服务
     * @param regServer
     * @return
     */
    @PostMapping(value = "/add")
    public ResponseResult add(@RequestBody RegServer regServer) {
        Assert.notNull(regServer, "未获取到对象");
        //默认禁止通行
        regServer.setStatus(Constants.NO);
        regServer.setCreateTime(new Date());
        this.validate(regServer);
        //验证注册服务是否重复
        RegServer qServer = new RegServer();
        qServer.setClientId(regServer.getClientId());
        qServer.setRouteId(regServer.getRouteId());
        long count = regServerService.count(qServer);
        Assert.isTrue(count <= 0, "客户端已注册该服务，请不要重复注册");
        //保存
        regServerService.save(regServer);
        customNacosConfigService.publishRegServerNacosConfig(regServer.getId());
        return ResponseResult.ok();
    }

    /**
     * 删除注册到网关路由的客户端服务
     * @param command
     * @return
     */
    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestBody LongIdCommand command) {
		Long id = command.getId();
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        regServerService.deleteById(id);
        customNacosConfigService.publishRegServerNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 更新注册到网关路由的客户端服务
     * @param regServer
     * @return
     */
    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody RegServer regServer) {
        Assert.notNull(regServer, "未获取到对象");
        regServer.setUpdateTime(new Date());
        this.validate(regServer);
        regServerService.update(regServer);
        customNacosConfigService.publishRegServerNacosConfig(regServer.getId());
        return ResponseResult.ok();
    }

    /**
     * 获取注册网关路由客户端服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById")
    public ResponseResult findById(@RequestParam Long id) {
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        return ResponseResult.ok(regServerService.findById(id));
    }

    /**
     * 获取网关路由服务列表（分页）
     * @param regServerReq
     * @return
     */
    @RequestMapping(value = "/serverPageList")
    public ResponseResult serverPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getClientId()), "未获取到对象查询ID");
        RegServer regServer = new RegServer();
        regServer.setClientId(regServerReq.getClientId());
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return ResponseResult.ok(regServerService.serverPageList(regServer, currentPage, pageSize));
    }

    /**
     * 获取客户端列表（分页）
     * @param regServerReq
     * @return
     */
    @PostMapping(value = "/clientPageList")
    public ResponseResult clientPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getRouteId()), "未获取到对象查询ID");
        RegServer regServer = new RegServer();
        regServer.setRouteId(regServerReq.getRouteId());
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return ResponseResult.ok(regServerService.clientPageList(regServer, currentPage, pageSize));
    }

    /**
     * 获取已注册网关路由列表
     * @param regServerReq
     * @return
     */
    @PostMapping(value = "/regClientList")
    public ResponseResult regClientList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getRouteId()), "未获取到对象查询ID");
        RegServer regServer = new RegServer();
        regServer.setRouteId(regServerReq.getRouteId());
        return ResponseResult.ok(regServerService.regClientList(regServer));
    }

    /**
     * 启用注册网关路由下的客户端
     * @param command
     * @return
     */
    @PostMapping(value = "/start")
    public ResponseResult start(@RequestBody LongIdCommand command) {
		Long id = command.getId();
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        RegServer dbRegServer = regServerService.findById(id);
        dbRegServer.setStatus(Constants.YES);
        dbRegServer.setUpdateTime(new Date());
        regServerService.update(dbRegServer);
        customNacosConfigService.publishRegServerNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 禁用注册网关路由下的客户端
     * @param command
     * @return
     */
    @PostMapping(value = "/stop")
    public ResponseResult stop(@RequestBody LongIdCommand command) {
		Long id = command.getId();
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        RegServer dbRegServer = regServerService.findById(id);
        dbRegServer.setStatus(Constants.NO);
        dbRegServer.setUpdateTime(new Date());
        regServerService.update(dbRegServer);
        customNacosConfigService.publishRegServerNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 禁用当前客户端，关闭其注册的所有网关路由服务端访问状态
     * @param command
     * @return
     */
    @PostMapping(value = "/stopClientAllRoute")
    public ResponseResult stopClientAllRoute(@RequestBody ClientIdCommand command) {
		String clientId = command.getClientId();
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象ID");
        regServerService.stopClientAllRoute(clientId);
        customNacosConfigService.publishClientNacosConfig(clientId);
        return ResponseResult.ok();
    }

    /**
     * 启用当前客户端，激活其注册的所有网关路由服务端访问状态
     * @param command
     * @return
     */
    @PostMapping(value = "/startClientAllRoute")
    public ResponseResult startClientAllRoute(@RequestBody ClientIdCommand command) {
		String clientId = command.getClientId();
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象ID");
        regServerService.startClientAllRoute(clientId);
        customNacosConfigService.publishClientNacosConfig(clientId);
        return ResponseResult.ok();
    }

    /**
     * 关闭网关路由下所有注册客户端
     * @param command
     * @return
     */
    @PostMapping(value = "/stopRouteAllClient")
    public ResponseResult stopRouteAllClient(@RequestBody RouteIdCommand command) {
		String routeId = command.getRouteId();
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象ID");
        regServerService.stopRouteAllClient(routeId);
        customNacosConfigService.publishRouteNacosConfig(routeId);
        return ResponseResult.ok();
    }

    /**
     * 启用网关路由下所有注册客户端
     * @param command
     * @return
     */
    @PostMapping(value = "/startRouteAllClient")
    public ResponseResult startRouteAllClient(@RequestBody RouteIdCommand command) {
		String routeId = command.getRouteId();
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象ID");
        regServerService.startRouteAllClient(routeId);
        customNacosConfigService.publishRouteNacosConfig(routeId);
        return ResponseResult.ok();
    }

    /**
     * 获取未注册到的服务列表（分页）
     * @param regServerReq
     * @return
     */
    @PostMapping(value = "/notRegServerPageList")
    public ResponseResult notRegServerPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getClientId()), "未获取到客户端ID");
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return ResponseResult.ok(regServerService.notRegServerPageList(regServerReq, currentPage, pageSize));
    }

    /**
     * 获取未注册的客户端列表（分页）
     * @param regServerReq
     * @return
     */
    @PostMapping(value = "/notRegClientPageList")
    public ResponseResult notRegClientPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getRouteId()), "未获取路由服务ID");
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return ResponseResult.ok(regServerService.notRegClientPageList(regServerReq, currentPage, pageSize));
    }

    /**
     * 创建Token
     * @param tokenReq
     * @return
     */
    @PostMapping(value = "/createToken")
    public ResponseResult createToken(@RequestBody TokenReq tokenReq){
        Assert.notNull(tokenReq, "未获取到对象");
        Assert.notNull(tokenReq.getRegServerId(), "未获取到对象ID");
        Assert.isTrue(tokenReq.getRegServerId() > 0, "ID值错误");
        RegServer regServer = regServerService.findById(tokenReq.getRegServerId());
        Assert.notNull(regServer, "未查询到对象");
        String sub = String.format("%s,%s,%d", regServer.getRouteId(), regServer.getClientId(), System.currentTimeMillis());
        Date tokenEffectiveTime = tokenReq.getTokenEffectiveTime();
        Assert.notNull(tokenEffectiveTime, "未获取Token有效过期时间");
        String secretKey = tokenReq.getSecretKey();
        if (StringUtils.isBlank(secretKey)){
            secretKey = regServer.getClientId();
        }
        //创建Token
        String jwtToken = JwtTokenUtils.createToken(sub, tokenEffectiveTime, secretKey);
        //每次加密成功，更新数据库已有Token
        regServer.setToken(jwtToken);
        regServer.setSecretKey(secretKey);
        regServer.setTokenEffectiveTime(tokenEffectiveTime);
        regServerService.update(regServer);
        customNacosConfigService.publishRegServerNacosConfig(regServer.getId());
        //返回最新Token
        return ResponseResult.ok(Constants.SUCCESS, jwtToken);
    }

    /**
     *清除Token
     * @param tokenReq
     * @return
     */
    @PostMapping(value = "/removeToken")
    public ResponseResult removeToken(@RequestBody TokenReq tokenReq){
        Assert.notNull(tokenReq, "未获取到对象");
        Assert.notNull(tokenReq.getRegServerId(), "未获取到对象ID");
        Assert.isTrue(tokenReq.getRegServerId() > 0, "ID值错误");
        RegServer regServer = regServerService.findById(tokenReq.getRegServerId());
        Assert.notNull(regServer, "未查询到对象");
        //清除已有Token
        regServer.setTokenEffectiveTime(null);
        regServer.setToken(null);
        regServer.setSecretKey(null);
        regServerService.update(regServer);
        customNacosConfigService.publishRegServerNacosConfig(regServer.getId());
        return ResponseResult.ok();
    }

}
