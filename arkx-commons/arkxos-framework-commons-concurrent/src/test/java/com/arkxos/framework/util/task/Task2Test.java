package com.arkxos.framework.util.task;

import java.util.Arrays;
import java.util.List;
import java.util.function.LongFunction;

public class Task2Test {

    public static void main(String[] args) {
        filesProducer(index->{
            List<String> files = Arrays.asList("file1", "file2", "file3");
//            return files.get(index);
            return index;
        });

        for (int i = 0; i < 20; i++) {
            
        }
    }

    public static <T> void filesProducer(LongFunction<T> producerDataPacketFunction) {

    }

}
