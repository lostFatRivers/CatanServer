package com.joker.tools.jdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * 测试map;
 *
 * @author: Joker
 * @date: Created in 2021/1/8 14:26
 * @version: 1.0
 */
public class MapTest {
    private static final Logger logger = LoggerFactory.getLogger("Test");

    public static void main(String[] args) {
        logger.info("start");
        testQueue();
    }

    public static void testUnmodifiableMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "1");
        map.put(2, "2");

        Map<Integer, String> unmodifiableMap = Collections.unmodifiableMap(map);

        logger.info("map:{}, unmodifiableMap:{}", map, unmodifiableMap);

        map.put(3, "3");
        unmodifiableMap.put(3, "3");
        logger.info("map:{}, unmodifiableMap:{}", map, unmodifiableMap);
    }

    /** 测试一个url是否可访问 **/
    public static boolean testUrlWithTimeOut(String urlString, int timeOutMillSeconds) {
        URL url;
        URLConnection co = null;
        try {
            url = new URL(urlString);
            co = url.openConnection();
            co.setConnectTimeout(timeOutMillSeconds);
            co.connect();
            return true;
        } catch (Exception e1) {
            logger.info("url error.", e1);
            return false;
        } finally {
            try {
                // 结束连接
                if (co != null && co.getDoInput()) {
                    co.getInputStream().close();
                }
                if (co != null && co.getDoOutput()) {
                    co.getOutputStream().close();
                }
            } catch (Exception e2) {
                logger.info("close output stream error:{}", e2.getMessage());
            }
        }
    }

    public static void testList() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
//        list.add(4);
//        list.add(5);
//        list.add(6);
//        list.add(7);

        List<Integer> subList = list.subList(0, 4);
        System.out.println(subList.toString());
    }

    public static void testQueue() {
        PriorityQueue<Long> slowQueue = new PriorityQueue<>();
        for (long i = 8000L; i < 8010L; i++) {
            slowQueue.offer(i);
        }
        logger.info("origin queue:{}", slowQueue);

        while (slowQueue.size() > 50) {
            slowQueue.poll();
        }

        List<Long> slowList = new ArrayList<>(slowQueue);
        slowList.sort(Comparator.reverseOrder());
        int toIndex = Math.min(5, slowList.size());
        List<Long> subList = slowList.subList(0, toIndex);
        logger.info("transform list:{}", subList);
    }
}
