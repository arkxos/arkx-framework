package com.flying.fish.formwork.dao;

import com.flying.fish.formwork.entity.GroovyScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
