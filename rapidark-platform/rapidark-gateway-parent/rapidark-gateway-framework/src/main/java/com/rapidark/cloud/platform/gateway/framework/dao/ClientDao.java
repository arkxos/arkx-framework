package com.rapidark.cloud.platform.gateway.framework.dao;

import com.rapidark.cloud.platform.gateway.framework.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description 客户端Dao数据层操作接口
 * @Author JL
 * @Date 2020/05/15
 * @Version V1.0
 */
public interface ClientDao extends JpaRepository<Client, String> {

}
