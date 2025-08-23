package io.arkx.framework.data.mybatis.pro.sample.springboot.service.impl;

import io.arkx.framework.data.mybatis.pro.sample.springboot.domain.Dict;
import io.arkx.framework.data.mybatis.pro.sample.springboot.service.DictService;
import io.arkx.framework.data.mybatis.pro.service.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * @author w.dehai
 *
 */
@Service
public class DictServiceImpl extends AbstractServiceImpl<Dict, Long> implements DictService {}
