package io.arkx.commons.exception.user;

import io.arkx.commons.exception.BaseException;

/**
 * 用户信息异常类
 * 
 * @author Darkness
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
