package com.arkxos.common.exception.file;

import com.arkxos.common.exception.BaseException;

/**
 * 文件信息异常类
 * 
 * @author Darkness
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
