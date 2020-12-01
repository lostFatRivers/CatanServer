package com.joker.tools.analyse;

import Game.ServerCore.logic.pb.PCmd.PBCMD;
import Game.ServerCore.logic.pb.UdpBattle.UDPCMD;
import cn.hutool.core.io.file.FileReader;

import java.util.List;

/**
 * 解析日志文件
 *
 * @author: Joker
 * @date: Created in 2020/11/12 11:36
 * @version: 1.0
 */
public class LogParser {

    public static void main(String[] args) {
        FileReader reader = new FileReader("C:/Users/Administrator/Desktop/log/04/msgTime.txt");
        List<String> lines = reader.readLines();
        lines.stream().map(CmdLine::new).sorted().forEach(System.out::println);
    }

    public static class CmdLine implements Comparable<CmdLine> {
        public final int cmd;
        public final double maxCostTime;
        public final double avgCostTime;

        public CmdLine(String line) {
            if (line.contains("message execute dump, ")) {
                line = line.split("message execute dump, ")[1];
            }
            String[] split = line.split(", ");
            cmd = Integer.parseInt(split[0].split(":")[1]);
            maxCostTime = Double.parseDouble(split[1].split(":")[1]);
            avgCostTime = Double.parseDouble(split[2].split(":")[1]);
        }

        @Override
        public String toString() {
            Object key = PBCMD.forNumber(cmd);
            if (key == null) {
                key = UDPCMD.forNumber(cmd);
            }
            if (key == null) {
                key = cmd;
            }
            return key + "\t" + cmd + "\t" + maxCostTime + "\t" + avgCostTime;
        }

        @Override
        public int compareTo(CmdLine o) {
            if (o.maxCostTime - maxCostTime > 0) {
                return 1;
            } else if (o.maxCostTime - maxCostTime < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
