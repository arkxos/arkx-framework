package io.arkx.framework.data.mybatis.pro.sample.springboot.misc;

import static io.arkx.framework.data.mybatis.pro.base.util.ClassPathUtil.resolvePackage;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Sets;

/**
 * 描述：通配符路径解析测试
 *
 * @author w.dehi.2022-02-28
 */
class ClassPathUtilTest {
    @Test
    void resolvePackageTest() {
        String path = "io.arkx.framework.data.mybatis.pro.sample.springboot.*.mapper";
        String a = "io.arkx.framework.data.mybatis.pro.sample.springboot.a.mapper";
        String b = "io.arkx.framework.data.mybatis.pro.sample.springboot.b.mapper";
        String[] pkg = resolvePackage(path);
        HashSet<String> result = Sets.newHashSet(pkg);
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));

        String basePath = "io.arkx.framework.data.mybatis.pro.sample.springboot.c";
        String[] pkgs = resolvePackage(basePath);
        if (!ObjectUtils.isEmpty(pkgs)) {
            for (String p : pkgs) {
                System.err.println(p);
            }
        }
    }
}
