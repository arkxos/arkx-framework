package com.rapidark.framework.commons.queueexecutor;

import java.util.Arrays;

import com.rapidark.framework.commons.queueexecutor.processor.ElementProcessor;

/**
 * @author Darkness
 * @date 2017年6月15日 下午3:24:42
 * @version 1.0
 * @since 1.0
 */
public class MultiThreadedQueueExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        final MultiThreadedQueueExecutor<String> executor =
                new MultiThreadedQueueExecutor<String>("test", "测试", new ElementProcessor<String>() {

                    @Override
                    public void process(Element<String> element, MultiThreadedQueueExecutor<String> executor) {
                        System.out.println("execute " + element.getSource());
                        executor.addElement(new Element<String>("1", "page3"));// [测试]去重
                        executor.addElement(new Element<String>("2", "page2"));// [测试]添加新的元素
                    }

                })
                        .thread(2)
                        .addElements(Arrays.asList(new Element<String>("1", "page1")));
        executor.onFinish(()-> {
            System.out.println("========finished, executor is shutdown:" + executor.isShutdown());
        });

        executor.startAsync();// 异步执行，不阻塞
        System.out.println("start async");

        while(true) {
            if(executor.isShutdown()) {
                executor.addElement(new Element<String>("4", "addElementOnFinished"));// [测试]去重
                executor.start();
                System.out.println("start async finish");
                break;
            }
        }
    }
}
