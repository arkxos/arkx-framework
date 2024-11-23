package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.bean.SecureIpReq;
import com.rapidark.cloud.platform.gateway.framework.entity.SecureIp;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import com.rapidark.cloud.platform.gateway.framework.service.SecureIpService;
import com.rapidark.cloud.platform.common.core.util.R;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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
    public R add(@RequestBody SecureIp secureIp) {
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
        return R.ok();
    }

    /**
     * 删除IP
     * @param ip
     * @return
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public R delete(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        secureIpService.deleteById(ip);
        //this.setIpCacheVersion();
        customNacosConfigService.publishIpNacosConfig(ip);
        return R.ok();
    }

    /**
     * 更新IP
     * @param secureIp
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public R update(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setUpdateTime(new Date());
        this.validate(secureIp);
        secureIpService.update(secureIp);
        //this.setIpCacheVersion();
        customNacosConfigService.publishIpNacosConfig(secureIp.getIp());
        return R.ok();
    }

    /**
     * 查询IP
     * @param ip
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public R findById(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        return R.ok(secureIpService.findById(ip));
    }

    /**
     * 分页查询
     * @param secureIpReq
     * @return
     */
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public R pageList(@RequestBody SecureIpReq secureIpReq){
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
        return R.ok(secureIpService.pageList(secureIp,currentPage, pageSize));
    }

    /**
     * 对IP数据进行变更后，设置redis中缓存的版本号
     */
    @Deprecated
    private void setIpCacheVersion(){
        redisTemplate.opsForHash().put(RouteConstants.SYNC_VERSION_KEY, RouteConstants.IP, String.valueOf(System.currentTimeMillis()));
    }

}
