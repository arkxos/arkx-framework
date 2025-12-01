package io.arkx.framework.util.task;

import io.arkx.framework.util.task.callback.Progress;
import io.arkx.framework.util.task.callback.TaskCompletedListener;
import io.arkx.framework.util.task.callback.TaskListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TreeTaskTest {

    private static TaskEngine taskEngine;

    @BeforeAll
    public static void before() {
        taskEngine = new TaskEngine.Builder()
            .windowsScheduledExecutor()
            .corePoolSize(4)
            .maxPoolSize(4)
            .build();
        taskEngine.start();
    }

    @AfterAll
    public static void after() {
//        taskEngine.shutdown();
    }

    @Test
    public void testTreeTask() throws InterruptedException {
        List<String> files = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            files.add("file" + i);
        }

        AnalyzeFilesTask analyzeFilesTask = new AnalyzeFilesTask(files);
        taskEngine.commit(analyzeFilesTask);

//        assertEquals(1, taskEngine.getRunningTasks().size());
//        assertEquals(TaskStatus.RUNNING, taskEngine.getRunningTasks().get(0).getStatus());

        analyzeFilesTask.await();

        analyzeFilesTask.addListener(new TaskListener() {
            @Override
            public void onExecuteFinish(TaskContext ctx, Exception error) {
                System.out.println("内部执行完成");
                analyzeFilesTask.print();

                for (TreeTask child : analyzeFilesTask.getChildren()) {
                    child.setProgress(new Progress() {
                        @Override
                        public void call(Task task, double progress) {
                            System.out.println("====[" + progress + "]" + task.toString());
                        }
                    });
                }
            }
        });
        analyzeFilesTask.setProgress(new Progress() {
            @Override
            public void call(Task task, double progress) {
                System.out.println("total[" + progress + "]" + task.toString());
            }
        });
        analyzeFilesTask.addCompletedListener(new TaskCompletedListener() {

            @Override
            public void onCompleteFinish() {
                System.out.println("完全完成");
                analyzeFilesTask.print();
            }
        });

        while(!analyzeFilesTask.isFinished()) {
            Thread.sleep(1000);
//            analyzeFilesTask.print();
        }

        analyzeFilesTask.print();

//        assertEquals(TaskStatus.SUCCESS, analyzeFilesTask.getStatus());

    }

}
