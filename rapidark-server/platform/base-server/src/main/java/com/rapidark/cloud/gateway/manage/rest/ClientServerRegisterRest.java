package com.rapidark.cloud.gateway.manage.rest;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.gateway.formwork.bean.ClientServerRegisterRegisterReq;
import com.rapidark.cloud.gateway.formwork.entity.ClientServerRegister;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.formwork.service.ClientServerRegisterService;
import com.rapidark.cloud.gateway.manage.service.dto.GatewayAppRouteRegServer;
import com.rapidark.common.utils.PageUtil;
import com.rapidark.common.utils.UuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.rapidark.cloud.gateway.formwork.base.BaseRest;
import com.rapidark.cloud.gateway.formwork.bean.TokenReq;
import com.rapidark.cloud.gateway.formwork.service.CustomNacosConfigService;
import com.rapidark.cloud.gateway.formwork.util.ApiResult;
import com.rapidark.cloud.gateway.formwork.util.Constants;
import com.rapidark.cloud.gateway.formwork.util.JwtTokenUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 客户端注册服务
 * @author darkness
 * @date 2022/6/13 16:07
 * @version 1.0
 */
@RestController
public class ClientServerRegisterRest extends BaseRest {

    @Resource
    private ClientServerRegisterService clientServerRegisterService;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 添加注册到网关路由的客户端服务
     * @param clientServerRegister
     * @return
     */
    @PostMapping("/regServer/add")
    public ApiResult add(@RequestBody ClientServerRegister clientServerRegister) {
        Assert.notNull(clientServerRegister, "未获取到对象");
        //默认禁止通行
        clientServerRegister.setStatus(Constants.NO);
        clientServerRegister.setCreateTime(new Date());
        this.validate(clientServerRegister);
        //验证注册服务是否重复
        ClientServerRegister qServer = new ClientServerRegister();
        clientServerRegister.setId(UuidUtil.base58Uuid());
        qServer.setClientId(clientServerRegister.getClientId());
        qServer.setRouteId(clientServerRegister.getRouteId());
        long count = clientServerRegisterService.count(qServer);
        Assert.isTrue(count <= 0, "客户端已注册该服务，请不要重复注册");
        //保存
        clientServerRegisterService.save(clientServerRegister);
        //this.setClientCacheVersion();
        customNacosConfigService.publishRegServerNacosConfig(clientServerRegister.getId());
        return new ApiResult();
    }

    /**
     * 删除注册到网关路由的客户端服务
     * @param id
     * @return
     */
    @PostMapping(value = "/regServer/delete")
    public ApiResult delete(@RequestParam String id) {
        Assert.notNull(id, "未获取到对象ID");
//        Assert.isTrue(id>0, "ID值错误");
        clientServerRegisterService.deleteById(id);
        //this.setClientCacheVersion();
        customNacosConfigService.publishRegServerNacosConfig(id);
        return new ApiResult();
    }

    /**
     * 更新注册到网关路由的客户端服务
     * @param clientServerRegister
     * @return
     */
    @PostMapping(value = "/regServer/update")
    public ApiResult update(@RequestBody ClientServerRegister clientServerRegister) {
        Assert.notNull(clientServerRegister, "未获取到对象");
        clientServerRegister.setUpdateTime(new Date());
        this.validate(clientServerRegister);
        clientServerRegisterService.update(clientServerRegister);
        //this.setClientCacheVersion();
        customNacosConfigService.publishRegServerNacosConfig(clientServerRegister.getId());
        return new ApiResult();
    }

    /**
     * 获取注册网关路由客户端服务
     * @param id
     * @return
     */
    @GetMapping(value = "/regServer/findById")
    public ApiResult findById(@RequestParam String id) {
        Assert.notNull(id, "未获取到对象ID");
//        Assert.isTrue(id>0, "ID值错误");
        return new ApiResult(clientServerRegisterService.findById(id));
    }

