package io.arkx.framework.util.task;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {

    private static TaskEngine taskEngine;

    @BeforeAll
    public static void before() {
        taskEngine = new TaskEngine.Builder().build();
    }

    @AfterAll
    public static void after() {
        taskEngine.shutdown();
    }

    @Test
    public void testTask() {
        Task task = taskEngine.buildTask(ctx -> {
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException ignore) {
                }
                ctx.onSuccess("success");
                ctx.onError("error", null);
            })
            .progress((task1, progress) -> {
                System.out.println("progress: " + progress + ", " + task1);
            })
            .end((ctx, error) -> {
                System.out.println("execute task finished");
                assertEquals("success", ctx.getResult().getString(0));
                assertEquals(TaskStatus.SUCCESS, ctx.getStatus());
            })
            .build();

        taskEngine.commit(task);

        assertEquals(1, taskEngine.getRunningTasks().size());
        assertEquals(TaskStatus.RUNNING, taskEngine.getRunningTasks().get(0).getStatus());

        task.await();

        assertEquals(TaskStatus.SUCCESS, task.getStatus());
    }

    @Test
    public void testResultTask() {
        ResultTask<String> task = taskEngine.buildResultTask(ctx -> {
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException ignore) {
            }
            return "success";
        }).build();

        taskEngine.commit(task);

        task.await();
        assertEquals("success", task.get());
        assertEquals(TaskStatus.SUCCESS, task.getStatus());

        task = taskEngine.commit(ctx -> {
            throw new Exception();
        });
        try {
            task.get();
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testTaskGroup() {
        TaskGroup group = taskEngine.prepareGroup();
        for (int i = 0; i < 1000; i++) {
            group.go(group.buildItem(ctx -> {
                ctx.group().incrementCounter();
                ctx.group().addData("a");
            }).build());
        }
        group.whenComplete(() -> {
            assertEquals(1000, group.getCounter());
            assertEquals(1000, group.getData().size());
            assertEquals("a", group.getData().getString(0));
            assertEquals("a", group.getData().getString(999));
        });
        group.await();
        assertEquals(1000, group.getCounter());
        assertEquals(1000, group.getData().size());
        assertEquals("a", group.getData().getString(0));
        assertEquals("a", group.getData().getString(999));
    }

    @Test
    public void testTaskTimeout() {
        Task task = taskEngine.buildTask(ctx -> {
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException ignore) {
            }
        }).build();
        taskEngine.commit(task);
        try {
            task.await(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testTaskCancel() {
        Task task = taskEngine.buildTask(ctx -> {
        }).build();
        task.cancel(false);
        assertEquals(TaskStatus.CANCEL, task.getStatus());

        task = taskEngine.buildTask(ctx -> {
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException ignore) {
            }
        }).build();
        taskEngine.commit(task);
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException ignore) {
        }
        task.cancel(true);
        assertEquals(TaskStatus.CANCEL, task.getStatus());
    }

}
