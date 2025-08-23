package io.arkx.framework.data.mybatis.pro.core;

import io.arkx.framework.data.mybatis.pro.sdk.Mapper;

import java.util.List;

/**
 * @author w.dehai
 */
public interface DemoMapper extends Mapper<Demo, Long> {

    Demo findByNameAndPassword(String name, String password);

    List<Demo> findByName(String name);

    Demo findById(Long id);

}
