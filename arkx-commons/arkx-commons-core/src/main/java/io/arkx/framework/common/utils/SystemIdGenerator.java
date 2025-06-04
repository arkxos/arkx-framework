package io.arkx.framework.common.utils;

import org.springframework.stereotype.Service;

import cn.hutool.core.util.IdUtil;

@Service
public class SystemIdGenerator {

    public long generate() {
        return IdUtil.getSnowflakeNextId();
    }

}
