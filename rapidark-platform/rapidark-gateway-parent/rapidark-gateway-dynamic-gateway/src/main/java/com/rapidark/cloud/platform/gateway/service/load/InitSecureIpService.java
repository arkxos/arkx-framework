package com.rapidark.cloud.platform.gateway.service.load;

import com.rapidark.cloud.platform.gateway.framework.entity.SecureIp;
import com.rapidark.cloud.platform.gateway.framework.service.SecureIpService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.cache.IpListCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description  初始化网关路IP名单配置
 * @Author JL
 * @Date 2021/09/22
 * @Version V1.0
 */
@Slf4j
@Service
public class InitSecureIpService {

    @Resource
    private SecureIpService secureIpService;

    /**
     * 第一次初始化加载
     */
    @PostConstruct
    public void initLoadSecureIp(){
        SecureIp secureIp = new SecureIp();
//        secureIp.setStatus(Constants.YES);
        List<SecureIp> list = secureIpService.findAll(secureIp);
        IpListCache.clear();
        int size = 0;
        if (!CollectionUtils.isEmpty(list)){
            size = list.size();
            list.forEach(s -> IpListCache.put(s.getIp(), s.getStatus().equals(Constants.YES)));
        }
        log.info("初始化加载IP配置共{}条", size);
    }

}
