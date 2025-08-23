package io.arkx.framework.data.mybatis.pro.sample.springboot.service.impl;

import io.arkx.framework.data.mybatis.pro.sample.springboot.domain.User;
import io.arkx.framework.data.mybatis.pro.sample.springboot.service.UserService;
import io.arkx.framework.data.mybatis.pro.service.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author w.dehai
 */
@Service
public class UserServiceImpl extends AbstractServiceImpl<User, Long> implements UserService {
}
