package com.rapidark.cloud.platform.gateway.framework.service;

import com.rapidark.cloud.platform.gateway.framework.base.BaseService;
import com.rapidark.cloud.platform.gateway.framework.repository.LoadServerRepository;
import com.rapidark.cloud.platform.gateway.framework.entity.Balanced;
import com.rapidark.cloud.platform.gateway.framework.entity.LoadServer;
import com.rapidark.cloud.platform.gateway.framework.entity.RouteConfig;
import com.rapidark.cloud.platform.gateway.framework.util.PageResult;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * @Description 负载服务业务实现类
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@Service
public class LoadServerService extends BaseService<LoadServer, Long, LoadServerRepository> {

    @Resource
    private LoadServerRepository loadServerRepository;

    /**
     * 查询当前负载网关已加配置路由服务
     * @param balancedId
     * @return
     */
    @Transactional(readOnly = true)
    public List loadServerList(String balancedId){
        return loadServerRepository.queryLoadServerList(balancedId);
    }

    /**
     * 查询所有路由服务
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult notLoadServerPageList(int currentPage, int pageSize){
        String sql ="SELECT r.id,r.name,r.groupCode,r.uri,r.path,r.method FROM route r WHERE r.status='0' ";
        return pageNativeQuery(sql, null, currentPage, pageSize);
    }

    /**
     *  删除指定负载下所有的路由服务
     * @param balancedId
     */
    public void deleteAllByBalancedId(String balancedId){
        loadServerRepository.deleteAllByBalancedId(balancedId);
    }

    /**
     * 查询指定负载下所有路由服务
     * @param balancedId
     * @return
     */
    public List<LoadServer> queryByBalancedId(String balancedId){
        return loadServerRepository.queryByBalancedId(balancedId);
    }

    /**
     * 查询指定路由关联的负载服务
     * @param routeId
     * @return
     */
    public List<LoadServer> queryByRouteId(String routeId){
        return loadServerRepository.queryByRouteId(routeId);
    }

    /**
     * 保存指定负载下的所有路由服务
     * @param serverList
     */
    public void updates(String balancedId, List<LoadServer> serverList){
        List<LoadServer> dbServerList = this.queryByBalancedId(balancedId);
        Map<Long, Integer> dbIdMap = new HashMap<>();
        for (LoadServer loadServer : serverList){
            //新增
            if (loadServer.getId() == null || loadServer.getId() <= 0){
                loadServer.setBalancedId(balancedId);
                loadServer.setCreateTime(new Date());
                this.save(loadServer);
            }else {
                //记录未变更的ID
                dbIdMap.put(loadServer.getId(), loadServer.getWeight());
            }
        }

        if (!CollectionUtils.isEmpty(dbServerList)){
            //idList中不存在，则需要删除数据库中，已经被前端取消绑定的服务
            for (LoadServer loadServer : dbServerList){
                if (dbIdMap.get(loadServer.getId()) == null){
                    this.deleteById(loadServer.getId());
                }else {
                    //比较weight值是否有改变
                    Integer weight = dbIdMap.get(loadServer.getId());
                    if (loadServer.getWeight().intValue() != weight.intValue()){
                        loadServer.setWeight(weight);
                        this.update(loadServer);
                    }
                }
            }
        }
    }

    /**
     * 设置负载均衡网关路由配置
     * @param balanced
     * @param loadServer
     * @param routeConfig
     */
    public void setBalancedRoute(Balanced balanced, LoadServer loadServer, RouteConfig routeConfig){
        //获取route，改变参数，构造一个新route对象
        String routeId = this.setBalancedRouteId(balanced.getId(), routeConfig.getId());
        routeConfig.setId(routeId);
        //设置断言路径
        routeConfig.setPath(RouteConstants.PARENT_PATH + balanced.getLoadUri());
        //设置负载参数,查找服务对应的路由服务
        String weightName = this.setBalancedWeightName(balanced.getId());
        routeConfig.setWeightName(weightName);
        //设置负载权重值
        routeConfig.setWeight(loadServer.getWeight());
        //默认负载的断言路径截取级别为1
        routeConfig.setStripPrefix(1);
    }

    /**
     * 设置负载均衡网关路由的WeightName
     * @param balancedId
     * @return
     */
    public String setBalancedWeightName(String balancedId){
        return RouteConstants.BALANCED + "-" + balancedId.replaceAll("-","");
    }

    /**
     * 设置负载均衡网关路由ID
     * @param balancedId
     * @param routeId
     * @return
     */
    public String setBalancedRouteId(String balancedId, String routeId){
        return setBalancedWeightName(balancedId) + "-" + routeId;
    }

}
