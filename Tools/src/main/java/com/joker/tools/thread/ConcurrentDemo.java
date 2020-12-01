package com.joker.tools.thread;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ConcurrentHashMap 遍历测试;
 *
 * @author: Joker
 * @date: Created in 2020/11/16 22:13
 * @version: 1.0
 */
public class ConcurrentDemo {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<Integer, String> hashMap = new ConcurrentHashMap<>();
        hashMap.put(1, "1");
        hashMap.put(2, "2");
        hashMap.put(3, "3");
        hashMap.put(4, "4");
        hashMap.put(5, "5");

        new Thread(() -> {
            hashMap.put(6, "6");
            hashMap.put(7, "7");
            hashMap.put(8, "8");
            hashMap.put(9, "9");
            hashMap.put(10, "10");
            hashMap.put(11, "11");
            hashMap.put(12, "12");
            hashMap.put(13, "13");
        }).start();

        for (Map.Entry<Integer, String> next : hashMap.entrySet()) {
            System.out.println("key:" + next.getKey() + " value:" + next.getValue());
        }

    }
}
