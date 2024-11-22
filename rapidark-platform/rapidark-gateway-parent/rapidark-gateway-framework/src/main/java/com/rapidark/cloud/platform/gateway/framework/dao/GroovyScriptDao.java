package com.rapidark.cloud.platform.gateway.framework.dao;

import com.rapidark.cloud.platform.gateway.framework.entity.GroovyScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author JL
 * @Date 2022/2/21
 * @Version V1.0
 */
public interface GroovyScriptDao extends JpaRepository<GroovyScript, Long> {

    /**
     * 删除指定routeId下所有groovy脚本记录
     * @param routeId
     */
    void deleteByRouteId(String routeId);

    /**
     * 获取routeId下正在使用的groovy脚本数量
     * @return
     */
    @Query("select g.routeId as routeId,count(g.id) as num from GroovyScript g where g.status='0' group by g.routeId")
    List<Map> findRouteScriptNum();

    /**
     * 获取routeId下最大orderNum
     * @param routeId
     * @param event
     * @return
     */
    @Query("select max(orderNum) from GroovyScript where routeId=?1 and event=?2")
    Integer findMaxOrderNum(String routeId, String event);


    /**
     * 获取routeId下status为0（启用）的id集合，并按id顺序排序
     * @param routeId
     * @return
     */
    @Query("select id from GroovyScript where routeId=?1 and status='0' order by orderNum asc")
    List<Long> findIdList(String routeId);

}
