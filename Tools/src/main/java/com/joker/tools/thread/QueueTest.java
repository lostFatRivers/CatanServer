package com.joker.tools.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队列测试;
 *
 * @author: Joker
 * @date: Created in 2021/1/5 17:53
 * @version: 1.0
 */
public class QueueTest {
    private static final Logger logger = LoggerFactory.getLogger("Queue");

    private static final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        createProducer();
        createConsumer();
    }

    private static void createConsumer() {
        new Thread(() -> {
            while(true) {
                while (!queue.isEmpty()) {
                    int id = queue.remove();
                    logger.info("consume queue element:{}", id);
                }
            }
        }, "Queue-Consumer").start();
    }

    private static void createProducer() {
        final AtomicInteger counter = new AtomicInteger(0);
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                while(true) {
                    int element = counter.incrementAndGet();
                    queue.add(element);
                    logger.info("producer create element:{}", element);
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (Exception ignore) {}
                }
            }, "Queue-Producer-" + i).start();
        }
    }

}
