package com.flying.fish.gateway.service.load;

import com.flying.fish.formwork.service.RegServerService;
import com.flying.fish.gateway.cache.RegServerCache;
import com.flying.fish.gateway.vo.GatewayRegServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Description  初始化网关路由客户端配置
 * @Author JL
 * @Date 2021/09/22
 * @Version V1.0
 */
@Slf4j
@Service
public class InitClientService {

    @Resource
    private RegServerService regServerService;

    /**
     * 第一次初始化加载
     */
    @PostConstruct
    public void initLoadClient(){
        List list = regServerService.allRegClientList();
        RegServerCache.clear();
        int size = 0;
        if (!CollectionUtils.isEmpty(list)){
            size = list.size();
            Iterator iterator = list.iterator();
            String routeId ;
            String ip;
            String id;
            String token;
            String secretKey;
            Object [] object;
            List<GatewayRegServer> regServers = new ArrayList<>();
            while (iterator.hasNext()){
                object = (Object[]) iterator.next();
                routeId = String.valueOf(object[0]);
                //添加网关路由注册的客户端ID
                id = String.valueOf(object[1]);
                //添加网关路由注册的客户端IP
                ip = String.valueOf(object[2]);
                //添加网关路由注册的客户端token
                token = String.valueOf(object[3]);
                //添加网关路由注册的客户端密钥（用于jwtToken解析）
                secretKey = String.valueOf(object[4]);
                //添加到缓存中
                GatewayRegServer regServer = new GatewayRegServer();
                regServer.setClientId(id);
                regServer.setIp(ip);
                regServer.setToken(token);
                regServer.setSecretKey(secretKey);
                regServers.add(regServer);
                RegServerCache.put(routeId, regServers);
            }
        }
        log.info("初始化加载客户端配置共{}条", size);
    }

}
