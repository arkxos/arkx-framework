package io.arkx.framework.data.mybatis.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.context.ContextLoader;

import io.arkx.framework.data.mybatis.mapper.SuperMapper;
import io.arkx.framework.data.mybatis.model.EntityMap;
import io.arkx.framework.data.mybatis.model.PageParams;
import io.arkx.framework.data.mybatis.query.CriteriaQuery;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @author: zyf
 * @date: 2018/12/24 12:49
 * @desc: 父类service
 */
public abstract class BaseServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> {

    @Autowired
    public ApplicationContext applicationContext;

    // @Resource
    // public SqlSessionTemplate sqlSession;

    public void pushEvent(ApplicationEvent applicationEvent) {
        ContextLoader.getCurrentWebApplicationContext().publishEvent(applicationEvent);
    }

    public IPage pageList(CriteriaQuery<?> wrapper) {
        PageParams page = wrapper.getPagerInfo();
        IPage list = this.baseMapper.pageList(page, wrapper);
        EntityMap.setEnumConvertInterceptor(null);
        return list;
    }

    public EntityMap getEntityMap(CriteriaQuery<?> cq) {
        List<EntityMap> maps = baseMapper.getEntityMap(cq);
        if (ObjectUtils.isEmpty(maps)) {
            // 避免空对象输出""
            return new EntityMap();
        }
        return maps.get(0);
    }

    public List<EntityMap> listEntityMaps(CriteriaQuery<?> cq) {
        List<EntityMap> map = baseMapper.getEntityMap(cq);
        return map;
    }

    /**
     * 自定义sql查询List<EntityMap>
     */
    public List<EntityMap> selectListEntityMap(String statement, EntityMap map) {
        if (ObjectUtils.isEmpty(map)) {
            return null;
        }
        // return sqlSession.selectList(getMapperName() + statement, map);
        return null;
    }

    /**
     * 自定义sql查询List<EntityMap>
     */
    public List<EntityMap> selectListEntityMap(EntityMap map) {
        if (ObjectUtils.isEmpty(map)) {
            return null;
        }
        // return sqlSession.selectList(getMapperName() + "selectListEntityMapByMap",
        // map);
        return null;
    }

    /**
     * 自定义sql查询List<EntityMap>
     */
    public List<EntityMap> selectListEntityMap(String statement, @Param("ew") CriteriaQuery<?> cq) {

        // return sqlSession.selectList(getMapperName() + statement, cq);
        return null;
    }

    /**
     * 自定义sql查询List<EntityMap>
     */
    public List<EntityMap> selectListEntityMap(@Param("ew") CriteriaQuery<?> cq) {
        // return sqlSession.selectList(getMapperName() + "selectListEntityMapByCq",
        // cq);
        return null;
    }

    /**
     * 获取mapperName
     */
    public String getMapperName() {
        String mapperName = "";
        Class cl = baseMapper.getClass();
        Class<?>[] interfaces = cl.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            mapperName = anInterface.getName();
        }
        return mapperName + ".";
    }

    public List<T> selectAll() {
        return this.baseMapper.selectList(null);
    }

}