    /**
     * 获取网关路由服务列表（分页）
     * @return
     */
    @GetMapping(value = "/regServer/serverPageList")
    public ApiResult serverPageList(String clientId, Pageable pageable) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象查询ID");
        Page<GatewayAppRouteRegServer> data = clientServerRegisterService.serverPageList(clientId, pageable);
        return new ApiResult(PageUtil.toPageData(data));
    }

    /**
     * 获取客户端列表（分页）
     * @return
     */
    @GetMapping(value = "/regServer/clientPageList")
    public ApiResult clientPageList(String routeId, Pageable pageable) {
//        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象查询ID");
//        ClientServerRegister regServer = new ClientServerRegister();
//        regServer.setRouteId(regServerReq.getRouteId());
//        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
//        int pageSize = getPageSize(regServerReq.getPageSize());
        Page<Map<String, Object>> data = clientServerRegisterService.clientPageList(routeId, pageable);
        return new ApiResult(data);
    }

    /**
     * 获取已注册网关路由列表
     * @return
     */
    @GetMapping(value = "/regServer/regClientList")
    public ApiResult regClientList(String routeId) {
//        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象查询ID");
//        ClientServerRegister regServer = new ClientServerRegister();
//        regServer.setRouteId(regServerReq.getRouteId());
        return new ApiResult(clientServerRegisterService.regClientList(routeId));
    }

    /**
     * 启用注册网关路由下的客户端
     * @param id
     * @return
     */
    @PostMapping(value = "/regServer/start")
    public ApiResult start(@RequestParam String id) {
        Assert.notNull(id, "未获取到对象ID");
//        Assert.isTrue(id>0, "ID值错误");
        ClientServerRegister dbClientServerRegister = clientServerRegisterService.findById(id);
        dbClientServerRegister.setStatus(Constants.YES);
        dbClientServerRegister.setUpdateTime(new Date());
        clientServerRegisterService.update(dbClientServerRegister);
        //this.setClientCacheVersion();
        customNacosConfigService.publishRegServerNacosConfig(id);
        return new ApiResult();
    }

    /**
     * 禁用注册网关路由下的客户端
     * @param id
     * @return
     */
    @PostMapping(value = "/regServer/stop")
    public ApiResult stop(@RequestParam String id) {
        Assert.notNull(id, "未获取到对象ID");
//        Assert.isTrue(id>0, "ID值错误");
        ClientServerRegister dbClientServerRegister = clientServerRegisterService.findById(id);
        dbClientServerRegister.setStatus(Constants.NO);
        dbClientServerRegister.setUpdateTime(new Date());
        clientServerRegisterService.update(dbClientServerRegister);
        //this.setClientCacheVersion();
        customNacosConfigService.publishRegServerNacosConfig(id);
        return new ApiResult();
    }

    /**
     * 禁用当前客户端，关闭其注册的所有网关路由服务端访问状态
     * @param clientId
     * @return
     */
    @PostMapping(value = "/regServer/stopClientAllRoute")
    public ApiResult stopClientAllRoute(@RequestParam String clientId) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象ID");
        clientServerRegisterService.stopClientAllRoute(clientId);
        customNacosConfigService.publishClientNacosConfig(clientId);
        return new ApiResult();
    }

    /**
     * 启用当前客户端，激活其注册的所有网关路由服务端访问状态
     * @param clientId
     * @return
     */
    @PostMapping(value = "/regServer/startClientAllRoute")
    public ApiResult startClientAllRoute(@RequestParam String clientId) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象ID");
        clientServerRegisterService.startClientAllRoute(clientId);
        customNacosConfigService.publishClientNacosConfig(clientId);
        return new ApiResult();
    }

    /**
     * 关闭网关路由下所有注册客户端
     * @param routeId
     * @return
     */
    @PostMapping(value = "/regServer/stopRouteAllClient")
    public ApiResult stopRouteAllClient(@RequestParam String routeId) {
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象ID");
        clientServerRegisterService.stopRouteAllClient(routeId);
        customNacosConfigService.publishRouteNacosConfig(routeId);
        return new ApiResult();
    }

    /**
     * 启用网关路由下所有注册客户端
     * @param routeId
     * @return
     */
    @PostMapping(value = "/regServer/startRouteAllClient")
    public ApiResult startRouteAllClient(@RequestParam String routeId) {
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象ID");
        clientServerRegisterService.startRouteAllClient(routeId);
        customNacosConfigService.publishRouteNacosConfig(routeId);
        return new ApiResult();
    }

    /**
     * 获取未注册到的服务列表（分页）
     * @param clientId
     * @return
     */
    @GetMapping(value = "/regServer/notRegServerPageList")
    public ApiResult notRegServerPageList(String clientId, Pageable pageable) {
//        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到客户端ID");
        Page<GatewayAppRoute> data = clientServerRegisterService.notRegServerPageList(clientId, pageable);
        return new ApiResult(data);
    }

    /**
     * 获取未注册的客户端列表（分页）
     * @return
     */
    @GetMapping(value = "/regServer/notRegClientPageList")
    public ApiResult notRegClientPageList(String routeId, Pageable pageable) {
//        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取路由服务ID");
//        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
//        int pageSize = getPageSize(regServerReq.getPageSize());
        Page<OpenApp> data = clientServerRegisterService.notRegClientPageList(routeId, pageable);
        return new ApiResult(data);
    }

    /**
     * 创建Token
     * @param tokenReq
     * @return
     */
    @PostMapping(value = "/regServer/createToken")
    public ApiResult createToken(@RequestBody TokenReq tokenReq){
        Assert.notNull(tokenReq, "未获取到对象");
        Assert.notNull(tokenReq.getRegServerId(), "未获取到对象ID");
//        Assert.isTrue(tokenReq.getRegServerId() > 0, "ID值错误");
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
        //返回最新Token
        return new ApiResult(Constants.SUCCESS, jwtToken);
    }

    /**
     *清除Token
     * @param tokenReq
     * @return
     */
    @PostMapping(value = "/regServer/removeToken")
    public ApiResult removeToken(@RequestBody TokenReq tokenReq){
        Assert.notNull(tokenReq, "未获取到对象");
        Assert.notNull(tokenReq.getRegServerId(), "未获取到对象ID");
//        Assert.isTrue(tokenReq.getRegServerId() > 0, "ID值错误");
        ClientServerRegister clientServerRegister = clientServerRegisterService.findById(tokenReq.getRegServerId());
        Assert.notNull(clientServerRegister, "未查询到对象");
        //清除已有Token
        clientServerRegister.setTokenEffectiveTime(null);
        clientServerRegister.setToken(null);
        clientServerRegister.setSecretKey(null);
        clientServerRegisterService.update(clientServerRegister);
        return new ApiResult();
    }

}
