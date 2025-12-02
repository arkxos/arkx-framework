package io.arkx.framework.data.mybatis.pro.core;

import java.util.List;

import io.arkx.framework.data.mybatis.pro.sdk.Mapper;

/**
 * @author w.dehai
 */
public interface DemoMapper extends Mapper<Demo, Long> {

	Demo findByNameAndPassword(String name, String password);

	List<Demo> findByName(String name);

	Demo findById(Long id);

}
