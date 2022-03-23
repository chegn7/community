package com.c.community;

import com.c.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.*;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTest.class);

    // JDK 线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    @Autowired
    AlphaService alphaService;

    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("hello ScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 1000, 2000, TimeUnit.MILLISECONDS);
        sleep(20000);
    }

    // spring 普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    // spring 定时任务线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("hello ThreadPoolTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(1000);
    }

    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("hello ThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 1000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(2000);
    }

    @Test
    public void testSimpleAsync() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }
        sleep(1000);
    }




    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
