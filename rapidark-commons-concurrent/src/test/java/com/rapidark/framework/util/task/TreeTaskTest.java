package com.rapidark.framework.util.task;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeTaskTest {

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
    public void testTreeTask() {
        List<String> files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            files.add("file" + i+1);
        }

        AnalyzeFilesTask analyzeFilesTask = new AnalyzeFilesTask(files);
        taskEngine.commit(analyzeFilesTask);

        assertEquals(1, taskEngine.getRunningTasks().size());
        assertEquals(TaskStatus.RUNNING, taskEngine.getRunningTasks().get(0).getStatus());

        analyzeFilesTask.await();

        assertEquals(TaskStatus.SUCCESS, analyzeFilesTask.getStatus());
    }

}
