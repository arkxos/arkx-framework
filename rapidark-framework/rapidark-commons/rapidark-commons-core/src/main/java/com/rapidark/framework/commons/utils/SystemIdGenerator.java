package com.rapidark.framework.commons.utils;

import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;

@Service
public class SystemIdGenerator {

    public long generate() {
        return IdUtil.getSnowflakeNextId();
    }

}
