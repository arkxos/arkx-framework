package com.rapidark.cloud.gateway.manage.rest;

import com.rapidark.common.model.ResultBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.rapidark.cloud.gateway.formwork.base.BaseRest;
import com.rapidark.cloud.gateway.formwork.bean.SecureIpReq;
import com.rapidark.cloud.gateway.formwork.entity.SecureIp;
import com.rapidark.cloud.gateway.formwork.service.CustomNacosConfigService;
import com.rapidark.cloud.gateway.formwork.service.SecureIpService;
import com.rapidark.cloud.gateway.formwork.util.RouteConstants;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description Ip管理控制器
 * @Author JL
 * @Date 2020/05/28
 * @Version V1.0
 */
@RestController
@RequestMapping("/ip")
public class SecureIpRest extends BaseRest {

    @Resource
    private SecureIpService secureIpService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    /**
     * 添加IP
     * @param secureIp
     * @return
     */
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public ResultBody add(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setCreateTime(new Date());
        this.validate(secureIp);
        //验证注册服务是否重复
        SecureIp qSecureIp = new SecureIp();
        qSecureIp.setIp(secureIp.getIp());
        long count = secureIpService.count(qSecureIp);
        Assert.isTrue(count <= 0, "该IP已添加，请不要重复添加");
        secureIpService.save(secureIp);
        //this.setIpCacheVersion();
        customNacosConfigService.publishIpNacosConfig(secureIp.getIp());
        return ResultBody.ok();
    }

    /**
     * 删除IP
     * @param ip
     * @return
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody delete(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        secureIpService.deleteById(ip);
        //this.setIpCacheVersion();
        customNacosConfigService.publishIpNacosConfig(ip);
        return ResultBody.ok();
    }

    /**
     * 更新IP
     * @param secureIp
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ResultBody update(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setUpdateTime(new Date());
        this.validate(secureIp);
        secureIpService.update(secureIp);
        //this.setIpCacheVersion();
        customNacosConfigService.publishIpNacosConfig(secureIp.getIp());
        return ResultBody.ok();
    }

    /**
     * 查询IP
     * @param ip
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody findById(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        return ResultBody.ok().data(secureIpService.findById(ip));
    }

    /**
     * 分页查询
     * @param secureIpReq
     * @return
     */
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody pageList(@RequestBody SecureIpReq secureIpReq){
        Assert.notNull(secureIpReq, "未获取到对象");
        int currentPage = getCurrentPage(secureIpReq.getCurrentPage());
        int pageSize = getPageSize(secureIpReq.getPageSize());
        SecureIp secureIp = new SecureIp();
        if (StringUtils.isNotBlank(secureIpReq.getIp())){
            secureIp.setIp(secureIpReq.getIp());
        }
        if (StringUtils.isNotBlank(secureIpReq.getStatus())){
            secureIp.setStatus(secureIpReq.getStatus());
        }
        return ResultBody.ok().data(secureIpService.pageList(secureIp,currentPage, pageSize));
    }

    /**
     * 对IP数据进行变更后，设置redis中缓存的版本号
     */
    @Deprecated
    private void setIpCacheVersion(){
        redisTemplate.opsForHash().put(RouteConstants.SYNC_VERSION_KEY, RouteConstants.IP, String.valueOf(System.currentTimeMillis()));
    }

}
