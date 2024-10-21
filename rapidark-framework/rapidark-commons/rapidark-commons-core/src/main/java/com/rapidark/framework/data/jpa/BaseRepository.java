package com.rapidark.framework.data.jpa;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.rapidark.framework.commons.collection.DataTable;

/**
 * <p>抽象DAO层基类 提供一些简便方法<br/>
 * <p/>
 * 想要使用该接口需要在spring配置文件的jpa:repositories中添加
 * factory-class="org.em.core.repository.support.GenericJpaRepositoryFactoryBean"
 * <p/>
 * <p>泛型 ： M 表示实体类型；ID表示主键类型
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/7
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> , JpaSpecificationExecutor<T>{

	boolean support(String modelType);
	
	Map<ID, T> mget(Collection<ID> ids);

    //for cache
    Map<ID, T> mgetOneByOne(Collection<ID> ids);

    //for cache
    List<T> findAllOneByOne(Collection<ID> ids);

    void toggleStatus(ID id);

    @SuppressWarnings("unchecked") 
    void fakeDelete(ID... ids);

	DataTable queryDataTable(String sql, Object... params);
	
	List<Map<String, Object>> queryMap(String sql, Object... params);
}
