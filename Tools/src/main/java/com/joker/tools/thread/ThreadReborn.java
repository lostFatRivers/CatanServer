package com.joker.tools.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 重新唤醒线程;
 *
 * @author: Joker
 * @date: Created in 2021/1/6 15:21
 * @version: 1.0
 */
public class ThreadReborn {
    private static final Logger logger = LoggerFactory.getLogger("Tread");

    public static void main(String[] args) {
        Thread thread = newThread();
        thread.start();

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception ignore) {
            }
            if (!thread.isAlive()) {
                logger.info("thread reborn.");
                thread = newThread();
                thread.start();
            }
        }
    }

    private static Thread newThread() {
        return new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                logger.info("tick thread:{}", i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ignore) {
                }
            }
            logger.info("thread run over.");
        });
    }
}
