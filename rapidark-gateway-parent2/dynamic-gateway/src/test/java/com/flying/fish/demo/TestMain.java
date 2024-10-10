package com.flying.fish.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/06/02
 * @Version V1.0
 */
@Slf4j
public class TestMain {

    @Test
    public void test(){
        log.trace("========== trace ============");
        log.debug("========== debug ============");
        log.info("========== info ============");
        log.error("========== error ============");
    }

}
