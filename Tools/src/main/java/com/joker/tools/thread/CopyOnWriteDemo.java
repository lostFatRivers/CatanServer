package com.joker.tools.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * CopyOnWriteArrayList 多线程下测试;
 *
 * @author: Joker
 * @date: Created in 2020/10/24 14:19
 * @version: 1.0
 */
public class CopyOnWriteDemo {
    private static final Logger logger = LoggerFactory.getLogger("Join");

    public static void main(String[] args) throws Exception {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();

        CountDownLatch latch = new CountDownLatch(1);

        ExecutorService exec = Executors.newFixedThreadPool(12);
        for (int i = 0; i < 10000; i++) {
            final int curId = i;
            exec.execute(() -> {
                try {
                    latch.await();
                    if (list.size() < 500) {
                        list.add(curId);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        logger.info("will start after 3 seconds");
        TimeUnit.SECONDS.sleep(3);

        logger.info("start");
        latch.countDown();

        TimeUnit.SECONDS.sleep(3);
        logger.info("list size:{}, content:{}", list.size(), list);
    }
}
