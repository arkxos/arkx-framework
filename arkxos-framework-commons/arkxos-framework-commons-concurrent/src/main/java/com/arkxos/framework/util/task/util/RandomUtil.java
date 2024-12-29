package com.arkxos.framework.util.task.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    public static int randomInt(int max) {
        return getRandom().nextInt(max + 1);
    }

    public static int randomInt(int min, int max) {
        return getRandom().nextInt(min, max + 1);
    }

}
