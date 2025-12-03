package org.ark.framework.infrastructure.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.ark.framework.infrastructure.ioc.IocManager;
import org.junit.jupiter.api.Test;

/**
 * @author Darkness
 * @date 2012-10-13 下午7:07:06
 * @version V1.0
 */
public class IocTest {

    /**
     * 测试ioc容器初始化bean正常
     *
     * @author Darkness
     * @date 2012-10-27 上午11:34:28
     * @version V1.0
     */
    @Test
    public void initBeans() {
        String personRepositoryClassName = IocManager.getBeanClass("personRepository");
        assertEquals(personRepositoryClassName, "org.ark.framework.infrastructure.repositories.PersonRepository");
    }

}
