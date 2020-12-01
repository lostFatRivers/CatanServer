package com.joker.tools.analyse;

import Game.ServerCore.logic.pb.PCmd;
import Game.ServerCore.logic.pb.UdpBattle;
import cn.hutool.core.io.file.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消息执行信息日志解析
 *
 * @author: Joker
 * @date: Created in 2020/11/13 20:58
 * @version: 1.0
 */
public class DelayLogParser {
    private static final Logger logger = LoggerFactory.getLogger("LogParser");

    private static final Map<String, Map<String, List<CmdExecute>>> allCmd = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        logger.info("log read start.");

        FileReader reader = new FileReader("C:/Users/Administrator/Desktop/log/04/msgDelay.txt");
        List<String> lines = reader.readLines();
        lines.parallelStream().map(CmdExecute::new).forEach(ce -> {
            if (!allCmd.containsKey(ce.serviceName)) {
                allCmd.putIfAbsent(ce.serviceName, new ConcurrentHashMap<>());
            }
            Map<String, List<CmdExecute>> handlerMap = allCmd.get(ce.serviceName);
            if (!handlerMap.containsKey(ce.handlerName)) {
                handlerMap.putIfAbsent(ce.handlerName, new CopyOnWriteArrayList<>());
            }
            handlerMap.get(ce.handlerName).add(ce);
        });

        Set<String> serviceNames = allCmd.keySet();
        for (String eachName : serviceNames) {
            Map<String, List<CmdExecute>> handlerMap = allCmd.get(eachName);
            Set<String> handlerNames = handlerMap.keySet();
            System.out.println(eachName);
            for (String eachHandler : handlerNames) {
                List<CmdExecute> cmdExecutes = handlerMap.get(eachHandler);
                System.out.println("\t" + eachHandler);
                int min = Math.min(10, cmdExecutes.size());
                cmdExecutes.sort((o1, o2) -> Integer.compare(o2.executeTime, o1.executeTime));
                System.out.println("\t\tcost top ten:");
                for (int i = 0; i < min; i++) {
                    System.out.println("\t\t\t" + cmdExecutes.get(i).toString());
                }
            }
        }
    }

    public static class CmdExecute {
        public final String serviceName;
        public final String handlerName;
        public final int cmd;
        public final int delayTime;
        public final int executeTime;

        public CmdExecute(String line) {
            String str1 = line.split(" {2}- {2}")[1];
            String[] str1s = str1.split(" execute message \\|\\|");
            String sAndH = str1s[0];
            String useStr = str1s[1];
            sAndH = sAndH.replace("[", "");
            sAndH = sAndH.replace("]", "");
            String[] sAndHs = sAndH.split("-");
            this.serviceName = sAndHs[0];
            this.handlerName = sAndHs[1];

            String[] split = useStr.split(", ");
            this.cmd = Integer.parseInt(split[0].split(":")[1]);
            int dTime = Integer.parseInt(split[2].split(":")[1]);
            this.delayTime = Math.max(dTime, 0);
            this.executeTime = Integer.parseInt(split[3].split(":")[1]);
        }

        @Override
        public String toString() {
            Object key = PCmd.PBCMD.forNumber(cmd);
            if (key == null) {
                key = UdpBattle.UDPCMD.forNumber(cmd);
            }
            if (key == null) {
                key = cmd;
            }
            return key + "\t" + cmd + "\t" + delayTime + "\t" + executeTime;
        }

    }
}
