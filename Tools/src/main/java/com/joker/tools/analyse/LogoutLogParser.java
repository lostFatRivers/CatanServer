package com.joker.tools.analyse;

import cn.hutool.core.io.file.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息执行信息日志解析
 *
 * @author: Joker
 * @date: Created in 2020/11/13 20:58
 * @version: 1.0
 */
public class LogoutLogParser {
    private static final Logger logger = LoggerFactory.getLogger("LogParser");

    private static final Map<Long, Logout> allCmd = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        logger.info("log read start.");

        FileReader reader = new FileReader("C:/Users/Administrator/Desktop/log/allLog/3/logout1.txt");
        List<String> lines = reader.readLines();
        lines.forEach(el -> {
            if (el.contains("start logout")) {
                Logout lo = new Logout(el);
                allCmd.put(lo.playerId, lo);
            } else {
                String str1 = el.split("logout, ")[1];
                String[] str1s = str1.split(",");
                String pidStr = str1s[0];
                String timeStr = str1s[1];
                long playerId = Long.parseLong(pidStr.split(":")[1]);
                long endTime = Long.parseLong(timeStr.split(":")[1]);
                if (!allCmd.containsKey(playerId) || !el.contains("LgOfflineProcess")) {
                    return;
                }
                Logout logout = allCmd.get(playerId);
                if (logout.endTime < endTime) {
                    logout.endTime = endTime;
                }
            }
        });

        List<Long> list = new ArrayList<>();
        allCmd.forEach((pid, logout) -> {
            long costTime = logout.endTime - logout.startTime;
            if (costTime < 0) {
                return;
            }
            list.add(costTime);
        });

        System.out.println(list.stream().max(Long::compare));
        System.out.println(list.stream().mapToDouble(Long::doubleValue).average());
        list.sort(Long::compare);
        for (Long aLong : list) {
            System.out.println(aLong);
        }
    }

    public static class Logout {
        public final long playerId;
        public final long startTime;
        public long endTime;

        public Logout(String line) {
            String str1 = line.split("logout, ")[1];
            String[] str1s = str1.split(",");
            String pidStr = str1s[0];
            String timeStr = str1s[1];
            playerId = Long.parseLong(pidStr.split(":")[1]);
            startTime = Long.parseLong(timeStr.split(":")[1]);
        }

    }
}
