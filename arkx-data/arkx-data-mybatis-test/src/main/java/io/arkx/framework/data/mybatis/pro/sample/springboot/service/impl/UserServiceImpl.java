package io.arkx.framework.data.mybatis.pro.sample.springboot.service.impl;

import org.springframework.stereotype.Service;

import io.arkx.framework.data.mybatis.pro.sample.springboot.domain.User;
import io.arkx.framework.data.mybatis.pro.sample.springboot.service.UserService;
import io.arkx.framework.data.mybatis.pro.service.service.AbstractServiceImpl;

/**
 * @author w.dehai
 */
@Service
public class UserServiceImpl extends AbstractServiceImpl<User, Long> implements UserService {

}
