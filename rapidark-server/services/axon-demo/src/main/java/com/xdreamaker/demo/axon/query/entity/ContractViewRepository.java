package com.xdreamaker.demo.axon.query.entity;

import com.xdreamaker.demo.axon.query.entity.ContractView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author darkness
 * @date 2021/6/26 22:54
 * @version 1.0
 */
@Repository
public interface ContractViewRepository extends JpaRepository<ContractView, Long> {

}
