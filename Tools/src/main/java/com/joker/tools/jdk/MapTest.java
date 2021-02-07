package com.joker.tools.jdk;

import com.joker.tools.match.PlayerInfo;
import com.joker.tools.match.WeaponType;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        testMatch();
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

    private static void consumerTest() {
        Consumer<Integer> consumer1 = i -> logger.info("consumer1 int:{}", i);
        Consumer<String> consumer2 = s -> logger.info("consumer2 String:{}", s);
        BiConsumer<Integer, String> consumer3 = (k, v) -> logger.info("consumer3 key:{}, value:{}", k, v);

        BiConsumer<Integer, String> consumer4 = consumer3.andThen((k, v) -> {
            consumer1.accept(k);
            consumer2.accept(v);
        });

        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            consumer4.accept(i, i + "_str");
            logger.info("cost time:{}", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    private static void jsonTest() {
        PlayerInfo info = new PlayerInfo(1, 1, WeaponType.BIG_SWORD);
        TestBean bean = new TestBean();
        String encode = Json.encode(bean);
        logger.info("encode json:{}", encode);
        JsonObject jsonObject = JsonObject.mapFrom(bean);
        logger.info("player json:{}", jsonObject);
    }

    private static void consumerTest2() {
        Consumer<String> consumer = s -> logger.info("consumer2 String:{}", s);

        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            consumer.accept(i + "");
            logger.info("1 cost time:{}", (System.nanoTime() - start) / 1000000.0f);

            start = System.nanoTime();
            printStr(i + "");
            logger.info("2 cost time:{}", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    private static void printStr(String str) {
        logger.info("printStr String:{}", str);
    }

    private static void strHashCode() {
        String str = "IfElse";
        logger.info("相同字符串的 hashCode 是固定的, code:{}", str.hashCode());
    }

    private static List<String> match(String contentStr, String shieldStr) {
        char[] contentChars = contentStr.toCharArray();
        char[] shieldChars = shieldStr.toCharArray();

        List<String> matchList = new ArrayList<>();

        // 深度, 屏蔽词中间穿插无关字符的个数
        final int DEEP_SIZE = 2;

        int matchIndex = 0;
        int notMatchNum = 0;

        StringBuilder sb = new StringBuilder();
        for (char eachChar : contentChars) {
            for (int i = matchIndex; i < shieldChars.length; i++) {
                char eachShield = shieldChars[i];
                if (eachChar == eachShield) {               // 匹配上字符
                    matchIndex++;
                    sb.append(eachChar);
                    notMatchNum = 0;
                    if (matchIndex == shieldChars.length) { // 匹配到末尾字符, 完成匹配并清理记录, 继续匹配后续字符
                        matchList.add(sb.toString());
                        matchIndex = 0;
                        sb = new StringBuilder();
                    }
                    break;
                }
                if (matchIndex <= 0) {                      // 没有匹配上字符, 并且非中间穿插无关字符
                    break;
                }
                notMatchNum ++;                             // 是中间穿插字符, 累计穿插字符个数
                sb.append(eachChar);
                if (notMatchNum <= DEEP_SIZE) {
                    break;
                }
                matchIndex = 0;                             // 中间穿插字符超过深度限制, 则清理记录, 继续匹配后续字符
                notMatchNum = 0;
                sb = new StringBuilder();
            }
        }
        return matchList;
    }

    private static void testMatch() {
        String contentStr = "有穷自动机的分类, 有穷自动1机的分类, 有穷自动12机的分类, 有穷自动123机的分类";
        String shieldStr = "动机";

        for (int i = 0; i < 20000; i++) {
            List<String> matchList = match(contentStr, shieldStr);
            logger.debug("match result: {}", matchList);
        }
        logger.info("match finished.");
    }
}
