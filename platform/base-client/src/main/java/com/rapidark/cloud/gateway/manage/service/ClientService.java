//package com.rapidark.cloud.gateway.formwork.service;
//
//import com.rapidark.cloud.base.client.model.OpenClient;
//import com.rapidark.cloud.gateway.manage.repository.OpenClientRepository;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.domain.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import com.rapidark.cloud.gateway.formwork.base.BaseService;
//import com.rapidark.cloud.gateway.formwork.entity.ClientServerRegister;
//import com.rapidark.cloud.gateway.formwork.util.PageData;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @Description 客户端管理业务类
// * @Author jianglong
// * @Date 2020/05/16
// * @Version V1.0
// */
//@Service
//public class ClientService extends BaseService<OpenClient, String, OpenClientRepository> {
//
//    @Resource
//    private ClientServerRegisterService regServerService;
//
//    /**
//     * 删除客户端
//     * @param client
//     */
////    @Override
////    public void delete(OpenClient client){
////        ClientServerRegister regServer = new ClientServerRegister();
////        regServer.setClientId(client.getAppId());
////        //查找是否有注册到其它网关服务上，如有一并删除
////        List<ClientServerRegister> regServerList = regServerService.findAll(regServer);
////        if (!CollectionUtils.isEmpty(regServerList)){
////            regServerService.delete(regServer);
////        }
////        super.delete(client);
////    }
//
//    /**
//     * 分页查询（支持模糊查询）
//     * @param client
//     * @param currentPage
//     * @param pageSize
//     * @return
//     */
//    @Override
//    public PageData<OpenClient> pageList(OpenClient client, int currentPage, int pageSize){
//        //构造条件查询方式
//        ExampleMatcher matcher = ExampleMatcher.matching();
//        if (StringUtils.isNotBlank(client.getAppName())) {
//            //支持模糊条件查询
//            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
//        }
//        return this.pageList(client, matcher, currentPage, pageSize);
//    }
//}
