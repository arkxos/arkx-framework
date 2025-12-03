package io.arkx.framework.data.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.arkx.framework.data.mybatis.model.EntityMap;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SuperMapper<T> extends BaseMapper<T> {

    IPage<T> pageList(Page<T> page, @Param("ew") Wrapper<?> wrapper);

    List<EntityMap> getEntityMap(@Param("ew") Wrapper<?> wrapper);

}
