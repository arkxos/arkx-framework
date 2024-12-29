package com.arkxos.framework.data.jpa.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;

/**
 * 转换器扩展.
 *
 * @author stormning on 16/6/16.
 */
public interface ConverterEx<S, D, ID> extends Converter<S, D> {

    List<D> convert(List<S> source);

    Map<ID, D> convert(Map<ID, S> source);

    Page<D> convert(Page<S> source);

    D get(ID id);

    Map<ID,D> mget(Collection<ID> ids);

    List<ID> getIds(List<S> source);
}
