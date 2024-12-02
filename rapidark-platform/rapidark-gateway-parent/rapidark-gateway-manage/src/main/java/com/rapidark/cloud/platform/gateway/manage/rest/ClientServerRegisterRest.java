package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.bean.ClientServerRegisterReq;
import com.rapidark.cloud.platform.gateway.framework.bean.TokenReq;
import com.rapidark.cloud.platform.gateway.framework.command.LongIdCommand;
import com.rapidark.cloud.platform.gateway.framework.command.ClientIdCommand;
import com.rapidark.cloud.platform.gateway.framework.command.RouteIdCommand;
import com.rapidark.cloud.platform.gateway.framework.entity.ClientServerRegister;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import com.rapidark.cloud.platform.gateway.framework.service.ClientServerRegisterService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
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
public class ClientServerRegisterRest extends BaseRest {

    @Resource
    private ClientServerRegisterService clientServerRegisterService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    /**
     * 添加注册到网关路由的客户端服务
     * @param clientServerRegister
     * @return
     */
    @PostMapping(value = "/add")
    public ResponseResult add(@RequestBody ClientServerRegister clientServerRegister) {
        Assert.notNull(clientServerRegister, "未获取到对象");
        //默认禁止通行
        clientServerRegister.setStatus(Constants.NO);
        clientServerRegister.setCreateTime(new Date());
        this.validate(clientServerRegister);
        //验证注册服务是否重复
        ClientServerRegister qServer = new ClientServerRegister();
        qServer.setClientId(clientServerRegister.getClientId());
        qServer.setRouteId(clientServerRegister.getRouteId());
        long count = clientServerRegisterService.count(qServer);
        Assert.isTrue(count <= 0, "客户端已注册该服务，请不要重复注册");
        //保存
        clientServerRegisterService.save(clientServerRegister);
        customNacosConfigService.publishRegServerNacosConfig(clientServerRegister.getId());
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
        clientServerRegisterService.deleteById(id);
        customNacosConfigService.publishRegServerNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 更新注册到网关路由的客户端服务
     * @param clientServerRegister
     * @return
     */
    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody ClientServerRegister clientServerRegister) {
        Assert.notNull(clientServerRegister, "未获取到对象");
        clientServerRegister.setUpdateTime(new Date());
        this.validate(clientServerRegister);
        clientServerRegisterService.update(clientServerRegister);
        customNacosConfigService.publishRegServerNacosConfig(clientServerRegister.getId());
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
        return ResponseResult.ok(clientServerRegisterService.findById(id));
    }

    /**
     * 获取网关路由服务列表（分页）
     * @param clientServerRegisterReq
     * @return
     */
    @RequestMapping(value = "/serverPageList")
    public ResponseResult serverPageList(@RequestBody ClientServerRegisterReq clientServerRegisterReq, Pageable pageable) {
        Assert.notNull(clientServerRegisterReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(clientServerRegisterReq.getClientId()), "未获取到对象查询ID");
        ClientServerRegister clientServerRegister = new ClientServerRegister();
        clientServerRegister.setClientId(clientServerRegisterReq.getClientId());
        int currentPage = getCurrentPage(clientServerRegisterReq.getCurrentPage());
        int pageSize = getPageSize(clientServerRegisterReq.getPageSize());
        return ResponseResult.ok(clientServerRegisterService.serverPageList(clientServerRegisterReq.getClientId(), pageable));
    }

    /**
     * 获取客户端列表（分页）
     * @param clientServerRegisterReq
     * @return
     */
    @PostMapping(value = "/clientPageList")
    public ResponseResult clientPageList(@RequestBody ClientServerRegisterReq clientServerRegisterReq, Pageable pageable) {
        Assert.notNull(clientServerRegisterReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(clientServerRegisterReq.getRouteId()), "未获取到对象查询ID");
        ClientServerRegister clientServerRegister = new ClientServerRegister();
        clientServerRegister.setRouteId(clientServerRegisterReq.getRouteId());
        int currentPage = getCurrentPage(clientServerRegisterReq.getCurrentPage());
        int pageSize = getPageSize(clientServerRegisterReq.getPageSize());
        return ResponseResult.ok(clientServerRegisterService.clientPageList(clientServerRegisterReq.getRouteId(), pageable));
    }

