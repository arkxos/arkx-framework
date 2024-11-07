package com.rapidark.framework.util.task.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    public static int randomInt(int limit) {
        return getRandom().nextInt(limit);
    }

}
