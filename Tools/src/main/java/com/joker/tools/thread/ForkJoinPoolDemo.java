package com.joker.tools.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fork Join 的使用;
 *
 * @author: Joker
 * @date: Created in 2020/10/22 10:06
 * @version: 1.0
 */
public class ForkJoinPoolDemo {
    private static final Logger logger = LoggerFactory.getLogger("Join");

    public static void main(String[] args) {
//        waitTest();
        awaitTest();
    }

    public static void joinTest() {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(() -> {
            Thread thread = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("join thread run");
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("out thread run");
        });
    }

    public static void waitTest() {
        ExecutorService exec = Executors.newCachedThreadPool();
        WaitTest test = new WaitTest();
        exec.execute(test::after);
        exec.execute(test::before);
    }

    private static class WaitTest {
        public synchronized void before() {
            logger.info("before");
            notifyAll();
        }

        public synchronized void after() {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("after");
        }
    }

    private static void awaitTest() {
        ExecutorService exec = Executors.newCachedThreadPool();
        AwaitSignalTest test = new AwaitSignalTest();
        exec.execute(test::after);
        exec.execute(test::before);
    }

    private static class AwaitSignalTest {
        private Lock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();

        public void before() {
            lock.lock();
            try {
                logger.info("before");
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void after() {
            lock.lock();
            try {

                condition.await();
                logger.info("after");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
