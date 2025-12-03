package io.arkx.framework.data.mybatis.pro.sample.springboot.mapper;

import org.apache.ibatis.annotations.Select;

import io.arkx.framework.data.mybatis.pro.sample.springboot.domain.Dict;
import io.arkx.framework.data.mybatis.pro.service.mapper.BaseMapper;

/**
 * @author w.dehai
 *
 */
public interface DictMapper extends BaseMapper<Dict, Long> {

    @Select("select * from smart_dict where id = 1")
    Dict alias();

}
