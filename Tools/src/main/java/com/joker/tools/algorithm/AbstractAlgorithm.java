package com.joker.tools.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 算法基类, 用于包装函数;
 *
 * @author: Joker
 * @date: Created in 2020/10/22 22:08
 * @version: 1.0
 */
public abstract class AbstractAlgorithm {
    protected Logger logger = LoggerFactory.getLogger("Algorithm");

    protected void actionMonitor() {
        long startTime = System.nanoTime();
        doAction();
        logger.info("cost time:{}", System.nanoTime() - startTime);
    }

    protected abstract void doAction();

    protected <T> void swap(T[] arr, int index1, int index2) {
        T t = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = t;
    }
}
