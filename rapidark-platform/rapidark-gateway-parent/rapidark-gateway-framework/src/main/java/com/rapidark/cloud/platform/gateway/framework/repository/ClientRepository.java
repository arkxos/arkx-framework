package com.rapidark.cloud.platform.gateway.framework.repository;

import com.rapidark.cloud.platform.gateway.framework.entity.Client;
import com.rapidark.framework.data.jpa.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description 客户端Dao数据层操作接口
 * @Author JL
 * @Date 2020/05/15
 * @Version V1.0
 */
public interface ClientRepository extends BaseRepository<Client, String> {

}