    /**
     * 获取已注册网关路由列表
     * @param clientServerRegisterReq
     * @return
     */
    @PostMapping(value = "/regClientList")
    public ResponseResult regClientList(@RequestBody ClientServerRegisterReq clientServerRegisterReq) {
        Assert.notNull(clientServerRegisterReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(clientServerRegisterReq.getRouteId()), "未获取到对象查询ID");
        ClientServerRegister clientServerRegister = new ClientServerRegister();
        clientServerRegister.setRouteId(clientServerRegisterReq.getRouteId());
        return ResponseResult.ok(clientServerRegisterService.regClientList(clientServerRegisterReq.getRouteId()));
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
        ClientServerRegister dbClientServerRegister = clientServerRegisterService.findById(id);
        dbClientServerRegister.setStatus(Constants.YES);
        dbClientServerRegister.setUpdateTime(new Date());
        clientServerRegisterService.update(dbClientServerRegister);
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
        ClientServerRegister dbClientServerRegister = clientServerRegisterService.findById(id);
        dbClientServerRegister.setStatus(Constants.NO);
        dbClientServerRegister.setUpdateTime(new Date());
        clientServerRegisterService.update(dbClientServerRegister);
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
        clientServerRegisterService.stopClientAllRoute(clientId);
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
        clientServerRegisterService.startClientAllRoute(clientId);
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
        clientServerRegisterService.stopRouteAllClient(routeId);
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
        clientServerRegisterService.startRouteAllClient(routeId);
        customNacosConfigService.publishRouteNacosConfig(routeId);
        return ResponseResult.ok();
    }

    /**
     * 获取未注册到的服务列表（分页）
     * @param clientServerRegisterReq
     * @return
     */
    @PostMapping(value = "/notRegServerPageList")
    public ResponseResult notRegServerPageList(@RequestBody ClientServerRegisterReq clientServerRegisterReq, Pageable pageable) {
        Assert.notNull(clientServerRegisterReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(clientServerRegisterReq.getClientId()), "未获取到客户端ID");
        int currentPage = getCurrentPage(clientServerRegisterReq.getCurrentPage());
        int pageSize = getPageSize(clientServerRegisterReq.getPageSize());
        return ResponseResult.ok(clientServerRegisterService.notRegServerPageList(clientServerRegisterReq.getClientId(), pageable));
    }

    /**
     * 获取未注册的客户端列表（分页）
     * @param clientServerRegisterReq
     * @return
     */
    @PostMapping(value = "/notRegClientPageList")
    public ResponseResult notRegClientPageList(@RequestBody ClientServerRegisterReq clientServerRegisterReq, Pageable pageable) {
        Assert.notNull(clientServerRegisterReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(clientServerRegisterReq.getRouteId()), "未获取路由服务ID");
        int currentPage = getCurrentPage(clientServerRegisterReq.getCurrentPage());
        int pageSize = getPageSize(clientServerRegisterReq.getPageSize());
        return ResponseResult.ok(clientServerRegisterService.notRegClientPageList(clientServerRegisterReq.getRouteId(), pageable));
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
        ClientServerRegister clientServerRegister = clientServerRegisterService.findById(tokenReq.getRegServerId());
        Assert.notNull(clientServerRegister, "未查询到对象");
        String sub = String.format("%s,%s,%d", clientServerRegister.getRouteId(), clientServerRegister.getClientId(), System.currentTimeMillis());
        Date tokenEffectiveTime = tokenReq.getTokenEffectiveTime();
        Assert.notNull(tokenEffectiveTime, "未获取Token有效过期时间");
        String secretKey = tokenReq.getSecretKey();
        if (StringUtils.isBlank(secretKey)){
            secretKey = clientServerRegister.getClientId();
        }
        //创建Token
        String jwtToken = JwtTokenUtils.createToken(sub, tokenEffectiveTime, secretKey);
        //每次加密成功，更新数据库已有Token
        clientServerRegister.setToken(jwtToken);
        clientServerRegister.setSecretKey(secretKey);
        clientServerRegister.setTokenEffectiveTime(tokenEffectiveTime);
        clientServerRegisterService.update(clientServerRegister);
        customNacosConfigService.publishRegServerNacosConfig(clientServerRegister.getId());
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
        ClientServerRegister clientServerRegister = clientServerRegisterService.findById(tokenReq.getRegServerId());
        Assert.notNull(clientServerRegister, "未查询到对象");
        //清除已有Token
        clientServerRegister.setTokenEffectiveTime(null);
        clientServerRegister.setToken(null);
        clientServerRegister.setSecretKey(null);
        clientServerRegisterService.update(clientServerRegister);
        customNacosConfigService.publishRegServerNacosConfig(clientServerRegister.getId());
        return ResponseResult.ok();
    }

}
