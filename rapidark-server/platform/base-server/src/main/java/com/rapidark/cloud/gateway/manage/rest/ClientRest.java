//package com.rapidark.cloud.gateway.manage.rest;
//
//import com.rapidark.cloud.base.client.model.entity.OpenApp;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.util.Assert;
//import org.springframework.web.bind.annotation.*;
//
//import com.rapidark.cloud.gateway.formwork.base.BaseRest;
//import com.rapidark.cloud.gateway.formwork.bean.ClientReq;
//import com.rapidark.cloud.gateway.formwork.entity.Client;
//import com.rapidark.cloud.gateway.formwork.service.ClientService;
//import com.rapidark.cloud.gateway.formwork.service.CustomNacosConfigService;
//import com.rapidark.common.model.ResultBody;
//import com.rapidark.cloud.gateway.formwork.util.Constants;
//import com.rapidark.cloud.gateway.formwork.util.UUIDUtils;
//
//import javax.annotation.Resource;
//import java.util.Date;
//
///**
// * @Description 客户端管理控制器类
// * @Author jianglong
// * @Date 2020/05/16
// * @Version V1.0
// */
//@RestController
//@RequestMapping("/client")
//public class ClientRest extends BaseRest {
//
//    @Resource
//    private ClientService clientService;
//
//    @Resource
//    private RedisTemplate redisTemplate;
//
//    @Resource
//    private CustomNacosConfigService customNacosConfigService;
//
//    /**
//     * 添加客户端
//     * @param client
//     * @return
//     */
//    @RequestMapping(value = "/add", method = {RequestMethod.POST})
//    public ResultBody add(@RequestBody OpenApp client) {
//        Assert.notNull(client, "未获取到对象");
//        client.setAppId(UUIDUtils.getUUIDString());
////        client.setCreateTime(new Date());
//        this.validate(client);
//        //验证名称是否重复
//        OpenApp qClinet = new OpenApp();
//        qClinet.setAppName(client.getAppName());
//        long count = clientService.count(qClinet);
//        Assert.isTrue(count <= 0, "客户端名称已存在，不能重复");
//        //保存
//        clientService.save(client);
//        customNacosConfigService.publishClientNacosConfig(client.getAppId());
//        return ResultBody.ok();
//    }
//
//    /**
//     * 删除客户端
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResultBody delete(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
//        OpenApp dbClient = clientService.findById(id);
//        Assert.notNull(dbClient, "未获取到对象");
//        clientService.delete(dbClient);
//        customNacosConfigService.publishClientNacosConfig(id);
//        return ResultBody.ok();
//    }
//
//    /**
//     * 更新客户端
//     * @param client
//     * @return
//     */
//    @RequestMapping(value = "/update", method = {RequestMethod.POST})
//    public ResultBody update(@RequestBody OpenApp client) {
//        Assert.notNull(client, "未获取到对象");
//        Assert.isTrue(StringUtils.isNotBlank(client.getAppId()), "未获取到对象ID");
////        client.setUpdateTime(new Date());
//        this.validate(client);
//        clientService.update(client);
//        customNacosConfigService.publishClientNacosConfig(client.getAppId());
//        return ResultBody.ok();
//    }
//
//    /**
//     * 查询客户端
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResultBody findById(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
//        return new ResultBody(clientService.findById(id));
//    }
//
//    /**
//     * 分页查询客户端
//     * @param clientReq
//     * @return
//     */
//    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResultBody pageList(@RequestBody ClientReq clientReq) {
//        OpenApp client = new OpenApp();
//        Integer reqCurrentPage = null;
//        Integer reqPageSize = null;
//        if (clientReq != null) {
//            reqCurrentPage = clientReq.getCurrentPage();
//            reqPageSize = clientReq.getPageSize();
//            BeanUtils.copyProperties(clientReq, client);
//            if (StringUtils.isBlank(client.getAppName())) {
//                client.setAppName(null);
//            }
//            if (StringUtils.isBlank(client.getIp())) {
//                client.setIp(null);
//            }
//            if (StringUtils.isBlank(client.getGroupCode())) {
//                client.setGroupCode(null);
//            }
////            if (StringUtils.isBlank(client.getStatus())) {
////                client.setStatus(null);
////            }
//        }
//        int currentPage = getCurrentPage(reqCurrentPage);
//        int pageSize = getPageSize(reqPageSize);
//        return new ResultBody(clientService.pageList(client, currentPage, pageSize));
//    }
//
//    /**
//     * 设置客户端状态为启用
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/start", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResultBody start(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
//        OpenApp dbClient = clientService.findById(id);
//        dbClient.setStatus(Integer.valueOf(Constants.YES));
//        clientService.update(dbClient);
//        customNacosConfigService.publishClientNacosConfig(id);
//        return ResultBody.ok();
//    }
//
//    /**
//     * 设置客户端状态为禁用
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/stop", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResultBody stop(@RequestParam String id) {
//        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
//        OpenApp dbClient = clientService.findById(id);
//        dbClient.setStatus(Integer.valueOf(Constants.NO));
//        clientService.update(dbClient);
//        customNacosConfigService.publishClientNacosConfig(id);
//        return ResultBody.ok();
//    }
//
//}
