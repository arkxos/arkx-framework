package com.rapidark.cloud.gateway.manage.rest;

import com.rapidark.cloud.gateway.formwork.util.*;
import com.rapidark.framework.commons.model.ResultBody;
import com.rapidark.framework.commons.utils.Constants;
import com.rapidark.framework.commons.utils.PageData;
import com.rapidark.framework.commons.utils.SystemIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.rapidark.cloud.gateway.formwork.base.BaseRest;
import com.rapidark.cloud.gateway.formwork.bean.BalancedReq;
import com.rapidark.cloud.gateway.formwork.bean.BalancedRsp;
import com.rapidark.cloud.gateway.formwork.entity.Balanced;
import com.rapidark.cloud.gateway.formwork.entity.LoadServer;
import com.rapidark.cloud.gateway.manage.service.BalancedService;
import com.rapidark.cloud.gateway.manage.service.CustomNacosConfigService;
import com.rapidark.cloud.gateway.manage.service.LoadServerService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description 负载配置管理控制器
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
@Slf4j
@RestController
public class BalancedRest extends BaseRest {

    @Resource
    private BalancedService balancedService;

    @Resource
    private LoadServerService loadServerService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    @Resource
    private SystemIdGenerator systemIdGenerator;

    /**
     * 添加负载配置
     * @param balancedReq
     * @return
     */
    @RequestMapping(value = "/balanced/add", method = {RequestMethod.POST})
    public ResultBody add(@RequestBody BalancedReq balancedReq) {
        Assert.notNull(balancedReq, "未获取到对象");
        Balanced balanced = new Balanced();
        balanced.setId(systemIdGenerator.generate()+"");//UUIDUtils.getUUIDString());
        balanced.setName(balancedReq.getName());
        balanced.setGroupCode(balancedReq.getGroupCode());
        balanced.setLoadUri(balancedReq.getLoadUri());
        balanced.setStatus(balancedReq.getStatus());
        balanced.setRemarks(balancedReq.getRemarks());
        balanced.setCreateTime(new Date());
        this.validate(balanced);

        //验证名称是否重复
        Balanced qBalanced = new Balanced();
        qBalanced.setName(balanced.getName());
        long count = balancedService.count(qBalanced);
        Assert.isTrue(count <= 0, "负载名称已存在，不能重复");
        //保存
        balancedService.save(balanced);
        //保存注册的服务列表
        List<LoadServer> serverList = balancedReq.getServerList();
        if (!CollectionUtils.isEmpty(serverList)) {
            for (LoadServer loadServer : serverList) {
                loadServer.setBalancedId(balanced.getId());
                loadServer.setCreateTime(new Date());
                loadServerService.save(loadServer);
            }
            //this.setRouteCacheVersion();
            customNacosConfigService.publishBalancedNacosConfig(balanced.getId());
        }
        return ResultBody.ok();
    }

    /**
     * 删除指定负载ID配置
     * @param id
     * @return
     */
    @RequestMapping(value = "/balanced/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody delete(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        balancedService.deleteAndServer(id);
        //this.setRouteCacheVersion();
        customNacosConfigService.publishBalancedNacosConfig(id);
        return ResultBody.ok();
    }

    /**
     * 更新负载数据对象
     * @param balancedReq
     * @return
     */
    @RequestMapping(value = "/balanced/update", method = {RequestMethod.POST})
    public ResultBody update(@RequestBody BalancedReq balancedReq) {
        Assert.notNull(balancedReq, "未获取到对象");
//        Assert.isTrue(StringUtils.isNotBlank(balancedReq.getId()), "未获取到对象ID");
        this.validate(balancedReq);
        Balanced balanced = balancedService.findById(balancedReq.getId());
        if (balanced != null) {
            balanced.setName(balancedReq.getName());
            balanced.setGroupCode(balancedReq.getGroupCode());
            balanced.setLoadUri(balancedReq.getLoadUri());
            balanced.setStatus(balancedReq.getStatus());
            balanced.setRemarks(balancedReq.getRemarks());
            balanced.setUpdateTime(new Date());
            balancedService.update(balanced);
            loadServerService.updates(balanced.getId(), balancedReq.getServerList());
            //this.setRouteCacheVersion();
            customNacosConfigService.publishBalancedNacosConfig(balanced.getId());
        }
        return ResultBody.ok();
    }

    /**
     * 获取指定负载ID对象
     * @param id
     * @return
     */
    @RequestMapping(value = "/balanced/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody findById(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Balanced balanced = balancedService.findById(id);
        if (balanced != null) {
            List<LoadServer> serverList = loadServerService.queryByBalancedId(id);
            BalancedRsp balancedRsp = new BalancedRsp();
            balancedRsp.setBalanced(balanced);
            balancedRsp.setServerList(serverList);
            return ResultBody.ok(balancedRsp);
        }
        return ResultBody.failed("未获取到对象");
    }

    /**
     * 分页查询
     * @param balancedReq
     * @return
     */
    @RequestMapping(value = "/balanced/pageList", method = { RequestMethod.POST })
    public ResultBody pageList(@RequestBody BalancedReq balancedReq) {
        Balanced balanced = new Balanced();
        if (balancedReq != null){
            if (StringUtils.isNotBlank(balancedReq.getName())) {
                balanced.setName(balancedReq.getName());
            }
            if (StringUtils.isNotBlank(balancedReq.getStatus())) {
                balanced.setStatus(balancedReq.getStatus());
            }
            if (StringUtils.isNotBlank(balancedReq.getGroupCode())) {
                balanced.setGroupCode(balancedReq.getGroupCode());
            }
        }
        int currentPage = getCurrentPage(balancedReq.getCurrentPage());
        int pageSize = getPageSize(balancedReq.getPageSize());
        PageData<Balanced> data = balancedService.pageList(balanced, currentPage, pageSize);
        return ResultBody.ok(data);
    }

    /**
     * 将状态置为：启用
     * @param id
     * @return
     */
    @RequestMapping(value = "/balanced/start", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody start(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Balanced dbBalanced = balancedService.findById(id);
        dbBalanced.setStatus(Constants.YES);
        balancedService.update(dbBalanced);
        //this.setRouteCacheVersion();
        customNacosConfigService.publishBalancedNacosConfig(id);
        return ResultBody.ok();
    }

    /**
     * 将状态置为：禁用
     * @param id
     * @return
     */
    @RequestMapping(value = "/balanced/stop", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody stop(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Balanced dbBalanced = balancedService.findById(id);
        dbBalanced.setStatus(Constants.NO);
        balancedService.update(dbBalanced);
        //this.setRouteCacheVersion();
        customNacosConfigService.publishBalancedNacosConfig(id);
        return ResultBody.ok();
    }

    /**
     * 对路由数据进行变更后，设置redis中缓存的版本号
     */
    @Deprecated
    private void setRouteCacheVersion(){
        redisTemplate.opsForHash().put(RouteConstants.SYNC_VERSION_KEY, RouteConstants.ROUTE, String.valueOf(System.currentTimeMillis()));
    }

}
